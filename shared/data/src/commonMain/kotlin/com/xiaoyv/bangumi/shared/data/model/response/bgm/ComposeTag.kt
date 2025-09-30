package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeTag(
    @SerialName("count") val count: Int = 0,
    @SerialName("name") val name: String = "",
    @SerialName("total_cont") val totalCont: Int = 0,
)