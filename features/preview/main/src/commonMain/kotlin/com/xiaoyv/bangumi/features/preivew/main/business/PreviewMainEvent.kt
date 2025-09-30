package com.xiaoyv.bangumi.features.preivew.main.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [PreviewMainEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class PreviewMainEvent {
    sealed class UI : PreviewMainEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : PreviewMainEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data class OnPageSelected(val index: Int) : Action()
    }
}