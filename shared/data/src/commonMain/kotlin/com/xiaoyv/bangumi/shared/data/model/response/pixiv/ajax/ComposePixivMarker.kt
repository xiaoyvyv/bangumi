package com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposePixivNovelMarkerBody(
    @SerialName("page") val page: Int = 0
) {
    companion object {
        val Empty = ComposePixivNovelMarkerBody()
    }
}

@Immutable
@Serializable
data class ComposePixivNovelMarkerItem(
    @SerialName("id") val id: Long = 0,
    @SerialName("title") val title: String = "",
    @SerialName("userId") val userId: Long = 0,
    @SerialName("userName") val userName: String = "",
    @SerialName("coverUrl") val coverUrl: String = "",
    @SerialName("textCount") val textCount: Int = 0,
    @SerialName("bookmarkCount") val bookmarkCount: Int = 0,
    @SerialName("tags") val tags: SerializeList<String> = persistentListOf(),
    @SerialName("description") val description: String = "",
    @SerialName("xRestrict") val xRestrict: Int = 0,
    @SerialName("seriesId") val seriesId: Long = 0,
    @SerialName("seriesTitle") val seriesTitle: String = ""
) {
    companion object {
        val Empty = ComposePixivNovelMarkerItem()
    }
}

@Immutable
@Serializable
data class ComposePixivNovelMarkerListBody(
    @SerialName("total") val total: Int = 0,
    @SerialName("novels") val novels: SerializeList<ComposePixivNovelMarkerItem> = persistentListOf()
) {
    companion object {
        val Empty = ComposePixivNovelMarkerListBody()
    }
}
