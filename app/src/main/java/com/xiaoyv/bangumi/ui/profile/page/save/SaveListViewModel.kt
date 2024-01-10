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
import com.xiaoyv.common.helper.lazyLiveSp

/**
 * Class: [SaveListViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class SaveListViewModel : BaseListViewModel<BrowserEntity.Item>() {
    internal var userId = ""
    internal var requireLogin = false

    /**
     * 默认的类型
     */
    @InterestCollectType
    internal val listType by lazyLiveSp(InterestCollectType.TYPE_DO) { "collect-list-type-$userId" }

    /**
     * 默认的排序
     */
    @BrowserSortType
    internal val sortType by lazyLiveSp(BrowserSortType.TYPE_DEFAULT) { "collect-sort-type-$userId" }

    /**
     * 默认的媒体类型
     */
    @MediaType
    internal val mediaType by lazyLiveSp(MediaType.TYPE_ANIME) { "collect-media-type-$userId" }

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
            mediaType = mediaType.value,
            userId = userId,
            listType = listType.value,
            sortType = sortType.value,
            tag = selectTag,
            page = current
        ).parserBrowserPage(mediaType = mediaType.value)
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
                        selected = mediaType.value == MediaType.TYPE_ANIME,
                    ),
                    FilterEntity.OptionItem(
                        title = GlobalConfig.mediaTypeName(MediaType.TYPE_BOOK),
                        field = filterFieldMediaType,
                        id = MediaType.TYPE_BOOK,
                        selected = mediaType.value == MediaType.TYPE_BOOK,
                    ),
                    FilterEntity.OptionItem(
                        title = GlobalConfig.mediaTypeName(MediaType.TYPE_MUSIC),
                        field = filterFieldMediaType,
                        id = MediaType.TYPE_MUSIC,
                        selected = mediaType.value == MediaType.TYPE_MUSIC,
                    ),
                    FilterEntity.OptionItem(
                        title = GlobalConfig.mediaTypeName(MediaType.TYPE_GAME),
                        field = filterFieldMediaType,
                        id = MediaType.TYPE_GAME,
                        selected = mediaType.value == MediaType.TYPE_GAME,
                    ),
                    FilterEntity.OptionItem(
                        title = GlobalConfig.mediaTypeName(MediaType.TYPE_REAL),
                        field = filterFieldMediaType,
                        id = MediaType.TYPE_REAL,
                        selected = mediaType.value == MediaType.TYPE_REAL,
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
                        selected = sortType.value == BrowserSortType.TYPE_DEFAULT
                    ),
                    FilterEntity.OptionItem(
                        title = "评价",
                        field = filterFieldOrderBy,
                        id = BrowserSortType.TYPE_RATE,
                        selected = sortType.value == BrowserSortType.TYPE_RATE
                    ),
                    FilterEntity.OptionItem(
                        title = "发售日",
                        field = filterFieldOrderBy,
                        id = BrowserSortType.TYPE_DATE,
                        selected = sortType.value == BrowserSortType.TYPE_DATE
                    ),
                    FilterEntity.OptionItem(
                        title = "名称",
                        field = filterFieldOrderBy,
                        id = BrowserSortType.TYPE_TITLE,
                        selected = sortType.value == BrowserSortType.TYPE_TITLE
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