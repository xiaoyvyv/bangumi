package com.xiaoyv.bangumi.shared.core.utils

import androidx.compose.runtime.Immutable

val bbcodeEmpty = BBCode("", "")
val bbcodeBold = BBCode("加粗内容", "b")
val bbcodeItalic = BBCode("斜体内容", "i")
val bbcodeUnderline = BBCode("下划线内容", "u")
val bbcodeStrikethrough = BBCode("删除线内容", "s")
val bbcodeFontSize = BBCode("字体大小内容", "size", "16")
val bbcodeMask = BBCode("加码内容", "mask")
val bbcodeFontColor = BBCode("字体颜色内容", "color", "#ff80ab")
val bbcodeLink = BBCode("链接描述内容", "url", "https://bgm.tv")
val bbcodeQuote = BBCode("引用内容", "quote")
val bbcodeCode = BBCode("代码内容", "code")


@Immutable
data class BBCode(
    val hint: String,
    val code: String = "",
    val value: String = "",
) {
    val codeString: String
        get() = if (value.isNotBlank()) "[$code=$value]$hint[/$code]" else "[$code]$hint[/$code]"

    val inputOffset: Int
        get() = if (value.isNotBlank()) 3 + code.length + value.length else 2 + code.length

    val selectLength: Int
        get() = hint.length
}
