package com.xiaoyv.subtitle.api.subtitle.config

import com.xiaoyv.subtitle.api.subtitle.common.TimedTextFile
import java.io.Serializable

data class SimpleSubConfig @JvmOverloads constructor(
    var styleName: String? = null,
    var sub: TimedTextFile<*>,
    var fontconfig: Font = Font(),
    var alignment: Int = 0,
    var verticalMargin: Int = 10,
) : Serializable
