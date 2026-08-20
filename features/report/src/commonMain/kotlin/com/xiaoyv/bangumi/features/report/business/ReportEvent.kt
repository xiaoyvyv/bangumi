package com.xiaoyv.bangumi.features.report.business

import androidx.compose.ui.text.input.TextFieldValue
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [ReportEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class ReportEvent {
    sealed class UI : ReportEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : ReportEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data class OnReasonChange(val reason: Int) : Action()
        data class OnCommentChange(val comment: TextFieldValue) : Action()
        data object OnReport : Action()
    }
}