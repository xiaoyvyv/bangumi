package com.xiaoyv.bangumi.features.report.business

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue
import com.xiaoyv.bangumi.shared.core.types.ReportReason
import com.xiaoyv.bangumi.shared.core.types.ReportType
import org.jetbrains.compose.resources.StringResource

/**
 * [ReportState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class ReportState(
    val title: StringResource,
    @field:ReportType val type: Int = ReportType.UNKNOWN,
    val reason: Int = ReportReason.UNKNOWN,
    val comment: TextFieldValue = TextFieldValue()
)
