package com.xiaoyv.bangumi.ui.media.detail.preview

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.response.GalleryEntity
import com.xiaoyv.widget.kts.orEmpty

/**
 * Class: [MediaPreviewViewModel]
 *
 * @author why
 * @since 1/12/24
 */
class MediaPreviewViewModel : BaseListViewModel<GalleryEntity>() {

    /**
     * 关键词
     */
    var douBanPhotoId: String = ""

    var mediaName: String = ""

    /**
     * 页数大小
     */
    private var pageSize = 30

    override suspend fun onRequestListImpl(): List<GalleryEntity> {
        require(douBanPhotoId.isNotBlank()) { "暂无预览数据" }

        return BgmApiManager.bgmJsonApi.queryDouBanPhotoList(
            mediaId = douBanPhotoId,
            start = pageSize * (current - 1),
            count = pageSize
        ).photos
            .orEmpty()
            .map {
                val availableImage = it.image?.availableImage
                val normalImage = it.image?.normal
                GalleryEntity(
                    id = it.id,
                    width = availableImage?.width.orEmpty(),
                    height = availableImage?.height.orEmpty(),
                    size = availableImage?.size ?: 0L,
                    imageUrl = normalImage?.url.orEmpty(),
                    largeImageUrl = availableImage?.url.orEmpty()
                )
            }
    }
}