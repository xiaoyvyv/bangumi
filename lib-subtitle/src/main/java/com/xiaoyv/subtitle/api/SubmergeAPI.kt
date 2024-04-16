package com.xiaoyv.subtitle.api

import com.xiaoyv.subtitle.api.subtitle.ass.ASSSub
import com.xiaoyv.subtitle.api.subtitle.ass.Events
import com.xiaoyv.subtitle.api.subtitle.common.TimedLine
import com.xiaoyv.subtitle.api.subtitle.common.TimedTextFile
import com.xiaoyv.subtitle.api.subtitle.config.SimpleSubConfig
import com.xiaoyv.subtitle.api.subtitle.srt.SRTLine
import com.xiaoyv.subtitle.api.subtitle.srt.SRTSub
import com.xiaoyv.subtitle.api.subtitle.srt.SRTTime
import com.xiaoyv.subtitle.api.utils.ConversionUtils.createEvent
import com.xiaoyv.subtitle.api.utils.ConversionUtils.createV4Style
import com.xiaoyv.subtitle.api.utils.ConversionUtils.toSRTString
import org.threeten.bp.LocalTime
import java.util.stream.Collectors

/**
 * 用于管理字幕的服务
 */
class SubmergeAPI {
    /**
     * Change the frame-rate of a subtitle
     *
     * @param timedFile the subtitle
     * @param sourceFrameRate le source frame-rate. Ex: 25.000
     * @param targetFrameRate the target frame-rate. Ex: 23.976
     */
    fun convertFrameRate(
        timedFile: TimedTextFile<*>,
        sourceFrameRate: Double,
        targetFrameRate: Double
    ) {
        val ratio = sourceFrameRate / targetFrameRate
        for (timedLine in timedFile.getTimedLines()) {
            val time = timedLine.getTime()
            val s = Math.round(time.getStart().toNanoOfDay() * ratio)
            val e = Math.round(time.getEnd().toNanoOfDay() * ratio)
            time.setStart(LocalTime.ofNanoOfDay(s))
            time.setEnd(LocalTime.ofNanoOfDay(e))
        }
    }

    /**
     * TimedTextFile to SRT conversion
     *
     * @param timedFile the TimedTextFile
     * @return the SRTSub object
     */
    fun toSRT(timedFile: TimedTextFile<*>): SRTSub {
        val srt = SRTSub()
        srt.setFileName(timedFile.getFileName())
        for ((i, timedLine) in timedFile.getTimedLines().withIndex()) {
            val id = i + 1
            val time = timedLine.getTime()
            val start = time.getStart()
            val end = time.getEnd()
            val srtTime = SRTTime(start, end)
            val textLines = timedLine.getTextLines()
            val newLines: ArrayList<String> = ArrayList()
            for (textLine in textLines) {
                newLines.add(toSRTString(textLine))
            }
            val srtLine = SRTLine(id, srtTime, newLines)
            srt.add(srtLine)
        }
        return srt
    }

    /**
     * SubInput to ASS conversion
     *
     * @param config the configuration object
     * @return the ASSSub object
     */
    fun toASS(config: SimpleSubConfig): ASSSub {
        return mergeToAss(config)
    }

    /**
     * Merge several subtitles into one ASS
     *
     * @param configs : configuration object of the subtitles
     */
    fun mergeToAss(vararg configs: SimpleSubConfig): ASSSub {
        val ass = ASSSub()
        val ev = ass.events
        for (config in configs) {
            ass.style.add(createV4Style(config))
            config.sub.getTimedLines().forEach { line: TimedLine? ->
                ev.add(
                    createEvent(
                        line!!, config.styleName
                    )
                )
            }
        }
        return ass
    }

    /**
     * Transform all multi-lines subtitles to single-line
     *
     * @param timedFile the TimedTextFile
     */
    fun mergeTextLines(timedFile: TimedTextFile<*>) {
        for (timedLine in timedFile.getTimedLines()) {
            val textLines = timedLine.getTextLines()
            if (textLines.size > 1) {
                textLines[0] = textLines.stream().collect(Collectors.joining(" "))
                textLines.subList(1, textLines.size).clear()
            }
        }
    }

    /**
     * Synchronise the timecodes of a subtitle from another one
     *
     * @param fileToAdjust the subtitle to modify
     * @param referenceFile the subtitle to take the timecodes from
     * @param delay the number of milliseconds allowed to differ
     */
    fun adjustTimecodes(
        fileToAdjust: TimedTextFile<*>,
        referenceFile: TimedTextFile<*>,
        delay: Int
    ) {
        val linesAPI = TimedLinesAPI()
        val timedLines: List<TimedLine> = ArrayList(fileToAdjust.getTimedLines())
        val referenceLines: List<TimedLine> = ArrayList(referenceFile.getTimedLines())
        for (lineToAdjust in timedLines) {
            val originalTime = lineToAdjust.getTime()
            val originalStart = originalTime.getStart()
            val referenceLine = linesAPI.closestByStart(referenceLines, originalStart, delay)
            if (referenceLine != null) {
                val targetStart = referenceLine.getTime().getStart()
                val targetEnd = referenceLine.getTime().getEnd()
                val fullIntersect = linesAPI.intersected(timedLines, targetStart, targetEnd)
                if (fullIntersect != null && lineToAdjust != fullIntersect) {
                    continue
                }
                val startIntersect = linesAPI.intersected(timedLines, targetStart)
                val endIntersect = linesAPI.intersected(timedLines, targetEnd)
                if (startIntersect == null || originalTime == startIntersect.getTime()) {
                    originalTime.setStart(targetStart)
                } else {
                    originalTime.setStart(startIntersect.getTime().getEnd())
                }
                if (endIntersect == null || originalTime.getStart() == endIntersect.getTime()
                        .getStart()
                ) {
                    originalTime.setEnd(targetEnd)
                } else {
                    originalTime.setEnd(endIntersect.getTime().getStart())
                }
            }
        }
        expandLongLines(timedLines, referenceLines, 1500)
    }

    companion object {
        /**
         * Expand lines in the adjusted file that should be displayed during 2 lines of the
         * reference file
         *
         * @param adjustedLines the adjusted lines (ascending sort)
         * @param referenceLines the reference lines (ascending sort)
         */
        private fun expandLongLines(
            adjustedLines: List<TimedLine>,
            referenceLines: List<TimedLine>,
            delay: Int
        ) {
            val linesAPI = TimedLinesAPI()
            for (i in adjustedLines.indices) {
                val currentElement = adjustedLines[i].getTime()
                val index = linesAPI.findByTime(referenceLines, currentElement)
                if (index >= 0) {
                    val nextReferenceIndex = index + 1
                    if (nextReferenceIndex >= referenceLines.size || i + 1 >= adjustedLines.size) continue
                    val nextReference = referenceLines[nextReferenceIndex].getTime()
                    val nextElement = adjustedLines[i + 1].getTime()
                    if (!linesAPI.isEqualsOrAfter(currentElement, nextReference)) continue
                    if (linesAPI.getDelay(
                            currentElement.getEnd(),
                            nextReference.getStart()
                        ) >= delay
                    ) continue
                    if (!linesAPI.isEqualsOrAfter(nextReference, nextElement)) continue
                    currentElement.setEnd(nextReference.getEnd())
                }
            }
        }
    }
}
