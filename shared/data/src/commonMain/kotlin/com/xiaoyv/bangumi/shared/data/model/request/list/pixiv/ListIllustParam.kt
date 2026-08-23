package com.xiaoyv.bangumi.shared.data.model.request.list.pixiv

import androidx.compose.runtime.Immutable
import com.appmattus.crypto.Algorithm
import com.xiaoyv.bangumi.shared.core.types.list.ListIllustType
import com.xiaoyv.bangumi.shared.core.types.pixiv.PixivIllustSearchMode
import com.xiaoyv.bangumi.shared.core.types.pixiv.PixivIllustSearchOrder
import com.xiaoyv.bangumi.shared.core.types.pixiv.PixivIllustSearchRatio
import com.xiaoyv.bangumi.shared.core.types.pixiv.PixivIllustSearchRating
import com.xiaoyv.bangumi.shared.core.types.pixiv.PixivArtworkSearchType
import com.xiaoyv.bangumi.shared.core.types.pixiv.PixivIllustrationSearchType
import com.xiaoyv.bangumi.shared.core.types.pixiv.PixivIllustSearchAiType
import com.xiaoyv.bangumi.shared.core.types.pixiv.PixivIllustSearchDefaults
import com.xiaoyv.bangumi.shared.core.types.pixiv.PixivRankingContentType
import com.xiaoyv.bangumi.shared.core.types.pixiv.PixivRankingMode
import com.xiaoyv.bangumi.shared.data.model.ui.PageUI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [IllustRankBody]
 */
@Immutable
@Serializable
data class IllustRankBody(
    @field:PixivRankingContentType
    @SerialName("content")
    val content: String = PixivRankingContentType.ALL,

    @field:PixivRankingMode
    @SerialName("mode")
    val mode: String = PixivRankingMode.DAILY,

    @SerialName("date")
    val date: String = "",
) {
    companion object {
        val Empty = IllustRankBody()
    }
}

/**
 * [IllustUserBody]
 */
@Immutable
@Serializable
data class IllustUserBody(
    @SerialName("user_id") val userId: Long = 0,
) {
    companion object {
        val Empty = IllustUserBody()
    }
}

/**
 * [IllustSearchBody]
 */
@Immutable
@Serializable
data class IllustSearchBody(
    @SerialName("keyword") val keyword: String = "",
    @field:PixivArtworkSearchType
    @SerialName("artwork_type")
    val artworkType: String = PixivArtworkSearchType.ILLUSTRATIONS,
    @field:PixivIllustrationSearchType
    @SerialName("illustration_type")
    val illustrationType: String = PixivIllustrationSearchType.ILLUST_AND_UGOIRA,
    @field:PixivIllustSearchMode
    @SerialName("search_mode")
    val searchMode: String = PixivIllustSearchMode.TAG_FULL,
    @field:PixivIllustSearchOrder
    @SerialName("order")
    val order: String = PixivIllustSearchOrder.LATEST,
    @field:PixivIllustSearchRating
    @SerialName("rating")
    val rating: String = PixivIllustSearchRating.ALL,
    @field:PixivIllustSearchAiType
    @SerialName("ai_type")
    val aiType: Int = PixivIllustSearchAiType.HIDE,
    @SerialName("csw")
    val csw: Int = PixivIllustSearchDefaults.CSW_DISABLED,
    @field:PixivIllustSearchRatio
    @SerialName("ratio")
    val ratio: String? = null,
    @SerialName("scd")
    val startDate: String? = null,
    @SerialName("ecd")
    val endDate: String? = null,
    @SerialName("work_lang")
    val workLanguage: String? = null,
    @SerialName("wlt")
    val minWidth: String? = null,
    @SerialName("wgt")
    val maxWidth: String? = null,
    @SerialName("hlt")
    val minHeight: String? = null,
    @SerialName("hgt")
    val maxHeight: String? = null,
    @SerialName("lang")
    val language: String = PixivIllustSearchDefaults.LANGUAGE_ZH,
) {
    companion object {
        val Empty = IllustSearchBody()
    }
}

/**
 * [ListIllustParam]
 *
 * @author why
 * @since 2025/1/25
 */
@Immutable
@Serializable
data class ListIllustParam(
    @field:ListIllustType
    @SerialName("type")
    val type: Int = ListIllustType.RANK,

    @SerialName("ui")
    val ui: PageUI = PageUI(gridLayout = true),

    /**
     * [ListIllustType.RANK] 排行榜的参数
     */
    @SerialName("rank")
    val rank: IllustRankBody = IllustRankBody.Empty,

    /**
     * [ListIllustType.USER] 用户画廊的参数
     */
    @SerialName("user")
    val user: IllustUserBody = IllustUserBody.Empty,

    /**
     * [ListIllustType.SEARCH] 搜索插画的参数
     */
    @SerialName("search")
    val search: IllustSearchBody = IllustSearchBody.Empty,
) {
    val uniqueKey = Algorithm.SHA_1.hash(toString().encodeToByteArray()).toHexString()

    companion object {
        val Empty = ListIllustParam()
    }
}
