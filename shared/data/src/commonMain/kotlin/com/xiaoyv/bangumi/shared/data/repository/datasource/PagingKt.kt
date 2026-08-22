package com.xiaoyv.bangumi.shared.data.repository.datasource

import androidx.paging.PagingConfig
import com.xiaoyv.bangumi.shared.data.repository.datasource.store.DefaultMemoryPagingController
import com.xiaoyv.bangumi.shared.data.repository.datasource.store.MemoryPagingStore
import com.xiaoyv.bangumi.shared.data.repository.datasource.store.PageResult

fun createPagingConfig(pageSize: Int): PagingConfig {
    return PagingConfig(
        pageSize = pageSize,
        initialLoadSize = pageSize,
        enablePlaceholders = false,
    )
}

fun <T : Any, Id : Any> createMemoryPageLimitPagingController(
    pagingConfig: PagingConfig,
    idSelector: (T) -> Id,
    onLoadData: suspend (Int) -> List<T>,
    onlyOnePage: Boolean = false,
): MemoryPagingController<T, Id> {
    val store = MemoryPagingStore(
        idSelector = idSelector,
        onLoadData = { page ->
            val currentPage = page ?: 1
            val data = onLoadData(currentPage)

            PageResult(
                data = data,
                nextCursor = when {
                    onlyOnePage -> null
                    data.isEmpty() -> null
                    else -> currentPage + 1
                }
            )
        }
    )

    return DefaultMemoryPagingController(
        store = store,
        pagingConfig = pagingConfig,
    )
}

fun <T : Any, Id : Any> createMemoryOffsetLimitPagingController(
    pagingConfig: PagingConfig,
    idSelector: (T) -> Id,
    onLoadData: suspend (Int) -> List<T>,
    transformData: (List<T>) -> List<T> = { it },
): MemoryPagingController<T, Id> {
    val store = MemoryPagingStore(
        idSelector = idSelector,
        onLoadData = { cursor ->
            val offset = cursor ?: 0
            val data = onLoadData(offset)
            PageResult(
                data = transformData(data),
                nextCursor = if (data.size < pagingConfig.pageSize) null else offset + data.size,
            )
        }
    )

    return DefaultMemoryPagingController(
        store = store,
        pagingConfig = pagingConfig,
    )
}

fun <T : Any, Id : Any> createMemoryStepUniquePagingController(
    pagingConfig: PagingConfig,
    idSelector: (T) -> Id,
    onLoadData: suspend (Int?) -> Pair<List<T>, Int?>,
): MemoryPagingController<T, Id> {
    val store = MemoryPagingStore(
        idSelector = idSelector,
        onLoadData = {
            val result = onLoadData(it)
            PageResult(
                data = result.first,
                nextCursor = result.second,
            )
        }
    )

    return DefaultMemoryPagingController(
        store = store,
        pagingConfig = pagingConfig,
    )
}
