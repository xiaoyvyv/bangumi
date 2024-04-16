package com.xiaoyv.subtitle.api.utils

import java.io.IOException
import java.io.InputStream

object IOUtils {
    @JvmStatic
    @Throws(IOException::class)
    fun toByteArray(stream: InputStream): ByteArray {
        return stream.readBytes()
    }
}