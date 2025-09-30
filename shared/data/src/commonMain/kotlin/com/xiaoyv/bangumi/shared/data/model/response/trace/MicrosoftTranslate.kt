package com.xiaoyv.bangumi.shared.data.model.response.trace

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MicrosoftTranslate(
    @SerialName("detectedLanguage") val detectedLanguage: DetectedLanguage = DetectedLanguage(),
    @SerialName("translations") val translations: SerializeList<Translation> = persistentListOf(),
) {
    @Immutable
    @Serializable
    data class DetectedLanguage(
        @SerialName("language") val language: String = "",
        @SerialName("score") val score: Double = 0.0,
    )

    @Serializable
    data class Translation(
        @SerialName("text") val text: String = "",
        @SerialName("to") val to: String = "",
    )
}