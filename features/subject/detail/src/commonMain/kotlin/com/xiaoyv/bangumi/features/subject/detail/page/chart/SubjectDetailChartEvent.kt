package com.xiaoyv.bangumi.features.subject.detail.page.chart

import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailEvent
import com.xiaoyv.bangumi.shared.core.types.CollectionEpisodeType
import com.xiaoyv.bangumi.shared.core.types.SubjectDetailTab
import com.xiaoyv.bangumi.shared.data.model.request.CollectionSubjectUpdate
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeEpisode
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

sealed class SubjectDetailChartEvent {
    sealed class UI : SubjectDetailChartEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
        data class OnSelectedPageType(@field:SubjectDetailTab val tab: Int) : UI()
    }

    sealed class Action : SubjectDetailChartEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data object DeleteCollection : Action()

        data class OnUpdateSubjectCollection(val update: CollectionSubjectUpdate, val showLoadingDialog: Boolean) : Action()

        data class OnUpdateEpisodeCollection(
            val episodes: List<ComposeEpisode>,
            @field:CollectionEpisodeType val type: Int,
        ) : Action()

    }
}