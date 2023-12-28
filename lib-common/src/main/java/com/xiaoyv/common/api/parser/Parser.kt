package com.xiaoyv.common.api.parser

import android.content.Intent
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import android.webkit.URLUtil
import androidx.core.text.parseAsHtml
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.EncodeUtils
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.request.EmojiParam
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.groupValue
import com.xiaoyv.common.kts.groupValueOne
import org.jsoup.nodes.Element
import org.jsoup.select.Elements


/**
 * Parse background-image background-image:url('//lain.bgm.tv/r/100x100/pic/cover/l/eb/e9/397808_m3g00.jpg');
 */
fun String.fetchStyleBackgroundUrl(): String {
    val imageUrl = "url\\((.*?)\\)".toRegex().find(this)?.groupValues?.getOrNull(1).orEmpty()
        .replace("['|\"]".toRegex(), "")
    if (imageUrl.startsWith("//")) {
        return "https:$imageUrl"
    }
    return imageUrl
}

/**
 * https://lain.bgm.tv/pic/crt/s/b7/e5/27194_prsn_cXmHm.jpg
 * --->
 * https://lain.bgm.tv/pic/crt/l/b7/e5/27194_prsn_cXmHm.jpg
 *
 * https://lain.bgm.tv/pic/cover/c/67/d1/876_dCfrd.jpg
 * https://lain.bgm.tv/pic/cover/s/67/d1/876_dCfrd.jpg?
 * https://lain.bgm.tv/pic/cover/m/37/93/144523_A8ffz.jpg
 * --->
 * https://lain.bgm.tv/pic/cover/l/37/93/144523_A8ffz.jpg
 *
 * https://lain.bgm.tv/pic/photo/g/41/30/823739_i8sWx.jpg
 * https://lain.bgm.tv/pic/photo/c/41/30/823739_i8sWx.jpg
 * https://lain.bgm.tv/pic/photo/s/41/30/823739_i8sWx.jpg
 * https://lain.bgm.tv/pic/photo/m/41/30/823739_i8sWx.jpg
 * --->
 * https://lain.bgm.tv/pic/photo/g/41/30/823739_i8sWx.jpg
 */
fun String.optImageUrl(
    large: Boolean = true,
    largest: Boolean = false,
    default: Boolean = true,
): String {
    val imageUrl = when {
        startsWith("//") -> "https:$this"
        startsWith("/") -> "${BgmApiManager.URL_BASE_WEB}$this"
        else -> this
    }
    val size = if (largest) "1200" else if (large) "400" else "200"
    val defaultImage = "file:///android_asset/image/info_only.jpg"
    return imageUrl
        .replace("/r/(.*?)/".toRegex(), "/")
        .replace("/pic/(.*?)/[gcsml]/".toRegex(), "/r/$size/pic/\$1/l/")
        .replace("https://(.*?)/info_only.png".toRegex(), defaultImage)
        .replace("https://(.*?)/no_icon.jpg".toRegex(), defaultImage)
        .replace("https://(.*?)/no_icon_subject.png".toRegex(), defaultImage)
        .replace("https://(.*?)/icon.jpg".toRegex(), defaultImage)
        .ifBlank { if (default) defaultImage else "" }
}

fun String?.parseCount(): Int {
    val orNull = this?.toIntOrNull()
    if (orNull != null) return orNull
    return "(\\d+)".toRegex().find(this ?: return 0)?.groupValues?.getOrNull(1)?.toIntOrNull() ?: 0
}

class GlobalChickHandler(private val span: URLSpan) : ClickableSpan() {
    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.bgColor = Color.TRANSPARENT
    }

    override fun onClick(view: View) {
        runCatching {
            val byteArray = span.url.orEmpty().encodeToByteArray()
            val encode = EncodeUtils.base64Encode2String(byteArray)
            val uri = "bgm://bangumi.android/route?data=$encode"
            require(ActivityUtils.startActivity(Intent.parseUri(uri, Intent.URI_ALLOW_UNSAFE))) {
                "Uri: 启动失败 -> $uri"
            }
        }.onFailure {
            it.printStackTrace()
        }
    }
}

/**
 * 解析 Html 并添加链接
 */
fun String.parseHtml(handleLink: Boolean = false): CharSequence {
    val spanned = parseAsHtml()
    if (!handleLink) return spanned

    val builder = SpannableStringBuilder(spanned)
    val urlSpans = builder.getSpans(0, builder.length, URLSpan::class.java)
    for (span in urlSpans) {
        val start = builder.getSpanStart(span)
        val end = builder.getSpanEnd(span)
        val flags = builder.getSpanFlags(span)
        val newSpan = GlobalChickHandler(span)
        builder.removeSpan(span)
        builder.setSpan(newSpan, start, end, flags)
    }
    return builder
}

/**
 * 查询日期
 * - 2004年1月1日
 * - 2004年11月1日
 * - 2004年11月11日
 * - 2004年1月
 * - 2025年
 */
fun String.parserTime(): String {
    return ("\\d{4}.\\d{1,2}.\\d{1,2}.".toRegex().find(this)
        ?: "\\d{4}.\\d{1,2}.".toRegex().find(this)
        ?: "\\d{4}.".toRegex().find(this))?.value.orEmpty()
}

/**
 * 处理 Html
 */
fun String?.preHandleHtml(): String {
    return orEmpty()
        .replace("src=\"//", "src=\"https://")
}

/**
 * 简介背景
 *
 * ```
 * [bg]https://i0.hdslb.com/bfs/article/dfe0c56f33f338f2a335fdc8fa22f4f1c1ea9964.gif[/bg]
 * ```
 */
fun String?.parserSignBackground(): String {
    val url = "\\[bg]\\s*(.*?)\\s*\\[/bg]".toRegex().groupValueOne(orEmpty()).trim()
    if (URLUtil.isNetworkUrl(url)) {
        return url
    }
    return ""
}

/**
 * 强制校验目标节点是否存在，不存在抛出错误
 */
@Throws(IllegalArgumentException::class)
fun <T : Element> T.selectLegal(selector: String): Elements {
    val elements = select(selector)
    require(elements.isNotEmpty()) { "Bangumi娘：报告数据不不见了" }
    return elements
}

fun Elements.hrefId(): String {
    return attr("href").substringAfterLast("/").substringBefore("?")
}

fun Element.hrefId(): String {
    return attr("href").substringAfterLast("/").substringBefore("?")
}

fun Elements.lastTextNode(): String {
    return textNodes().lastOrNull()?.text().orEmpty()
}

fun Elements.firsTextNode(): String {
    return textNodes().firstOrNull()?.text().orEmpty()
}

fun Elements.styleBackground(default: Boolean = true): String {
    return attr("style").fetchStyleBackgroundUrl().optImageUrl(default = default)
}

fun Element.parseStar(): Float {
    return select(".starstop-s > span").attr("class").let { starClass ->
        starClass.orEmpty()
            .split(" ")
            .find { it.startsWith("stars") }.orEmpty()
            .replace("stars", "")
            .toFloatOrNull() ?: 0f
    }
}

fun Elements.parseStar(): Float {
    return select(".starstop-s > span").attr("class").let { starClass ->
        starClass.orEmpty()
            .split(" ")
            .find { it.startsWith("stars") }.orEmpty()
            .replace("stars", "")
            .toFloatOrNull() ?: 0f
    }
}

fun <T : Element> T.requireNoError() {
    val errorMsg = select("#colunmNotice .text").text().trim()
    if (errorMsg.isNotBlank()) {
        throw IllegalArgumentException(errorMsg.ifBlank { "Bangumi娘：报告数据出错啦" })
    }
}

/**
 * 解析添加贴贴的参数信息
 *
 * ```
 *   <a href="javascript:void(0);" class="icon like_dropdown" data-like-type="8" data-like-main-id="391190"
 *      data-like-related-id="2557602" data-like-tpl-id="likes_reaction_menu">
 *     <span class="ico ico_like">&nbsp;</span>
 *     <span class="title">贴贴</span>
 *   </a>
 * ```
 */
fun Elements.parserLikeParam(): EmojiParam {
    val element = select("a[data-like-type]")
    val empty = element.isEmpty()
    val likeType = element.attr("data-like-type")
    val likeMainId = element.attr("data-like-main-id")
    val likeCommentId = element.attr("data-like-related-id")
    return EmojiParam(empty.not(), likeType, likeMainId, likeCommentId)
}

/**
 * 提取页面的 FormHash，这个值每个用户是固定的
 */
fun Element.parserFormHash(): String {
    val fromHashFunName = "(ignoreUser|erasePM)"
    val formHash = select("input[name=formhash]").attr("value")
        .ifBlank {
            val a = "gh=(\\S+?)\"".toRegex().groupValueOne(toString())
            a.trim()
        }
        .ifBlank {
            val b = "$fromHashFunName\\('[\\s\\S]+?'\\s*,\\s*'(.*?)'\\)".toRegex()
                .groupValue(toString(), 2)
            b.trim()
        }
        .ifBlank {
            val c = "disconnectFriend\\(\\d+?\\s*,\\s*'[\\s\\S]+?'\\s*,\\s*'(.*?)'\\)".toRegex()
                .groupValueOne(toString())
            c.trim()
        }

    debugLog { "页面 FormHash: $formHash" }
    return formHash
}








