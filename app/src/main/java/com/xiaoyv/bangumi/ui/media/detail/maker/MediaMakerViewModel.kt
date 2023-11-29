package com.xiaoyv.bangumi.ui.media.detail.maker

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.MediaMakerEntity
import com.xiaoyv.common.api.parser.impl.parserMediaMakers
import com.xiaoyv.common.config.annotation.MediaDetailType

/**
 * Class: [MediaMakerViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class MediaMakerViewModel : BaseListViewModel<MediaMakerEntity>() {
    /**
     * 媒体ID
     */
    internal var mediaId: String = ""

    override suspend fun onRequestListImpl(): List<MediaMakerEntity> {
        require(mediaId.isNotBlank()) { "媒体ID不存在" }
        return BgmApiManager.bgmWebApi.queryMediaDetail(
            mediaId = mediaId,
            type = MediaDetailType.TYPE_MAKER,
            page = current
        ).parserMediaMakers()
    }
}