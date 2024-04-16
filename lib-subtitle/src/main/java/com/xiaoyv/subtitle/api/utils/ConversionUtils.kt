package com.xiaoyv.subtitle.api.utils

import com.xiaoyv.subtitle.api.subtitle.ass.ASSTime
import com.xiaoyv.subtitle.api.subtitle.ass.Events
import com.xiaoyv.subtitle.api.subtitle.ass.V4Style
import com.xiaoyv.subtitle.api.subtitle.common.TimedLine
import com.xiaoyv.subtitle.api.subtitle.config.SimpleSubConfig

object ConversionUtils {
    private const val RGX_XML_TAG = "<[^>]+>"
    private const val RGX_ASS_FORMATTING = "\\{[^}]*\\}"
    private const val SRT_ITALIC_CLOSE = "</i>"
    private const val SRT_ITALIC_OPEN = "<i>"
    private const val ASS_ITALIC_CLOSE = "\\{\\\\i0\\}"
    private const val ASS_ITALIC_OPEN = "\\{\\\\i1\\}"
    private const val EMPTY = ""

    /**
     * Create an `Events` object from a timed line
     *
     * @param line:  a timed line
     * @param style: the style name
     * @return the corresponding `Events`
     */
    @JvmStatic
    fun createEvent(line: TimedLine, style: String?): Events {
        val newLine: ArrayList<String> = ArrayList()
        for (text in line.getTextLines()) {
            newLine.add(toASSString(text))
        }
        val timeLine = line.getTime()
        val time = ASSTime(timeLine.getStart(), timeLine.getEnd())
        return Events(style, time, newLine)
    }

    /**
     * Create a `V4Style` object from `SubInput`
     *
     * @param config: the configuration object
     * @return the corresponding style
     */
    @JvmStatic
    fun createV4Style(config: SimpleSubConfig): V4Style {
        val style = V4Style(config.styleName)
        val font = config.fontconfig
        style.fontname = font.name
        style.fontsize = font.size
        style.alignment = config.alignment
        style.primaryColour = ColorUtils.hexToBGR(font.color)
        style.setOutlineColor(ColorUtils.hexToBGR(font.outlineColor))
        style.outline = font.outlineWidth
        style.marginV = config.verticalMargin
        return style
    }

    /**
     * Format a text line to be srt compliant
     *
     * @param textLine the text line
     * @return the formatted text line
     */
    @JvmStatic
    fun toSRTString(textLine: String): String {
        var formatted = textLine.replace(ASS_ITALIC_OPEN.toRegex(), SRT_ITALIC_OPEN)
        formatted = formatted.replace(ASS_ITALIC_CLOSE.toRegex(), SRT_ITALIC_CLOSE)
        formatted = formatted.replace(RGX_ASS_FORMATTING.toRegex(), EMPTY)
        return formatted
    }

    /**
     * Format a text line to be ass compliant
     *
     * @param textLine the text line
     */
    @JvmStatic
    fun toASSString(textLine: String): String {
        var formatted = textLine.replace(SRT_ITALIC_OPEN.toRegex(), ASS_ITALIC_OPEN)
        formatted = formatted.replace(SRT_ITALIC_CLOSE.toRegex(), ASS_ITALIC_CLOSE)
        return formatted.replace(RGX_XML_TAG.toRegex(), EMPTY)
    }
}
