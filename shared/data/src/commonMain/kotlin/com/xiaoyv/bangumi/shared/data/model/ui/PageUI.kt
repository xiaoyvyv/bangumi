package com.xiaoyv.bangumi.shared.data.model.ui

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class PageUI(
    @SerialName("gridLayout") val gridLayout: Boolean = false,
    @SerialName("pageMode") val pageMode: Boolean = false,
    @SerialName("showSummary") val showSummary: Boolean = false,
)
