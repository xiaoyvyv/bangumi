package com.xiaoyv.bangumi.features.main.tab.tracking.business

import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_anime
import com.xiaoyv.bangumi.core_resource.resources.global_book
import com.xiaoyv.bangumi.core_resource.resources.global_real
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf

/**
 * [TrackingViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class TrackingViewModel(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<TrackingState, TrackingSideEffect, TrackingEvent>(savedStateHandle) {

    override fun initSate(onCreate: Boolean) = TrackingState(
        tabs = persistentListOf(
            ComposeTextTab(SubjectType.ANIME, Res.string.global_anime),
            ComposeTextTab(SubjectType.BOOK, Res.string.global_book),
            ComposeTextTab(SubjectType.REAL, Res.string.global_real),
        )
    )

    override fun onEvent(event: TrackingEvent) {

    }
}