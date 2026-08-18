package com.xiaoyv.bangumi.shared.sni

import java.io.ByteArrayOutputStream
import java.io.FilterOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress

class AntiSniSocket(
    private val delegate: Socket,
    private val fragmentationPolicy: TlsFragmentationPolicy,
    initialHost: String? = null,
) : Socket() {
    @Volatile
    private var fragmentationEnabled = fragmentationPolicy.shouldFragment(initialHost)

    private val fragmentingOutputStream by lazy {
        FragmentingOutputStream(delegate.getOutputStream())
    }

    override fun getOutputStream(): OutputStream {
        return fragmentingOutputStream
    }

    override fun getInputStream(): InputStream = delegate.getInputStream()
    override fun bind(bindpoint: SocketAddress?) = delegate.bind(bindpoint)
    override fun connect(endpoint: SocketAddress?) {
        captureHost(endpoint)
        delegate.connect(endpoint)
    }

    override fun connect(endpoint: SocketAddress?, timeout: Int) {
        captureHost(endpoint)
        delegate.connect(endpoint, timeout)
    }

    override fun close() = delegate.close()
    override fun isConnected() = delegate.isConnected
    override fun isClosed() = delegate.isClosed
    override fun isBound() = delegate.isBound
    override fun isInputShutdown() = delegate.isInputShutdown
    override fun isOutputShutdown() = delegate.isOutputShutdown
    override fun shutdownInput() = delegate.shutdownInput()
    override fun shutdownOutput() = delegate.shutdownOutput()
    override fun getInetAddress(): InetAddress? = delegate.inetAddress
    override fun getLocalAddress(): InetAddress? = delegate.localAddress
    override fun getPort() = delegate.port
    override fun getLocalPort() = delegate.localPort
    override fun getRemoteSocketAddress(): SocketAddress? = delegate.remoteSocketAddress
    override fun getLocalSocketAddress(): SocketAddress? = delegate.localSocketAddress
    override fun setTcpNoDelay(on: Boolean) {
        delegate.tcpNoDelay = on
    }

    override fun getTcpNoDelay() = delegate.tcpNoDelay
    override fun setSoLinger(on: Boolean, linger: Int) = delegate.setSoLinger(on, linger)
    override fun getSoLinger() = delegate.soLinger
    override fun sendUrgentData(data: Int) = delegate.sendUrgentData(data)
    override fun setOOBInline(on: Boolean) {
        delegate.oobInline = on
    }

    override fun getOOBInline() = delegate.oobInline
    override fun setSoTimeout(timeout: Int) {
        delegate.soTimeout = timeout
    }

    override fun getSoTimeout() = delegate.soTimeout
    override fun setSendBufferSize(size: Int) {
        delegate.sendBufferSize = size
    }

    override fun getSendBufferSize() = delegate.sendBufferSize
    override fun setReceiveBufferSize(size: Int) {
        delegate.receiveBufferSize = size
    }

    override fun getReceiveBufferSize() = delegate.receiveBufferSize
    override fun setKeepAlive(on: Boolean) {
        delegate.keepAlive = on
    }

    override fun getKeepAlive() = delegate.keepAlive
    override fun setTrafficClass(tc: Int) {
        delegate.trafficClass = tc
    }

    override fun getTrafficClass() = delegate.trafficClass
    override fun setReuseAddress(on: Boolean) {
        delegate.reuseAddress = on
    }

    override fun getReuseAddress() = delegate.reuseAddress
    override fun setPerformancePreferences(connectionTime: Int, latency: Int, bandwidth: Int) {
        delegate.setPerformancePreferences(connectionTime, latency, bandwidth)
    }

    override fun toString() = delegate.toString()

    private fun captureHost(endpoint: SocketAddress?) {
        val host = (endpoint as? InetSocketAddress)?.hostString
        if (!host.isNullOrBlank()) {
            fragmentationEnabled = fragmentationPolicy.shouldFragment(host)
        }
    }

    private inner class FragmentingOutputStream(originalStream: OutputStream) : FilterOutputStream(originalStream) {
        private val pending = ByteArrayOutputStream()
        private var firstRecordHandled = false

        override fun write(b: Int) {
            write(byteArrayOf(b.toByte()), 0, 1)
        }

        override fun write(b: ByteArray, off: Int, len: Int) {
            if (len <= 0) return

            if (firstRecordHandled) {
                out.write(b, off, len)
                return
            }

            if (!fragmentationEnabled) {
                firstRecordHandled = true
                out.write(b, off, len)
                return
            }

            pending.write(b, off, len)
            drainPendingIfReady()
        }

        override fun flush() {
            if (firstRecordHandled) {
                out.flush()
            }
        }

        private fun drainPendingIfReady() {
            val buffered = pending.toByteArray()
            val recordLength = TlsFragmentation.firstRecordLengthOrNull(buffered) ?: return
            if (buffered.size < recordLength) return

            firstRecordHandled = true
            pending.reset()

            val firstRecord = buffered.copyOfRange(0, recordLength)
            val remainder = buffered.copyOfRange(recordLength, buffered.size)

            val fragmented = TlsFragmentation.sendClientHelloFragments(out, firstRecord)
            if (!fragmented) {
                out.write(firstRecord)
            }

            if (remainder.isNotEmpty()) {
                out.write(remainder)
            }
            out.flush()
        }
    }
}

