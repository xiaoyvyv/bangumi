package com.xiaoyv.bangumi.special.picture.gallery

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.response.GalleryEntity
import com.xiaoyv.widget.kts.orEmpty

/**
 * AnimeGalleryViewModel
 *
 * @author why
 * @since 11/19/23
 */
class AnimeGalleryViewModel : BaseListViewModel<GalleryEntity>() {

    /**
     * 豆瓣媒体画廊
     */
    var douBanPhotoId: String = ""

    /**
     * 页数大小
     */
    private var pageSize = 30

    internal val isPreviewSubject: Boolean
        get() = douBanPhotoId.isNotBlank()

    override suspend fun onRequestListImpl(): List<GalleryEntity> {
        return when {
            // 豆瓣画廊
            isPreviewSubject -> {
                BgmApiManager.bgmJsonApi.queryDouBanPhotoList(
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

            else -> {
                BgmApiManager.bgmJsonApi
                    .queryAnimePicture(page = (current - 1).coerceAtLeast(0))
                    .posts.orEmpty()
                    .map {
                        GalleryEntity(
                            id = it.id,
                            width = it.width,
                            height = it.height,
                            size = it.size,
                            imageUrl = it.url,
                            largeImageUrl = it.largeUrl
                        )
                    }
            }
        }
    }
}
