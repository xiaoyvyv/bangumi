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
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.showInputLine2Dialog
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
                holderText = "加粗内容",
                icon = CommonDrawable.ic_format_bold
            ),
            BBCode(
                tagStart = "[i]",
                tagEnd = "[/i]",
                holderText = "斜体内容",
                icon = CommonDrawable.ic_format_italic
            ),
            BBCode(
                tagStart = "[u]",
                tagEnd = "[/u]",
                holderText = "下划线内容",
                icon = CommonDrawable.ic_format_underlined
            ),
            BBCode(
                tagStart = "[s]",
                tagEnd = "[/s]",
                holderText = "删除线内容",
                icon = CommonDrawable.ic_format_strikethrough
            ),
            BBCode(
                tagStart = "[size=%s]",
                tagEnd = "[/size]",
                holderTagStart = "16",
                holderTagStartTip = "字号",
                holderText = "",
                icon = CommonDrawable.ic_format_size
            ),
            BBCode(
                tagStart = "[mask]",
                tagEnd = "[/mask]",
                holderText = "遮罩内容",
                icon = CommonDrawable.ic_format_mask
            ),
            BBCode(
                tagStart = "[color=%s]",
                tagEnd = "[/color]",
                holderTagStart = "#5555FF",
                holderTagStartTip = "色值",
                holderText = "颜色文本内容",
                icon = CommonDrawable.ic_format_color
            ),
            BBCode(
                tagStart = "[url=%s]",
                tagEnd = "[/url]",
                holderTagStart = "https://xxx",
                holderTagStartTip = "链接地址",
                holderText = "链接描述",
                icon = CommonDrawable.ic_format_link
            ),
            BBCode(
                tagStart = "[img]",
                tagEnd = "[/img]",
                holderText = "图片链接",
                icon = CommonDrawable.ic_format_image
            ),
            BBCode(
                tagStart = "[code]",
                tagEnd = "[/code]",
                holderText = "代码内容",
                icon = CommonDrawable.ic_format_code
            ),
            BBCode(
                tagStart = "[quote]",
                tagEnd = "[/quote]",
                holderText = "引用内容",
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

//        if (pair.third == -1) {
//            editText.insert(pair.first + pair.second, pair.second.length, pair.second.length)
//        } else {
//            editText.insert(
//                pair.first + pair.second,
//                pair.second.length,
//                pair.second.length - pair.third
//            )
//        }
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