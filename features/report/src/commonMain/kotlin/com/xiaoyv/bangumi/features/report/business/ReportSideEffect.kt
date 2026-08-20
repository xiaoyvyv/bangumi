package com.xiaoyv.bangumi.features.report.business

/**
 * [ReportSideEffect]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class ReportSideEffect {
    data object OnReportSuccess : ReportSideEffect()
}