package com.xiaoyv.bangumi.features.subject.detail.page.chart

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubjectStats
import kotlinx.serialization.SerialName

@Immutable
data class SubjectDetailChartState(
    @SerialName("stats") val stats: ComposeSubjectStats = ComposeSubjectStats.Empty,
)