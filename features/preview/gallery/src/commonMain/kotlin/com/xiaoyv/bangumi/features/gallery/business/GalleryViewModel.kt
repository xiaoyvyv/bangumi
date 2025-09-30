package com.xiaoyv.bangumi.features.gallery.business

import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.features.gallery.GalleryArguments
import com.xiaoyv.bangumi.shared.core.mvi.BaseSyntax
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.data.repository.CacheRepository
import com.xiaoyv.bangumi.shared.data.repository.readViewModelCache
import com.xiaoyv.bangumi.shared.data.repository.writeViewModelCache
import com.xiaoyv.bangumi.shared.data.usecase.ImageRepoUseCase

/**
 * [GalleryViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class GalleryViewModel(
    savedStateHandle: SavedStateHandle,
    private val imageRepoUseCase: ImageRepoUseCase,
    private val cacheRepository: CacheRepository,
) : BaseViewModel<GalleryState, GallerySideEffect, GalleryEvent.Action>(savedStateHandle) {
    private val args = GalleryArguments(savedStateHandle)

    private val cacheKey = stringPreferencesKey(name = "gallery_${args.type}_" + args.id)

    override fun initBaseState() = readViewModelCache(
        cacheRepository = cacheRepository,
        cacheKey = cacheKey,
        loadWhenEmpty = true
    )

    override fun initSate(onCreate: Boolean) = GalleryState(
        id = args.id
    )

    override suspend fun BaseSyntax<GalleryState, GallerySideEffect>.refreshSync() {
        imageRepoUseCase.fetchPictureGallery(args.id, args.type)
            .onFailure { reduceError { it } }
            .onSuccess {
                reduceContent { state.copy(images = it) }
            }
            .onSuccess {
                writeViewModelCache(
                    cacheRepository = cacheRepository,
                    cacheKey = cacheKey,
                )
            }
    }

    override fun onEvent(event: GalleryEvent.Action) {

    }
}