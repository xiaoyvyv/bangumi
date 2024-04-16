package com.xiaoyv.subtitle.api.parser

import java.util.Locale

object ParserFactory {

    /**
     * Return the subtitle parser for the subtitle format matching the extension
     *
     * @param extension the subtitle extension
     * @return the subtitle parser, null if no matching parser
     */
    @JvmStatic
    fun getParser(extension: String): SubtitleParser<*> {
        val lowerExt = extension.lowercase(Locale.getDefault())
        return when {
            "ass" == lowerExt || "ssa" == lowerExt -> ASSParser()
            "srt".equals(lowerExt, ignoreCase = true) -> SRTParser()
            else -> throw UnsupportedOperationException("$extension format not supported")
        }
    }
}
