package com.xiaoyv.common.kts

/**
 * @author why
 * @since 12/15/23
 */
fun Regex.firstGroupValue(targetText: String): String {
    return groupValue(targetText, 1)
}

fun Regex.groupValue(targetText: String, group: Int): String {
    return find(targetText)?.groupValues?.getOrNull(group).orEmpty()
}
