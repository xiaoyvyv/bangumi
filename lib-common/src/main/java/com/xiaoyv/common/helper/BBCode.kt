package com.xiaoyv.common.helper

import android.text.SpannableStringBuilder
import android.widget.EditText

/**
 * Class: [BBCode]
 *
 * @author why
 * @since 12/1/23
 */
object BBCode {
    /**
     * 第三个参数为光标向右选中的偏移 index
     */
    val menuBold = Triple("[b]", "加粗内容[/b]", 4)
    val menuItalic = Triple("[i]", "斜体内容[/i]", 4)
    val menuUnderline = Triple("[u]", "下划线内容[/u]", 5)
    val menuStrikethrough = Triple("[s]", "删除线内容[/s]", 5)
    val menuFontSize = Triple("[size=16]", "[/size]", -1)
    val menuMask = Triple("[mask]", "遮罩内容[/mask]", 4)
    val menuFontColor = Triple("[color=#5555FF]", "颜色文本内容[/color]", 6)
    val menuUrl = Triple("[url=https://xxx]", "链接描述[/url]", 4)
    val menuImage = Triple("[img]", "图片链接[/img]", 4)
    val menuList = Triple("[list]", "[/list]", -1)
    val menuListItem = Triple("[*]", "", -1)
    val menuCode = Triple("[code]", "代码内容[/code]", 4)
    val menuQuote = Triple("[quote]", "引用内容[/quote]", 4)

    fun insert(editText: EditText, pair: Triple<String, String, Int>) {
        if (pair.third == -1) {
            editText.insert(pair.first + pair.second, pair.second.length, pair.second.length)
        } else {
            editText.insert(
                pair.first + pair.second,
                pair.second.length,
                pair.second.length - pair.third
            )
        }
    }

    private fun EditText.insert(textToInsert: String, backCursorStart: Int, backCursorEnd: Int) {
        runCatching {
            val start = selectionStart
            val end = selectionEnd
            val editable = getText()
            val builder = SpannableStringBuilder(editable)
            builder.replace(start, end, textToInsert)
            text = builder

            if (backCursorStart == backCursorEnd) {
                setSelection(start + (textToInsert.length - backCursorStart).coerceAtLeast(0))
            } else {
                val startIndex = start + (textToInsert.length - backCursorStart).coerceAtLeast(0)
                val endIndex = start + (textToInsert.length - backCursorEnd).coerceAtLeast(0)
                setSelection(startIndex, endIndex)
            }
        }
    }
}