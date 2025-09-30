package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeUnRead(
    /**
     * 未登录会返回 null
     */
    @SerialName("count") val count: Int? = null,
) {
    companion object {
        val Empty = ComposeUnRead()
    }
}
