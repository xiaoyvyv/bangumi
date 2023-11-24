package com.xiaoyv.common.api.api

import com.xiaoyv.common.api.BgmApiManager
import org.jsoup.nodes.Document
import retrofit2.http.GET

/**
 * Class: [BgmWebApi]
 *
 * @author why
 * @since 11/24/23
 */
interface BgmWebApi {

    @GET(BgmApiManager.URL_BASE_WEB)
    suspend fun queryMainPage(): Document
}