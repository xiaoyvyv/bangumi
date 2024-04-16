package com.xiaoyv.subtitle.api.subtitle.common

import org.threeten.bp.LocalTime
import java.io.Serializable

/**
 *
 * Simple object that contains timed start ant end
 */
interface TimedObject : Serializable, Comparable<TimedObject?>, Comparator<TimedObject?> {
    /**
     * Return the time elapsed during script playback at which the text will appear
     * onscreen.
     *
     * @return start time
     */
    fun getStart(): LocalTime

    /**
     * Return the time elapsed during script playback at which the text will disappear
     * offscreen.
     *
     * @return end time
     */
    fun getEnd(): LocalTime

    /**
     * Set the time elapsed during script playback at which the text will appear onscreen.
     *
     * @param start time
     */
    fun setStart(start: LocalTime)

    /**
     * Set the time elapsed during script playback at which the text will disappear
     * offscreen.
     *
     * @param end time
     */
    fun setEnd(end: LocalTime)
}