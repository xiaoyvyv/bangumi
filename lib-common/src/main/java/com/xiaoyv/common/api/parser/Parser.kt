package com.xiaoyv.common.api.parser

import androidx.core.text.parseAsHtml
import com.xiaoyv.common.api.BgmApiManager


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
fun String.optImageUrl(): String {
    val imageUrl = when {
        startsWith("//") -> "https:$this"
        startsWith("/") -> "${BgmApiManager.URL_BASE_WEB}$this"
        else -> this
    }
    return imageUrl
        .replace("/r/(.*?)/".toRegex(), "/r/600/")
        .replace("/pic/(.*?)/[gcsml]/".toRegex(), "/pic/\$1/l/")
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