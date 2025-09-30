package com.xiaoyv.bangumi.shared.data.repository

import androidx.paging.Pager
import com.xiaoyv.bangumi.shared.data.model.request.list.album.ListAlbumParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMono
import com.xiaoyv.bangumi.shared.data.model.response.image.ComposeGallery

/**
 * [ImageRepository]
 *
 * @since 2025/5/22
 */
interface ImageRepository {

    fun fetchAlbumPager(param: ListAlbumParam): Pager<Int, ComposeGallery>

    fun fetchAnimePictures(
        searchTags: String? = null,
        deniedTags: String? = null,
    ): Pager<Int, ComposeGallery>

    fun fetchPixivPictures(tag: String): Pager<Int, ComposeGallery>

    suspend fun fetchAlbumList(param: ListAlbumParam, page: Int, size: Int): Result<List<ComposeGallery>>

    suspend fun fetchPixivPictureDetail(id: String): Result<List<ComposeGallery>>

    suspend fun fetchAnimePictureTag(data: ComposeMono): Result<List<String>>
}