package com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Immutable
@Serializable
data class ComposeTimelineNickname(
    @SerialName("after") val after: String = "",
    @SerialName("before") val before: String = "",
) {
    companion object {
        val Empty = ComposeTimelineNickname()
    }
}