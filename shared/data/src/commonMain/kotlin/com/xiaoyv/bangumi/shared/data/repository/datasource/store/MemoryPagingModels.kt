package com.xiaoyv.bangumi.shared.data.repository.datasource.store

internal data class PageResult<T : Any>(
    val data: List<T>,
    val nextCursor: PageCursor,
)

internal data class MemoryPageSnapshot<T : Any>(
    val data: List<T>,
    val totalCount: Int,
    val nextKey: Int?,
)

internal typealias PageCursor = Int?
