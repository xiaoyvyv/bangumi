@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.ui.profile.page.save

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.BrowserEntity
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.api.parser.impl.BrowserParser.parserBrowserPage
import com.xiaoyv.common.config.GlobalConfig
import com.xiaoyv.common.config.annotation.BrowserSortType
import com.xiaoyv.common.config.annotation.InterestCollectType
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.config.bean.FilterEntity
import com.xiaoyv.common.helper.UserHelper

/**
 * Class: [SaveListViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class SaveListViewModel : BaseListViewModel<BrowserEntity.Item>() {

    @InterestCollectType
    internal var listType = InterestCollectType.TYPE_DO

    internal var userId = ""
    internal var requireLogin = false

    @BrowserSortType
    internal var sortType: String = BrowserSortType.TYPE_DEFAULT

    @MediaType
    internal var mediaType: String = MediaType.TYPE_ANIME

    internal var selectTag: String? = null

    /**
     * TAGS 结果缓存
     */
    private var mediaTags: List<MediaDetailEntity.MediaTag> = emptyList()

    /**
     * 筛选的标识符
     */
    internal val filterFieldMediaType = "mediaType"
    internal val filterFieldOrderBy = "orderby"
    internal val filterFieldTag = "tag"

    override suspend fun onRequestListImpl(): List<BrowserEntity.Item> {
        if (requireLogin) require(UserHelper.isLogin) { "你还没有登录呢" }
        val browserPage = BgmApiManager.bgmWebApi.queryUserCollect(
            mediaType = mediaType,
            userId = userId,
            listType = listType,
            sortType = sortType,
            tag = selectTag,
            page = current
        ).parserBrowserPage(mediaType = mediaType)
        mediaTags = browserPage.tags
        return browserPage.items
    }

    /**
     * 过滤参数
     */
    fun createFilterOptions(): List<FilterEntity> {
        val options = mutableListOf(
            FilterEntity(
                id = "条目类型",
                options = listOf(
                    FilterEntity.OptionItem(
                        title = GlobalConfig.mediaTypeName(MediaType.TYPE_ANIME),
                        field = filterFieldMediaType,
                        id = MediaType.TYPE_ANIME,
                        selected = mediaType == MediaType.TYPE_ANIME,
                    ),
                    FilterEntity.OptionItem(
                        title = GlobalConfig.mediaTypeName(MediaType.TYPE_BOOK),
                        field = filterFieldMediaType,
                        id = MediaType.TYPE_BOOK,
                        selected = mediaType == MediaType.TYPE_BOOK,
                    ),
                    FilterEntity.OptionItem(
                        title = GlobalConfig.mediaTypeName(MediaType.TYPE_MUSIC),
                        field = filterFieldMediaType,
                        id = MediaType.TYPE_MUSIC,
                        selected = mediaType == MediaType.TYPE_MUSIC,
                    ),
                    FilterEntity.OptionItem(
                        title = GlobalConfig.mediaTypeName(MediaType.TYPE_GAME),
                        field = filterFieldMediaType,
                        id = MediaType.TYPE_GAME,
                        selected = mediaType == MediaType.TYPE_GAME,
                    ),
                    FilterEntity.OptionItem(
                        title = GlobalConfig.mediaTypeName(MediaType.TYPE_REAL),
                        field = filterFieldMediaType,
                        id = MediaType.TYPE_REAL,
                        selected = mediaType == MediaType.TYPE_REAL,
                    ),
                )
            ),
            FilterEntity(
                id = "排序类型",
                options = listOf(
                    FilterEntity.OptionItem(
                        title = "收藏时间",
                        field = filterFieldOrderBy,
                        id = BrowserSortType.TYPE_DEFAULT,
                        selected = sortType == BrowserSortType.TYPE_DEFAULT
                    ),
                    FilterEntity.OptionItem(
                        title = "评价",
                        field = filterFieldOrderBy,
                        id = BrowserSortType.TYPE_RATE,
                        selected = sortType == BrowserSortType.TYPE_RATE
                    ),
                    FilterEntity.OptionItem(
                        title = "发售日",
                        field = filterFieldOrderBy,
                        id = BrowserSortType.TYPE_DATE,
                        selected = sortType == BrowserSortType.TYPE_DATE
                    ),
                    FilterEntity.OptionItem(
                        title = "名称",
                        field = filterFieldOrderBy,
                        id = BrowserSortType.TYPE_TITLE,
                        selected = sortType == BrowserSortType.TYPE_TITLE
                    ),
                )
            )
        )
        if (mediaTags.isNotEmpty()) {
            options.add(FilterEntity(
                id = "相关标签",
                options = mediaTags.map {
                    FilterEntity.OptionItem(
                        title = it.title,
                        field = filterFieldTag,
                        id = it.tagName,
                        selected = selectTag == it.tagName
                    )
                }
            ))
        }
        return options
    }
}