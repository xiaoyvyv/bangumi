package com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Immutable
@Serializable
data class ComposePixivIllustSimple(
    @SerialName("id") val id: Long = 0,
    @SerialName("title") val title: String = "",
    @SerialName("illustType") val illustType: Int = 0,
    @SerialName("xRestrict") val xRestrict: Int = 0,
    @SerialName("restrict") val restrict: Int = 0,
    @SerialName("sl") val sl: Int = 0,
    @SerialName("url") val url: String = "",
    @SerialName("description") val description: String = "",
    @SerialName("tags") val tags: SerializeList<String> = persistentListOf(),
    @SerialName("userId") val userId: Long = 0,
    @SerialName("userName") val userName: String = "",
    @SerialName("width") val width: Int = 0,
    @SerialName("height") val height: Int = 0,
    @SerialName("pageCount") val pageCount: Int = 0,
    @SerialName("isBookmarkable") val isBookmarkable: Boolean = false,
    @SerialName("bookmarkData") val bookmarkData: ComposePixivBookmarkData = ComposePixivBookmarkData.Empty,
    @SerialName("alt") val alt: String = "",
    @SerialName("titleCaptionTranslation") val titleCaptionTranslation: ComposePixivTitleCaptionTranslation = ComposePixivTitleCaptionTranslation.Empty,
    @SerialName("createDate") val createDate: String = "",
    @SerialName("updateDate") val updateDate: String = "",
    @SerialName("isUnlisted") val isUnlisted: Boolean = false,
    @SerialName("isMasked") val isMasked: Boolean = false,
    @SerialName("aiType") val aiType: Int = 0,
    @SerialName("visibilityScope") val visibilityScope: Int = 0,
    @SerialName("profileImageUrl") val profileImageUrl: String = "",
    @SerialName("type") val type: String = "",
    @SerialName("urls") val urls: SerializeMap<String, String> = persistentMapOf(),
    @SerialName("seriesId") val seriesId: Long = 0,
    @SerialName("seriesTitle") val seriesTitle: String = ""
) {
    companion object {
        val Empty = ComposePixivIllustSimple()
    }
}

@Immutable
@Serializable
data class ComposePixivIllustUrls(
    @SerialName("mini") val mini: String = "",
    @SerialName("thumb") val thumb: String = "",
    @SerialName("small") val small: String = "",
    @SerialName("regular") val regular: String = "",
    @SerialName("original") val original: String = ""
) {
    companion object {
        val Empty = ComposePixivIllustUrls()
    }
}

@Immutable
@Serializable
data class ComposePixivIllustTags(
    @SerialName("authorId") val authorId: Long = 0,
    @SerialName("isLocked") val isLocked: Boolean = false,
    @SerialName("tags") val tags: SerializeList<ComposePixivTag> = persistentListOf(),
    @SerialName("writable") val writable: Boolean = true
) {
    companion object {
        val Empty = ComposePixivIllustTags()
    }
}

@Immutable
@Serializable
data class ComposePixivPollChoice(
    @SerialName("id") val id: Int = 0,
    @SerialName("text") val text: String = "",
    @SerialName("count") val count: Int = 0
) {
    companion object {
        val Empty = ComposePixivPollChoice()
    }
}

@Immutable
@Serializable
data class ComposePixivPollData(
    @SerialName("question") val question: String = "",
    @SerialName("choices") val choices: SerializeList<ComposePixivPollChoice> = persistentListOf(),
    @SerialName("selectedValue") val selectedValue: Int = 0,
    @SerialName("total") val total: Int = 0
) {
    companion object {
        val Empty = ComposePixivPollData()
    }
}

@Immutable
@Serializable
data class ComposePixivSeriesNavItem(
    @SerialName("id") val id: Long = 0,
    @SerialName("title") val title: String = "",
    @SerialName("order") val order: Int = 0
) {
    companion object {
        val Empty = ComposePixivSeriesNavItem()
    }
}

@Immutable
@Serializable
data class ComposePixivSeriesNavData(
    @SerialName("seriesType") val seriesType: String = "",
    @SerialName("seriesId") val seriesId: Long = 0,
    @SerialName("title") val title: String = "",
    @SerialName("order") val order: Int = 0,
    @SerialName("isWatched") val isWatched: Boolean = false,
    @SerialName("isNotifying") val isNotifying: Boolean = false,
    @SerialName("prev") val prev: ComposePixivSeriesNavItem = ComposePixivSeriesNavItem.Empty,
    @SerialName("next") val next: ComposePixivSeriesNavItem = ComposePixivSeriesNavItem.Empty
) {
    companion object {
        val Empty = ComposePixivSeriesNavData()
    }
}

@Immutable
@Serializable
data class ComposePixivComicPromotion(
    @SerialName("userId") val userId: Long = 0,
    @SerialName("author") val author: String = "",
    @SerialName("title") val title: String = "",
    @SerialName("workUrl") val workUrl: String = "",
    @SerialName("description") val description: String = "",
    @SerialName("imgSrc") val imgSrc: String = "",
    @SerialName("amazonUrl") val amazonUrl: String = "",
    @SerialName("magazine") val magazine: String = "",
    @SerialName("magazineUrl") val magazineUrl: String = ""
) {
    companion object {
        val Empty = ComposePixivComicPromotion()
    }
}

@Immutable
@Serializable
data class ComposePixivFanboxPromotion(
    @SerialName("userName") val userName: String = "",
    @SerialName("userImageUrl") val userImageUrl: String = "",
    @SerialName("contentUrl") val contentUrl: String = "",
    @SerialName("description") val description: String = "",
    @SerialName("imageUrl") val imageUrl: String = "",
    @SerialName("imageUrlMobile") val imageUrlMobile: String = "",
    @SerialName("hasAdultContent") val hasAdultContent: Boolean = false
) {
    companion object {
        val Empty = ComposePixivFanboxPromotion()
    }
}

@Immutable
@Serializable
data class ComposePixivContestBanner(
    @SerialName("url") val url: String = "",
    @SerialName("icon") val icon: String = "",
    @SerialName("title") val title: String = ""
) {
    companion object {
        val Empty = ComposePixivContestBanner()
    }
}

@Immutable
@Serializable
data class ComposePixivContestData(
    @SerialName("url") val url: String = "",
    @SerialName("icon") val icon: String = "",
    @SerialName("title") val title: String = ""
) {
    companion object {
        val Empty = ComposePixivContestData()
    }
}

@Immutable
@Serializable
data class ComposePixivIllustDetailBody(
    @SerialName("illustId") val illustId: Long = 0,
    @SerialName("illustTitle") val illustTitle: String = "",
    @SerialName("illustComment") val illustComment: String = "",
    @SerialName("id") val id: Long = 0,
    @SerialName("title") val title: String = "",
    @SerialName("description") val description: String = "",
    @SerialName("illustType") val illustType: Int = 0,
    @SerialName("createDate") val createDate: String = "",
    @SerialName("uploadDate") val uploadDate: String = "",
    @SerialName("restrict") val restrict: Int = 0,
    @SerialName("xRestrict") val xRestrict: Int = 0,
    @SerialName("sl") val sl: Int = 0,
    @SerialName("urls") val urls: ComposePixivIllustUrls = ComposePixivIllustUrls.Empty,
    @SerialName("tags") val tags: ComposePixivIllustTags = ComposePixivIllustTags.Empty,
    @SerialName("alt") val alt: String = "",
    @SerialName("storableTags") val storableTags: SerializeList<String> = persistentListOf(),
    @SerialName("userId") val userId: Long = 0,
    @SerialName("userName") val userName: String = "",
    @SerialName("userAccount") val userAccount: String = "",
    @SerialName("userImageUrl") val userImageUrl: String = "",
    @SerialName("profileImageUrl") val profileImageUrl: String = "",
    @SerialName("userIllusts") val userIllusts: SerializeMap<String, ComposePixivIllustSimple?> = persistentMapOf(),
    @SerialName("likeData") val likeData: Boolean = false,
    @SerialName("width") val width: Int = 0,
    @SerialName("height") val height: Int = 0,
    @SerialName("pageCount") val pageCount: Int = 0,
    @SerialName("bookmarkCount") val bookmarkCount: Int = 0,
    @SerialName("likeCount") val likeCount: Int = 0,
    @SerialName("commentCount") val commentCount: Int = 0,
    @SerialName("responseCount") val responseCount: Int = 0,
    @SerialName("viewCount") val viewCount: Int = 0,
    @SerialName("bookStyle") val bookStyle: String = "0",
    @SerialName("isHowto") val isHowto: Boolean = false,
    @SerialName("isOriginal") val isOriginal: Boolean = false,
    @SerialName("pollData") val pollData: ComposePixivPollData = ComposePixivPollData.Empty,
    @SerialName("seriesNavData") val seriesNavData: ComposePixivSeriesNavData = ComposePixivSeriesNavData.Empty,
    @SerialName("descriptionBoothId") val descriptionBoothId: Long = 0,
    @SerialName("descriptionYoutubeId") val descriptionYoutubeId: Long = 0,
    @SerialName("comicPromotion") val comicPromotion: ComposePixivComicPromotion = ComposePixivComicPromotion.Empty,
    @SerialName("fanboxPromotion") val fanboxPromotion: ComposePixivFanboxPromotion = ComposePixivFanboxPromotion.Empty,
    @SerialName("contestBanners") val contestBanners: SerializeList<ComposePixivContestBanner> = persistentListOf(),
    @SerialName("isBookmarkable") val isBookmarkable: Boolean = true,
    @SerialName("bookmarkData") val bookmarkData: ComposePixivBookmarkData = ComposePixivBookmarkData.Empty,
    @SerialName("contestData") val contestData: SerializeList<ComposePixivContestData> = persistentListOf(),
    @SerialName("zoneConfig") val zoneConfig: ComposePixivZoneConfig = ComposePixivZoneConfig.Empty,
    @SerialName("extraData") val extraData: ComposePixivExtraData = ComposePixivExtraData.Empty,
    @SerialName("titleCaptionTranslation") val titleCaptionTranslation: ComposePixivTitleCaptionTranslation = ComposePixivTitleCaptionTranslation.Empty,
    @SerialName("isUnlisted") val isUnlisted: Boolean = false,
    @SerialName("commentOff") val commentOff: Int = 0,
    @SerialName("aiType") val aiType: Int = 0,
    @SerialName("reuploadDate") val reuploadDate: String = "",
    @SerialName("locationMask") val locationMask: Boolean = false,
    @SerialName("commissionLinkHidden") val commissionLinkHidden: Boolean = false,
    @SerialName("isLoginOnly") val isLoginOnly: Boolean = false
) {
    companion object {
        val Empty = ComposePixivIllustDetailBody()
    }
}

@Immutable
@Serializable
data class ComposePixivLikeBody(
    @SerialName("isLiked") val isLiked: Boolean = false
) {
    companion object {
        val Empty = ComposePixivLikeBody()
    }
}

@Immutable
@Serializable
data class ComposePixivPageUrls(
    @SerialName("thumb_mini") val thumb_mini: String = "",
    @SerialName("small") val small: String = "",
    @SerialName("regular") val regular: String = "",
    @SerialName("original") val original: String = ""
) {
    companion object {
        val Empty = ComposePixivPageUrls()
    }
}

@Immutable
@Serializable
data class ComposePixivPageInfo(
    @SerialName("urls") val urls: ComposePixivPageUrls = ComposePixivPageUrls.Empty,
    @SerialName("width") val width: Int = 0,
    @SerialName("height") val height: Int = 0
) {
    companion object {
        val Empty = ComposePixivPageInfo()
    }
}

@Immutable
@Serializable
data class ComposePixivUgoiraFrame(
    @SerialName("file") val file: String = "",
    @SerialName("delay") val delay: Int = 0
) {
    companion object {
        val Empty = ComposePixivUgoiraFrame()
    }
}

@Immutable
@Serializable
data class ComposePixivUgoiraMetaBody(
    @SerialName("src") val src: String = "",
    @SerialName("originalSrc") val originalSrc: String = "",
    @SerialName("mime_type") val mime_type: String = "",
    @SerialName("frames") val frames: SerializeList<ComposePixivUgoiraFrame> = persistentListOf()
) {
    companion object {
        val Empty = ComposePixivUgoiraMetaBody()
    }
}

@Immutable
@Serializable
data class ComposePixivThumbnails(
    @SerialName("illust") val illust: SerializeList<ComposePixivIllustSimple> = persistentListOf(),
    @SerialName("novel") val novel: SerializeList<ComposePixivNovelSimple> = persistentListOf()
) {
    companion object {
        val Empty = ComposePixivThumbnails()
    }
}

@Immutable
@Serializable
data class ComposePixivDiscoveryBody(
    @SerialName("thumbnails") val thumbnails: ComposePixivThumbnails = ComposePixivThumbnails.Empty,
    @Transient val tagTranslation: SerializeMap<String, SerializeMap<String, String>> = persistentMapOf()
) {
    companion object {
        val Empty = ComposePixivDiscoveryBody()
    }
}

@Immutable
@Serializable
data class ComposePixivFollowLatestPage(
    @SerialName("ids") val ids: SerializeList<Long> = persistentListOf(),
    @SerialName("isLastPage") val isLastPage: Boolean = false,
    @SerialName("tags") val tags: SerializeList<String> = persistentListOf()
) {
    companion object {
        val Empty = ComposePixivFollowLatestPage()
    }
}

@Immutable
@Serializable
data class ComposePixivFollowLatestBody(
    @SerialName("page") val page: ComposePixivFollowLatestPage = ComposePixivFollowLatestPage.Empty,
    @SerialName("thumbnails") val thumbnails: ComposePixivThumbnails = ComposePixivThumbnails.Empty,
    @SerialName("illustSeries") val illustSeries: SerializeList<Long> = persistentListOf(),
    @SerialName("requests") val requests: SerializeList<String> = persistentListOf(),
    @SerialName("users") val users: SerializeList<Long> = persistentListOf()
) {
    companion object {
        val Empty = ComposePixivFollowLatestBody()
    }
}

@Immutable
@Serializable
data class ComposePixivIllustRecommendBody(
    @SerialName("illusts") val illusts: SerializeList<ComposePixivIllustSimple> = persistentListOf()
) {
    companion object {
        val Empty = ComposePixivIllustRecommendBody()
    }
}

@Immutable
@Serializable
data class ComposePixivRecommendMetadata(
    @SerialName("methods") val methods: SerializeList<String> = persistentListOf(),
    @SerialName("score") val score: Double = 0.0,
    @SerialName("seedIllustIds") val seedIllustIds: SerializeList<Long> = persistentListOf(),
    @SerialName("banditInfo") val banditInfo: String = "",
    @SerialName("recommendListId") val recommendListId: String = ""
) {
    companion object {
        val Empty = ComposePixivRecommendMetadata()
    }
}

@Immutable
@Serializable
data class ComposePixivIllustRecommendInitBody(
    @SerialName("illusts") val illusts: SerializeList<ComposePixivIllustSimple> = persistentListOf(),
    @SerialName("nextIds") val nextIds: SerializeList<Long> = persistentListOf(),
    @SerialName("details") val details: SerializeMap<String, ComposePixivRecommendMetadata> = persistentMapOf()
) {
    companion object {
        val Empty = ComposePixivIllustRecommendInitBody()
    }
}

@Immutable
@Serializable
data class ComposePixivIllustMangaData(
    @SerialName("data") val data: SerializeList<ComposePixivIllustSimple> = persistentListOf(),
    @SerialName("total") val total: Int = 0,
    @SerialName("lastPage") val lastPage: Int = 0
) {
    companion object {
        val Empty = ComposePixivIllustMangaData()
    }
}

@Immutable
@Serializable
data class ComposePixivPopularData(
    @SerialName("recent") val recent: SerializeList<ComposePixivIllustSimple> = persistentListOf(),
    @SerialName("permanent") val permanent: SerializeList<ComposePixivIllustSimple> = persistentListOf()
) {
    companion object {
        val Empty = ComposePixivPopularData()
    }
}

@Immutable
@Serializable
data class ComposePixivIllustSearchBody(
    @SerialName("illustManga") val illustManga: ComposePixivIllustMangaData = ComposePixivIllustMangaData.Empty,
    @SerialName("popular") val popular: ComposePixivPopularData = ComposePixivPopularData.Empty,
    @SerialName("relatedTags") val relatedTags: SerializeList<String> = persistentListOf(),
    @SerialName("zoneConfig") val zoneConfig: ComposePixivZoneConfig = ComposePixivZoneConfig.Empty,
    @SerialName("extraData") val extraData: ComposePixivExtraData = ComposePixivExtraData.Empty
) {
    companion object {
        val Empty = ComposePixivIllustSearchBody()
    }
}
