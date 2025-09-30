package com.xiaoyv.bangumi.shared.core.utils

/**
 * @author why
 * @since 12/15/23
 */
fun Regex.groupValueOne(targetText: String): String {
    return groupValue(targetText, 1)
}

fun Regex.groupValue(targetText: String, group: Int): String {
    return find(targetText)?.groupValues?.getOrNull(group).orEmpty()
}

/**
 * 获取磁力链接 hash
 */
fun String.magnetHash(): String {
    return "magnet:\\?xt=urn:btih:(\\w+)"
        .toRegex(RegexOption.IGNORE_CASE)
        .groupValueOne(this)
        .uppercase()
}