package com.xiaoyv.bangumi.ui.media.detail.review

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.MediaReviewBlogEntity
import com.xiaoyv.common.api.parser.impl.parserMediaBlog
import com.xiaoyv.common.config.annotation.MediaDetailType

/**
 * Class: [MediaReviewViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class MediaReviewViewModel : BaseListViewModel<MediaReviewBlogEntity>() {
    /**
     * 媒体ID
     */
    internal var mediaId: String = ""

    override suspend fun onRequestListImpl(): List<MediaReviewBlogEntity> {
        require(mediaId.isNotBlank()) { "媒体ID不存在" }
        return BgmApiManager.bgmWebApi.queryMediaDetail(
            mediaId = mediaId,
            type = MediaDetailType.TYPE_REVIEW,
            page = current
        ).parserMediaBlog()
    }
}