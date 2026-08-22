package com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposePixivRankingContent(
    @SerialName("title") val title: String = "",
    @SerialName("date") val date: String = "",
    @SerialName("tags") val tags: SerializeList<String> = persistentListOf(),
    @SerialName("url") val url: String = "",
    @SerialName("illust_type") val illust_type: String = "0",
    @SerialName("illust_page_count") val illust_page_count: String = "1",
    @SerialName("user_name") val user_name: String = "",
    @SerialName("profile_img") val profile_img: String = "",
    @SerialName("illust_id") val illust_id: Long = 0,
    @SerialName("width") val width: Int = 0,
    @SerialName("height") val height: Int = 0,
    @SerialName("user_id") val user_id: Long = 0,
    @SerialName("rank") val rank: Int = 0,
    @SerialName("yes_rank") val yes_rank: Int = 0,
    @SerialName("rating_count") val rating_count: Int = 0,
    @SerialName("view_count") val view_count: Int = 0,
    @SerialName("illust_upload_timestamp") val illust_upload_timestamp: Long = 0,
    @SerialName("is_masked") val is_masked: Boolean = false,
    @SerialName("is_bookmarked") val is_bookmarked: Boolean = false,
    @SerialName("bookmarkable") val bookmarkable: Boolean = true,
    @SerialName("bookmark_id") val bookmark_id: Long = 0
) {
    companion object {
        val Empty = ComposePixivRankingContent()
    }
}

@Immutable
@Serializable
data class ComposePixivRankingResponse(
    @SerialName("contents") val contents: SerializeList<ComposePixivRankingContent> = persistentListOf(),
    @SerialName("mode") val mode: String = "",
    @SerialName("content") val content: String = "",
    @SerialName("page") val page: Int = 1,
    @SerialName("date") val date: String = "",
    @SerialName("rank_total") val rank_total: Int = 0
) {
    companion object {
        val Empty = ComposePixivRankingResponse()
    }
}

@Immutable
@Serializable
data class ComposePixivNovelRankingBody(
    @SerialName("works") val works: SerializeList<ComposePixivNovelSimple> = persistentListOf(),
    @SerialName("total") val total: Int = 0
) {
    companion object {
        val Empty = ComposePixivNovelRankingBody()
    }
}
