package com.xiaoyv.bangumi.features.tag.detail.business

import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [TagDetailViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class TagDetailViewModel(
    private val args: Screen.TagDetail,
) : BaseViewModel<TagDetailState, TagDetailSideEffect, TagDetailEvent.Action>() {

    override fun createInitialState() = TagDetailState(
        type = args.type
    )

    override fun onEvent(event: TagDetailEvent.Action) {
        when (event) {
            is TagDetailEvent.Action.OnRefresh -> refresh(contentLoading = event.loading)
        }
    }
}