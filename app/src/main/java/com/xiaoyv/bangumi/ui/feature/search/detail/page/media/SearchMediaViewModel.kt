package com.xiaoyv.bangumi.ui.feature.search.detail.page.media

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.StringUtils
import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.SearchResultEntity
import com.xiaoyv.common.api.parser.impl.parserSearchResult
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.SearchCatType
import com.xiaoyv.common.config.bean.SearchItem
import com.xiaoyv.common.kts.CommonString

/**
 * Class: [SearchMediaViewModel]
 *
 * @author why
 * @since 1/18/24
 */
class SearchMediaViewModel : BaseListViewModel<SearchResultEntity>() {
    internal var isLegacy = MutableLiveData(false)
    internal var isSearchMedia = true
    internal var itemIndex = MutableLiveData(0)

    internal var keyword: String = ""

    private val mediaItems by lazy {
        listOf(
            SearchItem(
                label = "动画",
                pathType = BgmPathType.TYPE_SEARCH_SUBJECT,
                id = SearchCatType.TYPE_ANIME,
            ),
            SearchItem(
                label = "书籍",
                pathType = BgmPathType.TYPE_SEARCH_SUBJECT,
                id = SearchCatType.TYPE_BOOK,
            ),
            SearchItem(
                label = "音乐",
                pathType = BgmPathType.TYPE_SEARCH_SUBJECT,
                id = SearchCatType.TYPE_MUSIC,
            ),
            SearchItem(
                label = "游戏",
                pathType = BgmPathType.TYPE_SEARCH_SUBJECT,
                id = SearchCatType.TYPE_GAME,
            ),
            SearchItem(
                label = "三次元",
                pathType = BgmPathType.TYPE_SEARCH_SUBJECT,
                id = SearchCatType.TYPE_REAL,
            )
        )
    }

    private val monoItems by lazy {
        listOf(
            SearchItem(
                label = "虚拟角色",
                pathType = BgmPathType.TYPE_SEARCH_MONO,
                id = SearchCatType.TYPE_CHARACTER,
            ),
            SearchItem(
                label = "现实人物",
                pathType = BgmPathType.TYPE_SEARCH_MONO,
                id = SearchCatType.TYPE_PERSON,
            )
        )
    }

    internal val searchItems
        get() = if (isSearchMedia) mediaItems else monoItems

    /**
     * 搜索的条目类型
     */
    private val searchItem
        get() = searchItems[requireNotNull(itemIndex.value)]

    override suspend fun onRequestListImpl(): List<SearchResultEntity> {
        require(keyword.isNotBlank()) { "请输入搜索内容" }

        return BgmApiManager.bgmWebApi.querySearchMedia(
            pathType = searchItem.pathType,
            cat = searchItem.id,
            keyword = keyword,
            page = current,
            legacy = if (isLegacy.value == true) 1 else 0
        ).parserSearchResult(searchItem.pathType).apply {
            if (isRefresh && isEmpty()) {
                throw IllegalArgumentException(StringUtils.getString(CommonString.common_empty_tip))
            }
        }
    }
}