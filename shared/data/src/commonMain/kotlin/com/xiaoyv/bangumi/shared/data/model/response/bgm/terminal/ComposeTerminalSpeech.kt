package com.xiaoyv.bangumi.shared.data.model.response.bgm.terminal

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeTerminalSpeech(
    @SerialName("id") val id: Long = 0,
    @SerialName("speech") val speech: String = "",
    @SerialName("creator") val creator: String? = null,
    @SerialName("createdAt") val createdAt: Long? = null,
)
