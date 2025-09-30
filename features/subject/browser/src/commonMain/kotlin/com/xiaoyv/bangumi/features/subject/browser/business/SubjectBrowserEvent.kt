package com.xiaoyv.bangumi.features.subject.browser.business

import com.xiaoyv.bangumi.shared.data.model.request.list.subject.SubjectBrowserBody
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [SubjectBrowserEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class SubjectBrowserEvent {
    sealed class UI : SubjectBrowserEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : SubjectBrowserEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data class OnUpdateBrowserSubjectParam(val body: SubjectBrowserBody) : Action()
        data object OnChangeLayoutMode : Action()
    }
}