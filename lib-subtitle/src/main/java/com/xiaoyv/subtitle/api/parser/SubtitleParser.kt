package com.xiaoyv.subtitle.api.parser

import com.xiaoyv.subtitle.api.parser.exception.InvalidFileException
import com.xiaoyv.subtitle.api.parser.exception.InvalidSubException
import com.xiaoyv.subtitle.api.subtitle.common.TimedLine
import com.xiaoyv.subtitle.api.subtitle.common.TimedTextFile
import java.io.File
import java.io.InputStream

interface SubtitleParser<T : TimedLine> {
    /**
     * Parse a subtitle file and return the corresponding subtitle object
     *
     * @param file the subtitle file
     * @return the subtitle object
     * @throws InvalidSubException if the subtitle is not valid
     * @throws InvalidFileException if the file is not valid
     */
    @Throws(InvalidSubException::class, InvalidFileException::class)
    fun parse(file: File): TimedTextFile<T>

    /**
     * Parse a subtitle file from an input stream and return the corresponding subtitle
     * object
     *
     * @param `is` the input stream
     * @param fileName the fileName
     * @return the subtitle object
     * @throws InvalidSubException if the subtitle is not valid
     * @throws InvalidFileException if the file is not valid
     */
    @Throws(InvalidSubException::class, InvalidFileException::class)
    fun parse(stream: InputStream, fileName: String): TimedTextFile<T>
}
