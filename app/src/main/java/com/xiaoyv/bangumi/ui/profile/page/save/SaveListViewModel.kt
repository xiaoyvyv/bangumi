package com.xiaoyv.bangumi.ui.profile.page.save

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.BrowserEntity
import com.xiaoyv.common.api.parser.impl.BrowserParser.parserBrowserPage
import com.xiaoyv.common.config.annotation.BrowserSortType
import com.xiaoyv.common.config.annotation.InterestCollectType
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.helper.UserHelper

/**
 * Class: [SaveListViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class SaveListViewModel : BaseListViewModel<BrowserEntity.Item>() {

    internal var listType = InterestCollectType.TYPE_WISH
    internal var userId = ""
    internal var requireLogin = false

    @BrowserSortType
    private var sortType: String = BrowserSortType.TYPE_DEFAULT

    @MediaType
    internal var mediaType: String = MediaType.TYPE_ANIME

    override suspend fun onRequestListImpl(): List<BrowserEntity.Item> {
        if (requireLogin) require(UserHelper.isLogin) { "你还没有登录呢" }
        return BgmApiManager.bgmWebApi.queryUserCollect(
            mediaType = mediaType,
            userId = userId,
            listType = listType,
            sortType = sortType,
            page = current
        ).parserBrowserPage(mediaType = mediaType).items
    }
}