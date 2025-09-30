package com.xiaoyv.bangumi.shared.data.api.magnet

import com.fleeksoft.ksoup.nodes.Document
import com.xiaoyv.bangumi.shared.core.types.AppJsonApiDsl
import com.xiaoyv.bangumi.shared.data.model.response.trace.ComposeTraceName
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

/**
 * [MikanApi]
 *
 * @author why
 * @since 2025/1/25
 */
@AppJsonApiDsl
interface MikanApi {

    /**
     * https://share.dmhy.org/topics/list/page/2?keyword=%E8%8A%99%E8%8E%89%E8%8E%B2&sort_id=2&team_id=619&order=date-desc
     */
    @GET("https://xget.xiaoyv.com.cn/dmhy/topics/list/page/{page}")
    suspend fun fetchGardenResource(
        @Path("page") page: Int,
        @Query("keyword") keyword: String,
        @Query("sort_id") sortId: Int? = null,
        @Query("team_id") teamId: Int? = null,
        @Query("order") order: String = "rel",
    ): Document

    /**
     * 获取蜜柑计划ID映射表
     */
    @GET("https://fastly.jsdelivr.net/gh/xiaoyvyv/bangumi-data@latest/data/mikan/bangumi-mikan.json")
    suspend fun fetchMikanIdMapByJsdelivr(): Map<String, String>

    /**
     * 获取蜜柑计划ID映射表
     */
    @GET("https://raw.githubusercontent.com/xiaoyvyv/bangumi-data/main/data/mikan/bangumi-mikan.json")
    suspend fun fetchMikanIdMapByGithub(): Map<String, String>

    /**
     * 获取Ani名称映射表
     */
    @GET("https://fastly.jsdelivr.net/gh/soruly/anilist-chinese@latest/anilist-chinese.json")
    suspend fun fetchAniTitleMapByJsdelivr(): List<ComposeTraceName>

    /**
     * 获取Ani名称映射表
     */
    @GET("https://raw.githubusercontent.com/soruly/anilist-chinese/master/anilist-chinese.json")
    suspend fun fetchAniTitleMapByGithub(): List<ComposeTraceName>


    /**
     * 获取蜜柑计划媒体详情
     */
    @GET("https://mikanime.tv/Home/Bangumi/{mikanId}")
    suspend fun fetchMikanGroup(@Path("mikanId") mikanId: String): Document

    /**
     * 字幕组资源信息
     */
    @GET("https://mikanime.tv/Home/ExpandEpisodeTable")
    suspend fun fetchMikanResources(
        @Query("bangumiId") bangumiId: String,
        @Query("subtitleGroupId") subtitleGroupId: String,
        @Query("take") take: Int = 1000,
    ): Document
}