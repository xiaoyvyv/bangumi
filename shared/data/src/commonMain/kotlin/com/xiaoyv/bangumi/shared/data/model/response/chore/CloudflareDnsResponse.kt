package com.xiaoyv.bangumi.shared.data.model.response.chore

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class CloudflareDnsResponse(
    @SerialName("Status") val status: Int = -1,
    @SerialName("Answer") val answers: List<Answer> = emptyList(),
) {
    @Immutable
    @Serializable
    data class Answer(
        @SerialName("type") val type: Int = 0,
        @SerialName("data") val data: String = "",
    )
}
