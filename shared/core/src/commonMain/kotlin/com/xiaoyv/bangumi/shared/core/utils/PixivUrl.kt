package com.xiaoyv.bangumi.shared.core.utils


/**
 * 依据 Pixiv 缩略图 URL 规范 (https://github.com/kepstin/Fix-pixiv-thumbnails/blob/master/thumbnail_urls.md)
 *
 * 将低清/裁剪缩略图转化为指定尺寸的高清/等比例缩略图（默认使用 /c/600x600/ 标准尺寸）。
 *
 * @param prefix 目标尺寸前缀，例如 "/c/600x600/"、"/c/600x1200_90/" 或 "/c/540x540_70/"
 */
fun String.toPixivHighResUrl(prefix: String = "/c/600x600/"): String {
    if (isBlank()) return ""
    var url = this
    // 替换 /c/xxx/ 前缀
    if (url.contains("/c/")) {
        url = if (prefix.isBlank()) {
            url.replace(Regex("""/c/[^/]+/"""), "/")
        } else {
            url.replace(Regex("""/c/[^/]+/"""), prefix)
        }
    } else if (prefix.isNotBlank() && url.contains("/img-master/")) {
        url = url.replace("/img-master/", "${prefix.removeSuffix("/")}/img-master/")
    }

    url = url
        .replace("custom-thumb/img/", "img-master/img/")
        .replace("_square1200", "_master1200")
        .replace("_custom1200", "_master1200")
    return url
}

/**
 * 转换为 Pixiv 原图链接
 */
fun String.toPixivOriginalUrl(ext: String = "jpg"): String {
    if (isBlank()) return ""
    return toPixivHighResUrl(prefix = "")
        .replace("/img-master/", "/img-original/")
        .replace("_master1200.jpg", ".$ext")
        .replace("_master1200.png", ".$ext")
}
