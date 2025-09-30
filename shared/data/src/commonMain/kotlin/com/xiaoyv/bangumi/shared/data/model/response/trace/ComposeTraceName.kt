package com.xiaoyv.bangumi.shared.data.model.response.trace

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [ComposeTraceName]
 *
 * @since 2025/5/15
 */
@Immutable
@Serializable
data class ComposeTraceName(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("synonyms")
    val synonyms: List<String>? = null,
    @SerialName("title")
    val title: String? = "",
)