@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.api

import com.xiaoyv.bangumi.shared.core.types.AppDsl
import com.xiaoyv.bangumi.shared.data.model.response.trace.ComposeTraceCharacter
import com.xiaoyv.bangumi.shared.data.model.response.trace.ComposeTraceMoe
import com.xiaoyv.bangumi.shared.data.model.response.trace.MicrosoftTranslate
import com.xiaoyv.bangumi.shared.data.model.response.trace.MicrosoftTranslateParam
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.statement.HttpResponse

/**
 * [TraceApi]
 *
 * @author why
 * @since 2025/1/25
 */
@AppDsl
interface TraceApi {


    /**
     * 搜索
     */
    @POST("https://api.trace.moe/search")
    suspend fun fetchSubjectInfoFromImage(
        @Header("Content-Type") name: String,
        @Body data: ByteArray,
        @Query("cutBorders") cutBorders: Boolean? = true,
        @Query("anilistInfo") anilistInfo: String = "",
    ): ComposeTraceMoe

    /**
     * 搜索
     */
    @POST("https://api.animetrace.com/v1/search")
    suspend fun fetchCharacterInfoFromImage(@Body body: MultiPartFormDataContent): ComposeTraceCharacter

    /**
     * 查询翻译 Token
     */
    @GET("https://edge.microsoft.com/translate/auth")
    suspend fun queryEdgeAuthToken(): HttpResponse

    /**
     * 翻译
     */
    @POST("https://api.cognitive.microsofttranslator.com/translate")
    suspend fun submitMicrosoftTranslate(
        @Header("Authorization") authentication: String,
        @Query("api-version") apiVersion: String = "3.0",
        @Query("to") to: String = "zh-Hans",
        @Query("textType") textType: String = "plain",
        @Body param: List<MicrosoftTranslateParam>,
    ): List<MicrosoftTranslate>
}