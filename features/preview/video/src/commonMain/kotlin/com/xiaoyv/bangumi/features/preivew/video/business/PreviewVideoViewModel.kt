package com.xiaoyv.bangumi.features.preivew.video.business

import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import kotlinx.collections.immutable.toImmutableMap

/**
 * [PreviewVideoViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class PreviewVideoViewModel(val args: Screen.PreviewVideo) : BaseViewModel<PreviewVideoState, PreviewVideoSideEffect, PreviewVideoEvent.Action>() {

    override fun createInitialState() = PreviewVideoState(
        url = args.videoUrl,
        headers = args.headers.toImmutableMap()
    )

    override fun onEvent(event: PreviewVideoEvent.Action) {
        when (event) {
            is PreviewVideoEvent.Action.OnRefresh -> refresh(event.loading)
        }
    }

}