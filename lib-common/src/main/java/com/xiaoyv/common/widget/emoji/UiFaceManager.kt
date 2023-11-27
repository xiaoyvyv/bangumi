package com.xiaoyv.common.widget.emoji

import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.widget.TextView
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.widget.emoji.span.UiFaceEditSpan
import com.xiaoyv.emoji.BgmEmoji
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

/**
 * @author why
 */
class UiFaceManager private constructor() {
    /**
     * 表情
     */
    private val emojiMap = ConcurrentHashMap<String, Int>()

//    private val emojiMaxTextLength = 20
    /*
        */
    /**
     * 表情的偏移值
     *//*
    private val emojiOffsetY = ConvertUtils.dp2px(1f)

    */
    /**
     * 表情缩放因素
     *//*
    var TextView.emojiIconScale = 1.125f
        set(value) {
            field = value
            text = text
        }*/

    /**
     * 初始化表情资源映射
     */
    fun initEmojiMap() {
        emojiMap.putAll(BgmEmoji.bgmEmoji)
        emojiMap.putAll(BgmEmoji.normalEmoji)
        emojiMap.putAll(BgmEmoji.tvEmoji)
    }

    /**
     * 将给定 Spannable 的表情符号字符转换为相应的表情符号
     */
    private fun replaceWithEmoji(
        textView: TextView,
        spannable: Editable,
        emojiIconScale: Float,
        emojiOffsetY: Int,
        emojiMaxTextLength: Int
    ) {
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
            val isEmoji = findEmoji(textStr, processIdx, results, emojiMaxTextLength)
            val skip = results[1]
            if (isEmoji) {
                val iconResId = results[0]
                val span = UiFaceEditSpan(
                    textView.context,
                    iconResId,
                    (textView.textSize * emojiIconScale).toInt(),
                    textView.textSize.toInt()
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
    private fun findEmoji(
        text: String,
        start: Int,
        result: IntArray,
        emojiMaxTextLength: Int
    ): Boolean {
        var emojiIconRes = 0
        var emojiSkipLength = Character.charCount(Character.codePointAt(text, start))
        val firstChar = text[start]
        if (firstChar == splitLeft) {
            val emojiCloseIndex = text.indexOf(splitRight, start)
            // 单个表情解析长度小于等于20
            if (emojiCloseIndex > 0 && emojiCloseIndex - start <= emojiMaxTextLength) {
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
        var splitLeft = '('

        var splitRight = ')'

        @JvmStatic
        val manager by lazy { UiFaceManager() }

        /**
         * 表情映射
         */
        val emojiMap: ConcurrentHashMap<String, Int>
            get() = manager.emojiMap

        fun TextView.wrapEmoji(
            text: CharSequence?,
            emojiIconScale: Float,
            emojiOffsetY: Int,
            emojiMaxTextLength: Int,
            result: (CharSequence?) -> Unit
        ) {
            wrapEmoji(text, null, null, emojiIconScale, emojiOffsetY, emojiMaxTextLength, result)
        }

        fun TextView.wrapEmoji(
            text: CharSequence?,
            start: Int? = null,
            end: Int? = null,
            emojiIconScale: Float,
            emojiOffsetY: Int,
            emojiMaxTextLength: Int,
            result: (CharSequence?) -> Unit
        ) {
            val lifecycleOwner = findViewTreeLifecycleOwner()
            if (lifecycleOwner == null) {
                result(text)
                return
            }

            val textView = this@wrapEmoji

            lifecycleOwner.launchUI {
                val spannable = withContext(Dispatchers.IO) {
                    val stringBuilder =
                        if (start != null && end != null) SpannableStringBuilder(text, start, end)
                        else SpannableStringBuilder(text)

                    stringBuilder.apply {
                        manager.replaceWithEmoji(
                            textView = textView,
                            spannable = this,
                            emojiIconScale = emojiIconScale,
                            emojiOffsetY = emojiOffsetY,
                            emojiMaxTextLength = emojiMaxTextLength
                        )
                    }
                }
                result(spannable)
            }
        }
    }
}