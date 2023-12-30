package com.xiaoyv.bangumi.ui.media.detail.comments

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.MediaCommentEntity
import com.xiaoyv.common.api.parser.impl.parserMediaComments
import com.xiaoyv.common.config.annotation.MediaDetailType

/**
 * Class: [MediaCommentViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class MediaCommentViewModel : BaseListViewModel<MediaCommentEntity>() {
    /**
     * 媒体ID
     */
    internal var mediaId: String = ""

    override suspend fun onRequestListImpl(): List<MediaCommentEntity> {
        require(mediaId.isNotBlank()) { "媒体ID不存在" }
        return BgmApiManager.bgmWebApi.queryMediaDetail(
            mediaId = mediaId,
            type = MediaDetailType.TYPE_RATING,
            page = current
        ).parserMediaComments()
    }
}