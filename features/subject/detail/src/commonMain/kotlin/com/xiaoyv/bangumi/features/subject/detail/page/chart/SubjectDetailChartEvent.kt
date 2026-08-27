package com.xiaoyv.bangumi.features.subject.detail.page.chart

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

sealed class SubjectDetailChartEvent {
    sealed class UI : SubjectDetailChartEvent() {
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : SubjectDetailChartEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
    }
}