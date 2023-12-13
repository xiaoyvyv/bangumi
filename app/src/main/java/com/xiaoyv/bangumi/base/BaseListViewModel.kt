package com.xiaoyv.bangumi.base

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.StringUtils
import com.chad.library.adapter.base.loadState.LoadState
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.widget.kts.copyAddAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [BaseListViewModel]
 *
 * @author why
 * @since 11/29/23
 */
abstract class BaseListViewModel<T> : BaseViewModel() {
    internal val onListLiveData = MutableLiveData<List<T>?>()

    internal var loadingMoreState: LoadState = LoadState.None

    internal var current = 1

    internal val isRefresh: Boolean
        get() = current == 1

    internal open val emptyTip: String
        get() = StringUtils.getString(CommonString.common_empty_tip)

    fun refresh() {
        current = 1
        loadListData()
    }

    fun loadMore() {
        current++
        loadListData()
    }

    private fun loadListData() {
        launchUI(
            stateView = loadingViewState,
            error = {
                it.printStackTrace()
                if (isRefresh) {
                    loadingMoreState = LoadState.None
                    onListLiveData.value = null
                } else {
                    loadingMoreState = LoadState.Error(it)
                    current--
                }
            },
            block = {
                val responseList = withContext(Dispatchers.IO) {
                    onRequestListImpl().apply {
                        if (isRefresh) {
                            require(isNotEmpty()) { emptyTip }
                        }
                    }
                }

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
            }
        )
    }

    abstract suspend fun onRequestListImpl(): List<T>
}