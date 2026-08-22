package com.xiaoyv.bangumi.shared.data.repository.datasource.store

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface MemoryPagingController<T : Any, Id : Any> {
    val flow: Flow<PagingData<T>>

    suspend fun updateById(id: Id, transform: (T) -> T): Boolean

    suspend fun updateWhere(predicate: (T) -> Boolean, transform: (T) -> T): Boolean

    suspend fun replaceById(id: Id, item: T): Boolean

    suspend fun removeById(id: Id): Boolean

    suspend fun insert(item: T, index: Int = 0): Boolean

    suspend fun filter(predicate: (T) -> Boolean): Boolean

    suspend fun sortWith(comparator: Comparator<in T>): Boolean

    suspend fun replaceAll(transform: (List<T>) -> List<T>): Boolean
}
