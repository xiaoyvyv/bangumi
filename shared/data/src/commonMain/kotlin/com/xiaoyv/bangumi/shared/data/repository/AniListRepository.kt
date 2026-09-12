package com.xiaoyv.bangumi.shared.data.repository

import com.xiaoyv.bangumi.shared.data.model.request.anilist.ComposeAniListGraphQLRequest
import com.xiaoyv.bangumi.shared.data.model.response.anilist.ComposeAniListMedia
import com.xiaoyv.bangumi.shared.data.model.response.anilist.ComposeAniListSearchResponse
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject

/**
 * [AniListRepository]
 *
 * AniList 数据仓库接口
 *
 * @author why
 * @since 2026/1/25
 */
interface AniListRepository {

    /**
     * 根据 Bangumi 条目列表批量获取对应的 AniList 媒体数据映射表
     *
     * @param subjects Bangumi 条目列表
     * @return Map<Long, ComposeAniListMedia>，Key 为 Bangumi 条目 ID，Value 为 AniList 媒体数据，无匹配时填充 ComposeAniListMedia.Empty
     */
    suspend fun fetchAniListMediaBySubjects(subjects: List<ComposeSubject>): Result<Map<Long, ComposeAniListMedia>>

    /**
     * GraphQL 通用查询接口
     *
     * @param request GraphQL 请求 Payload
     */
    suspend fun queryGraphQL(request: ComposeAniListGraphQLRequest): Result<ComposeAniListSearchResponse>
}
