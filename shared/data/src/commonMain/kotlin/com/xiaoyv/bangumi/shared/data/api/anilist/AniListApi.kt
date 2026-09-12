package com.xiaoyv.bangumi.shared.data.api.anilist

import com.xiaoyv.bangumi.shared.data.marker.AppApiDsl
import com.xiaoyv.bangumi.shared.data.model.request.anilist.ComposeAniListGraphQLRequest
import com.xiaoyv.bangumi.shared.data.model.response.anilist.ComposeAniListSearchResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

/**
 * [AniListApi]
 *
 * AniList GraphQL API 定义接口
 *
 * @author why
 * @since 2026/1/25
 */
@AppApiDsl
interface AniListApi {

    /**
     * GraphQL 通用查询接口
     *
     * @param request GraphQL 请求 Payload
     */
    @POST("https://graphql.anilist.co")
    suspend fun queryGraphQL(
        @Body request: ComposeAniListGraphQLRequest,
    ): ComposeAniListSearchResponse
}

/**
 * 根据关键词搜索单个匹配动画及其放送时间表信息
 */
suspend fun AniListApi.searchSingleMedia(keyword: String): ComposeAniListSearchResponse {
    return queryGraphQL(ComposeAniListGraphQLRequest.buildSingleSearchQuery(keyword))
}

/**
 * 根据关键词分页搜索动画列表及放送时间表信息
 */
suspend fun AniListApi.searchMediaList(keyword: String, page: Int = 1, perPage: Int = 10): ComposeAniListSearchResponse {
    return queryGraphQL(ComposeAniListGraphQLRequest.buildPageSearchQuery(keyword, page, perPage))
}
