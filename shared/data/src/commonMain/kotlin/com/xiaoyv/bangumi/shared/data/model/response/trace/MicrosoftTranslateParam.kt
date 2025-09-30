package com.xiaoyv.bangumi.shared.data.model.response.trace

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MicrosoftTranslateParam(
    @SerialName("Text") val text: String? = null,
)