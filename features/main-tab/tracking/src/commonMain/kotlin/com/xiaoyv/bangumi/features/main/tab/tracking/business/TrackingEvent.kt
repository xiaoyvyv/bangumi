package com.xiaoyv.bangumi.features.main.tab.tracking.business

import com.xiaoyv.bangumi.shared.data.model.request.bgm.CollectionSubjectParam
import com.xiaoyv.bangumi.shared.data.model.request.bgm.CollectionSubjectProgressParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeEpisode
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [TrackingEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class TrackingEvent {
    sealed class UI : TrackingEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : TrackingEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data class OnUpdateSubjectCollection(val subject: ComposeSubject, val param: CollectionSubjectParam) : Action()
        data class OnUpdateSubjectProgress(val subject: ComposeSubject, val param: CollectionSubjectProgressParam) : Action()
        data class OnUpdateEpisode(val subject: ComposeSubject, val eps: List<ComposeEpisode>, val type: Int) : Action()
    }
}