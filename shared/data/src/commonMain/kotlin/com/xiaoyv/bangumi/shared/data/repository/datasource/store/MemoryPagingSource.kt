package com.xiaoyv.bangumi.shared.data.repository.datasource.store

import androidx.paging.PagingSource
import androidx.paging.PagingState

/**
 * 将 [MemoryPagingStore] 的连续内存快照适配为 Paging3 offset 分页源。
 *
 * @param T 列表项类型。
 * @param Id 列表项的稳定唯一标识类型。
 * @param store 提供快照和网络补页能力的内存 Store。
 */
internal class MemoryPagingSource<T : Any, Id : Any>(
    private val store: MemoryPagingStore<T, Id>,
) : PagingSource<Int, T>() {
    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        val anchor = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchor) ?: return null

        return page.prevKey?.plus(state.config.pageSize)
            ?: page.nextKey?.minus(state.config.pageSize)?.coerceAtLeast(0)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> = try {
        when (params) {
            is LoadParams.Refresh -> store.loadRefresh(params.loadSize)
            is LoadParams.Append -> store.load(
                offset = params.key,
                loadSize = params.loadSize,
            )

            is LoadParams.Prepend -> store.load(
                offset = params.key,
                loadSize = params.loadSize,
            )
        }
    } catch (e: Exception) {
        LoadResult.Error(e)
    }
}
