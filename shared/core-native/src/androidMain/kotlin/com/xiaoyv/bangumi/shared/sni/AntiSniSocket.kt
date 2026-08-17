package com.xiaoyv.bangumi.shared.sni

import java.io.ByteArrayOutputStream
import java.io.FilterOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.net.Socket
import java.nio.ByteBuffer

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

    private inner class FragmentingOutputStream(
        originalStream: OutputStream,
    ) : FilterOutputStream(originalStream) {
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

private object TlsFragmentation {
    private const val TLS_HANDSHAKE = 0x16
    private const val CLIENT_HELLO = 0x01
    private const val TLS_HEADER_SIZE = 5
    private const val TLS_RECORD_COUNT = 4

    fun firstRecordLengthOrNull(data: ByteArray): Int? {
        if (data.size < TLS_HEADER_SIZE) return null
        if (data[0].toInt() and 0xFF != TLS_HANDSHAKE) return data.size
        val recordLength = readUnsignedShort(data[3], data[4]) + TLS_HEADER_SIZE
        return recordLength
    }

    fun sendClientHelloFragments(
        output: OutputStream,
        record: ByteArray,
    ): Boolean {
        if (!looksLikeTlsHandshake(record)) return false

        val mutableRecord = record.copyOf()
        val sni = parseSni(mutableRecord) ?: return false
        mutableRecord[2] = 0x04

        val lastDot = findLastDot(mutableRecord, sni.position, sni.length)
        val header = mutableRecord.copyOfRange(0, 3)
        val payloadBeforeDot = mutableRecord.copyOfRange(TLS_HEADER_SIZE, lastDot)
        val payloadAfterDot = mutableRecord.copyOfRange(lastDot, mutableRecord.size)
        val records = buildList {
            addAll(splitIntoTlsRecords(header, payloadBeforeDot, TLS_RECORD_COUNT / 2))
            addAll(splitIntoTlsRecords(header, payloadAfterDot, TLS_RECORD_COUNT - size))
        }

        if (records.isEmpty()) return false

        // SniShaper's default tls-rf rule uses segments=1: multiple TLS records,
        // merged into a single TCP write without the configured record interval.
        val merged = ByteArrayOutputStream(record.size + records.size * TLS_HEADER_SIZE)
        records.forEach { fragment ->
            merged.write(fragment)
        }
        output.write(merged.toByteArray())
        return true
    }

    private fun looksLikeTlsHandshake(record: ByteArray): Boolean {
        if (record.size < TLS_HEADER_SIZE + 4) return false
        if (record[0].toInt() and 0xFF != TLS_HANDSHAKE) return false
        if (record[1].toInt() and 0xFF != 0x03) return false
        return record[TLS_HEADER_SIZE].toInt() and 0xFF == CLIENT_HELLO
    }

    private fun parseSni(record: ByteArray): SniLocation? {
        if (!looksLikeTlsHandshake(record)) return null

        val payloadLimit = readUnsignedShort(record[3], record[4]) + TLS_HEADER_SIZE
        if (payloadLimit > record.size) return null

        var offset = TLS_HEADER_SIZE
        val handshakeLength = readUnsignedMedium(record, offset + 1)
        offset += 4
        if (offset + handshakeLength > payloadLimit) return null

        offset += 2 // client version
        offset += 32 // random
        if (offset >= payloadLimit) return null

        val sessionIdLength = record[offset].toInt() and 0xFF
        offset += 1 + sessionIdLength
        if (offset + 2 > payloadLimit) return null

        val cipherSuitesLength = readUnsignedShort(record[offset], record[offset + 1])
        offset += 2 + cipherSuitesLength
        if (offset >= payloadLimit) return null

        val compressionMethodsLength = record[offset].toInt() and 0xFF
        offset += 1 + compressionMethodsLength
        if (offset + 2 > payloadLimit) return null

        val extensionsLength = readUnsignedShort(record[offset], record[offset + 1])
        offset += 2
        val extensionsEnd = offset + extensionsLength
        if (extensionsEnd > payloadLimit) return null

        while (offset + 4 <= extensionsEnd) {
            val extensionType = readUnsignedShort(record[offset], record[offset + 1])
            val extensionLength = readUnsignedShort(record[offset + 2], record[offset + 3])
            val extensionDataStart = offset + 4
            val extensionDataEnd = extensionDataStart + extensionLength
            if (extensionDataEnd > extensionsEnd) return null

            if (extensionType == 0x0000) {
                if (extensionLength < 5) return null
                val listLength = readUnsignedShort(record[extensionDataStart], record[extensionDataStart + 1])
                if (listLength + 2 > extensionLength) return null

                val nameTypeOffset = extensionDataStart + 2
                if (record[nameTypeOffset].toInt() != 0) return null

                val nameLength = readUnsignedShort(record[nameTypeOffset + 1], record[nameTypeOffset + 2])
                val nameStart = nameTypeOffset + 3
                val nameEnd = nameStart + nameLength
                if (nameEnd > extensionDataEnd) return null
                return SniLocation(nameStart, nameLength)
            }

            offset = extensionDataEnd
        }

        return null
    }

    private fun findLastDot(record: ByteArray, sniPosition: Int, sniLength: Int): Int {
        val sniEnd = sniPosition + sniLength - 1
        for (index in sniEnd downTo sniPosition) {
            if (record[index] == '.'.code.toByte()) {
                return index
            }
        }
        return sniPosition + (sniLength / 2)
    }

    private fun splitIntoTlsRecords(header: ByteArray, payload: ByteArray, count: Int): List<ByteArray> {
        if (payload.isEmpty() || count <= 0) return emptyList()
        if (count == 1 || payload.size <= count) return listOf(createTlsRecord(header, payload))

        val result = ArrayList<ByteArray>(count)
        val baseChunkSize = payload.size / count
        var start = 0
        repeat(count) { index ->
            val end = if (index == count - 1) payload.size else start + baseChunkSize
            result += createTlsRecord(header, payload.copyOfRange(start, end))
            start = end
        }
        return result
    }

    private fun createTlsRecord(header: ByteArray, payload: ByteArray): ByteArray {
        val buffer = ByteBuffer.allocate(TLS_HEADER_SIZE + payload.size)
        buffer.put(header)
        buffer.putShort(payload.size.toShort())
        buffer.put(payload)
        return buffer.array()
    }

    private fun readUnsignedShort(high: Byte, low: Byte): Int {
        return ((high.toInt() and 0xFF) shl 8) or (low.toInt() and 0xFF)
    }

    private fun readUnsignedMedium(data: ByteArray, offset: Int): Int {
        return ((data[offset].toInt() and 0xFF) shl 16) or
            ((data[offset + 1].toInt() and 0xFF) shl 8) or
            (data[offset + 2].toInt() and 0xFF)
    }

    private data class SniLocation(
        val position: Int,
        val length: Int,
    )
}
