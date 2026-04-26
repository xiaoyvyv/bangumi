package com.xiaoyv.bangumi.shared.data.parser.bbcode


import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.sp
import com.mohamedrejeb.ksoup.entities.KsoupEntities
import com.xiaoyv.bangumi.shared.core.utils.TagCode
import com.xiaoyv.bangumi.shared.core.utils.TagImage
import com.xiaoyv.bangumi.shared.core.utils.TagLink
import com.xiaoyv.bangumi.shared.core.utils.TagMask
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.core.utils.parseParagraphStyleAttributes
import com.xiaoyv.bangumi.shared.core.utils.parseSpanStyleAttributes
import com.xiaoyv.bangumi.shared.core.utils.preHandleHtml

fun String.parseAsBbcode(
    linkColor: Color = Color.Blue,
    requiresHtmlDecode: Boolean = false
): AnnotatedString {
    val string = AnnotatedString.Builder()
    if (isBlank()) return string.toAnnotatedString()
    val spanStylePushedStack = mutableListOf<Boolean>()
    val paragraphStylePushedStack = mutableListOf<Boolean>()
    val maskPushedStack = mutableListOf<Boolean>()

    val handler = BBCodeParser(
        object : BBCodeHandler {
            override fun onOpenTagName(name: String) {

            }

            override fun onOpenTag(
                name: String,
                attributes: Map<String, String>,
                isImplied: Boolean
            ) {
                when (name.trim().lowercase()) {
                    ElementCodeImg -> {
                        val smileId = attributes["smileid"].orEmpty()
                        val src = attributes["src"].orEmpty().ifBlank { attributes["value"].orEmpty() }
                        val alt = attributes["alt"].orEmpty()
                        if (smileId.isNotBlank()) {
                            string.appendInlineContent(smileId, alt.ifBlank { smileId })
                        } else if (src.startsWith("http")) {
                            // 针对图片，这里直接拼接个内联样式，外套一层 Annotation 可标记点击事件；
                            // 然后立即 pop()，因为 img 已经为最小，不能包裹内容了，不需要再等待 onCloseTag 再 pop()
                            string.pushStringAnnotation(TagImage, src)
                            string.pushStyle(
                                ParagraphStyle(
                                    lineHeightStyle = LineHeightStyle(
                                        alignment = LineHeightStyle.Alignment.Top,
                                        trim = LineHeightStyle.Trim.Both
                                    )
                                )
                            )
                            string.appendInlineContent(TagImage, src)
                            string.pop()
                            string.pop()
                        }
                    }

                    // 代码块
                    ElementCodeCode -> {
                        string.pushStringAnnotation(TagCode, TagCode)
                        string.pushStyle(
                            ParagraphStyle(
                                lineHeight = 24.sp,
                                textIndent = TextIndent(firstLine = 8.sp, restLine = 8.sp)
                            )
                        )
                        string.pushStyle(
                            SpanStyle(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color.White,
                            )
                        )
                    }

                    /*ElementHtmlDiv,
                    ElementHtmlP,
                        -> {
                        val style = attributes["style"].orEmpty()

                        val paragraphStyle = parseParagraphStyleAttributes(style)
                        if (paragraphStyle != null) {
                            string.pushStyle(paragraphStyle)
                            paragraphStylePushedStack.add(true)
                        }
                    }

                    ElementHtmlSpan -> {
                        val className = attributes["class"].orEmpty().trim().lowercase()
                        val style = attributes["style"].orEmpty()
                        val spanStyle = parseSpanStyleAttributes(style)
                        when {
                            // 自带样式
                            spanStyle != null -> {
                                if (className == "text_mask") {
                                    string.pushStringAnnotation(TagMask, "")
                                    maskPushedStack.add(true)
                                }
                                string.pushStyle(spanStyle)
                                spanStylePushedStack.add(true)
                            }
                            // 关键字样式
                            className == "keyword" -> {
                                string.pushStyle(SpanStyle(color = Color.Green.copy(green = 0.8f), fontWeight = FontWeight.Medium))
                                spanStylePushedStack.add(true)
                            }
                        }
                    }*/

                    ElementCodeB -> string.pushStyle(SpanStyle(fontWeight = FontWeight.Bold))

//                    ElementHtmlBr -> {
//                        string.appendLine()
//                    }

                    ElementCodeUrl -> {
                        val url = attributes["value"].orEmpty()
                        string.pushStringAnnotation(TagLink, url)
                        string.pushStyle(
                            SpanStyle(
                                color = linkColor,
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    }

                    ElementCodeU -> string.pushStyle(
                        SpanStyle(
                            color = Color.Unspecified,
                            textDecoration = TextDecoration.Underline
                        )
                    )

                    ElementCodeI -> string.pushStyle(
                        SpanStyle(
                            color = Color.Unspecified,
                            fontStyle = FontStyle.Italic
                        )
                    )

                    ElementCodeS -> string.pushStyle(
                        SpanStyle(
                            color = Color.Unspecified,
                            textDecoration = TextDecoration.LineThrough,
                        )
                    )

                    else -> debugLog { "onOpenTag: Unhandled $name" }
                }
            }

            override fun onCloseTag(name: String, isImplied: Boolean) {
                when (name.trim().lowercase()) {
                    ElementCodeImg -> {}

                    ElementCodeCode -> {
                        string.pop()
                        string.pop()
                        string.pop()
                    }

                    /*       ElementHtmlDiv,
                           ElementHtmlP,
                               -> {
                               if (paragraphStylePushedStack.removeLastOrNull() == true) {
                                   string.pop()
                               }
                           }
       */
                    /*      ElementHtmlBr -> {}
                          ElementHtmlSpan -> {
                              if (maskPushedStack.removeLastOrNull() == true) {
                                  string.pop()
                              }
                              if (spanStylePushedStack.removeLastOrNull() == true) {
                                  string.pop()
                              }
                          }*/

//                    ElementHtmlStrong,
                    ElementCodeB,
                    ElementCodeU,
                    ElementCodeI,
                    ElementCodeS,
                        -> string.pop()

                    ElementCodeUrl -> {
                        string.pop() // corresponds to pushStyle
                        string.pop() // corresponds to pushStringAnnotation
                    }

                    else -> debugLog { "onCloseTag: Unhandled $name" }
                }
            }

            override fun onText(text: String) {
                string.append(text)

            }

            override fun onEnd() {
            }

            override fun onError(error: Exception) {

            }
        }
    )

    val html = if (requiresHtmlDecode) KsoupEntities.decodeHtml(this) else this
    handler.parse(html.preHandleHtml())
    return string.toAnnotatedString()
}