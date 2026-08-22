package com.xiaoyv.bangumi.shared.data.repository.datasource.store

import androidx.paging.PagingSource
import androidx.paging.PagingState

internal class MemoryPagingSource<T : Any, Id : Any>(
    private val sourceId: Int,
    private val store: MemoryPagingStore<T, Id>,
) : PagingSource<Int, T>() {
    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        val anchor = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchor) ?: return null

        return page.prevKey?.plus(state.config.pageSize)
            ?: page.nextKey?.minus(state.config.pageSize)?.coerceAtLeast(0)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> = try {
        store.load(
            offset = params.key ?: 0,
            loadSize = params.loadSize,
        )
    } catch (e: Exception) {
        LoadResult.Error(e)
    }
}
