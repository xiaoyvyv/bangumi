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

/**
 * https://lain.bgm.tv/pic/crt/s/b7/e5/27194_prsn_cXmHm.jpg
 * https://lain.bgm.tv/pic/crt/l/b7/e5/27194_prsn_cXmHm.jpg
 *
 * https://lain.bgm.tv/pic/cover/c/67/d1/876_dCfrd.jpg
 * https://lain.bgm.tv/pic/cover/l/67/d1/876_dCfrd.jpg?
 */
fun String.optImageUrl(): String {
    val imageUrl = if (startsWith("//")) "https:$this" else this
    return imageUrl
        .replace("/r/(.*?)/".toRegex(), "/r/600/")
        .replace("/pic/crt/s/", "/pic/crt/l/")
        .replace("/pic/cover/c/", "/pic/cover/l/")
}