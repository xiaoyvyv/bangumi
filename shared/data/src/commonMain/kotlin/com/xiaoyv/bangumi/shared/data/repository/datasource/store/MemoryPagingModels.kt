package com.xiaoyv.bangumi.shared.data.repository.datasource.store

/**
 * 单次网络加载返回的分页数据与后续游标。
 *
 * @param T 列表项类型。
 * @param data 当前网络页的数据。
 * @param nextCursor 下一页游标；为 `null` 表示没有更多数据。
 */
internal data class PageResult<T : Any>(
    val data: List<T>,
    val nextCursor: PageCursor,
)

/**
 * 从 Store 读取后交给 PagingSource 的不可变页快照。
 *
 * @param T 列表项类型。
 * @param data 当前请求范围内的数据。
 * @param startIndex 当前页在内存快照中的实际起始位置。
 * @param totalCount 当前内存快照的总项数。
 * @param nextKey 下一次 offset；为 `null` 表示当前已到达末尾。
 */
internal data class MemoryPageSnapshot<T : Any>(
    val data: List<T>,
    val startIndex: Int,
    val totalCount: Int,
    val nextKey: Int?,
)

internal typealias PageCursor = Int?
