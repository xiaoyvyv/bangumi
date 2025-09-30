package com.xiaoyv.bangumi.features.mono.detail.business

import com.xiaoyv.bangumi.shared.core.types.MonoDetailTab
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [MonoDetailEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class MonoDetailEvent {
    sealed class UI : MonoDetailEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
        data class OnSelectedPageType(@field:MonoDetailTab val page: Int) : UI()
    }

    sealed class Action : MonoDetailEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data object OnToggleBookmarkMono : Action()
    }
}