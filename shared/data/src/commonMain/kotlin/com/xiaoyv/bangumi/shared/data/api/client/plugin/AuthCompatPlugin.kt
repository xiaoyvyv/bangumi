package com.xiaoyv.bangumi.shared.data.api.client.plugin

import io.ktor.client.call.HttpClientCall
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.statement.HttpReceivePipeline
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.InternalAPI
import kotlin.coroutines.CoroutineContext

class RewrittenStatusResponse(
    private val origin: HttpResponse,
    override val status: HttpStatusCode,
    private val cachedBytes: ByteArray
) : HttpResponse() {
    override val call: HttpClientCall get() = origin.call
    override val coroutineContext: CoroutineContext get() = origin.coroutineContext
    override val headers: Headers get() = origin.headers
    override val requestTime: GMTDate get() = origin.requestTime
    override val responseTime: GMTDate get() = origin.responseTime
    override val version: HttpProtocolVersion get() = origin.version

    @InternalAPI
    override val rawContent: ByteReadChannel get() = ByteReadChannel(cachedBytes)
}

val AuthCompat = createClientPlugin("AuthCompat") {
    client.receivePipeline.intercept(HttpReceivePipeline.Before) { originalResponse ->
        if (originalResponse.status == HttpStatusCode.BadRequest &&
            originalResponse.headers["Content-Type"]?.contains("application/json") == true
        ) {
            val bodyString = originalResponse.bodyAsText()

            // 检查 Body 内容
            val isTokenExpiredError = bodyString.contains("invalid_grant", true)
                    || bodyString.contains("auth", true)
                    || bodyString.contains("token", true)
                    || bodyString.contains("invalid", true)
                    || bodyString.contains("Unauthorized", true)

            if (isTokenExpiredError) {
                // 改写为 401，并把读出的 bytes 封回新的 Response 中
                val rewrittenResponse = RewrittenStatusResponse(
                    origin = originalResponse,
                    status = HttpStatusCode.Unauthorized,
                    cachedBytes = bodyString.encodeToByteArray()
                )
                proceedWith(rewrittenResponse)
            } else {
                // 不是 Token 问题，保留 400，但依然要还原已读空的 content
                val restoredResponse = RewrittenStatusResponse(
                    origin = originalResponse,
                    status = originalResponse.status,
                    cachedBytes = bodyString.encodeToByteArray()
                )
                proceedWith(restoredResponse)
            }
        } else {
            proceed()
        }
    }
}
