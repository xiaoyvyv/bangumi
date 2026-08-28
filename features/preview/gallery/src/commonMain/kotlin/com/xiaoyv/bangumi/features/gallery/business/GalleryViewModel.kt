package com.xiaoyv.bangumi.features.gallery.business

import androidx.datastore.preferences.core.byteArrayPreferencesKey
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.reduceError
import com.xiaoyv.bangumi.shared.data.repository.CacheRepository
import com.xiaoyv.bangumi.shared.data.repository.readViewModelCache
import com.xiaoyv.bangumi.shared.data.repository.writeViewModelCache
import com.xiaoyv.bangumi.shared.data.usecase.ImageRepoUseCase
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import org.orbitmvi.orbit.syntax.Syntax

/**
 * [GalleryViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class GalleryViewModel(
    private val args: Screen.Gallery,
    private val imageRepoUseCase: ImageRepoUseCase,
    private val cacheRepository: CacheRepository,
) : BaseViewModel<GalleryState, GallerySideEffect, GalleryEvent.Action>() {

    private val cacheKey = byteArrayPreferencesKey(name = "gallery:${args.type}:${args.id}")

    override fun initBaseState() = readViewModelCache(
        cacheRepository = cacheRepository,
        cacheKey = cacheKey,
        loadWhenEmpty = true
    )

    override fun createInitialState() = GalleryState(
        id = args.id
    )

    override suspend fun Syntax<UiState<GalleryState>, UiSideEffect<GallerySideEffect>>.refreshSync() {
        imageRepoUseCase.fetchPictureGallery(args.id, args.type)
            .onFailure { reduceError { it } }
            .onSuccess {
                reduceData { state.copy(images = it) }
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
