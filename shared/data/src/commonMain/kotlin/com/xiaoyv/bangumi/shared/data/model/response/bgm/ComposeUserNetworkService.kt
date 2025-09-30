package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeUserNetworkService(
    @SerialName("account") val account: String = "",
    @SerialName("color") val color: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("title") val title: String = "",
    @SerialName("url") val url: String = "",
)