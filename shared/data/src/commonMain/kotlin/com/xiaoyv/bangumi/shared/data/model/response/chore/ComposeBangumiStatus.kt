package com.xiaoyv.bangumi.shared.data.model.response.chore

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ComposeBangumiStatus(
    @SerialName("message") val message: String = "",
    @SerialName("status") val status: String = "",
    @SerialName("updated_at") val updatedAt: Long = 0L,
)
