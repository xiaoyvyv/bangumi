package com.xiaoyv.subtitle.api.parser

import com.xiaoyv.subtitle.api.parser.exception.InvalidFileException
import com.xiaoyv.subtitle.api.parser.exception.InvalidSubException
import com.xiaoyv.subtitle.api.subtitle.common.TimedLine
import com.xiaoyv.subtitle.api.subtitle.common.TimedTextFile
import com.xiaoyv.subtitle.api.utils.FileUtils.guessEncoding
import com.xiaoyv.subtitle.api.utils.IOUtils
import com.xiaoyv.subtitle.api.utils.StringUtils.isEmpty
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.reflect.ParameterizedType

abstract class BaseParser<TIMELINE : TimedLine, T : TimedTextFile<TIMELINE>> :
    SubtitleParser<TIMELINE> {

    override fun parse(file: File): T {
        if (!file.isFile()) {
            throw InvalidFileException("File " + file.getName() + " is invalid")
        }
        try {
            FileInputStream(file).use { fis -> return parse(fis, file.getName()) }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun parse(stream: InputStream, fileName: String): T {
        return try {
            val type = this.javaClass.getGenericSuperclass()
            check(type is ParameterizedType)
            val argument = requireNotNull(type.actualTypeArguments[1])
            check(argument is Class<*>)
            val subtitle = argument.getDeclaredConstructor().newInstance() as T
            val bytes = IOUtils.toByteArray(stream)
            ByteArrayInputStream(bytes).use { nis ->
                InputStreamReader(nis, guessEncoding(bytes)).use { isr ->
                    BufferedReader(isr).use { br ->
                        skipBom(br)
                        subtitle.setFileName(fileName)
                        parse(br, subtitle)
                    }
                }
            }
            subtitle
        } catch (e: IOException) {
            throw InvalidFileException(e)
        } catch (e: InstantiationException) {
            throw RuntimeException(e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        } catch (e: IllegalStateException) {
            throw RuntimeException(e)
        }
    }

    /**
     * Parse the subtitle file into a `ParsableSubtitle` object
     *
     * @param br: the buffered reader
     * @param sub : the subtitle object to fill
     * @throws InvalidSubException if an error has occurred when parsing the subtitle file
     */
    @Throws(InvalidSubException::class, IOException::class)
    protected abstract fun parse(br: BufferedReader, sub: T)

    companion object {
        /**
         * UTF-8 BOM Marker
         */
        private const val BOM_MARKER = '\ufeff'

        /**
         * Ignore blank spaces and return the first text line
         *
         * @param br: the buffered reader
         */
        @JvmStatic
        @Throws(IOException::class)
        protected fun readFirstTextLine(br: BufferedReader): String? {
            var line: String?
            while (br.readLine().also { line = it } != null) {
                if (!isEmpty(line!!.trim { it <= ' ' })) {
                    break
                }
            }
            return line
        }

        /**
         * Remove the byte order mark if exists
         *
         * @param br: the buffered reader
         */
        @Throws(IOException::class)
        private fun skipBom(br: BufferedReader) {
            br.mark(4)
            if (BOM_MARKER.code != br.read()) {
                br.reset()
            }
        }

        /**
         * Reset the reader at the previous mark if the current line is a new section
         *
         * @param br:   the reader
         * @param line: the current line
         */
        @JvmStatic
        @Throws(IOException::class)
        protected fun reset(br: BufferedReader, line: String?) {
            if (line != null && line.startsWith("[")) {
                br.reset()
            }
        }

        /**
         * Mark the position in the reader and read the next text line
         *
         * @param br: the buffered reader
         * @return the next text line
         */
        @JvmStatic
        @Throws(IOException::class)
        protected fun markAndRead(br: BufferedReader): String? {
            br.mark(32)
            return readFirstTextLine(br)
        }
    }
}
