package com.xiaoyv.bangumi.features.tag.detail.business

import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [TagDetailViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class TagDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val args: Screen.TagDetail,
) : BaseViewModel<TagDetailState, TagDetailSideEffect, TagDetailEvent.Action>(savedStateHandle) {

    override fun initSate(onCreate: Boolean) = TagDetailState(
        type = args.type
    )

    override fun onEvent(event: TagDetailEvent.Action) {
        when (event) {
            is TagDetailEvent.Action.OnRefresh -> refresh(loading = event.loading)
        }
    }
}