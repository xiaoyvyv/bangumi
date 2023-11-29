package com.xiaoyv.common.api.api

import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.response.CalendarEntity
import com.xiaoyv.common.api.response.MediaJsonEntity
import org.jsoup.nodes.Document
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Class: [BgmJsonApi]
 *
 * @author why
 * @since 11/24/23
 */
interface BgmJsonApi {

    @GET(BgmApiManager.URL_BASE_WEB)
    suspend fun queryMainPage(): Document

    /**
     * 每日放送
     */
    @GET("/calendar")
    suspend fun queryCalendar(): CalendarEntity

    @GET("/v0/subjects/{mediaId}")
    suspend fun queryMediaDetail(@Path("mediaId", encoded = true) mediaId: String): MediaJsonEntity
}