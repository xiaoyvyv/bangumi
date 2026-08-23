package com.xiaoyv.bangumi.features.pixiv.tag.main.business

import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.PageStatus
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.reduceError
import com.xiaoyv.bangumi.shared.data.repository.PixivRepository
import org.orbitmvi.orbit.syntax.Syntax

/**
 * Loads the metadata displayed in a Pixiv tag header.
 */
class PixivTagViewModel(
    private val tag: String,
    private val pixivRepository: PixivRepository,
) : BaseViewModel<PixivTagState, PixivTagSideEffect, PixivTagEvent.Action>() {

    override fun initBaseState(): UiState<PixivTagState> =
        UiState(data = createInitialState(), status = PageStatus.Loading)

    override fun createInitialState() = PixivTagState(tag = tag)

    override suspend fun Syntax<UiState<PixivTagState>, UiSideEffect<PixivTagSideEffect>>.refreshSync() {
        if (state.data.tag.isBlank()) return

        pixivRepository.fetchTagInfo(state.data.tag)
            .onFailure { reduceError { it } }
            .onSuccess { tagInfo ->
                reduceData { state.copy(tagInfo = tagInfo) }
            }
    }

    override fun onEvent(event: PixivTagEvent.Action) {
        when (event) {
            is PixivTagEvent.Action.OnRefresh -> refresh(loading = event.loading)
        }
    }
}
