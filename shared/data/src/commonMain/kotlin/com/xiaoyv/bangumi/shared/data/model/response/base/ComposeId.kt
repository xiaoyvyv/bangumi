package com.xiaoyv.bangumi.shared.data.model.response.base

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeId(
    @SerialName(value = "id") val id: Long
)