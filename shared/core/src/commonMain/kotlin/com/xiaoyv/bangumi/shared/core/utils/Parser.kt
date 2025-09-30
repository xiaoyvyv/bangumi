@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.core.utils

import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.select.Elements
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.parse_data_none
import com.xiaoyv.bangumi.core_resource.resources.parse_mine_x
import com.xiaoyv.bangumi.core_resource.resources.parse_them_x
import org.jetbrains.compose.resources.getString

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

val blankImageUrlRegex = "https://.*/(info_only\\.png|no_icon\\.jpg|no_photo\\.png|no_icon_subject\\.png|icon\\.jpg)".toRegex()

/**
 * 优化图片
 *
 * @param mode g|c|s|m|l
 */
fun String.optImageUrl(
    mode: String = "l",
    defaultImage: String = "https://lain.bgm.tv/img/no_icon_subject.png",
): String {
    val imageUrl = when {
        startsWith("//") -> "https:$this"
        startsWith("/") -> "https://bgm.tv$this"
        else -> this
    }
    return imageUrl
        .substringBefore("?")
        .replace("/r/(.*?)/".toRegex(), "/")
        .replace("/pic/(.*?)/[gcsml]/".toRegex(), "/pic/$1/$mode/")
        .replace(blankImageUrlRegex, defaultImage)
        .ifBlank { defaultImage }
}

fun String?.parseCount(): Int {
    val orNull = this?.toIntOrNull()
    if (orNull != null) return orNull
    return "(\\d+)".toRegex().find(this ?: return 0)?.groupValues?.getOrNull(1)?.toIntOrNull() ?: 0
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
 * 简介背景
 *
 * ```
 * [bg]https://i0.hdslb.com/bfs/article/dfe0c56f33f338f2a335fdc8fa22f4f1c1ea9964.gif[/bg]
 * 我是小玉。
 * [size=0][bg]/sdcard/.transforms/synthetic/picker/0/com.android.providers.media.photopicker/media/1000001761.jpg[/bg][/size]
 * ```
 */
fun String?.parserSignBackground(): String {
    return "\\[bg]\\s*(.*?)\\s*\\[/bg]".toRegex().groupValueOne(orEmpty()).trim()
}

/**
 * 强制校验目标节点是否存在，不存在抛出错误
 */
@Throws(Exception::class)
suspend fun <T : Element> T.selectLegal(selector: String): Elements {
    val elements = select(selector)
    require(elements.isNotEmpty()) { getString(Res.string.parse_data_none) }
    return elements
}

fun Elements.href() = attr("href")
fun Element.href() = attr("href")

fun Element.hrefId() = href().substringAfterLast("/").substringBefore("?")
fun Elements.hrefId() = href().substringAfterLast("/").substringBefore("?")

fun Element.hrefLongId() = hrefId().toLongValue()
fun Elements.hrefLongId() = hrefId().toLongValue()

fun Elements.lastTextNode() = textNodes().lastOrNull()?.text().orEmpty()
fun Element.lastTextNode() = textNodes().lastOrNull()?.text().orEmpty()

fun Elements.firsTextNode() = textNodes().firstOrNull()?.text().orEmpty()
fun Element.firsTextNode() = textNodes().firstOrNull()?.text().orEmpty()

fun Element.parseStar(): Double {
    return select(".starstop-s > span, .starstop > span").attr("class")
        .split(" ")
        .find { it.startsWith("stars") }.orEmpty()
        .replace("stars", "")
        .toDoubleOrNull() ?: 0.0
}

fun Elements.parseStar(): Double {
    return select(".starstop-s > span").attr("class")
        .split(" ")
        .find { it.startsWith("stars") }.orEmpty()
        .replace("stars", "")
        .toDoubleOrNull() ?: 0.0
}

suspend fun <T : Element> T.requireNoError() {
    val errorMsg = select("#colunmNotice .text").text().trim()
    if (errorMsg.isNotBlank()) {
        throw IllegalArgumentException(errorMsg.ifBlank { getString(Res.string.parse_data_none) })
    }
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

/**
 * 提取图片链接
 */
fun String.parserImage(): String {
    return "//(.*?)\\.(jpg|jpeg|png|webp|gif)".toRegex(RegexOption.IGNORE_CASE)
        .find(this)?.value.orEmpty().trim()
        .let {
            if (it.isNotBlank()) "https:$it" else it
        }
}

suspend fun Elements.parserUserCollectTitle(): String {
    return text().replace("/", "").trim().let {
        val typeName = it.substringAfterLast("的")
        if (it.contains("我的")) {
            getString(Res.string.parse_mine_x, typeName)
        } else {
            getString(Res.string.parse_them_x, typeName)
        }
    }
}








