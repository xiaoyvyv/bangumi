@file:OptIn(ExperimentalSerializationApi::class)

package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.System
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Immutable
@Serializable
data class ComposeStatus(
    @SerialName("status") val status: String = "",
    @SerialName("id") val id: Long = 0,
)


@Immutable
@Serializable
data class ComposeEmptyBody(
    @SerialName("_") val seq: Long = System.currentTimeMillis(),
) {
    companion object {
        val Empty = ComposeEmptyBody()
    }
}