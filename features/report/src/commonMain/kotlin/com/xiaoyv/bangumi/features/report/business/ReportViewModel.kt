package com.xiaoyv.bangumi.features.report.business

import androidx.compose.ui.text.input.TextFieldValue
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.action_report_other_empty_tip
import com.xiaoyv.bangumi.core_resource.resources.action_report_select_one
import com.xiaoyv.bangumi.core_resource.resources.action_report_submit_success
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.postEffect
import com.xiaoyv.bangumi.shared.core.mvi.postToast
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.withActionLoading
import com.xiaoyv.bangumi.shared.core.types.ReportReason
import com.xiaoyv.bangumi.shared.core.types.ReportType
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.data.repository.UserRepository
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import org.jetbrains.compose.resources.getString

/**
 * [ReportViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class ReportViewModel(
    private val args: Screen.Report,
    private val userRepository: UserRepository,
) : BaseViewModel<ReportState, ReportSideEffect, ReportEvent.Action>() {

    override fun createInitialState() = ReportState(title = ReportType.stringRes(args.type))

    override fun onEvent(event: ReportEvent.Action) {
        when (event) {
            is ReportEvent.Action.OnRefresh -> refresh(loading = event.loading)
            is ReportEvent.Action.OnReasonChange -> onReasonChange(event.reason)
            is ReportEvent.Action.OnCommentChange -> onCommentChange(event.comment)
            ReportEvent.Action.OnReport -> onReport()
        }
    }

    private fun onReasonChange(reason: Int) = intent {
        reduceData { state.copy(reason = reason) }
    }

    private fun onCommentChange(comment: TextFieldValue) = intent {
        reduceData { state.copy(comment = comment) }
    }

    private fun onReport() = intent {
        if (state.data.reason == ReportReason.UNKNOWN) {
            postToast { getString(Res.string.action_report_select_one) }
            return@intent
        }
        val commentText = state.data.comment.text.trim()
        if (state.data.reason == ReportReason.OTHER && commentText.isBlank()) {
            postToast { getString(Res.string.action_report_other_empty_tip) }
            return@intent
        }

        withActionLoading {
            userRepository.submitReport(
                type = args.type,
                id = args.targetId,
                reason = state.reason,
                comment = commentText,
            )
        }.onFailure {
            postToast { it.errMsg }
        }.onSuccess {
            postToast { getString(Res.string.action_report_submit_success) }

            postEffect { ReportSideEffect.OnReportSuccess }
        }
    }
}