package com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeTimelineSource(
    @SerialName("name") val name: String = "",
    @SerialName("url") val url: String = "",
) {
    companion object {
        val Empty = ComposeTimelineSource()
    }
}