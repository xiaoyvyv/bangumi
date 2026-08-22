package com.xiaoyv.bangumi.shared.data.api.pixiv

import com.xiaoyv.bangumi.shared.core.types.AppDsl
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.model.SearchIllustrations
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

/**
 * [PixivAjaxApi]
 *
 * @since 2025/5/26
 */
@AppDsl
interface PixivAjaxApi {
    /**
     * 搜索插画作品。
     *
     * @param word 搜索关键词，必填。
     * @param searchTarget 搜索目标类型，默认为 `partial_match_for_tags`。
     *  - `partial_match_for_tags`: 标签部分匹配
     *  - `exact_match_for_tags`: 标签完全匹配
     *  - `title_and_caption`: 标题和说明匹配
     * @param sort 排序方式，默认为 `date_desc`。
     *  - `date_desc`: 按日期降序
     *  - `date_asc`: 按日期升序
     *  - `popular_desc`: 热门（需会员）
     * @param duration 时间范围过滤，可选。
     *  - `within_last_day`: 最近一天
     *  - `within_last_week`: 最近一周
     *  - `within_last_month`: 最近一月
     * @param startDate 起始日期，格式为 `yyyy-MM-dd`，可选。
     * @param endDate 截止日期，格式为 `yyyy-MM-dd`，可选。
     * @param filter 过滤器，默认为 `for_ios`。
     * @param searchAiType 是否显示 AI 生成作品，0 过滤，1 显示，默认为 null（不指定）。
     * @param offset 翻页偏移量，用于分页查询，默认为 null。
     */
    @GET("v1/search/illust")
    suspend fun searchIllust(
        @Query("word") word: String,
        @Query("search_target") searchTarget: String = "exact_match_for_tags", // enum可定义: partial_match_for_tags, exact_match_for_tags, title_and_caption
        @Query("sort") sort: String = "date_desc", // enum: date_desc, date_asc, popular_desc
        @Query("duration") duration: String? = null, // enum: within_last_day, within_last_week, within_last_month
        @Query("start_date") startDate: String? = null, // 格式: yyyy-MM-dd
        @Query("end_date") endDate: String? = null, // 格式: yyyy-MM-dd
        @Query("filter") filter: String = "for_ios",
        @Query("search_ai_type") searchAiType: Int? = null,
        @Query("offset") offset: Int? = null,
    ): SearchIllustrations
}