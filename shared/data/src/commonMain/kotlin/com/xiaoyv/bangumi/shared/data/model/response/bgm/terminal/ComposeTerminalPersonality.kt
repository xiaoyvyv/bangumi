package com.xiaoyv.bangumi.shared.data.model.response.bgm.terminal

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeTerminalPersonality(
    @SerialName("id") val id: Long = 0,
    @SerialName("name") val name: String = "",
    @SerialName("speechCount") val speechCount: Int = 0,
    @SerialName("creator") val creator: String = "",
    @SerialName("createdAt") val createdAt: Long = 0,
)
