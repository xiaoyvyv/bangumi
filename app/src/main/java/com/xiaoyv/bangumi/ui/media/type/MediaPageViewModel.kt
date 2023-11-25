package com.xiaoyv.bangumi.ui.media.type

import androidx.lifecycle.MutableLiveData
import com.chad.library.adapter.base.loadState.LoadState
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.BrowserEntity
import com.xiaoyv.common.api.parser.impl.BrowserParser.parserBrowserPage
import com.xiaoyv.common.config.annotation.BrowserSortType
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.config.bean.MediaTab
import com.xiaoyv.widget.kts.copyAddAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [MediaPageViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class MediaPageViewModel : BaseViewModel() {
    internal var mediaTable: MediaTab? = null

    internal val onBrowserRankLiveData = MutableLiveData<List<BrowserEntity.Item>?>()
    internal val onBrowserOptionLiveData = MutableLiveData<List<BrowserEntity.Option>?>()

    internal var current = 1
    internal var sortType = BrowserSortType.TYPE_RANK

    internal var loadingMoreState: LoadState = LoadState.None

    internal val isRefresh get() = current == 1

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
                onBrowserRankLiveData.value = null
            },
            block = {
                val response = withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.browserRank(
                        mediaType = mediaTable?.type ?: MediaType.TYPE_ANIME,
                        subPath = buildSubPath(),
                        page = current,
                        sortType = sortType
                    ).parserBrowserPage()
                }
                val responseList = response.items

                if (isRefresh) {
                    onBrowserRankLiveData.value = responseList
                } else {
                    onBrowserRankLiveData.value =
                        onBrowserRankLiveData.value.copyAddAll(responseList)
                }

                loadingMoreState = if (isRefresh && responseList.isEmpty()) {
                    LoadState.None
                } else {
                    LoadState.NotLoading(responseList.isEmpty())
                }

                onBrowserOptionLiveData.value = response.options
            }
        )
    }

    private fun buildSubPath(): String {
        return ""
    }

}