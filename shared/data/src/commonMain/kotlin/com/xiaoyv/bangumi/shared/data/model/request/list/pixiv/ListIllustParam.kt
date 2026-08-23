package com.xiaoyv.bangumi.shared.data.model.request.list.pixiv

import androidx.compose.runtime.Immutable
import com.appmattus.crypto.Algorithm
import com.xiaoyv.bangumi.shared.core.types.list.ListIllustType
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
