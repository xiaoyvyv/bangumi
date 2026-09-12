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
    @SerialName("illust_type") val illustType: String = "0",
    @SerialName("illust_page_count") val illustPageCount: String = "1",
    @SerialName("user_name") val userName: String = "",
    @SerialName("profile_img") val profileImg: String = "",
    @SerialName("illust_id") val illustId: Long = 0,
    @SerialName("width") val width: Int = 0,
    @SerialName("height") val height: Int = 0,
    @SerialName("user_id") val userId: Long = 0,
    @SerialName("rank") val rank: Int = 0,
    @SerialName("yes_rank") val yesRank: Int = 0,
    @SerialName("rating_count") val ratingCount: Int = 0,
    @SerialName("view_count") val viewCount: Int = 0,
    @SerialName("illust_upload_timestamp") val illustUploadTimestamp: Long = 0,
    @SerialName("is_masked") val isMasked: Boolean = false,
    @SerialName("is_bookmarked") val isBookmarked: Boolean = false,
    @SerialName("bookmarkable") val bookmarkable: Boolean = true,
    @SerialName("bookmark_id") val bookmarkId: Long = 0
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
    @SerialName("rank_total") val rankTotal: Int = 0
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
