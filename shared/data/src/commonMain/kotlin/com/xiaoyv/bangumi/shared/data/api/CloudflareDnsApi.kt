package com.xiaoyv.bangumi.shared.data.api

import com.xiaoyv.bangumi.shared.core.types.AppJsonApiDsl
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.statement.HttpResponse

@AppJsonApiDsl
interface CloudflareDnsApi {
    @GET("dns-query")
    suspend fun fetchDns(
        @Query("name") hostname: String,
        @Query("type") type: String = "A",
        @Header("Accept") accept: String = "application/dns-json",
    ): HttpResponse
}
