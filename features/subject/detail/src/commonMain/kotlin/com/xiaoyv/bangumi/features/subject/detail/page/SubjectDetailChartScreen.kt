package com.xiaoyv.bangumi.features.subject.detail.page

import androidx.compose.runtime.Composable
import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailState
import com.xiaoyv.bangumi.features.subject.detail.page.chart.SubjectDetailChartRoute


/**
 * [SubjectDetailTopicScreen]
 *
 * @since 2025/5/11
 */
@Composable
fun SubjectDetailChartScreen(state: SubjectDetailState) {
    SubjectDetailChartRoute(state.subject.id)
}

