package com.xiaoyv.bangumi.special.picture.gallery

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.response.ImageGalleryEntity

/**
 * AnimeGalleryViewModel
 *
 * @author why
 * @since 11/19/23
 */
class AnimeGalleryViewModel : BaseListViewModel<ImageGalleryEntity.Post>() {

    override suspend fun onRequestListImpl(): List<ImageGalleryEntity.Post> {
        // https://cpreview.anime-pictures.net/f4f/f4fb2fa5b021f27cffc6b7d51e2ca8e1_bp.jpg.avif
        // https://cpreview.anime-pictures.net/f4f/f4fb2fa5b021f27cffc6b7d51e2ca8e1_bp.jpg.avif
        // https://cimages.anime-pictures.net/f4f/f4fb2fa5b021f27cffc6b7d51e2ca8e1.jpg
        return BgmApiManager.bgmJsonApi
            .queryAnimePicture(page = current)
            .posts.orEmpty()
    }
}
