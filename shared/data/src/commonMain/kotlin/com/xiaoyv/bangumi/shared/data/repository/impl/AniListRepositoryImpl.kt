package com.xiaoyv.bangumi.shared.data.repository.impl

import com.xiaoyv.bangumi.shared.core.utils.defaultJson
import com.xiaoyv.bangumi.shared.core.utils.runResult
import com.xiaoyv.bangumi.shared.data.api.client.ApiClient
import com.xiaoyv.bangumi.shared.data.model.request.anilist.ComposeAniListGraphQLRequest
import com.xiaoyv.bangumi.shared.data.model.response.anilist.ComposeAniListMedia
import com.xiaoyv.bangumi.shared.data.model.response.anilist.ComposeAniListPage
import com.xiaoyv.bangumi.shared.data.model.response.anilist.ComposeAniListSearchResponse
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.repository.AniListRepository
import kotlinx.serialization.json.decodeFromJsonElement

/**
 * [AniListRepositoryImpl]
 *
 * AniList 数据仓库实现类
 *
 * @author why
 * @since 2026/1/25
 */
class AniListRepositoryImpl(
    private val client: ApiClient,
) : AniListRepository {
    override suspend fun queryGraphQL(request: ComposeAniListGraphQLRequest): Result<ComposeAniListSearchResponse> = client.requestAniListApi {
        queryGraphQL(request)
    }

    override suspend fun fetchAniListMediaBySubjects(subjects: List<ComposeSubject>): Result<Map<Long, ComposeAniListMedia>> = runResult {
        if (subjects.isEmpty()) return@runResult emptyMap()

        // 1. 自动组装单次 GraphQL 批量别名 Query
        val request = ComposeAniListGraphQLRequest.buildBatchSubjectsQuery(subjects)
        if (request.query.isBlank()) return@runResult subjects.associate { it.id to ComposeAniListMedia.Empty }

        // 2. 发起单次 HTTP POST 请求
        val response = client.aniListApi.queryGraphQL(request)
        val dataObject = response.data ?: return@runResult subjects.associate { it.id to ComposeAniListMedia.Empty }

        // 3. 解析并映射返回结果
        subjects.associate { subject ->
            val subjectKey = "s_${subject.id}"
            val pageElement = dataObject[subjectKey]
            val media = runCatching {
                val pageData = pageElement?.let { defaultJson.decodeFromJsonElement<ComposeAniListPage>(it) }
                pageData?.media?.firstOrNull()
            }.getOrNull() ?: ComposeAniListMedia.Empty

            subject.id to media
        }
    }
}
