package com.xiaoyv.bangumi.shared.data.model.response.pixiv

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.core.utils.toPixivHighResUrl
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivIllustSimple
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivRankingContent
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [ComposePixivIllustCard]
 *
 * Pixiv 插画/漫画卡片 UI 数据模型
 *
 * @since 2025/5/26
 */
@Immutable
@Serializable
data class ComposePixivIllustCard(
    @SerialName("id") val id: Long = 0,
    @SerialName("title") val title: String = "",
    @SerialName("url") val url: String = "",
    @SerialName("illust_type") val illustType: String = "0",
    @SerialName("page_count") val pageCount: Int = 1,
    @SerialName("user_id") val userId: Long = 0,
    @SerialName("user_name") val userName: String = "",
    @SerialName("profile_img") val profileImg: String = "",
    @SerialName("width") val width: Int = 0,
    @SerialName("height") val height: Int = 0,
    @SerialName("rank") val rank: Int = 0,
    @SerialName("yes_rank") val yesRank: Int = 0,
    @SerialName("rating_count") val ratingCount: Int = 0,
    @SerialName("view_count") val viewCount: Int = 0,
    @SerialName("is_masked") val isMasked: Boolean = false,
    @SerialName("is_bookmarked") val isBookmarked: Boolean = false,
    @SerialName("bookmark_id") val bookmarkId: Long = 0,
    @SerialName("tags") val tags: SerializeList<String> = persistentListOf(),
    @SerialName("create_date") val createDate: String = "",
) {
    companion object {
        val Empty = ComposePixivIllustCard()
    }
}

fun ComposePixivRankingContent.toIllustCard(): ComposePixivIllustCard {
    return ComposePixivIllustCard(
        id = illustId,
        title = title,
        url = url.toPixivHighResUrl(),
        illustType = illustType,
        pageCount = illustPageCount.toIntOrNull() ?: 1,
        userId = userId,
        userName = userName,
        profileImg = profileImg,
        width = width,
        height = height,
        rank = rank,
        yesRank = yesRank,
        ratingCount = ratingCount,
        viewCount = viewCount,
        isMasked = isMasked,
        isBookmarked = isBookmarked,
        bookmarkId = bookmarkId,
        tags = tags,
        createDate = date
    )
}

fun ComposePixivIllustSimple.toIllustCard(): ComposePixivIllustCard {
    val displayUrl = urls["540x540"]?.toPixivHighResUrl()
        ?: urls["600x1200"]?.toPixivHighResUrl()
        ?: urls["regular"]?.toPixivHighResUrl()
        ?: url.toPixivHighResUrl()

    return ComposePixivIllustCard(
        id = id,
        title = title,
        url = displayUrl,
        illustType = illustType.toString(),
        pageCount = pageCount,
        userId = userId,
        userName = userName,
        profileImg = profileImageUrl,
        width = width,
        height = height,
        rank = 0,
        yesRank = 0,
        ratingCount = 0,
        viewCount = 0,
        isMasked = isMasked,
        isBookmarked = (bookmarkData?.id ?: 0) > 0,
        bookmarkId = bookmarkData?.id ?: 0,
        tags = tags,
        createDate = createDate
    )
}
