package com.xiaoyv.bangumi.shared.data.repository.datasource.store

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

/**
 * [MemoryPagingController] 的默认实现，负责将 Store 接入 Paging3 的 Pager。
 *
 * @param T 列表项类型。
 * @param Id 列表项的稳定唯一标识类型。
 * @param store 内存分页数据层。
 * @param pagingConfig Paging3 的分页配置。
 */
internal class DefaultMemoryPagingController<T : Any, Id : Any>(
    private val store: MemoryPagingStore<T, Id>,
    pagingConfig: PagingConfig,
) : MemoryPagingController<T, Id> {
    private val pager = Pager(
        config = pagingConfig,
        pagingSourceFactory = store::createPagingSource,
    )

    override val flow: Flow<PagingData<T>> = pager.flow

    override suspend fun updateById(id: Id, transform: (T) -> T) = store.updateById(id, transform)

    override suspend fun updateWhere(predicate: (T) -> Boolean, transform: (T) -> T) =
        store.updateWhere(predicate, transform)

    override suspend fun replaceById(id: Id, item: T) = store.replaceById(id, item)

    override suspend fun removeById(id: Id) = store.removeById(id)

    override suspend fun insert(item: T, index: Int) = store.insert(item, index)

    override suspend fun filter(predicate: (T) -> Boolean) = store.filter(predicate)

    override suspend fun sortWith(comparator: Comparator<in T>) = store.sortWith(comparator)

    override suspend fun replaceAll(transform: (List<T>) -> List<T>) = store.replaceAll(transform)
}
