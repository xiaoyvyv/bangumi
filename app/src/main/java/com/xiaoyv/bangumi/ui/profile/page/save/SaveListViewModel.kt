package com.xiaoyv.bangumi.ui.profile.page.save

import androidx.lifecycle.MutableLiveData
import com.chad.library.adapter.base.loadState.LoadState
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.BrowserEntity
import com.xiaoyv.common.api.parser.impl.BrowserParser.parserBrowserPage
import com.xiaoyv.common.config.annotation.BrowserSortType
import com.xiaoyv.common.config.annotation.InterestCollectType
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.widget.kts.copyAddAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [SaveListViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class SaveListViewModel : BaseViewModel() {
    internal val onListLiveData = MutableLiveData<List<BrowserEntity.Item>?>()

    internal var listType = InterestCollectType.TYPE_WISH
    internal var userId = ""
    internal var requireLogin = false

    /**
     * 搜索条件
     */
    private var current = 1

    @BrowserSortType
    private var sortType: String = BrowserSortType.TYPE_DEFAULT

    @MediaType
    internal var mediaType: String = MediaType.TYPE_ANIME

    internal var loadingMoreState: LoadState = LoadState.None

    internal val isRefresh: Boolean
        get() = current == 1

    fun refresh() {
        current = 1
        queryBrowserRank()
    }

    fun loadMore() {
        current++
        queryBrowserRank()
    }

    private fun queryBrowserRank() {
        launchUI(
            stateView = loadingViewState,
            error = {
                it.printStackTrace()
                onListLiveData.value = null
            },
            block = {
                if (requireLogin) require(UserHelper.isLogin) { "你还没有登录呢" }

                val response = withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.queryUserCollect(
                        mediaType = mediaType,
                        userId = userId,
                        listType = listType,
                        sortType = sortType,
                        page = current
                    ).parserBrowserPage(mediaType)
                }

                val responseList = response.items

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

    fun clearList() {
        onListLiveData.value = emptyList()
    }
}