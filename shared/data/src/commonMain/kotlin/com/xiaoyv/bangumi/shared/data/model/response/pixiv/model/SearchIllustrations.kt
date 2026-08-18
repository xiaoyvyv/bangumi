package com.xiaoyv.bangumi.shared.data.model.response.pixiv.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param illusts
 * @param nextUrl
 * @param searchSpanLimit
 * @param showAi
 */
@Serializable
data class SearchIllustrations(
    @SerialName(value = "illusts")
    val illusts: List<IllustrationInfo>? = null,

    @SerialName(value = "next_url")
    val nextUrl: String = "",

    @SerialName(value = "search_span_limit")
    val searchSpanLimit: Int = 0,

    @SerialName(value = "show_ai")
    val showAi: Boolean? = null
)

