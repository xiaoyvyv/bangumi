package com.xiaoyv.bangumi.shared.data.api.app

import com.xiaoyv.bangumi.shared.core.types.AppJsonApiDsl
import com.xiaoyv.bangumi.shared.data.model.response.base.ComposeAppPage
import com.xiaoyv.bangumi.shared.data.model.response.base.ComposeAppResponse
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSearchIndex
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSearchTopic
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

/**
 * [AppApi]
 *
 * @author why
 * @since 2025/1/14
 */
@AppJsonApiDsl
interface AppApi {

    /**
     * 搜索目录
     */
    @GET("api/bgm/index/search")
    suspend fun fetchSearchIndex(
        @Query("keyword") keyword: String,
        @Query("exact") exact: Boolean,
        @Query("order") order: String = "updated_at",
        @Query("current") page: Int,
        @Query("size") size: Int,
    ): ComposeAppResponse<ComposeAppPage<ComposeSearchIndex>>

    /**
     * 搜索帖子
     */
    @GET("api/bgm/topic/search")
    suspend fun fetchSearchTopic(
        @Query("keyword") keyword: String,
        @Query("exact") exact: Boolean,
        @Query("order") order: String = "time_date",
        @Query("current") page: Int,
        @Query("size") size: Int,
    ): ComposeAppResponse<ComposeAppPage<ComposeSearchTopic>>
}