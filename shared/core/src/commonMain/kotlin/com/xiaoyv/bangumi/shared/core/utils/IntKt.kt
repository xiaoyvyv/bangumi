package com.xiaoyv.bangumi.shared.core.utils

fun Int.toApiOffset(pageSize: Int): Int {
    require(this > 0) { "page must be >= 1" }
    require(pageSize > 0) { "pageSize must be > 0" }
    return (this - 1) * pageSize
}

fun Int.toApiPage(pageSize: Int): Int {
    require(this >= 0) { "offset must be >= 0" }
    require(pageSize > 0) { "pageSize must be > 0" }
    return (this / pageSize) + 1
}

