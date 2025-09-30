package com.xiaoyv.bangumi.shared.data.model.request.list.topic

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [TopicSearchBody]
 *
 * @author why
 * @since 2025/1/25
 */
@Immutable
@Serializable
data class TopicSearchBody(
    @SerialName("keyword") val keyword: String,
    @SerialName("exact") val exact: Boolean = false,
    @SerialName("order") val order: String = "time_date",
) {
    companion object Companion {
        val Empty = TopicSearchBody(keyword = "")
    }
}