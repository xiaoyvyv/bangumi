package com.xiaoyv.bangumi.ui.media.detail.board

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.MediaBoardEntity
import com.xiaoyv.common.api.parser.impl.parserMediaBoards
import com.xiaoyv.common.config.annotation.MediaDetailType

/**
 * Class: [MediaBoardViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class MediaBoardViewModel : BaseListViewModel<MediaBoardEntity>() {
    /**
     * 媒体ID
     */
    internal var mediaId: String = ""

    override suspend fun onRequestListImpl(): List<MediaBoardEntity> {
        require(mediaId.isNotBlank()) { "媒体ID不存在" }
        return BgmApiManager.bgmWebApi.queryMediaDetail(
            mediaId = mediaId,
            type = MediaDetailType.TYPE_TOPIC,
            page = current
        ).parserMediaBoards()
    }
}