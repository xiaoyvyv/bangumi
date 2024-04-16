package com.xiaoyv.subtitle.api.subtitle.config

import com.xiaoyv.subtitle.api.constant.FontName
import java.io.Serializable

data class Font(
    /**
     * Font name
     */
    var name: String = FontName.Arial.toString(),

    /**
     * Font size
     */
    var size: Int = 16,

    /**
     * Font color
     */
    var color: String = "#fffff9",

    /**
     * Outline color
     */
    var outlineColor: String = "#000000",

    /**
     * Outline width
     */
    var outlineWidth: Double = 2.0
) : Serializable
