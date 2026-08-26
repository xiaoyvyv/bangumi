package com.xiaoyv.bangumi.shared.data.repository.datasource.store

import androidx.paging.PagingSource
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.max
import kotlin.math.min

/**
 * 管理分页快照、游标和本地 tombstone 的内存数据层。
 *
 * @param T 列表项类型。
 * @param Id 列表项的稳定唯一标识类型。
 * @param idSelector 从列表项获取稳定 ID 的函数。
 * @param onLoadData 按游标加载网络页的函数；返回 `null` next cursor 表示没有下一页。
 */
internal class MemoryPagingStore<T : Any, Id : Any>(
    private val idSelector: (T) -> Id,
    private val onLoadData: suspend (PageCursor) -> PageResult<T>,
) {
    private val mutex = Mutex()
    private val items = mutableListOf<T>()
    private val deletedIds = mutableSetOf<Id>()
    private val sourcesLock = SynchronizedObject()
    private val activeSources = linkedMapOf<Int, MemoryPagingSource<T, Id>>()
    private val internallyInvalidatedSourceIds = mutableSetOf<Int>()
    private val nextSourceId = atomic(0)
    private val pendingRefresh = atomic(false)

    private var nextCursor: PageCursor = null
    private var endReached = false
    private var initialized = false

    /**
     * 创建与当前 Store 快照关联的 PagingSource。
     *
     * Source 的外部失效会标记下次加载为网络 refresh；Store 内部变更引发的失效则只重读内存快照。
     *
     * @return 供 [androidx.paging.Pager] 使用的 PagingSource。
     */
    fun createPagingSource(): PagingSource<Int, T> {
        val sourceId = nextSourceId.incrementAndGet()
        return MemoryPagingSource(store = this).also { source ->
            synchronized(sourcesLock) {
                activeSources[sourceId] = source
            }
            source.registerInvalidatedCallback {
                val internallyInvalidated = synchronized(sourcesLock) {
                    activeSources.remove(sourceId)
                    internallyInvalidatedSourceIds.remove(sourceId)
                }
                if (!internallyInvalidated) {
                    pendingRefresh.value = true
                }
            }
        }
    }

    /**
     * 按内存 offset 返回分页页，并在数据不足时顺序加载网络页。
     *
     * @param offset 当前 PagingSource 请求的内存偏移量。
     * @param loadSize 当前 PagingSource 请求的项数。
     * @return 包含内存快照及下一页 offset 的加载结果。
     */
    suspend fun load(offset: Int, loadSize: Int): PagingSource.LoadResult.Page<Int, T> {
        val snapshot = mutex.withLock {
            if (pendingRefresh.value) {
                resetLocked()
            }

            ensureLoadedLocked(offset + loadSize)
            createPageSnapshotLocked(offset, loadSize)
        }

        return PagingSource.LoadResult.Page(
            data = snapshot.data,
            prevKey = if (offset == 0) null else max(offset - loadSize, 0),
            nextKey = snapshot.nextKey,
            itemsBefore = snapshot.startIndex,
            itemsAfter = max(snapshot.totalCount - snapshot.startIndex - snapshot.data.size, 0),
        )
    }

    /**
     * 重新创建 PagingSource 时返回 Store 当前已加载的完整前缀。
     *
     * 这使关闭 placeholders 的列表仍能保持原有 index；首次加载或 UI refresh 后 Store 为空，
     * 因而仍只请求 [loadSize] 条数据。
     *
     * @param loadSize Store 尚未初始化时需要加载的最小数量。
     * @return 从 index 0 开始、覆盖当前全部已加载项的页面。
     */
    suspend fun loadRefresh(loadSize: Int): PagingSource.LoadResult.Page<Int, T> {
        val snapshot = mutex.withLock {
            if (pendingRefresh.value) {
                resetLocked()
            }

            ensureLoadedLocked(loadSize)
            createPageSnapshotLocked(offset = 0, loadSize = items.size)
        }

        return PagingSource.LoadResult.Page(
            data = snapshot.data,
            prevKey = null,
            nextKey = snapshot.nextKey,
            itemsBefore = 0,
            itemsAfter = max(snapshot.totalCount - snapshot.data.size, 0),
        )
    }

    suspend fun updateById(id: Id, transform: (T) -> T): Boolean {
        return mutateLocked { current ->
            val index = current.indexOfFirst { idSelector(it) == id }
            if (index < 0) return@mutateLocked false
            val updated = transform(current[index])
            if (updated == current[index]) return@mutateLocked false
            current[index] = updated
            true
        }
    }

    suspend fun updateWhere(predicate: (T) -> Boolean, transform: (T) -> T): Boolean {
        return mutateLocked { current ->
            var changed = false
            current.indices.forEach { index ->
                val item = current[index]
                if (predicate(item)) {
                    val updated = transform(item)
                    if (updated != item) {
                        current[index] = updated
                        changed = true
                    }
                }
            }
            changed
        }
    }

    suspend fun replaceById(id: Id, item: T): Boolean {
        return mutateLocked { current ->
            val index = current.indexOfFirst { idSelector(it) == id }
            if (index < 0) return@mutateLocked false
            if (current[index] == item) return@mutateLocked false
            deletedIds.remove(id)
            current[index] = item
            true
        }
    }

    suspend fun removeById(id: Id): Boolean {
        return mutateLocked { current ->
            val tombstoneAdded = deletedIds.add(id)
            val itemRemoved = current.removeAll { idSelector(it) == id }
            tombstoneAdded || itemRemoved
        }
    }

    suspend fun insert(item: T, index: Int): Boolean {
        return mutateLocked { current ->
            val id = idSelector(item)
            if (current.any { idSelector(it) == id }) return@mutateLocked false

            deletedIds.remove(id)
            current.add(index.coerceIn(0, current.size), item)
            true
        }
    }

    suspend fun filter(predicate: (T) -> Boolean): Boolean {
        return mutateLocked { current ->
            val filtered = current.filter(predicate)
            if (filtered.size == current.size) return@mutateLocked false
            current.clear()
            current.addAll(filtered)
            true
        }
    }

    suspend fun sortWith(comparator: Comparator<in T>): Boolean {
        return mutateLocked { current ->
            val sorted = current.sortedWith(comparator)
            if (sorted == current) return@mutateLocked false
            current.clear()
            current.addAll(sorted)
            true
        }
    }

    suspend fun replaceAll(transform: (List<T>) -> List<T>): Boolean {
        return mutateLocked { current ->
            val replaced = transform(current.toList())
            if (replaced == current) return@mutateLocked false
            current.clear()
            current.addAll(replaced)
            true
        }
    }

    /**
     * 触发网络刷新。
     */
    fun refresh() {
        pendingRefresh.value = true
        val sources = synchronized(sourcesLock) {
            activeSources.values.toList()
        }
        sources.forEach { it.invalidate() }
    }

    private suspend fun mutateLocked(block: (MutableList<T>) -> Boolean): Boolean {
        val changed = mutex.withLock {
            block(items)
        }

        if (changed) {
            invalidateActiveSources()
        }

        return changed
    }

    private suspend fun ensureLoadedLocked(targetSize: Int) {
        if (!initialized) {
            val firstPage = onLoadData(nextCursor)
            applyLoadedPageLocked(firstPage)
        }

        while (items.size < targetSize && !endReached) {
            val page = onLoadData(nextCursor)
            applyLoadedPageLocked(page)
        }
    }

    private fun applyLoadedPageLocked(result: PageResult<T>) {
        initialized = true
        val itemCountBefore = items.size

        result.data.forEach { incoming ->
            val id = idSelector(incoming)
            if (id in deletedIds) return@forEach
            val index = items.indexOfFirst { idSelector(it) == id }

            if (index >= 0) {
                items[index] = incoming
            } else {
                items.add(incoming)
            }
        }

        nextCursor = result.nextCursor
        // A cursor that only returns existing IDs cannot advance the local snapshot.
        // Stop here so a repeated remote page cannot trigger an unbounded append loop.
        endReached = result.nextCursor == null || items.size == itemCountBefore
        pendingRefresh.value = false
    }

    private fun resetLocked() {
        items.clear()
        nextCursor = null
        endReached = false
        initialized = false
        pendingRefresh.value = false
    }

    private fun createPageSnapshotLocked(offset: Int, loadSize: Int): MemoryPageSnapshot<T> {
        val startIndex = min(offset, items.size)
        val endIndex = min(offset + loadSize, items.size)
        val pageData = items.subList(startIndex, endIndex).toList()
        val nextKey = when {
            endIndex < items.size -> endIndex
            endReached -> null
            pageData.isEmpty() -> null
            else -> endIndex
        }

        return MemoryPageSnapshot(
            data = pageData,
            startIndex = startIndex,
            totalCount = items.size,
            nextKey = nextKey,
        )
    }

    private fun invalidateActiveSources() {
        val sources = synchronized(sourcesLock) {
            activeSources.map { (sourceId, source) ->
                internallyInvalidatedSourceIds.add(sourceId)
                source
            }
        }
        if (sources.isEmpty()) return

        sources.forEach { it.invalidate() }
    }
}
