package com.xiaoyv.subtitle.api.subtitle.common

import java.io.Serializable

/**
 * Object that represents a text file containing timed lines
 */
interface TimedTextFile<T : TimedLine> : Serializable {
    /**
     * Get the filename
     *
     * @return the filename
     */
    fun getFileName(): String

    /**
     * Set the filename
     *
     * @param fileName: the filename
     */
    fun setFileName(fileName: String)

    /**
     * Get the timed lines
     *
     * @return lines
     */
    fun getTimedLines(): Set<T>
}