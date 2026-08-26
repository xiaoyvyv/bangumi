package com.xiaoyv.bangumi.shared.data.repository.datasource.store

import androidx.paging.PagingData
import androidx.paging.PagingDataEvent
import androidx.paging.PagingDataPresenter
import androidx.paging.cachedIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

/**
 * 内存分页数据的操作入口。
 *
 * 本地操作会失效当前 [androidx.paging.PagingSource]，由 Paging3 重新读取内存快照；
 * 不会主动触发网络刷新。
 *
 * @param T 列表项类型。
 * @param Id 列表项的稳定唯一标识类型。
 */
interface MemoryPagingController<T : Any, Id : Any> {
    /**
     * Paging3 消费的分页数据流。
     */
    val flow: Flow<PagingData<T>>

    /**
     * 缓存 PagingData，并在 [scope] 存活期间持续预热最新 generation。
     *
     * MemoryStore 更新导致 PagingSource invalidate 时，即使页面暂时没有 UI collector，后台
     * Presenter 也会完成内存 REFRESH。页面返回后，官方 LazyPagingItems 可以直接从 cachedIn
     * 的 replay 数据恢复已加载快照，避免 itemCount 短暂变为 0。
     *
     * @param scope 缓存及后台 Presenter 使用的协程作用域。
     * @return 可直接交给官方 `collectAsLazyPagingItems` 的标准 PagingData Flow。
     */
    fun cachedIn(scope: CoroutineScope): Flow<PagingData<T>> {
        val cachedFlow = flow.cachedIn(scope)
        scope.launch {
            val presenter = object : PagingDataPresenter<T>(
                mainContext = EmptyCoroutineContext,
            ) {
                override suspend fun presentPagingDataEvent(event: PagingDataEvent<T>) = Unit
            }
            cachedFlow.collectLatest(presenter::collectFrom)
        }
        return cachedFlow
    }

    /**
     * 变换指定 ID 的已加载项。
     *
     * @param id 要更新的稳定 ID。
     * @param transform 基于当前项生成新项的函数。
     * @return 仅当已找到且内容实际变化时返回 `true`。
     */
    suspend fun updateById(id: Id, transform: (T) -> T): Boolean

    /**
     * 变换所有满足条件的已加载项。
     *
     * @param predicate 用于选择需要更新项的条件。
     * @param transform 基于当前项生成新项的函数。
     * @return 仅当至少一个项实际变化时返回 `true`。
     */
    suspend fun updateWhere(predicate: (T) -> Boolean, transform: (T) -> T): Boolean

    /**
     * 替换指定 ID 的已加载项。
     *
     * @param id 要替换的稳定 ID。
     * @param item 新的列表项。
     * @return 仅当已找到且内容实际变化时返回 `true`。
     */
    suspend fun replaceById(id: Id, item: T): Boolean

    /**
     * 删除指定 ID，并记录 tombstone 以过滤后续网络页中的同一项。
     *
     * @param id 要删除的稳定 ID。
     * @return 仅当新增 tombstone 或移除已加载项时返回 `true`。
     */
    suspend fun removeById(id: Id): Boolean

    /**
     * 仅向当前内存快照插入新项。
     *
     * @param item 要插入的列表项；同 ID 已存在时不会重复插入。
     * @param index 插入位置，越界时会收敛到有效范围。
     * @return 仅当项被实际插入时返回 `true`。
     */
    suspend fun insert(item: T, index: Int = 0): Boolean

    /**
     * 过滤当前已加载项。
     *
     * @param predicate 保留项必须满足的条件。
     * @return 仅当过滤结果发生变化时返回 `true`。
     */
    suspend fun filter(predicate: (T) -> Boolean): Boolean

    /**
     * 重排当前已加载项。
     *
     * @param comparator 排序比较器。
     * @return 仅当排序结果发生变化时返回 `true`。
     */
    suspend fun sortWith(comparator: Comparator<in T>): Boolean

    /**
     * 用转换结果完整替换当前已加载项。
     *
     * @param transform 基于当前快照生成新列表的函数。
     * @return 仅当替换结果发生变化时返回 `true`。
     */
    suspend fun replaceAll(transform: (List<T>) -> List<T>): Boolean

    /**
     * 触发完整刷新，清除当前内存缓存并重新加载。
     */
    fun refresh()
}
