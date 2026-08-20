package com.xiaoyv.bangumi.shared.data.model.response.trace

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Immutable
@Serializable
data class MicrosoftTranslate(
    @SerialName("detectedLanguage") val detectedLanguage: DetectedLanguage? = null,
    @SerialName("translations") val translations: SerializeList<Translation> = persistentListOf(),
) {
    @Immutable
    @Serializable
    data class DetectedLanguage(
        @SerialName("language") val language: String = "",
        @SerialName("score") val score: Double = 0.0,
    )

    @Immutable
    @Serializable
    data class Translation(
        @SerialName("text") val text: String = "",
        @SerialName("to") val to: String = "",
        @SerialName("sentLen") val sentLen: SentLen? = null,
    ) {
        @Immutable
        @Serializable
        data class SentLen(
            @SerialName("srcSentLen") val srcSentLen: SerializeList<Int> = persistentListOf(),
            @SerialName("transSentLen") val transSentLen: SerializeList<Int> = persistentListOf(),
        )
    }
}