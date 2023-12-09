package com.xiaoyv.common.api.parser

import androidx.core.text.parseAsHtml
import com.xiaoyv.common.api.BgmApiManager
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
fun String.optImageUrl(large: Boolean = true): String {
    val imageUrl = when {
        startsWith("//") -> "https:$this"
        startsWith("/") -> "${BgmApiManager.URL_BASE_WEB}$this"
        else -> this
    }
    val size = if (large) "400" else "200"
    val defaultImage = "file:///android_asset/image/info_only.jpg"
    return imageUrl
        .replace("/r/(.*?)/".toRegex(), "/")
        .replace("/pic/(.*?)/[gcsml]/".toRegex(), "/r/$size/pic/\$1/l/")
        .replace("https://(.*?)/info_only.png".toRegex(), defaultImage)
        .replace("https://(.*?)/no_icon.jpg".toRegex(), defaultImage)
        .replace("https://(.*?)/no_icon_subject.png".toRegex(), defaultImage)
        .replace("https://(.*?)/icon.jpg".toRegex(), defaultImage)
        .ifBlank { defaultImage }
}

fun String?.parseCount(): Int {
    val orNull = this?.toIntOrNull()
    if (orNull != null) return orNull
    return "(\\d+)".toRegex().find(this ?: return 0)?.groupValues?.getOrNull(1)?.toIntOrNull() ?: 0
}

fun String.parseHtml(): CharSequence {
    return parseAsHtml()
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

fun String?.replaceSmiles(): String {
    return orEmpty().replace(
        "src=\"/img/smiles/(.*?)\"".toRegex(),
        "src=\"${BgmApiManager.URL_BASE_WEB}/img/smiles/\$1\""
    )
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

fun <T : Element> T.requireNoError() {
    val errorMsg = select("#colunmNotice .text").text().trim()
    if (errorMsg.isNotBlank()) {
        throw IllegalArgumentException(errorMsg.ifBlank { "Bangumi娘：报告数据出错啦" })
    }
}