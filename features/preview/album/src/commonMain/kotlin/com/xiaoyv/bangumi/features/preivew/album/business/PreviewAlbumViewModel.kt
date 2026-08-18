package com.xiaoyv.bangumi.features.preivew.album.business

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.data.model.request.list.album.ListAlbumParam
import com.xiaoyv.bangumi.shared.data.repository.ImageRepository
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun rememberPreviewAlbumViewModel(param: ListAlbumParam): PreviewAlbumViewModel {
    return koinViewModel(
        key = param.uniqueKey,
        parameters = { parametersOf(param) }
    )
}

/**
 * [PreviewAlbumViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class PreviewAlbumViewModel(
    private val param: ListAlbumParam,
    private val imageRepository: ImageRepository,
) : BaseViewModel<PreviewAlbumState, PreviewAlbumSideEffect, PreviewAlbumEvent.Action>() {
    private val albumPager = imageRepository.fetchAlbumPager(param)

    val album = albumPager.flow.cachedIn(viewModelScope)

    override fun createInitialState() = PreviewAlbumState(
        type = param.type
    )

    override fun onEvent(event: PreviewAlbumEvent.Action) {
        when (event) {
            is PreviewAlbumEvent.Action.OnRefresh -> refresh(loading = event.loading)
        }
    }

}