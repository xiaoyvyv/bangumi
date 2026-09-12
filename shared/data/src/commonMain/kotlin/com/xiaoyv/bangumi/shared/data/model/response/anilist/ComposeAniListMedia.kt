package com.xiaoyv.bangumi.shared.data.model.response.anilist

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * [ComposeAniListSearchResponse]
 *
 * AniList GraphQL 搜索响应模型
 *
 * @author why
 * @since 2026/1/25
 */
@Immutable
@Serializable
data class ComposeAniListSearchResponse(
    @SerialName("data")
    val data: JsonObject? = null,
    @SerialName("errors")
    val errors: List<ComposeAniListGraphQLError>? = null,
)

@Immutable
@Serializable
data class ComposeAniListGraphQLError(
    @SerialName("message")
    val message: String = "",
    @SerialName("status")
    val status: Int = 0,
)

@Immutable
@Serializable
data class ComposeAniListPage(
    @SerialName("pageInfo")
    val pageInfo: ComposeAniListPageInfo? = null,
    @SerialName("media")
    val media: List<ComposeAniListMedia> = emptyList(),
)

@Immutable
@Serializable
data class ComposeAniListPageInfo(
    @SerialName("total")
    val total: Int = 0,
    @SerialName("currentPage")
    val currentPage: Int = 1,
    @SerialName("hasNextPage")
    val hasNextPage: Boolean = false,
)

@Immutable
@Serializable
data class ComposeAniListMedia(
    @SerialName("id")
    val id: Long = 0,
    @SerialName("title")
    val title: ComposeAniListTitle = ComposeAniListTitle(),
    @SerialName("status")
    val status: String? = null,
    @SerialName("siteUrl")
    val siteUrl: String? = null,
    @SerialName("nextAiringEpisode")
    val nextAiringEpisode: ComposeAniListAiringEpisode? = null,
    @SerialName("airingSchedule")
    val airingSchedule: ComposeAniListAiringSchedule? = null,
) {
    companion object {
        val Empty = ComposeAniListMedia()
    }
}

@Immutable
@Serializable
data class ComposeAniListAiringSchedule(
    @SerialName("nodes")
    val nodes: SerializeList<ComposeAniListAiringEpisode> = persistentListOf()
)

/**
 *
 *   airingSchedule(notYetAired: false, perPage: 1) {
 *     nodes {
 *       id
 *       episode # 集数编号（如 1, 2, 3...）
 *       airingAt # 该集的播出时间（Unix 时间戳，秒）
 *       timeUntilAiring # 倒计时（秒，已播出的为负数）
 *     }
 *   }
 */
@Immutable
@Serializable
data class ComposeAniListTitle(
    @SerialName("native")
    val native: String? = null,
    @SerialName("romaji")
    val romaji: String? = null,
    @SerialName("english")
    val english: String? = null,
) {
    val displayName: String
        get() = native.takeIf { !it.isNullOrBlank() }
            ?: romaji.takeIf { !it.isNullOrBlank() }
            ?: english.orEmpty()
}

@Immutable
@Serializable
data class ComposeAniListAiringEpisode(
    @SerialName("episode")
    val episode: Int = 0,
    @SerialName("airingAt")
    val airingAt: Long = 0,
    @SerialName("timeUntilAiring")
    val timeUntilAiring: Long = 0,
)
