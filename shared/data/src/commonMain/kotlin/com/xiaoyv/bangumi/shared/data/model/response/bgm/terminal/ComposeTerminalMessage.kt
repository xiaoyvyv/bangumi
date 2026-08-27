package com.xiaoyv.bangumi.shared.data.model.response.bgm.terminal

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeTerminalMessage(
    @SerialName("message") val message: String = "",
)
