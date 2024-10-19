package com.xiaoyv.common.helper

import android.os.Parcelable
import android.text.SpannableStringBuilder
import android.widget.EditText
import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.fragment.app.FragmentActivity
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.helper.callback.IdEntity
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.showInputLine2Dialog
import com.xiaoyv.common.kts.i18n
import com.xiaoyv.common.widget.text.AnimeEditTextView
import kotlinx.coroutines.delay
import kotlinx.parcelize.Parcelize

/**
 * Class: [BBCodeHelper]
 *
 * @author why
 * @since 12/1/23
 */
object BBCodeHelper {
    val bbCodes: List<BBCode> by lazy {
        listOf(
            BBCode(
                tagStart = "[b]",
                tagEnd = "[/b]",
                holderText = i18n(CommonString.bbcode_holder_b),
                icon = CommonDrawable.ic_format_bold
            ),
            BBCode(
                tagStart = "[i]",
                tagEnd = "[/i]",
                holderText = i18n(CommonString.bbcode_holder_i),
                icon = CommonDrawable.ic_format_italic
            ),
            BBCode(
                tagStart = "[u]",
                tagEnd = "[/u]",
                holderText = i18n(CommonString.bbcode_holder_u),
                icon = CommonDrawable.ic_format_underlined
            ),
            BBCode(
                tagStart = "[s]",
                tagEnd = "[/s]",
                holderText = i18n(CommonString.bbcode_holder_s),
                icon = CommonDrawable.ic_format_strikethrough
            ),
            BBCode(
                tagStart = "[size=%s]",
                tagEnd = "[/size]",
                holderTagStart = "16",
                holderTagStartTip = i18n(CommonString.bbcode_holder_size),
                holderText = "",
                icon = CommonDrawable.ic_format_size
            ),
            BBCode(
                tagStart = "[mask]",
                tagEnd = "[/mask]",
                holderText = i18n(CommonString.bbcode_holder_mask),
                icon = CommonDrawable.ic_format_mask
            ),
            BBCode(
                tagStart = "[color=%s]",
                tagEnd = "[/color]",
                holderTagStart = "#5555FF",
                holderTagStartTip = i18n(CommonString.bbcode_holder_color_value),
                holderText = i18n(CommonString.bbcode_holder_color),
                icon = CommonDrawable.ic_format_color
            ),
            BBCode(
                tagStart = "[url=%s]",
                tagEnd = "[/url]",
                holderTagStart = "https://xxx",
                holderTagStartTip =i18n(CommonString.bbcode_holder_link),
                holderText = i18n(CommonString.bbcode_holder_link_desc),
                icon = CommonDrawable.ic_format_link
            ),
            BBCode(
                tagStart = "[img]",
                tagEnd = "[/img]",
                holderText = i18n(CommonString.bbcode_holder_img),
                icon = CommonDrawable.ic_format_image
            ),
            BBCode(
                tagStart = "[code]",
                tagEnd = "[/code]",
                holderText = i18n(CommonString.bbcode_holder_code),
                icon = CommonDrawable.ic_format_code
            ),
            BBCode(
                tagStart = "[quote]",
                tagEnd = "[/quote]",
                holderText = i18n(CommonString.bbcode_holder_quote),
                icon = CommonDrawable.ic_format_quote
            )
        )
    }

    /**
     * 第三个参数为光标向右选中的偏移 index
     */
    inline fun insert(
        activity: FragmentActivity,
        editText: EditText,
        pair: BBCode?,
        crossinline onBeforeInsert: suspend () -> Unit = {},
    ) {
        val bbCode = pair ?: return

        if (bbCode.tagStart.contains("%s")) {
            activity.showInputLine2Dialog(
                title = bbCode.holderText,
                inputHint1 = bbCode.holderTagStartTip,
                inputHint2 = bbCode.holderText,
                default1 = bbCode.holderTagStart,
                default2 = bbCode.holderText,
                onInput = { input1, input2 ->
                    activity.launchUI {
                        delay(200)
                        onBeforeInsert()
                        delay(100)
                        val startText = String.format(bbCode.tagStart, input1)
                        val text = startText + input2 + bbCode.tagEnd
                        editText.insert(
                            textToInsert = text,
                            startOffset = startText.length,
                            endOffset = (startText + input2).length
                        )
                    }
                }
            )
        } else {
            activity.launchUI {
                onBeforeInsert()
                delay(100)

                val text = bbCode.tagStart + bbCode.holderText + bbCode.tagEnd
                editText.insert(
                    textToInsert = text,
                    startOffset = bbCode.tagStart.length,
                    endOffset = bbCode.tagStart.length + bbCode.holderText.length
                )
            }
        }
    }

    fun EditText.insert(textToInsert: String, startOffset: Int, endOffset: Int) {
        runCatching {
            val start = selectionStart
            val end = selectionEnd
            val editable = getText()
            val builder = SpannableStringBuilder(editable)
            builder.replace(start, end, textToInsert)
            text = builder

            if (startOffset == endOffset) {
                setSelection(start + startOffset.coerceAtLeast(0))
            } else {
                val startIndex = start + startOffset.coerceAtLeast(0)
                val endIndex = start + endOffset.coerceAtLeast(0)
                setSelection(startIndex, endIndex)
            }
        }
    }

    /**
     * 插入图片链接
     */
    fun insertImage(edReply: AnimeEditTextView, imageUrl: String) {
        edReply.append("[img]$imageUrl[/img]")
    }

    @Keep
    @Parcelize
    data class BBCode(
        var tagStart: String = "",
        var tagEnd: String = "",
        var holderTagStart: String = "",
        var holderTagStartTip: String = "",
        var holderTagEnd: String = "",
        var holderText: String = "",
        @DrawableRes var icon: Int = 0,
    ) : Parcelable, IdEntity {
        override var id: String
            get() = tagStart + tagEnd
            set(value) = debugLog { value }
    }
}