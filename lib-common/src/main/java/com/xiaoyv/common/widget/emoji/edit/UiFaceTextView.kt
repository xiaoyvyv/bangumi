package com.xiaoyv.common.widget.emoji.edit

import android.content.Context
import android.util.AttributeSet
import com.xiaoyv.common.widget.emoji.UiFaceManager.Companion.wrapEmoji
import com.xiaoyv.common.widget.text.AnimeTextView
import com.xiaoyv.widget.kts.getDpx


/**
 * UiFaceEditView
 *
 * @author why
 * @since 2021/03/29
 **/
class UiFaceTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AnimeTextView(context, attrs) {
    private val emojiMaxTextLength = 20

    /**
     * 表情的偏移值
     */
    private val emojiOffsetY = getDpx(1f)

    /**
     * 表情缩放因素
     */
    var emojiIconScale = 1.125f
        set(value) {
            field = value
            text = text
        }

    override fun setText(text: CharSequence?, type: BufferType?) {
        wrapEmoji(text, emojiIconScale, emojiOffsetY, emojiMaxTextLength) { spannable ->
            super.setText(spannable, type)
        }
    }

    override fun append(text: CharSequence?, start: Int, end: Int) {
        wrapEmoji(text, start, end, emojiIconScale, emojiOffsetY, emojiMaxTextLength) { spannable ->
            super.append(spannable, start, end)
        }
    }
}