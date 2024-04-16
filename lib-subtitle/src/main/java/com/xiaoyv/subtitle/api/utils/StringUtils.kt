package com.xiaoyv.subtitle.api.utils

import android.text.TextUtils
import java.util.Locale

object StringUtils {
    private val EMPTY_STRING_ARRAY = arrayOf<String>()

    const val EMPTY = ""

    @JvmStatic
    fun isEmpty(text: String?): Boolean {
        return TextUtils.isEmpty(text)
    }

    @JvmStatic
    fun capitalize(sectionName: String): String {
        return sectionName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    @JvmStatic
    fun unCapitalize(property: String): String {
        return property.replaceFirstChar { it.lowercase(Locale.getDefault()) }
    }

    @JvmStatic
    fun deleteWhitespace(str: String): String {
        if (isEmpty(str)) {
            return str
        }
        val sz = str.length
        val chs = CharArray(sz)
        var count = 0
        for (i in 0 until sz) {
            if (!Character.isWhitespace(str[i])) {
                chs[count++] = str[i]
            }
        }
        if (count == sz) {
            return str
        }
        return if (count == 0) EMPTY else String(chs, 0, count)
    }

    @JvmStatic
    fun splitByWholeSeparatorPreserveAllTokens(info: String?, separator: String?): Array<String>? {
        return splitByWholeSeparatorWorker(info, separator, -1, true)
    }

    /**
     * Performs the logic for the `splitByWholeSeparatorPreserveAllTokens` methods.
     *
     * @param str  the String to parse, may be `null`
     * @param separator  String containing the String to be used as a delimiter,
     * `null` splits on whitespace
     * @param max  the maximum number of elements to include in the returned
     * array. A zero or negative value implies no limit.
     * @param preserveAllTokens if `true`, adjacent separators are
     * treated as empty token separators; if `false`, adjacent
     * separators are treated as one separator.
     * @return an array of parsed Strings, `null` if null String input
     * @since 2.4
     */
    private fun splitByWholeSeparatorWorker(
        str: String?, separator: String?, max: Int, preserveAllTokens: Boolean
    ): Array<String>? {
        if (str == null) {
            return null
        }
        val len = str.length
        if (len == 0) {
            return EMPTY_STRING_ARRAY
        }
        if (separator == null || EMPTY == separator) {
            // Split on whitespace.
            return splitWorker(str, null, max, preserveAllTokens)
        }
        val separatorLength = separator.length
        val substrings = ArrayList<String>()
        var numberOfSubstrings = 0
        var beg = 0
        var end = 0
        while (end < len) {
            end = str.indexOf(separator, beg)
            if (end > -1) {
                if (end > beg) {
                    numberOfSubstrings += 1
                    if (numberOfSubstrings == max) {
                        end = len
                        substrings.add(str.substring(beg))
                    } else {
                        // The following is OK, because String.substring( beg, end ) excludes
                        // the character at the position 'end'.
                        substrings.add(str.substring(beg, end))

                        // Set the starting point for the next search.
                        // The following is equivalent to beg = end + (separatorLength - 1) + 1,
                        // which is the right calculation:
                        beg = end + separatorLength
                    }
                } else {
                    // We found a consecutive occurrence of the separator, so skip it.
                    if (preserveAllTokens) {
                        numberOfSubstrings += 1
                        if (numberOfSubstrings == max) {
                            end = len
                            substrings.add(str.substring(beg))
                        } else {
                            substrings.add(EMPTY)
                        }
                    }
                    beg = end + separatorLength
                }
            } else {
                // String.substring( beg ) goes from 'beg' to the end of the String.
                substrings.add(str.substring(beg))
                end = len
            }
        }
        return substrings.toArray(EMPTY_STRING_ARRAY)
    }


    /**
     * Performs the logic for the `split` and
     * `splitPreserveAllTokens` methods that return a maximum array
     * length.
     *
     * @param str  the String to parse, may be `null`
     * @param separatorChars the separate character
     * @param max  the maximum number of elements to include in the
     * array. A zero or negative value implies no limit.
     * @param preserveAllTokens if `true`, adjacent separators are
     * treated as empty token separators; if `false`, adjacent
     * separators are treated as one separator.
     * @return an array of parsed Strings, `null` if null String input
     */
    private fun splitWorker(
        str: String?,
        separatorChars: String?,
        max: Int,
        preserveAllTokens: Boolean
    ): Array<String>? {
        // Performance tuned for 2.0 (JDK1.4)
        // Direct code is quicker than StringTokenizer.
        // Also, StringTokenizer uses isSpace() not isWhitespace()
        if (str == null) {
            return null
        }
        val len = str.length
        if (len == 0) {
            return EMPTY_STRING_ARRAY
        }
        val list: MutableList<String> = java.util.ArrayList()
        var sizePlus1 = 1
        var i = 0
        var start = 0
        var match = false
        var lastMatch = false
        if (separatorChars == null) {
            // Null separator means use whitespace
            while (i < len) {
                if (Character.isWhitespace(str[i])) {
                    if (match || preserveAllTokens) {
                        lastMatch = true
                        if (sizePlus1++ == max) {
                            i = len
                            lastMatch = false
                        }
                        list.add(str.substring(start, i))
                        match = false
                    }
                    start = ++i
                    continue
                }
                lastMatch = false
                match = true
                i++
            }
        } else if (separatorChars.length == 1) {
            // Optimise 1 character case
            val sep = separatorChars[0]
            while (i < len) {
                if (str[i] == sep) {
                    if (match || preserveAllTokens) {
                        lastMatch = true
                        if (sizePlus1++ == max) {
                            i = len
                            lastMatch = false
                        }
                        list.add(str.substring(start, i))
                        match = false
                    }
                    start = ++i
                    continue
                }
                lastMatch = false
                match = true
                i++
            }
        } else {
            // standard case
            while (i < len) {
                if (separatorChars.indexOf(str[i]) >= 0) {
                    if (match || preserveAllTokens) {
                        lastMatch = true
                        if (sizePlus1++ == max) {
                            i = len
                            lastMatch = false
                        }
                        list.add(str.substring(start, i))
                        match = false
                    }
                    start = ++i
                    continue
                }
                lastMatch = false
                match = true
                i++
            }
        }
        if (match || preserveAllTokens && lastMatch) {
            list.add(str.substring(start, i))
        }
        return list.toTypedArray()
    }

    @JvmStatic
    fun removeEnd(str: String, remove: String): String {
        if (isEmpty(str) || isEmpty(remove)) {
            return str
        }
        return if (str.endsWith(remove)) {
            str.substring(0, str.length - remove.length)
        } else str
    }
}