package com.xiaoyv.subtitle.api

import com.xiaoyv.subtitle.api.subtitle.common.SubtitleLine
import com.xiaoyv.subtitle.api.subtitle.common.SubtitleTime
import com.xiaoyv.subtitle.api.subtitle.common.TimedLine
import com.xiaoyv.subtitle.api.subtitle.common.TimedObject
import org.threeten.bp.LocalTime
import org.threeten.bp.temporal.ChronoUnit
import java.util.Collections
import java.util.TreeSet
import kotlin.math.abs

class TimedLinesAPI {
    /**
     * Search the line that has the closest start time compared to a specified time. If
     * the gap between the two start times is greater than the toleranceDelay (in ms) the
     * line will be ignored.
     *
     * @param tolerance the maximum gap in millis
     * @param lines the lines (ascending sort)
     * @param time the target start time
     */
    fun closestByStart(lines: List<TimedLine>, time: LocalTime?, tolerance: Int): TimedLine? {
        // Binary search will find the first "random" match
        val iAnyMatch = Collections.binarySearch(
            lines,
            SubtitleLine(SubtitleTime(time, null))
        ) { compare: TimedLine, base: TimedLine ->
            val search = base.getTime().getStart()
            val start = compare.getTime().getStart()
            if (getDelay(search, start) < tolerance) {
                return@binarySearch 0
            }
            start.compareTo(search)
        }
        if (iAnyMatch < 0) {
            return null
        }

        // Search for other matches
        val matches: MutableSet<TimedLine> = TreeSet()
        matches.add(lines[iAnyMatch])
        var i = iAnyMatch
        while (i > 0) {
            val previous = lines[--i]
            if (getDelay(time, previous.getTime().getStart()) >= tolerance) {
                break
            }
            matches.add(previous)
        }
        i = iAnyMatch
        while (i < lines.size - 1) {
            val next = lines[++i]
            if (getDelay(time, next.getTime().getStart()) >= tolerance) {
                break
            }
            matches.add(next)
        }

        // return the closest match
        return matches.stream()
            .sorted { m1: TimedLine, m2: TimedLine ->
                getDelay(
                    m1.getTime().getStart(),
                    time
                ) - getDelay(m2.getTime().getStart(), time)
            }
            .findFirst().get()
    }

    /**
     * Get the absolute delay between 2 times
     *
     * @return the absolute delay between 2 times
     */
    fun getDelay(start: LocalTime?, end: LocalTime?): Int {
        return abs(ChronoUnit.MILLIS.between(start, end).toDouble()).toInt()
    }

    /**
     * Check if a timed object appear before or at the same time as an other timed object
     *
     * @param elementToCompare
     * @param comparedElement
     */
    fun isEqualsOrAfter(elementToCompare: TimedObject, comparedElement: TimedObject): Boolean {
        return comparedElement.getStart()
            .isAfter(elementToCompare.getEnd()) || comparedElement.getStart() == elementToCompare.getEnd()
    }

    /**
     * Find the line displayed at `targetTime`
     *
     * @param lines the lines (ascending sort)
     * @param time the target time
     * @return
     */
    fun intersected(lines: List<TimedLine>, time: LocalTime?): TimedLine? {
        val index = Collections.binarySearch(
            lines, SubtitleLine(SubtitleTime(time, null))
        ) { compare, base ->
            val search = base.getTime().getStart()
            val start = compare.getTime().getStart()
            val end = compare.getTime().getEnd()
            if ((start.isBefore(search) || start == search)
                && (end.isAfter(search) || start == search)
            ) {
                0
            } else start.compareTo(search)
        }
        return if (index < 0) null else lines[index]
    }

    /**
     * Find a line displayed between 2 times
     *
     * @param lines the lines (ascending sort)
     */
    fun intersected(lines: List<TimedLine>, start: LocalTime?, end: LocalTime?): TimedLine? {
        val index = Collections.binarySearch(
            lines, SubtitleLine(SubtitleTime(start, end))
        ) { compare, base ->
            val searchStart = base.getTime().getStart()
            val searchEnd = base.getTime().getEnd()
            val start = compare.getTime().getStart()
            val end = compare.getTime().getEnd()
            if (searchStart.isBefore(start) && searchEnd.isAfter(end)) {
                0
            } else compare.compareTo(base)
        }
        return if (index < 0) null else lines[index]
    }

    /**
     * Find a subtitle line from it's time
     *
     * @param lines the subtitle lines
     * @param time the timed object
     */
    fun findByTime(lines: List<TimedLine>?, time: TimedObject): Int {
        return Collections.binarySearch(
            lines.orEmpty(),
            SubtitleLine(time),
            SubtitleLine.timeComparator
        )
    }
}
