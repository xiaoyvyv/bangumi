package com.xiaoyv.bangumi.shared.core.utils

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

fun TextFieldValue.limit(length: Int = 2000): TextFieldValue {
    return if (text.length > length) copy(text = text.substring(0, length)) else this
}

fun TextFieldValue.digit(old: TextFieldValue): TextFieldValue {
    return if (text.all { it.isDigit() }) this else old
}


fun String.asTextFieldValue(): TextFieldValue {
    return TextFieldValue(this, selection = TextRange(length))
}

fun TextFieldValue.appendText(newText: String): TextFieldValue {
    return (text + newText).asTextFieldValue()
}

fun TextFieldValue.insertEmoji(emoji: BgmEmoji): TextFieldValue {
    val emojiText = if (emoji.number < 10000) "(bgm${emoji.number})" else "(${emoji.smileId})"
    val start = selection.start.coerceAtLeast(0)
    val end = selection.end.coerceAtLeast(0)

    val newText = text.replaceRange(start, end, emojiText)
    val newCursor = start + emojiText.length

    return copy(
        text = newText,
        selection = TextRange(newCursor)
    )
}

fun TextFieldValue.insertBBCode(
    bbCode: BBCode,
    prefix: String = "",
    suffix: String = "",
): TextFieldValue {
    // 当前光标位置
    val start = selection.start.coerceAtLeast(0)
    val end = selection.end.coerceAtLeast(0)

    // 插入文本
    val newText = StringBuilder(text)
        .replaceRange(start, end, prefix + bbCode.codeString + suffix)
        .toString()

    val selectionStart = start + bbCode.inputOffset + prefix.length
    val selectionEnd = selectionStart + bbCode.selectLength + prefix.length

    return TextFieldValue(
        text = newText,
        selection = TextRange(selectionStart, selectionEnd)
    )
}

