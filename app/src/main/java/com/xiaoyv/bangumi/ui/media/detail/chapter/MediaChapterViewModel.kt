package com.xiaoyv.bangumi.ui.media.detail.chapter

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.bangumi.ui.media.detail.MediaDetailViewModel
import com.xiaoyv.common.api.response.api.ApiUserEpEntity
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.helper.FullQueryHelper

/**
 * Class: [MediaChapterViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class MediaChapterViewModel : BaseListViewModel<ApiUserEpEntity>() {
    lateinit var activityViewModel: MediaDetailViewModel

    /**
     * 媒体ID
     */
    internal var mediaId: String = ""

    @MediaType
    private val mediaType get() = activityViewModel.requireMediaType

    override suspend fun onRequestListImpl(): List<ApiUserEpEntity> {
        require(mediaId.isNotBlank()) { "媒体ID不存在" }
        return FullQueryHelper.requestAllEpisode(mediaId, mediaType)
    }
}