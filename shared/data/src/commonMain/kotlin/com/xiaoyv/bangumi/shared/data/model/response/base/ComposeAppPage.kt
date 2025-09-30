package com.xiaoyv.bangumi.shared.data.model.response.base

import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ComposeAppResponse<T>(
    @SerialName("data") val `data`: T,
    @SerialName("code") val code: Int = 0,
)

@Serializable
data class ComposeAppPage<T>(
    @SerialName("current") val current: Int = 0,
    @SerialName("pages") val pages: Int = 0,
    @SerialName("records") val records: SerializeList<T> = persistentListOf(),
    @SerialName("searchCount") val searchCount: Boolean = false,
    @SerialName("size") val size: Int = 0,
    @SerialName("total") val total: Int = 0,
)
