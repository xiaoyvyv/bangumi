package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeMembership(
    @SerialName("joinedAt") val joinedAt: Long = 0,
    @SerialName("role") val role: Int = 0,
    @SerialName("uid") val uid: Int = 0,
) {
    companion object {
        val Empty = ComposeMembership()
    }
}