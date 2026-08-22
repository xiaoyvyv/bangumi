package com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposePixivIllustSeriesBody(
    @SerialName("illust_series_id") val illustSeriesId: Long = 0,
    @SerialName("illust_series_user_id") val illustSeriesUserId: Long = 0,
    @SerialName("illust_series_title") val illustSeriesTitle: String = "",
    @SerialName("illust_series_caption") val illustSeriesCaption: String = "",
    @SerialName("illust_series_content_count") val illustSeriesContentCount: String = "",
    @SerialName("illust_series_create_datetime") val illustSeriesCreateDatetime: String = "",
    @SerialName("page_url") val pageUrl: String = ""
) {
    companion object {
        val Empty = ComposePixivIllustSeriesBody()
    }
}

@Immutable
@Serializable
data class ComposePixivNovelSeriesBody(
    @SerialName("id") val id: Long = 0,
    @SerialName("title") val title: String = "",
    @SerialName("caption") val caption: String = "",
    @SerialName("userId") val userId: Long = 0,
    @SerialName("userName") val userName: String = "",
    @SerialName("profileImageUrl") val profileImageUrl: String = "",
    @SerialName("xRestrict") val xRestrict: Int = 0,
    @SerialName("isOriginal") val isOriginal: Boolean = false,
    @SerialName("isConcluded") val isConcluded: Boolean = false,
    @SerialName("tags") val tags: SerializeList<String> = persistentListOf(),
    @SerialName("publishedContentCount") val publishedContentCount: Int = 0,
    @SerialName("createDate") val createDate: String = "",
    @SerialName("updateDate") val updateDate: String = "",
    @SerialName("isWatched") val isWatched: Boolean = false,
    @SerialName("isNotifying") val isNotifying: Boolean = false
) {
    companion object {
        val Empty = ComposePixivNovelSeriesBody()
    }
}

@Immutable
@Serializable
data class ComposePixivNovelSeriesContentItem(
    @SerialName("id") val id: Long = 0,
    @SerialName("title") val title: String = "",
    @SerialName("order") val order: Int = 0
) {
    companion object {
        val Empty = ComposePixivNovelSeriesContentItem()
    }
}

@Immutable
@Serializable
data class ComposePixivNovelSeriesContentBody(
    @SerialName("seriesContents") val seriesContents: SerializeList<ComposePixivNovelSeriesContentItem> = persistentListOf(),
    @SerialName("total") val total: Int = 0
) {
    companion object {
        val Empty = ComposePixivNovelSeriesContentBody()
    }
}

@Immutable
@Serializable
data class ComposePixivNovelSeriesTitle(
    @SerialName("id") val id: Long = 0,
    @SerialName("title") val title: String = ""
) {
    companion object {
        val Empty = ComposePixivNovelSeriesTitle()
    }
}
