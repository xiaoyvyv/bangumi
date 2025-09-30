package com.xiaoyv.bangumi.features.preivew.gallery.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [PreviewTextEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class PreviewTextEvent {
    sealed class UI : PreviewTextEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : PreviewTextEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data object OnToggleTranslate : Action()
    }
}