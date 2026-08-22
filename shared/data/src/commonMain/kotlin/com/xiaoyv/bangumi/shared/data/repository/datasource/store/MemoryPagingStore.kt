package com.xiaoyv.bangumi.shared.data.repository.datasource.store

import androidx.paging.PagingSource
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.max
import kotlin.math.min

internal class MemoryPagingStore<T : Any, Id : Any>(
    private val idSelector: (T) -> Id,
    private val onLoadData: suspend (PageCursor) -> PageResult<T>,
) {
    private val mutex = Mutex()
    private val items = mutableListOf<T>()
    private val deletedIds = mutableSetOf<Id>()
    private val activeSources = linkedMapOf<Int, MemoryPagingSource<T, Id>>()
    private val nextSourceId = atomic(0)
    private val internalInvalidations = atomic(0)
    private val pendingRefresh = atomic(false)

    private var nextCursor: PageCursor = null
    private var endReached = false
    private var initialized = false

    fun createPagingSource(): PagingSource<Int, T> {
        val sourceId = nextSourceId.incrementAndGet()
        return MemoryPagingSource(
            sourceId = sourceId,
            store = this,
        ).also { source ->
            activeSources[sourceId] = source
            source.registerInvalidatedCallback {
                activeSources.remove(sourceId)

                val remaining = internalInvalidations.value
                if (remaining > 0) {
                    internalInvalidations.decrementAndGet()
                } else {
                    pendingRefresh.value = true
                }
            }
        }
    }

    suspend fun load(offset: Int, loadSize: Int): PagingSource.LoadResult.Page<Int, T> {
        val snapshot = mutex.withLock {
            if (pendingRefresh.value) {
                resetLocked()
            }

            ensureLoadedLocked(offset + loadSize)

            val startIndex = min(offset, items.size)
            val endIndex = min(offset + loadSize, items.size)
            val pageData = items.subList(startIndex, endIndex).toList()
            val nextKey = when {
                endIndex < items.size -> endIndex
                endReached -> null
                pageData.isEmpty() -> null
                else -> endIndex
            }

            MemoryPageSnapshot(
                data = pageData,
                totalCount = items.size,
                nextKey = nextKey,
            )
        }

        return PagingSource.LoadResult.Page(
            data = snapshot.data,
            prevKey = if (offset == 0) null else max(offset - loadSize, 0),
            nextKey = snapshot.nextKey,
            itemsBefore = offset,
            itemsAfter = max(snapshot.totalCount - offset - snapshot.data.size, 0),
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
            deletedIds.add(id)
            current.removeAll { idSelector(it) == id }
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
        endReached = result.nextCursor == null
        pendingRefresh.value = false
    }

    private fun resetLocked() {
        items.clear()
        nextCursor = null
        endReached = false
        initialized = false
        pendingRefresh.value = false
    }

    private fun invalidateActiveSources() {
        val sources = activeSources.values.toList()
        if (sources.isEmpty()) return

        internalInvalidations.addAndGet(sources.size)
        sources.forEach { it.invalidate() }
    }
}
