package com.xiaoyv.bangumi.shared.data.model.response.pixiv.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param novels
 * @param nextUrl
 * @param searchSpanLimit
 * @param showAi
 */
@Serializable

data class SearchNovel(

    @SerialName(value = "novels")
    val novels: List<NovelInfo>? = null,

    @SerialName(value = "next_url")
    val nextUrl: String = "",

    @SerialName(value = "search_span_limit")
    val searchSpanLimit: Int = 0,

    @SerialName(value = "show_ai")
    val showAi: Boolean? = null

)

