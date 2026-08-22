package com.xiaoyv.bangumi.shared.data.repository.datasource

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.max
import kotlin.math.min

interface MemoryPagingController<T : Any, Id : Any> {
    val flow: Flow<PagingData<T>>

    suspend fun updateById(id: Id, transform: (T) -> T): Boolean

    suspend fun replaceById(id: Id, item: T): Boolean

    suspend fun removeById(id: Id): Boolean

    suspend fun filter(predicate: (T) -> Boolean): Boolean

    suspend fun sortWith(comparator: Comparator<in T>): Boolean

    suspend fun replaceAll(transform: (List<T>) -> List<T>): Boolean
}

internal class DefaultMemoryPagingController<T : Any, Id : Any>(
    private val store: MemoryPagingStore<T, Id>,
    pagingConfig: PagingConfig,
) : MemoryPagingController<T, Id> {
    private val pager = Pager(
        config = pagingConfig,
        pagingSourceFactory = store::createPagingSource
    )

    override val flow: Flow<PagingData<T>> = pager.flow

    override suspend fun updateById(id: Id, transform: (T) -> T): Boolean {
        return store.updateById(id, transform)
    }

    override suspend fun replaceById(id: Id, item: T): Boolean {
        return store.replaceById(id, item)
    }

    override suspend fun removeById(id: Id): Boolean {
        return store.removeById(id)
    }

    override suspend fun filter(predicate: (T) -> Boolean): Boolean {
        return store.filter(predicate)
    }

    override suspend fun sortWith(comparator: Comparator<in T>): Boolean {
        return store.sortWith(comparator)
    }

    override suspend fun replaceAll(transform: (List<T>) -> List<T>): Boolean {
        return store.replaceAll(transform)
    }
}

internal class MemoryPagingStore<T : Any, Id : Any>(
    private val idSelector: (T) -> Id,
    private val onLoadData: suspend (PageCursor) -> PageResult<T>,
) {
    private val mutex = Mutex()
    private val items = mutableListOf<T>()
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
            current[index] = transform(current[index])
            true
        }
    }

    suspend fun replaceById(id: Id, item: T): Boolean {
        return mutateLocked { current ->
            val index = current.indexOfFirst { idSelector(it) == id }
            if (index < 0) return@mutateLocked false
            current[index] = item
            true
        }
    }

    suspend fun removeById(id: Id): Boolean {
        return mutateLocked { current ->
            current.removeAll { idSelector(it) == id }
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

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            store.load(
                offset = params.key ?: 0,
                loadSize = params.loadSize,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}

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
