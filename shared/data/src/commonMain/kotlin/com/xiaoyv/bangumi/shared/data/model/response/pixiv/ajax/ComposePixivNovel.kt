package com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposePixivNovelSimple(
    @SerialName("id") val id: Long = 0,
    @SerialName("title") val title: String = "",
    @SerialName("genre") val genre: String = "",
    @SerialName("xRestrict") val xRestrict: Int = 0,
    @SerialName("restrict") val restrict: Int = 0,
    @SerialName("url") val url: String = "",
    @SerialName("tags") val tags: SerializeList<String> = persistentListOf(),
    @SerialName("userId") val userId: Long = 0,
    @SerialName("userName") val userName: String = "",
    @SerialName("profileImageUrl") val profileImageUrl: String = "",
    @SerialName("textCount") val textCount: Int = 0,
    @SerialName("wordCount") val wordCount: Int = 0,
    @SerialName("readingTime") val readingTime: Int = 0,
    @SerialName("useWordCount") val useWordCount: Boolean = false,
    @SerialName("description") val description: String = "",
    @SerialName("isBookmarkable") val isBookmarkable: Boolean = true,
    @SerialName("bookmarkData") val bookmarkData: ComposePixivBookmarkData = ComposePixivBookmarkData.Empty,
    @SerialName("bookmarkCount") val bookmarkCount: Int = 0,
    @SerialName("isOriginal") val isOriginal: Boolean = false,
    @SerialName("marker") val marker: Int = 0,
    @SerialName("titleCaptionTranslation") val titleCaptionTranslation: ComposePixivTitleCaptionTranslation = ComposePixivTitleCaptionTranslation.Empty,
    @SerialName("createDate") val createDate: String = "",
    @SerialName("updateDate") val updateDate: String = "",
    @SerialName("isMasked") val isMasked: Boolean = false,
    @SerialName("aiType") val aiType: Int = 0,
    @SerialName("seriesId") val seriesId: Long = 0,
    @SerialName("seriesTitle") val seriesTitle: String = "",
    @SerialName("isUnlisted") val isUnlisted: Boolean = false,
    @SerialName("visibilityScope") val visibilityScope: Int = 0,
    @SerialName("language") val language: String = "ja",
    @SerialName("maskReason") val maskReason: String = ""
) {
    companion object {
        val Empty = ComposePixivNovelSimple()
    }
}

@Immutable
@Serializable
data class ComposePixivNovelSeriesSimple(
    @SerialName("id") val id: Long = 0,
    @SerialName("title") val title: String = ""
) {
    companion object {
        val Empty = ComposePixivNovelSeriesSimple()
    }
}

@Immutable
@Serializable
data class ComposePixivNovelTagInfo(
    @SerialName("authorId") val authorId: Long = 0,
    @SerialName("isLocked") val isLocked: Boolean = false,
    @SerialName("tags") val tags: SerializeList<ComposePixivTag> = persistentListOf(),
    @SerialName("writable") val writable: Boolean = false
) {
    companion object {
        val Empty = ComposePixivNovelTagInfo()
    }
}

@Immutable
@Serializable
data class ComposePixivNovelSeriesNavItem(
    @SerialName("title") val title: String = "",
    @SerialName("order") val order: Int = 0,
    @SerialName("id") val id: Long = 0,
    @SerialName("available") val available: Boolean = true
) {
    companion object {
        val Empty = ComposePixivNovelSeriesNavItem()
    }
}

@Immutable
@Serializable
data class ComposePixivNovelSeriesNavData(
    @SerialName("seriesType") val seriesType: String = "",
    @SerialName("seriesId") val seriesId: Long = 0,
    @SerialName("title") val title: String = "",
    @SerialName("isConcluded") val isConcluded: Boolean = false,
    @SerialName("isReplaceable") val isReplaceable: Boolean = false,
    @SerialName("isWatched") val isWatched: Boolean = false,
    @SerialName("isNotifying") val isNotifying: Boolean = false,
    @SerialName("order") val order: Int = 0,
    @SerialName("prev") val prev: ComposePixivNovelSeriesNavItem = ComposePixivNovelSeriesNavItem.Empty,
    @SerialName("next") val next: ComposePixivNovelSeriesNavItem = ComposePixivNovelSeriesNavItem.Empty
) {
    companion object {
        val Empty = ComposePixivNovelSeriesNavData()
    }
}

@Immutable
@Serializable
data class ComposePixivNovelEmbeddedImageUrls(
    @SerialName("240mw") val small: String = "",
    @SerialName("480mw") val medium: String = "",
    @SerialName("1200x1200") val large: String = "",
    @SerialName("128x128") val thumbnail: String = "",
    @SerialName("original") val original: String = ""
) {
    companion object {
        val Empty = ComposePixivNovelEmbeddedImageUrls()
    }
}

@Immutable
@Serializable
data class ComposePixivNovelEmbeddedImage(
    @SerialName("novelImageId") val novelImageId: Long = 0,
    @SerialName("sl") val sl: String = "",
    @SerialName("urls") val urls: ComposePixivNovelEmbeddedImageUrls = ComposePixivNovelEmbeddedImageUrls.Empty
) {
    companion object {
        val Empty = ComposePixivNovelEmbeddedImage()
    }
}

@Immutable
@Serializable
data class ComposePixivNovelDetailBody(
    @SerialName("id") val id: Long = 0,
    @SerialName("title") val title: String = "",
    @SerialName("content") val content: String = "",
    @SerialName("createDate") val createDate: String = "",
    @SerialName("uploadDate") val uploadDate: String = "",
    @SerialName("description") val description: String = "",
    @SerialName("bookmarkCount") val bookmarkCount: Int = 0,
    @SerialName("likeCount") val likeCount: Int = 0,
    @SerialName("viewCount") val viewCount: Int = 0,
    @SerialName("commentCount") val commentCount: Int = 0,
    @SerialName("markerCount") val markerCount: Int = 0,
    @SerialName("marker") val marker: Int = 0,
    @SerialName("pageCount") val pageCount: Int = 0,
    @SerialName("isOriginal") val isOriginal: Boolean = false,
    @SerialName("isBungei") val isBungei: Boolean = false,
    @SerialName("xRestrict") val xRestrict: Int = 0,
    @SerialName("restrict") val restrict: Int = 0,
    @SerialName("userId") val userId: Long = 0,
    @SerialName("userName") val userName: String = "",
    @SerialName("tags") val tags: ComposePixivNovelTagInfo = ComposePixivNovelTagInfo.Empty,
    @SerialName("bookmarkData") val bookmarkData: ComposePixivBookmarkData = ComposePixivBookmarkData.Empty,
    @SerialName("coverUrl") val coverUrl: String = "",
    @SerialName("characterCount") val characterCount: Int = 0,
    @SerialName("wordCount") val wordCount: Int = 0,
    @SerialName("readingTime") val readingTime: Int = 0,
    @SerialName("useWordCount") val useWordCount: Boolean = false,
    @SerialName("language") val language: String = "",
    @SerialName("genre") val genre: String = "",
    @SerialName("aiType") val aiType: Int = 0,
    @SerialName("isUnlisted") val isUnlisted: Boolean = false,
    @SerialName("isLoginOnly") val isLoginOnly: Boolean = false,
    @SerialName("likeData") val likeData: Boolean = false,
    @SerialName("isBookmarkable") val isBookmarkable: Boolean = true,
    @SerialName("seriesNavData") val seriesNavData: ComposePixivNovelSeriesNavData = ComposePixivNovelSeriesNavData.Empty,
    @SerialName("textEmbeddedImages") val textEmbeddedImages: SerializeMap<String, ComposePixivNovelEmbeddedImage> = persistentMapOf()
) {
    companion object {
        val Empty = ComposePixivNovelDetailBody()
    }
}

@Immutable
@Serializable
data class ComposePixivNovelRecommendBody(
    @SerialName("novels") val novels: SerializeList<ComposePixivNovelSimple> = persistentListOf()
) {
    companion object {
        val Empty = ComposePixivNovelRecommendBody()
    }
}

@Immutable
@Serializable
data class ComposePixivNovelRecommendInitBody(
    @SerialName("novels") val novels: SerializeList<ComposePixivNovelSimple> = persistentListOf(),
    @SerialName("nextIds") val nextIds: SerializeList<Long> = persistentListOf()
) {
    companion object {
        val Empty = ComposePixivNovelRecommendInitBody()
    }
}

@Immutable
@Serializable
data class ComposePixivNovelSearchData(
    @SerialName("data") val data: SerializeList<ComposePixivNovelSimple> = persistentListOf(),
    @SerialName("total") val total: Int = 0,
    @SerialName("lastPage") val lastPage: Int = 0
) {
    companion object {
        val Empty = ComposePixivNovelSearchData()
    }
}

@Immutable
@Serializable
data class ComposePixivPopularNovelData(
    @SerialName("recent") val recent: SerializeList<ComposePixivNovelSimple> = persistentListOf(),
    @SerialName("permanent") val permanent: SerializeList<ComposePixivNovelSimple> = persistentListOf()
) {
    companion object {
        val Empty = ComposePixivPopularNovelData()
    }
}

@Immutable
@Serializable
data class ComposePixivNovelSearchBody(
    @SerialName("novel") val novel: ComposePixivNovelSearchData = ComposePixivNovelSearchData.Empty,
    @SerialName("popular") val popular: ComposePixivPopularNovelData = ComposePixivPopularNovelData.Empty,
    @SerialName("relatedTags") val relatedTags: SerializeList<String> = persistentListOf(),
    @SerialName("zoneConfig") val zoneConfig: ComposePixivZoneConfig = ComposePixivZoneConfig.Empty,
    @SerialName("extraData") val extraData: ComposePixivExtraData = ComposePixivExtraData.Empty
) {
    companion object {
        val Empty = ComposePixivNovelSearchBody()
    }
}

@Immutable
@Serializable
data class ComposePixivNovelBookmarkStatusBody(
    @SerialName("bookmarkData") val bookmarkData: ComposePixivBookmarkData = ComposePixivBookmarkData.Empty
) {
    companion object {
        val Empty = ComposePixivNovelBookmarkStatusBody()
    }
}
