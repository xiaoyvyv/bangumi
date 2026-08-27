package com.xiaoyv.bangumi.shared.data.model.request.bgm

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class NextWebLoginParam(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("turnstileToken") val turnstileToken: String
)