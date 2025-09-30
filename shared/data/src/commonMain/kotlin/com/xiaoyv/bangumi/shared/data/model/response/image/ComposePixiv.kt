package com.xiaoyv.bangumi.shared.data.model.response.image

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
@Immutable
data class ComposePixivResponse<T>(
    @SerialName("body")
    val body: T? = null,
    @SerialName("error")
    val error: Boolean = false,
)

@Immutable
@Serializable
data class ComposePixivImageBody(
    @SerialName("illust")
    val illust: ComposePixivImageResponse? = null,
)

@Immutable
@Serializable
data class ComposePixivImageResponse(
    @SerialName("data")
    val `data`: List<ComposePixivImage>? = null,
    @SerialName("lastPage")
    val lastPage: Int = 0,
    @SerialName("total")
    val total: Int = 0,
)

@Immutable
@Serializable
data class ComposePixivImage(
    @SerialName("aiType")
    val aiType: Int = 0,
    @SerialName("alt")
    val alt: String? = null,
    @SerialName("createDate")
    val createDate: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("height")
    val height: Int = 0,
    @SerialName("id")
    val id: String? = null,
    @SerialName("illustType")
    val illustType: Int = 0,
    @SerialName("isBookmarkable")
    val isBookmarkable: Boolean = false,
    @SerialName("isMasked")
    val isMasked: Boolean = false,
    @SerialName("isUnlisted")
    val isUnlisted: Boolean = false,
    @SerialName("pageCount")
    val pageCount: Int = 0,
    @SerialName("profileImageUrl")
    val profileImageUrl: String? = null,
    @SerialName("restrict")
    val restrict: Int = 0,
    @SerialName("sl")
    val sl: Int = 0,
    @SerialName("tags")
    val tags: List<String>? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("updateDate")
    val updateDate: String? = null,
    @SerialName("url")
    val url: String? = null,
    @SerialName("urls")
    val urls: Urls? = null,
    @SerialName("userId")
    val userId: String? = null,
    @SerialName("userName")
    val userName: String? = null,
    @SerialName("visibilityScope")
    val visibilityScope: Int = 0,
    @SerialName("width")
    val width: Int = 0,
    @SerialName("xRestrict")
    val xRestrict: Int = 0,
) {
    @Serializable
    data class Urls(
        @SerialName("original")
        val original: String? = null,
        @SerialName("regular")
        val regular: String? = null,
        @SerialName("small")
        val small: String? = null,
        @SerialName("thumb_mini")
        val thumbMini: String? = null,
    )
}

