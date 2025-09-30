package com.xiaoyv.bangumi.shared.data.api

import com.xiaoyv.bangumi.shared.core.types.AppDsl
import com.xiaoyv.bangumi.shared.data.model.response.db.ComposeDoubanPhoto
import com.xiaoyv.bangumi.shared.data.model.response.db.ComposeDoubanSuggest
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

/**
 * [DouBanApi]
 *
 * @author why
 * @since 2025/1/25
 */
@AppDsl
interface DouBanApi {

    @GET("https://frodo.douban.com/api/v2/search/suggestion")
    suspend fun queryDouBanSuggestion(
        @Query("q") q: String,
        @Query("apikey") apikey: String = "0dad551ec0f84ed02907ff5c42e8ec70",
    ): ComposeDoubanSuggest


    @GET("https://frodo.douban.com/api/v2/{type}/{mediaId}/photos")
    suspend fun queryDouBanPhotoList(
        @Path("mediaId") mediaId: String,
        @Path("type") type: String,
        @Query("start") start: Int = 0,
        @Query("count") count: Int = 10,
        @Query("apikey") apikey: String = "0dad551ec0f84ed02907ff5c42e8ec70",
    ): ComposeDoubanPhoto
}