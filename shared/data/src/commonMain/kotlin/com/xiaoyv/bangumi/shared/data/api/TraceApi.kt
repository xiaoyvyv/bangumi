@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.api

import com.xiaoyv.bangumi.shared.core.types.AppDsl
import com.xiaoyv.bangumi.shared.data.model.response.trace.ComposeTraceCharacter
import com.xiaoyv.bangumi.shared.data.model.response.trace.ComposeTraceMoe
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.request.forms.MultiPartFormDataContent

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
     * 翻译
     */
    @POST("https://edge.microsoft.com/translate/translatetext")
    suspend fun submitMicrosoftTranslate(
        @Query("from") from: String = "",
        @Query("to") to: String = "zh-Hans",
        @Query("textType") textType: String = "plain",
        @Query("isEnterpriseClient") isEnterpriseClient: Boolean = false,
        @Body param: List<String>,
    ): String
}