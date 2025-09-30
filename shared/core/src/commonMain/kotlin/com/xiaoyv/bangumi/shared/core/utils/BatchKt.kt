package com.xiaoyv.bangumi.shared.core.utils

suspend fun <T> fetchAllPages(
    pageSize: Int = 100,
    block: suspend (offset: Int, limit: Int) -> List<T>,
): List<T> {
    require(pageSize > 0) { "pageSize must be > 0" }

    val resultList = mutableListOf<T>()
    var offset = 0

    while (true) {
        val pageItems = block(offset, pageSize)
        resultList.addAll(pageItems)
        if (pageItems.size < pageSize) break
        offset += pageSize
    }

    return resultList
}
