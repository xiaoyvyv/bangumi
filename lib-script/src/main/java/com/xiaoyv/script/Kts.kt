package com.xiaoyv.script

import org.jsoup.select.Elements


fun Elements.styleBackground(): String {
    return attr("style").fetchStyleBackgroundUrl().optImageUrl()
}

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
 * 提取图片链接
 */
fun String.parserImage(): String {
    return "//(.*?)\\.(jpg|jpeg|png|webp|gif)".toRegex(RegexOption.IGNORE_CASE)
        .find(this)?.value.orEmpty().trim()
        .let {
            if (it.isNotBlank()) "https:$it" else it
        }
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
): String {
    val imageUrl = when {
        startsWith("//") -> "https:$this"
        startsWith("/") -> "https://bgm.tv$this"
        else -> this
    }
    val size = if (largest) "1200" else if (large) "400" else "200"
    return imageUrl
        .replace("/r/(.*?)/".toRegex(), "/")
        .replace("/pic/(.*?)/[gcsml]/".toRegex(), "/r/$size/pic/\$1/l/")
}