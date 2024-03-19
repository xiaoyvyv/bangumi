package com.xiaoyv.bangumi.special.yuc.detail

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.BrowserEntity
import com.xiaoyv.common.api.parser.impl.BrowserParser.parserBrowserPage
import com.xiaoyv.common.config.annotation.BrowserSortType
import com.xiaoyv.common.config.annotation.MediaType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [YucDetailViewModel]
 *
 * @author why
 * @since 3/19/24
 */
class YucDetailViewModel : BaseListViewModel<BrowserEntity.Item>() {
    internal var yearMonth: String = "2024-4"

    @MediaType
    internal val mediaType: String = MediaType.TYPE_ANIME

    @BrowserSortType
    internal var sortType = BrowserSortType.TYPE_DEFAULT

    override suspend fun onRequestListImpl(): List<BrowserEntity.Item> {
        val response = withContext(Dispatchers.IO) {
            BgmApiManager.bgmWebApi.browserRank(
                mediaType = mediaType,
                subPath = "tv/airtime/$yearMonth",
                page = current,
                sortType = sortType
            ).parserBrowserPage(mediaType)
        }
        return response.items
    }

    /**
     * Yuc 对应的新番记录
     */
    fun buildYucUrl(): String {
        val split = yearMonth.split("-")
        val year = split.getOrNull(0).orEmpty()
        val month = split.getOrNull(1).orEmpty()
            .let { if (it.length == 1) "0$it" else it }
        return "https://yuc.wiki/$year$month"
    }
}