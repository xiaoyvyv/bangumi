package com.xiaoyv.subtitle.api.utils

import com.xiaoyv.subtitle.api.utils.IOUtils.toByteArray
import com.xiaoyv.subtitle.api.utils.icu.text.CharsetDetector
import org.mozilla.universalchardet.UniversalDetector
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream

object FileUtils {
    /**
     * Detect charset encoding of a file
     *
     * @param file: the file to detect encoding from
     * @return the charset encoding
     */
    @JvmStatic
    @Throws(IOException::class)
    fun guessEncoding(file: File?): String? {
        FileInputStream(file).use { return guessEncoding(it) }
    }

    /**
     * Detect charset encoding of an input stream
     *
     * @param `is` the InputStream to detect encoding from
     * @return the charset encoding
     */
    @JvmStatic
    @Throws(IOException::class)
    fun guessEncoding(stream: InputStream): String? {
        return guessEncoding(toByteArray(stream))
    }

    /**
     * Detect charset encoding of a byte array
     *
     * @param bytes: the byte array to detect encoding from
     * @return the charset encoding
     */
    @JvmStatic
    fun guessEncoding(bytes: ByteArray): String? {
        val detector = UniversalDetector(null)
        detector.handleData(bytes, 0, bytes.size)
        detector.dataEnd()
        var encoding = detector.detectedCharset
        detector.reset()
        if (encoding == null || "MACCYRILLIC" == encoding) {
            // juniversalchardet incorrectly detects windows-1256 as MACCYRILLIC
            // If encoding is MACCYRILLIC or null, we use ICU4J
            val detected = CharsetDetector()
                .setText(bytes).detect()
            encoding = if (detected != null) {
                detected.name
            } else {
                "UTF-8"
            }
        }
        return encoding
    }
}
