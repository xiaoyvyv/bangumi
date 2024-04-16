package com.xiaoyv.subtitle.api.subtitle.common

import java.io.Serializable

/**
 * Simple object that contains a text line with a time
 *
 */
interface TimedLine : Serializable, Comparable<TimedLine>, Comparator<TimedLine> {
    /**
     * Get the text lines
     *
     * @return textLines
     */
    fun getTextLines(): ArrayList<String>

    /**
     * Get the timed object
     *
     * @return the time
     */
    fun getTime(): TimedObject
}