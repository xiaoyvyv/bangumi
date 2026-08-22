package com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposePixivisionArticle(
    @SerialName("id") val id: Long = 0,
    @SerialName("title") val title: String = "",
    @SerialName("url") val url: String = "",
    @SerialName("thumbnailUrl") val thumbnailUrl: String = "",
    @SerialName("category") val category: String = "",
    @SerialName("tags") val tags: SerializeList<String> = persistentListOf(),
    @SerialName("publishDate") val publishDate: String = ""
) {
    companion object {
        val Empty = ComposePixivisionArticle()
    }
}

@Immutable
@Serializable
data class ComposePixivisionArticleListResponse(
    @SerialName("articles") val articles: SerializeList<ComposePixivisionArticle> = persistentListOf()
) {
    companion object {
        val Empty = ComposePixivisionArticleListResponse()
    }
}

@Immutable
@Serializable
data class ComposePixivisionArtwork(
    @SerialName("artworkId") val artworkId: Long = 0,
    @SerialName("artworkTitle") val artworkTitle: String = "",
    @SerialName("artworkImageUrl") val artworkImageUrl: String = "",
    @SerialName("authorId") val authorId: Long = 0,
    @SerialName("authorName") val authorName: String = "",
    @SerialName("authorAvatarUrl") val authorAvatarUrl: String = ""
) {
    companion object {
        val Empty = ComposePixivisionArtwork()
    }
}

@Immutable
@Serializable
data class ComposePixivisionArticleDetail(
    @SerialName("id") val id: Long = 0,
    @SerialName("title") val title: String = "",
    @SerialName("description") val description: String = "",
    @SerialName("coverImageUrl") val coverImageUrl: String = "",
    @SerialName("category") val category: String = "",
    @SerialName("publishDate") val publishDate: String = "",
    @SerialName("artworks") val artworks: SerializeList<ComposePixivisionArtwork> = persistentListOf()
) {
    companion object {
        val Empty = ComposePixivisionArticleDetail()
    }
}
