package com.xiaoyv.common.kts

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView

/**
 * 添加一个方法用于高亮指定文本
 */
fun TextView.highlightText(textList: List<String>, highlightColor: Int) {
    val fullText = this.text.toString()

    val spannableString = SpannableString(fullText)

    for (text in textList) {
        val startIndex = fullText.indexOf(text, ignoreCase = true)
        val endIndex = startIndex + text.length

        if (startIndex != -1) {
            val highlightSpan = ForegroundColorSpan(highlightColor)
            spannableString.setSpan(
                highlightSpan,
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    this.text = spannableString
}