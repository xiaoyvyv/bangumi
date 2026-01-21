package com.xiaoyv.bangumi.shared.core.utils


/**
 * Image size semantic variants
 *
 * Approx max edge (px):
 * - GRID      ~100   (grid / tiny thumb)
 * - S         ~200   (default cover)
 * - M         ~400   (detail page)
 * - L         ~800   (large preview)
 * - XL        ~1200  (high resolution)
 * - ORIGINAL
 */
enum class BgmImageVariant {
    GRID,
    S,
    M,
    L,
    XL,
    ORIGINAL
}

private val removeResizeRegex = "/lain.bgm.tv/r/\\d+".toRegex()
private val removeModeRegex = "/pic/(.*?)/[gcsml]/".toRegex()

/**
 * 图片连接变体
 *
 * 人像格子可以用这个，会识别头部 https://lain.bgm.tv/pic/crt/g/
 *
 * - https://lain.bgm.tv/pic/cover/l/3f/22/523141_ifKkU.jpg
 * - https://lain.bgm.tv/pic/crt/l/28/e5/181459_crt_a8ITi.jpg
 * - https://lain.bgm.tv/pic/crt/g/28/e5/181459_crt_a8ITi.jpg
 */
fun String.bgmImageUrl(variant: BgmImageVariant = BgmImageVariant.S): String {
    if (isBlank() || !contains("lain.bgm.tv")) return this
    val url = replace("http://", "https://")
        .substringBeforeLast("?")
        .replace(removeResizeRegex, "/lain.bgm.tv")
        .replace(removeModeRegex, "/pic/$1/l/")

    return when (variant) {
        BgmImageVariant.GRID -> if (contains("crt")) url.replace("/l/", "/g/") else url.replace("/pic/", "/r/100/pic/")
        BgmImageVariant.S -> url.replace("/pic/", "/r/200/pic/")
        BgmImageVariant.M -> url.replace("/pic/", "/r/400/pic/")
        BgmImageVariant.L -> url.replace("/pic/", "/r/800/pic/")
        BgmImageVariant.XL -> url.replace("/pic/", "/r/1200/pic/")
        BgmImageVariant.ORIGINAL -> url
    }
}

