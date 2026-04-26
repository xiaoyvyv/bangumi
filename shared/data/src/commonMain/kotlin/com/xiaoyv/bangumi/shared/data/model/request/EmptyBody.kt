package com.xiaoyv.bangumi.shared.data.model.request

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class EmptyBody(@SerialName("data") val data: String? = null)
