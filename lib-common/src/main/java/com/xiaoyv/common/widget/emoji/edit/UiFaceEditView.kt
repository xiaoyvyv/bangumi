package com.xiaoyv.common.widget.emoji.edit

import android.content.Context
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import com.blankj.utilcode.util.CollectionUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.RegexUtils
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.widget.emoji.UiFaceManager
import com.xiaoyv.common.widget.emoji.span.UiFaceEditSpan
import com.xiaoyv.common.widget.text.AnimeEditTextView


/**
 * UiFaceEditView
 *
 * @author why
 * @since 2021/03/29
 **/
class UiFaceEditView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AnimeEditTextView(context, attrs) {

    /**
     * 表情的偏移值
     */
    private val emojiOffsetY = ConvertUtils.dp2px(1f)

    /**
     * 表情缩放因素
     */
    var emojiIconScale = 1.125f
        set(value) {
            field = value
            text = text
        }
    private var isAddEmoji = false

    /**
     * 插入表情
     */
    private fun insertEmoji(emojiStr: String) {
        isAddEmoji = true
        val spannable = SpannableStringBuilder(emojiStr)
        replaceWithEmoji(spannable)
        append(spannable)
    }

    override fun onTextChanged(text: CharSequence?, start: Int, before: Int, end: Int) {
        val editable = getText() ?: return
        if (end > EMOJI_TEXT_MIN_LENGTH) {
            if (isAddEmoji) {
                isAddEmoji = false
                debugLog { "表情输入，不解析输入内容" }
                return
            }
            val inputText = editable.substring(start, start + end)
            val hasEmoji = "\\(([a-zA-Z]{1,20})\\)"
            val matches = RegexUtils.getMatches(hasEmoji, inputText)

            debugLog { "解析其他输入内容是否含有表情字符：$inputText" }

            if (!CollectionUtils.isEmpty(matches)) {
                // 限制粘贴表情个数防止 OOM
                if (matches.size > EMOJI_TEXT_MAX_PASTE_SIZE) {
                    debugLog { "检测到大量表情粘贴操作，已屏蔽" }
                    editable.replace(start, start + end, "")
                    return
                }
                replaceWithEmoji(editable)
            }
        }
    }

    /**
     * 将给定 Spannable 的表情符号字符转换为相应的表情符号。
     */
    private fun replaceWithEmoji(spannable: Editable) {
        val textStr = spannable.toString()
        val textLengthToProcess = textStr.length

        // 删除所有文本中的 EmojiIconSpan
        val oldSpans = spannable.getSpans(0, spannable.length, UiFaceEditSpan::class.java)
        for (oldSpan in oldSpans) {
            spannable.removeSpan(oldSpan)
        }

        val results = IntArray(2)
        var processIdx = 0
        while (processIdx < textLengthToProcess) {
            val isEmoji = findEmoji(textStr, processIdx, results)
            val skip = results[1]
            if (isEmoji) {
                val iconResId = results[0]
                val span = UiFaceEditSpan(
                    context,
                    iconResId,
                    (textSize * emojiIconScale).toInt(),
                    textSize.toInt()
                )
                span.setTranslateY(emojiOffsetY)
                val emojiEndIdx = processIdx + skip
                spannable.setSpan(span, processIdx, emojiEndIdx, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            processIdx += skip
        }
        spannable.subSequence(0, processIdx)
    }

    /**
     * 判断文本位于 start 的字节是否为 emoji
     *
     * @param result 资源 id, 占位长度
     */
    private fun findEmoji(text: String, start: Int, result: IntArray): Boolean {
        var emojiIconRes = 0
        var emojiSkipLength = Character.charCount(Character.codePointAt(text, start))
        val firstChar = text[start]
        if (firstChar == UiFaceManager.splitLeft) {
            val emojiCloseIndex = text.indexOf(UiFaceManager.splitRight, start)
            // 单个表情解析长度小于等于20
            if (emojiCloseIndex > 0 && emojiCloseIndex - start <= EMOJI_TEXT_MAX_LENGTH) {
                val charSequence = text.subSequence(start, emojiCloseIndex + 1)
                val value = UiFaceManager.emojiMap[charSequence.toString()]
                if (value != null) {
                    emojiIconRes = value
                    emojiSkipLength = emojiCloseIndex + 1 - start
                }
            }
        }
        result[0] = emojiIconRes
        result[1] = emojiSkipLength
        return emojiIconRes > 0
    }

    companion object {
        /**
         * 表情符号最效 3 个
         */
        private const val EMOJI_TEXT_MIN_LENGTH = 3

        /**
         * 表情符号最大 20 个字符
         */
        private const val EMOJI_TEXT_MAX_LENGTH = 20

        /**
         * 表情符粘贴个数现限制
         */
        private const val EMOJI_TEXT_MAX_PASTE_SIZE = 20
    }
}