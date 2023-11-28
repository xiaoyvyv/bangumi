package com.xiaoyv.bangumi.ui.media.detail.chapter

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.MediaChapterEntity
import com.xiaoyv.common.api.parser.impl.parserChapter
import com.xiaoyv.common.config.annotation.MediaDetailType

/**
 * Class: [MediaChapterViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class MediaChapterViewModel : BaseListViewModel<MediaChapterEntity>() {
    /**
     * 媒体ID
     */
    internal var mediaId: String = ""

    override suspend fun onRequestListImpl(): List<MediaChapterEntity> {
        require(mediaId.isNotBlank()) { "媒体ID不存在" }
        return BgmApiManager.bgmWebApi.queryMediaDetail(mediaId, MediaDetailType.TYPE_CHAPTER)
            .parserChapter()
    }
}