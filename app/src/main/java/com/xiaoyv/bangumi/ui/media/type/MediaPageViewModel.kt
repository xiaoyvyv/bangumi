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
import com.xiaoyv.common.config.bean.MediaOptionConfig
import com.xiaoyv.common.config.bean.MediaTab
import com.xiaoyv.common.kts.debugLog
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

    /**
     * 搜索条件
     */
    private var current = 1

    /**
     * 默认按排名
     */
    @BrowserSortType
    private var sortType: String? = BrowserSortType.TYPE_RANK
    private var orderBy: String? = null
    private var subPath: String = ""

    @MediaType
    val mediaType: String
        get() = mediaTable?.type ?: MediaType.TYPE_ANIME

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
                onBrowserRankLiveData.value = null
            },
            block = {

                val response = withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.browserRank(
                        mediaType = mediaType,
                        subPath = subPath,
                        page = current,
                        sortType = sortType,
                        orderby = orderBy
                    ).parserBrowserPage(mediaType)
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
            }
        )
    }

    /**
     * 处理查询条件
     */
    fun handleOption(it: Map<String, List<MediaOptionConfig.Config.Option.Item>>?) {
        val optionItemMap = it.orEmpty()
        if (!optionItemMap.containsKey(mediaType)) {
            return
        }

        val items = requireNotNull(optionItemMap[mediaType]).toMutableList()
        val pinYin = items.find { it.isSortPinYin }
        if (pinYin != null) {
            items.remove(pinYin)
        }

        val sortOption = items.find { it.isSortOption }
        if (sortOption != null) {
            items.remove(sortOption)
        }

        val path = items
            .filter { it.value.orEmpty().isNotBlank() }
            .map {
                when {
                    it.isYear -> "/airtime/" + it.value
                    else -> it.value
                }
            }

        val targetSubPath = path.joinToString("")
            .replace("//", "")
            .removeSuffix("/")
            .removePrefix("/")

        val orderByPinYin = pinYin?.value

        val targetSortType = sortOption?.value

        // 没有变化不更新
        if (targetSubPath == subPath && orderByPinYin == orderBy && sortType == targetSortType) {
            debugLog { "没有变化不更新: $subPath, $orderBy" }
            return
        }

        subPath = targetSubPath
        orderBy = orderByPinYin
        sortType = targetSortType

        debugLog { "更新: $subPath, $orderBy" }

        refresh()
    }
}
