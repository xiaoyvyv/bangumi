package com.xiaoyv.bangumi.base

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.StringUtils
import com.chad.library.adapter.base.loadState.LoadState
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.widget.kts.copyAddAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

/**
 * Class: [BaseListViewModel]
 *
 * @author why
 * @since 11/29/23
 */
abstract class BaseListViewModel<T> : BaseViewModel() {
    private var loadJob: Job? = null
    private var firstLoad = true

    internal val onListLiveData = MutableLiveData<List<T>?>()
    internal var loadingMoreState: LoadState = LoadState.None
    internal var current = 1

    internal val isRefresh: Boolean
        get() = current == 1

    internal open val emptyTip: String
        get() = StringUtils.getString(CommonString.common_empty_tip)

    /**
     * 返回 offset - limit 格式的分页参数
     */
    internal val pager: Pair<Int, Int>
        get() {
            val limit = 1000
            val offset = (current - 1) * limit
            return offset to limit
        }

    var isOnlyOnePage: Boolean = false

    open val emptyCheck: Boolean
        get() = true

    fun refresh() {
        current = 1
        loadListData()
    }

    fun loadMore() {
        current++
        loadListData()
    }

    fun clearList() {
        current = 1
        onListLiveData.value = emptyList()
    }

    private fun loadListData() {
        loadJob?.cancel()
        loadJob = launchUI(
            stateView = loadingViewState,
            error = {
                loadJob = null

                it.printStackTrace()
                if (it is CancellationException) return@launchUI

                if (isRefresh) {
                    loadingMoreState = LoadState.None
                    onListLiveData.value = null
                } else {
                    loadingMoreState = LoadState.Error(it)
                    current--
                    onListLiveData.value = onListLiveData.value
                }
            },
            block = {
                // 首次进入缓存加载
                if (isOnlyOnePage && firstLoad) {
                    val cacheImpl = withContext(Dispatchers.IO) { onRequestCacheImpl() }
                    if (cacheImpl.isNotEmpty()) {
                        onListLiveData.value = cacheImpl
                        // 有缓存提前改变加载状态
                        loadingViewState.showContent()
                    }
                }

                firstLoad = false

                val responseList = withContext(Dispatchers.IO) {
                    onRequestListImpl().apply {
                        if (isRefresh && emptyCheck) {
                            require(isNotEmpty()) { emptyTip }
                        }
                    }
                }

                ensureActive()

                if (isRefresh) {
                    onListLiveData.value = responseList
                } else {
                    onListLiveData.value = onListLiveData.value.copyAddAll(responseList)
                }

                loadingMoreState = if (isRefresh && responseList.isEmpty()) {
                    LoadState.None
                } else {
                    LoadState.NotLoading(responseList.isEmpty())
                }

                loadJob = null
            }
        )
    }

    abstract suspend fun onRequestListImpl(): List<T>

    open suspend fun onRequestCacheImpl(): List<T> {
        return emptyList()
    }
}