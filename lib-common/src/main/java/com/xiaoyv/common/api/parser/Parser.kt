package com.xiaoyv.common.api.parser


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

fun String.optImageUrl(): String {
    val imageUrl = if (startsWith("//")) "https:$this" else this
    return imageUrl.replace("/r/(.*?)/".toRegex(), "/r/600/")
}