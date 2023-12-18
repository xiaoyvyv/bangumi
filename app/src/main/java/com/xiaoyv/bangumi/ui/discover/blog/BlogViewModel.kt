package com.xiaoyv.bangumi.ui.discover.blog

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.BlogEntity
import com.xiaoyv.common.api.parser.impl.parserBlogList
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.helper.UserHelper

/**
 * Class: [BlogViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class BlogViewModel : BaseListViewModel<BlogEntity>() {
    internal var userId = ""
    internal var requireLogin = false

    @MediaType
    internal var mediaType: String = MediaType.TYPE_ANIME

    internal var tag = ""

    /**
     * 默认日志查询路径为 媒体的动漫
     */
    private var queryPath = MediaType.TYPE_ANIME

    /**
     * 日志拼接路径
     */
    private var tagPath = ""

    /**
     * 是否为自己的帖子列表
     */
    internal val isMine: Boolean
        get() = userId.isNotBlank() && userId == UserHelper.currentUser.id

    override suspend fun onRequestListImpl(): List<BlogEntity> {
        buildQueryTagPath()

        if (requireLogin) {
            require(UserHelper.isLogin) { "你还没有登录呢" }
        }

        return BgmApiManager.bgmWebApi.queryBlogList(queryPath, tagPath, current)
            .parserBlogList(userId.isNotBlank(), isMine)
    }

    /**
     * 构建查询和标签路径
     */
    private fun buildQueryTagPath() {
        // 直接拼接用户ID或媒体类型的字符串
        queryPath = if (userId.isNotBlank()) "user/$userId" else mediaType

        // TAG 路径
        tagPath = if (tag.isNotBlank()) "/tag/$tag" else ""
    }
}