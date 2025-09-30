package com.xiaoyv.bangumi.features.tag.page.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [TagPageEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class TagPageEvent {
    sealed class UI : TagPageEvent() {
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : TagPageEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
    }
}