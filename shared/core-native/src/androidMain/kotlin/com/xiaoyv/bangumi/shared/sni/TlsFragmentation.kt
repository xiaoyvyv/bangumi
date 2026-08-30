package com.xiaoyv.bangumi.shared.sni

import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer

internal object TlsFragmentation {
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
