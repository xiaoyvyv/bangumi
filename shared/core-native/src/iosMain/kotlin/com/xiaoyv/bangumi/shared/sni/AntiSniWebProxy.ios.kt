package com.xiaoyv.bangumi.shared.sni

import kotlinx.atomicfu.atomic
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.refTo
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.sizeOf
import kotlinx.cinterop.value
import platform.CoreFoundation.CFSwapInt16BigToHost
import platform.darwin.DISPATCH_QUEUE_CONCURRENT
import platform.darwin.DISPATCH_SOURCE_TYPE_READ
import platform.darwin.dispatch_async
import platform.darwin.dispatch_queue_create
import platform.darwin.dispatch_resume
import platform.darwin.dispatch_source_cancel
import platform.darwin.dispatch_source_create
import platform.darwin.dispatch_source_set_event_handler
import platform.darwin.dispatch_source_t
import platform.darwin.inet_pton
import platform.posix.AF_INET
import platform.posix.AF_UNSPEC
import platform.posix.EINTR
import platform.posix.F_GETFL
import platform.posix.F_SETFL
import platform.posix.IPPROTO_TCP
import platform.posix.O_NONBLOCK
import platform.posix.SHUT_RDWR
import platform.posix.SOCK_STREAM
import platform.posix.SOL_SOCKET
import platform.posix.SO_REUSEADDR
import platform.posix.TCP_NODELAY
import platform.posix.accept
import platform.posix.addrinfo
import platform.posix.bind
import platform.posix.close
import platform.posix.connect
import platform.posix.errno
import platform.posix.fcntl
import platform.posix.freeaddrinfo
import platform.posix.getaddrinfo
import platform.posix.getsockname
import platform.posix.listen
import platform.posix.recv
import platform.posix.send
import platform.posix.setsockopt
import platform.posix.shutdown
import platform.posix.sockaddr_in
import platform.posix.socket
import platform.posix.socklen_tVar

actual class AntiSniWebProxy actual constructor(
    initialHosts: Map<String, List<String>>,
    tlsFragmentationDomains: Collection<String>,
    connectTimeoutMillis: Int,
    headerTimeoutMillis: Int,
    errorHandler: (Throwable) -> Unit,
) : AutoCloseable {
    private data class ConnectTarget(val host: String, val port: Int)

    private var serverFd: Int = -1

    var boundPort: Int = 0
        private set

    private val hostsState = atomic(initialHosts)
    private val activeSockets = atomic<Set<Int>>(emptySet())

    private val queue = dispatch_queue_create("com.hosts.proxy.queue", DISPATCH_QUEUE_CONCURRENT)
    private var serverSource: dispatch_source_t = null

    actual fun start(): Int {
        if (serverFd >= 0) return boundPort

        val fd = socketListener() ?: return 0
        serverFd = fd

        val source = dispatch_source_create(
            DISPATCH_SOURCE_TYPE_READ,
            fd.toULong(),
            0u,
            queue
        ) ?: run {
            closeSocket(fd)
            serverFd = -1
            return 0
        }

        dispatch_source_set_event_handler(source) {
            while (true) {
                val clientFd = accept(fd, null, null)
                if (clientFd < 0) {
                    break
                }

                configureTcpSocket(clientFd)
                dispatch_async(queue) { handleConnectRequest(clientFd) }
            }
        }

        dispatch_resume(source)
        serverSource = source
        return boundPort
    }

    private fun socketListener(): Int? {
        val fd = socket(AF_INET, SOCK_STREAM, 0)
        if (fd < 0) return null

        memScoped {
            val opt = alloc<IntVar>().apply { value = 1 }
            setsockopt(fd, SOL_SOCKET, SO_REUSEADDR, opt.ptr, sizeOf<IntVar>().toUInt())

            val flags = fcntl(fd, F_GETFL, 0)
            if (flags >= 0) {
                fcntl(fd, F_SETFL, flags or O_NONBLOCK)
            }

            val serverAddr = alloc<sockaddr_in>().apply {
                sin_family = AF_INET.toUByte()
                inet_pton(AF_INET, "127.0.0.1", sin_addr.ptr)
                sin_port = 0u
            }

            if (bind(fd, serverAddr.ptr.reinterpret(), sizeOf<sockaddr_in>().toUInt()) < 0) {
                closeSocket(fd)
                return null
            }

            if (listen(fd, 512) < 0) {
                closeSocket(fd)
                return null
            }

            val len = alloc<socklen_tVar>().apply { value = sizeOf<sockaddr_in>().toUInt() }
            if (getsockname(fd, serverAddr.ptr.reinterpret(), len.ptr) < 0) {
                closeSocket(fd)
                return null
            }

            boundPort = CFSwapInt16BigToHost(serverAddr.sin_port).toInt()
        }

        return fd
    }

    private fun handleConnectRequest(clientFd: Int) {
        val requestHeader = readConnectHeader(clientFd) ?: run {
            closeSocket(clientFd)
            return
        }

        val target = parseConnectTarget(requestHeader) ?: run {
            sendProxyError(clientFd, 400, "Bad Request")
            closeSocket(clientFd)
            return
        }

        val currentHosts = hostsState.value
        val resolvedTarget = currentHosts[target.host]?.firstOrNull() ?: target.host
        val remoteFd = connectRemote(resolvedTarget, target.port) ?: run {
            sendProxyError(clientFd, 502, "Bad Gateway")
            closeSocket(clientFd)
            return
        }

        registerSocket(clientFd)
        registerSocket(remoteFd)

        val response = "HTTP/1.1 200 Connection Established\r\n\r\n"
        if (!sendFully(clientFd, response.encodeToByteArray(), response.length)) {
            closeTrackedPair(clientFd, remoteFd)
            return
        }

        bridgeSockets(clientFd, remoteFd)
    }

    private fun readConnectHeader(clientFd: Int): String? {
        val buffer = ByteArray(1024)
        val header = StringBuilder()

        while (header.length < MAX_HEADER_SIZE) {
            val readBytes = recv(clientFd, buffer.refTo(0), buffer.size.toULong(), 0)
            when {
                readBytes > 0 -> {
                    header.append(buffer.decodeToString(0, readBytes.toInt()))
                    if (header.contains(HEADER_TERMINATOR)) {
                        return header.toString()
                    }
                }

                readBytes == 0L -> return null
                errno == EINTR -> continue
                else -> return null
            }
        }

        return null
    }

    private fun parseConnectTarget(requestHeader: String): ConnectTarget? {
        val firstLine = requestHeader.lineSequence().firstOrNull()?.trim().orEmpty()
        val parts = firstLine.split(' ', limit = 3)
        if (parts.size < 2 || !parts[0].equals("CONNECT", ignoreCase = true)) return null

        val authority = parts[1]
        if (authority.isEmpty()) return null

        if (authority.startsWith('[')) {
            val closingBracket = authority.indexOf(']')
            if (closingBracket <= 1) return null
            val host = authority.substring(1, closingBracket)
            val port = authority.substring(closingBracket + 1)
                .removePrefix(":")
                .toIntOrNull()
                ?: DEFAULT_HTTPS_PORT
            return ConnectTarget(host, port)
        }

        val separatorIndex = authority.lastIndexOf(':')
        if (separatorIndex <= 0 || separatorIndex == authority.lastIndex) {
            return ConnectTarget(authority, DEFAULT_HTTPS_PORT)
        }

        val host = authority.substring(0, separatorIndex)
        val port = authority.substring(separatorIndex + 1).toIntOrNull() ?: return null
        return ConnectTarget(host, port)
    }

    private fun connectRemote(host: String, port: Int): Int? {
        memScoped {
            val hints = alloc<addrinfo>().apply {
                ai_family = AF_UNSPEC
                ai_socktype = SOCK_STREAM
            }
            val resultPtr = allocPointerTo<addrinfo>()
            if (getaddrinfo(host, port.toString(), hints.ptr, resultPtr.ptr) != 0) {
                return null
            }

            var cursor = resultPtr.value
            while (cursor != null) {
                val candidate = cursor.pointed
                val fd = socket(candidate.ai_family, candidate.ai_socktype, candidate.ai_protocol)
                if (fd >= 0) {
                    configureTcpSocket(fd)
                    if (connect(fd, candidate.ai_addr, candidate.ai_addrlen) == 0) {
                        freeaddrinfo(resultPtr.value)
                        return fd
                    }
                    closeSocket(fd)
                }
                cursor = candidate.ai_next
            }

            freeaddrinfo(resultPtr.value)
        }

        return null
    }

    private fun bridgeSockets(clientFd: Int, remoteFd: Int) {
        val isClosed = atomic(false)

        fun closeAll() {
            if (isClosed.compareAndSet(expect = false, update = true)) {
                closeTrackedPair(clientFd, remoteFd)
            }
        }

        dispatch_async(queue) { relay(clientFd, remoteFd, ::closeAll) }
        dispatch_async(queue) { relay(remoteFd, clientFd, ::closeAll) }
    }

    private fun relay(sourceFd: Int, targetFd: Int, closeAll: () -> Unit) {
        val buffer = ByteArray(BRIDGE_BUFFER_SIZE)

        while (true) {
            val readBytes = recv(sourceFd, buffer.refTo(0), buffer.size.toULong(), 0)
            when {
                readBytes > 0 -> {
                    if (!sendFully(targetFd, buffer, readBytes.toInt())) {
                        closeAll()
                        return
                    }
                }

                readBytes == 0L -> {
                    closeAll()
                    return
                }

                errno == EINTR -> continue
                else -> {
                    closeAll()
                    return
                }
            }
        }
    }

    private fun sendFully(fd: Int, bytes: ByteArray, length: Int): Boolean {
        var offset = 0
        while (offset < length) {
            val sent = send(fd, bytes.refTo(offset), (length - offset).toULong(), 0)
            when {
                sent > 0 -> offset += sent.toInt()
                sent == 0L -> return false
                errno == EINTR -> continue
                else -> return false
            }
        }
        return true
    }

    private fun sendProxyError(clientFd: Int, code: Int, reason: String) {
        val body = "$code $reason"
        val response = buildString {
            append("HTTP/1.1 ")
            append(body)
            append("\r\n")
            append("Content-Length: 0\r\n")
            append("Connection: close\r\n\r\n")
        }
        sendFully(clientFd, response.encodeToByteArray(), response.length)
    }

    private fun configureTcpSocket(fd: Int) {
        memScoped {
            val noDelay = alloc<IntVar>().apply { value = 1 }
            setsockopt(fd, IPPROTO_TCP, TCP_NODELAY, noDelay.ptr, sizeOf<IntVar>().toUInt())
        }
    }

    private fun registerSocket(fd: Int) {
        while (true) {
            val current = activeSockets.value
            if (activeSockets.compareAndSet(current, current + fd)) {
                return
            }
        }
    }

    private fun unregisterSocket(fd: Int) {
        while (true) {
            val current = activeSockets.value
            if (fd !in current) return
            if (activeSockets.compareAndSet(current, current - fd)) {
                return
            }
        }
    }

    private fun closeTrackedPair(fd1: Int, fd2: Int) {
        closeSocket(fd1)
        closeSocket(fd2)
    }

    private fun closeSocket(fd: Int) {
        if (fd < 0) return
        unregisterSocket(fd)
        shutdown(fd, SHUT_RDWR)
        close(fd)
    }

    fun stop() {
        serverSource?.let {
            dispatch_source_cancel(it)
            serverSource = null
        }

        val currentServerFd = serverFd
        serverFd = -1
        boundPort = 0
        closeSocket(currentServerFd)

        activeSockets.value.forEach(::closeSocket)
    }

    actual override fun close() = stop()

    companion object {
        private const val DEFAULT_HTTPS_PORT = 443
        private const val MAX_HEADER_SIZE = 32 * 1024
        private const val BRIDGE_BUFFER_SIZE = 16 * 1024
        private const val HEADER_TERMINATOR = "\r\n\r\n"
    }
}
