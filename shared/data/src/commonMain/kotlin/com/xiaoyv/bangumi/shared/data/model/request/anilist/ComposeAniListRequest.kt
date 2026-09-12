package com.xiaoyv.bangumi.shared.data.model.request.anilist

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

/**
 * [ComposeAniListGraphQLRequest]
 *
 * AniList GraphQL 请求 Payload
 *
 * @author why
 * @since 2026/1/25
 */
@Immutable
@Serializable
data class ComposeAniListGraphQLRequest(
    @SerialName("query")
    val query: String = "",
    @SerialName("variables")
    val variables: JsonObject? = null,
) {
    companion object {
        const val SEARCH_MEDIA_SINGLE_QUERY =
            "query (\$search: String) { Media(search: \$search, type: ANIME) { id title { native romaji english } status siteUrl nextAiringEpisode { episode airingAt timeUntilAiring } } }"

        const val SEARCH_MEDIA_PAGE_QUERY =
            "query (\$search: String, \$page: Int, \$perPage: Int) { Page(page: \$page, perPage: \$perPage) { pageInfo { total currentPage hasNextPage } media(search: \$search, type: ANIME) { id title { native romaji english } status siteUrl nextAiringEpisode { episode airingAt timeUntilAiring }   airingSchedule(notYetAired: false, perPage: 1) {\n" +
                    "    nodes {\n" +
                    "      id\n" +
                    "      episode # 集数编号（如 1, 2, 3...）\n" +
                    "      airingAt # 该集的播出时间（Unix 时间戳，秒）\n" +
                    "      timeUntilAiring # 倒计时（秒，已播出的为负数）\n" +
                    "    }\n" +
                    "  } } } }"

        fun buildSingleSearchQuery(search: String): ComposeAniListGraphQLRequest {
            val jsonObject = buildJsonObject {
                put("search", JsonPrimitive(search))
            }
            return ComposeAniListGraphQLRequest(
                query = SEARCH_MEDIA_SINGLE_QUERY,
                variables = jsonObject,
            )
        }

        fun buildPageSearchQuery(search: String, page: Int = 1, perPage: Int = 10): ComposeAniListGraphQLRequest {
            val jsonObject = buildJsonObject {
                put("search", JsonPrimitive(search))
                put("page", JsonPrimitive(page))
                put("perPage", JsonPrimitive(perPage))
            }
            return ComposeAniListGraphQLRequest(
                query = SEARCH_MEDIA_PAGE_QUERY,
                variables = jsonObject,
            )
        }

        /**
         * 自动根据 Bangumi 条目列表组装单次 GraphQL 批量别名查询
         */
        fun buildBatchSubjectsQuery(subjects: List<ComposeSubject>): ComposeAniListGraphQLRequest {
            val validSubjects = subjects.filter { (it.name.ifBlank { it.nameCn }).isNotBlank() }
            if (validSubjects.isEmpty()) {
                return ComposeAniListGraphQLRequest()
            }

            val varDeclarations = validSubjects.joinToString(", ") { "\$k_${it.id}: String" }
            val aliasedQueries = validSubjects.joinToString(" ") { subject ->
                "s_${subject.id}: Page(page: 1, perPage: 1) { media(search: \$k_${subject.id}, type: ANIME, status_in: [RELEASING, FINISHED], sort: [SEARCH_MATCH, START_DATE_DESC]) { ...AnimeDetail } }"
            }

            val query =
                "query ($varDeclarations) { $aliasedQueries } fragment AnimeDetail on Media { id title { native romaji english } status siteUrl nextAiringEpisode { episode airingAt timeUntilAiring } }"

            val variables = buildJsonObject {
                validSubjects.forEach { subject ->
                    put("k_${subject.id}", JsonPrimitive(subject.name.ifBlank { subject.nameCn }))
                }
            }

            return ComposeAniListGraphQLRequest(
                query = query,
                variables = variables,
            )
        }
    }
}
