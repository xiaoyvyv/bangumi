package com.xiaoyv.bangumi.ui.feature.search.detail.page.tag

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.StringUtils
import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.SearchResultEntity
import com.xiaoyv.common.api.parser.impl.parserSearchResult
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.config.bean.SearchItem
import com.xiaoyv.common.kts.CommonString

/**
 * Class: [SearchTagViewModel]
 *
 * @author why
 * @since 1/18/24
 */
class SearchTagViewModel : BaseListViewModel<SearchResultEntity>() {
    internal var itemIndex = MutableLiveData(0)
    internal var keyword: String = ""

    /**
     * 搜索的条目类型
     */
    internal val searchItem
        get() = tagItems[itemIndex.value!!]

    private val tagItems by lazy {
        listOf(
            SearchItem(
                label = "动画",
                pathType = BgmPathType.TYPE_SEARCH_TAG,
                id = MediaType.TYPE_ANIME
            ),
            SearchItem(
                label = "书籍",
                pathType = BgmPathType.TYPE_SEARCH_TAG,
                id = MediaType.TYPE_BOOK
            ),
            SearchItem(
                label = "音乐",
                pathType = BgmPathType.TYPE_SEARCH_TAG,
                id = MediaType.TYPE_MUSIC
            ),
            SearchItem(
                label = "游戏",
                pathType = BgmPathType.TYPE_SEARCH_TAG,
                id = MediaType.TYPE_GAME
            ),
            SearchItem(
                label = "三次元",
                pathType = BgmPathType.TYPE_SEARCH_TAG,
                id = MediaType.TYPE_REAL
            )
        )
    }

    override suspend fun onRequestListImpl(): List<SearchResultEntity> {
        require(keyword.isNotBlank()) { "请输入搜索内容" }

        return BgmApiManager.bgmWebApi.querySearchTag(
            mediaType = searchItem.id,
            keyword = keyword,
        ).parserSearchResult(searchItem.pathType).apply {
            if (isRefresh && isEmpty()) {
                throw IllegalArgumentException(StringUtils.getString(CommonString.common_empty_tip))
            }
        }
    }
}