package com.xiaoyv.subtitle.api.parser

import com.xiaoyv.subtitle.api.parser.exception.InvalidSRTSubException
import com.xiaoyv.subtitle.api.parser.exception.InvalidSubException
import com.xiaoyv.subtitle.api.subtitle.srt.SRTLine
import com.xiaoyv.subtitle.api.subtitle.srt.SRTSub
import com.xiaoyv.subtitle.api.subtitle.srt.SRTTime
import com.xiaoyv.subtitle.api.utils.StringUtils.isEmpty
import org.threeten.bp.format.DateTimeParseException
import java.io.BufferedReader
import java.io.IOException

/**
 * Parse SRT subtitles
 */
class SRTParser : BaseParser<SRTLine, SRTSub>() {

    @Throws(IOException::class, InvalidSubException::class)
    override fun parse(br: BufferedReader, sub: SRTSub) {
        var line: SRTLine?
        while (firstIn(br).also { line = it } != null) {
            sub.add(line)
        }
    }

    companion object {
        /**
         * Extract the first SRTLine found in a buffered reader.
         *
         *
         * Example of SRT line:
         *
         * ```
         * 1
         * 00:02:46,813 --> 00:02:50,063
         * A text line
         * ```
         *
         * @param br
         * @return SRTLine the line extracted, null if no SRTLine found
         * @throws IOException
         * @throws InvalidSRTSubException
         */
        @Throws(IOException::class, InvalidSRTSubException::class)
        private fun firstIn(br: BufferedReader): SRTLine? {
            val idLine = readFirstTextLine(br)
            val timeLine = br.readLine()
            if (idLine == null || timeLine == null) return null
            val id = parseId(idLine)
            val time = parseTime(timeLine)
            val textLines = arrayListOf<String>()
            var testLine: String
            while (br.readLine().also { testLine = it } != null) {
                if (isEmpty(testLine.trim())) break
                textLines.add(testLine)
            }
            return SRTLine(id, time, textLines)
        }

        /**
         * Extract a subtitle id from string
         *
         * @param textLine ex 1
         * @return the id extracted
         * @throws InvalidSRTSubException
         */
        @Throws(InvalidSRTSubException::class)
        private fun parseId(textLine: String): Int {
            val idSRTLine = try {
                textLine.trim().toInt()
            } catch (e: NumberFormatException) {
                throw InvalidSRTSubException("Expected id not found -> $textLine")
            }
            return idSRTLine
        }

        /**
         * Extract a subtitle time from string
         *
         * @param timeLine: ex 00:02:08,822 --> 00:02:11,574
         * @return the SRTTime object
         * @throws InvalidSRTSubException
         */
        @Throws(InvalidSRTSubException::class)
        private fun parseTime(timeLine: String): SRTTime {
            val times = timeLine
                .split(SRTTime.DELIMITER.trim().toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()

            if (times.size != 2) {
                throw InvalidSRTSubException("Subtitle $timeLine - invalid times : $timeLine")
            }
            val time = try {
                val start = SRTTime.fromString(times[0])
                val end = SRTTime.fromString(times[1])
                SRTTime(start, end)
            } catch (e: DateTimeParseException) {
                throw InvalidSRTSubException("Invalid time string : $timeLine", e)
            }
            return time
        }
    }
}
