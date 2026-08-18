package com.xiaoyv.bangumi.shared.sni

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.webkit.ProxyConfig
import androidx.webkit.ProxyController
import androidx.webkit.WebViewFeature
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.URI
import java.nio.charset.StandardCharsets
import java.util.Collections
import java.util.Locale
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * Loopback HTTP proxy that applies the existing TLS ClientHello fragmentation to WebView traffic.
 *
 * WebView proxy overrides are process-wide on Android, so starting one instance affects every
 * WebView in the current application process.
 */
actual class AntiSniWebProxy actual constructor(
    initialHosts: Map<String, List<String>>,
    tlsFragmentationDomains: Collection<String>,
    private val connectTimeoutMillis: Int,
    private val headerTimeoutMillis: Int,
    private val errorHandler: (Throwable) -> Unit,
) : AutoCloseable {
    private val dns = AntiSniDns(initialHosts)
    private val fragmentationPolicy = DomainTlsFragmentationPolicy(tlsFragmentationDomains)
    private val activeSockets = Collections.synchronizedSet(mutableSetOf<Socket>())

    @Volatile
    private var serverSocket: ServerSocket? = null

    @Volatile
    private var workerPool: ExecutorService? = null

    @Volatile
    private var webProxyApplied = false

    val isRunning: Boolean
        get() = serverSocket?.isClosed == false

    val boundPort: Int
        get() = serverSocket?.localPort ?: 0


    @Synchronized
    actual fun start(): Int {
        return try {
            startInternal()
        } catch (error: Exception) {
            reportError(error)
            stop()
            0
        }
    }

    private fun startInternal(): Int {
        check(WebViewFeature.isFeatureSupported(WebViewFeature.PROXY_OVERRIDE)) {
            "The installed WebView provider does not support proxy overrides"
        }
        serverSocket?.takeUnless(ServerSocket::isClosed)?.let {
            applyWebProxy(it.localPort)
            return it.localPort
        }

        val workers = Executors.newCachedThreadPool(ProxyThreadFactory("anti-sni-proxy"))
        val server = ServerSocket().apply {
            reuseAddress = true
            bind(InetSocketAddress(LOOPBACK_ADDRESS, 0), ACCEPT_BACKLOG)
        }
        workerPool = workers
        serverSocket = server
        workers.execute { acceptConnections(server, workers) }
        applyWebProxy(server.localPort)
        return server.localPort
    }

    private fun applyWebProxy(port: Int) {
        val proxyConfig = ProxyConfig.Builder()
            .addProxyRule("$LOOPBACK_ADDRESS:$port")
            .addBypassRule("localhost")
            .addBypassRule(LOOPBACK_ADDRESS)
            .addBypassRule("[::1]")
            .build()

        ProxyController.getInstance().setProxyOverride(
            proxyConfig,
            WEBVIEW_EXECUTOR,
        ) {
            try {
                if (boundPort != port) {
                    ProxyController.getInstance().clearProxyOverride(WEBVIEW_EXECUTOR) {}
                    return@setProxyOverride
                }
                webProxyApplied = true
            } catch (error: Exception) {
                reportError(error)
            }
        }
    }

    /**
     * Stops accepting traffic, closes active tunnels and clears the process-wide WebView override.
     */
    @Synchronized
    fun stop(onStopped: () -> Unit = {}) {
        try {
            serverSocket?.closeQuietly()
            serverSocket = null

            synchronized(activeSockets) {
                activeSockets.forEach(Socket::closeQuietly)
                activeSockets.clear()
            }

            workerPool?.shutdownNow()
            workerPool = null

            if (webProxyApplied && WebViewFeature.isFeatureSupported(WebViewFeature.PROXY_OVERRIDE)) {
                webProxyApplied = false
                ProxyController.getInstance().clearProxyOverride(WEBVIEW_EXECUTOR) {
                    invokeSafely(onStopped)
                }
            } else {
                invokeSafely(onStopped)
            }
        } catch (error: Exception) {
            reportError(error)
            invokeSafely(onStopped)
        }
    }

    actual override fun close() = stop()

    private fun acceptConnections(server: ServerSocket, workers: ExecutorService) {
        while (!server.isClosed) {
            val client = try {
                server.accept()
            } catch (error: Exception) {
                if (!server.isClosed) reportError(error)
                break
            }

            configureSocket(client)
            activeSockets += client
            try {
                workers.execute { handleClient(client, workers) }
            } catch (error: Exception) {
                reportError(error)
                unregisterAndClose(client)
            }
        }
    }

    private fun handleClient(client: Socket, workers: ExecutorService) {
        var remote: Socket? = null
        try {
            client.soTimeout = headerTimeoutMillis
            val clientInput = BufferedInputStream(client.getInputStream())
            val request = readProxyRequest(clientInput) ?: return
            client.soTimeout = 0

            val target = request.target ?: run {
                sendProxyError(client.getOutputStream(), 400, "Bad Request")
                return
            }
            remote = connectRemote(target.host, target.port)
            activeSockets += remote

            if (request.isConnect) {
                client.getOutputStream().write(CONNECTED_RESPONSE)
                client.getOutputStream().flush()
            } else {
                remote.getOutputStream().write(request.forwardHeader)
                remote.getOutputStream().flush()
            }

            bridge(
                client = client,
                clientInput = clientInput,
                remote = remote,
                workers = workers,
            )
        } catch (error: Exception) {
            if (!client.isClosed && isRunning) reportError(error)
            runCatching {
                if (remote == null) {
                    sendProxyError(client.getOutputStream(), 502, "Bad Gateway")
                }
            }
        } finally {
            unregisterAndClose(client)
            remote?.let(::unregisterAndClose)
        }
    }

    private fun connectRemote(host: String, port: Int): Socket {
        var lastFailure: Exception? = null
        for (address in dns.lookup(host)) {
            val socket = Socket()
            try {
                configureSocket(socket)
                socket.connect(InetSocketAddress(address, port), connectTimeoutMillis)
                return AntiSniSocket(socket, fragmentationPolicy, host)
            } catch (exception: Exception) {
                lastFailure = exception
                socket.closeQuietly()
            }
        }
        throw lastFailure ?: IllegalStateException("No address available for $host")
    }

    private fun bridge(
        client: Socket,
        clientInput: InputStream,
        remote: Socket,
        workers: ExecutorService,
    ) {
        val closed = AtomicBoolean(false)
        val closeBoth = {
            if (closed.compareAndSet(false, true)) {
                client.closeQuietly()
                remote.closeQuietly()
            }
        }

        workers.execute {
            try {
                relay(remote.getInputStream(), client.getOutputStream())
            } catch (_: Exception) {
                // The other relay may close both sockets before this task acquires its streams.
            } finally {
                closeBoth()
            }
        }
        try {
            relay(clientInput, remote.getOutputStream())
        } catch (_: Exception) {
            // Closing either side is the normal tunnel termination path.
        } finally {
            closeBoth()
        }
    }

    private fun relay(input: InputStream, output: OutputStream) {
        val buffer = ByteArray(RELAY_BUFFER_SIZE)
        try {
            while (true) {
                val count = input.read(buffer)
                if (count < 0) return
                output.write(buffer, 0, count)
                output.flush()
            }
        } catch (_: Exception) {
            // Closing either side is the normal tunnel termination path.
        }
    }

    private fun readProxyRequest(input: InputStream): ProxyRequest? {
        val bytes = ByteArrayOutputStream()
        var matched = 0
        while (bytes.size() < MAX_HEADER_SIZE) {
            val value = input.read()
            if (value < 0) return null
            bytes.write(value)

            matched = if (value == HEADER_TERMINATOR[matched].toInt()) {
                matched + 1
            } else if (value == HEADER_TERMINATOR[0].toInt()) {
                1
            } else {
                0
            }
            if (matched == HEADER_TERMINATOR.size) break
        }
        if (matched != HEADER_TERMINATOR.size) return null

        val header = bytes.toString(StandardCharsets.ISO_8859_1.name())
        val lines = header.split(CRLF)
        val requestParts = lines.firstOrNull()?.split(' ', limit = 3).orEmpty()
        if (requestParts.size != 3) return null

        val method = requestParts[0]
        if (method.equals("CONNECT", ignoreCase = true)) {
            return ProxyRequest(
                isConnect = true,
                target = parseAuthority(requestParts[1], DEFAULT_HTTPS_PORT),
                forwardHeader = ByteArray(0),
            )
        }

        val headers = lines.drop(1)
            .filter(String::isNotEmpty)
            .mapNotNull { line ->
                val separator = line.indexOf(':')
                if (separator <= 0) null else line.substring(0, separator).trim() to
                        line.substring(separator + 1).trim()
            }
        val absoluteUri = runCatching { URI(requestParts[1]) }.getOrNull()
        val target = when {
            absoluteUri?.host != null && absoluteUri.scheme.equals("http", ignoreCase = true) ->
                ConnectTarget(
                    host = absoluteUri.host,
                    port = absoluteUri.port.takeIf { it > 0 } ?: DEFAULT_HTTP_PORT,
                )

            else -> headers.firstOrNull { it.first.equals("Host", ignoreCase = true) }
                ?.second
                ?.let { parseAuthority(it, DEFAULT_HTTP_PORT) }
        }
        if (target == null) return null

        val requestTarget = if (absoluteUri?.host != null) {
            absoluteUri.rawPath.ifEmpty { "/" } +
                    absoluteUri.rawQuery?.let { "?$it" }.orEmpty()
        } else {
            requestParts[1]
        }
        val forwardHeader = buildString {
            append(method).append(' ').append(requestTarget).append(' ').append(requestParts[2]).append(CRLF)
            headers.forEach { (name, value) ->
                if (!name.equals("Proxy-Connection", ignoreCase = true) &&
                    !name.equals("Connection", ignoreCase = true)
                ) {
                    append(name).append(": ").append(value).append(CRLF)
                }
            }
            append("Connection: close").append(CRLF)
            append(CRLF)
        }.toByteArray(StandardCharsets.ISO_8859_1)

        return ProxyRequest(false, target, forwardHeader)
    }

    private fun parseAuthority(authority: String, defaultPort: Int): ConnectTarget? {
        val value = authority.trim()
        if (value.startsWith('[')) {
            val bracket = value.indexOf(']')
            if (bracket <= 1) return null
            val host = value.substring(1, bracket)
            val port = value.substring(bracket + 1)
                .removePrefix(":")
                .takeIf(String::isNotEmpty)
                ?.toIntOrNull()
                ?: defaultPort
            return ConnectTarget(host, port).takeIf(ConnectTarget::isValid)
        }

        val separator = value.lastIndexOf(':')
        val hasSingleSeparator = separator > 0 && value.indexOf(':') == separator
        val host = if (hasSingleSeparator) value.substring(0, separator) else value
        val port = if (hasSingleSeparator) {
            value.substring(separator + 1).toIntOrNull() ?: return null
        } else {
            defaultPort
        }
        return ConnectTarget(host.lowercase(Locale.US), port).takeIf(ConnectTarget::isValid)
    }

    private fun configureSocket(socket: Socket) {
        socket.tcpNoDelay = true
        socket.keepAlive = true
    }

    private fun sendProxyError(output: OutputStream, code: Int, reason: String) {
        output.write(
            "HTTP/1.1 $code $reason\r\nConnection: close\r\nContent-Length: 0\r\n\r\n"
                .toByteArray(StandardCharsets.US_ASCII)
        )
        output.flush()
    }

    private fun unregisterAndClose(socket: Socket) {
        activeSockets -= socket
        socket.closeQuietly()
    }

    private fun invokeSafely(callback: () -> Unit) {
        try {
            callback()
        } catch (error: Exception) {
            reportError(error)
        }
    }

    private fun reportError(error: Throwable) {
        try {
            errorHandler(error)
        } catch (handlerError: Exception) {
            Log.e(LOG_TAG, "Web proxy error handler failed", handlerError)
        }
    }

    private data class ProxyRequest(
        val isConnect: Boolean,
        val target: ConnectTarget?,
        val forwardHeader: ByteArray,
    )

    private data class ConnectTarget(
        val host: String,
        val port: Int,
    ) {
        fun isValid(): Boolean = host.isNotBlank() && port in 1..65535
    }

    private inner class ProxyThreadFactory(private val prefix: String) : ThreadFactory {
        private val threadId = AtomicInteger()

        override fun newThread(task: Runnable): Thread {
            return Thread(task, "$prefix-${threadId.incrementAndGet()}").apply {
                isDaemon = true
                uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { thread, error ->
                    if (error is Exception) {
                        reportError(error)
                    } else {
                        Thread.getDefaultUncaughtExceptionHandler()?.uncaughtException(thread, error)
                    }
                }
            }
        }
    }

    private companion object {
        const val LOOPBACK_ADDRESS = "127.0.0.1"
        const val ACCEPT_BACKLOG = 128
        const val MAX_HEADER_SIZE = 32 * 1024
        const val RELAY_BUFFER_SIZE = 16 * 1024
        const val DEFAULT_HTTP_PORT = 80
        const val DEFAULT_HTTPS_PORT = 443
        const val CRLF = "\r\n"
        const val LOG_TAG = "AntiSniWebProxy"

        val HEADER_TERMINATOR = byteArrayOf(13, 10, 13, 10)
        val CONNECTED_RESPONSE = "HTTP/1.1 200 Connection Established\r\n\r\n".toByteArray(StandardCharsets.US_ASCII)
        val WEBVIEW_EXECUTOR = Executor { task ->
            Handler(Looper.getMainLooper()).post(task)
        }
    }
}

private fun Socket.closeQuietly() {
    runCatching(::close)
}

private fun ServerSocket.closeQuietly() {
    runCatching(::close)
}
