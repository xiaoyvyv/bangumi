package com.xiaoyv.bangumi.features.subject.page.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [SubjectPageEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class SubjectPageEvent {
    sealed class UI : SubjectPageEvent() {
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : SubjectPageEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
    }
}