package com.xiaoyv.bangumi.shared.data.workflow.port

import com.fleeksoft.ksoup.Ksoup
import com.xiaoyv.bangumi.shared.core.utils.defaultJson
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHttpBodyType
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHttpResponseKey
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionHttpRequestEffect
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.timeout
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Cookie
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.Parameters
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.http.formUrlEncode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlin.time.Duration.Companion.milliseconds

/**
 * 工作流 HTTP 节点所需的请求能力端口。
 *
 * 工作流引擎仅依赖这个抽象，不感知 Ktor、Cookie、应用网络设置等 data 基础设施。
 * 宿主可替换实现，以适配测试、受限网络或其他平台的 HTTP 客户端。
 */
fun interface ActionHttpRequestExecutor {
    /**
     * 执行 HTTP 请求，并返回可供后续节点通过 `steps.<nodeId>` 读取的结构化输出。
     *
     * @param request HTTP 请求节点声明的请求副作用。
     * @return 标准化的 HTTP 响应输出。
     */
    suspend fun execute(request: ActionHttpRequestEffect): JsonObject
}


/**
 * 工作流内置 HTTP 请求执行器。
 *
 * 使用应用统一配置的 Ktor 客户端执行请求，并将状态码与解析后的 JSON 响应作为节点输出返回。
 *
 * @param hostCookieStorge 宿主的 Cookie 库，仅支持访问且需要严格控制权限；
 */
class DefaultActionHttpRequestExecutor(
    private val httpClient: HttpClient,
    private val hostCookieStorge: CookiesStorage,
) : ActionHttpRequestExecutor {
    /**
     * 不需要 Cookie 的 HttpClient
     */
    private val anonymousHttpClient by lazy {
        httpClient.config {
            install(HttpCookies) {
                storage = object : CookiesStorage {
                    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) = Unit
                    override suspend fun get(requestUrl: Url): List<Cookie> = emptyList()
                    override fun close() = Unit
                }
            }
        }
    }

    /**
     * 执行请求并返回可写入 `steps.<nodeId>` 的标准输出。
     *
     * @param request HTTP 请求副作用。
     */
    override suspend fun execute(request: ActionHttpRequestEffect): JsonObject {
        var lastFailure: Throwable? = null
        repeat(request.retryCount.coerceAtLeast(0) + 1) { attempt ->
            try {
                val output = executeOnce(request)
                val statusCode = output[ActionHttpResponseKey.STATUS_CODE]?.let { (it as JsonPrimitive).content.toInt() } ?: 0
                if (statusCode !in 500..599 || attempt >= request.retryCount) return output
                if (request.retryDelayMillis > 0) delay(request.retryDelayMillis.milliseconds)
            } catch (throwable: Throwable) {
                if (throwable is CancellationException) throw throwable
                lastFailure = throwable
                if (attempt >= request.retryCount) throw throwable
                if (request.retryDelayMillis > 0) delay(request.retryDelayMillis.milliseconds)
            }
        }
        throw checkNotNull(lastFailure)
    }

    /**
     * 执行一次 HTTP 请求，并规范化响应体。
     */
    private suspend fun executeOnce(request: ActionHttpRequestEffect): JsonObject {
        val httpClient = if (request.useLocalCookieStorage) httpClient else anonymousHttpClient
        val response = httpClient.request(request.url) {
            method = HttpMethod.parse(request.method)
            url { request.query.forEach { (key, value) -> parameters.append(key, value.toString().trim('"')) } }
            request.headers.forEach { (key, value) -> headers.append(key, value.toString().trim('"')) }
            request.timeoutMillis?.let { timeout { requestTimeoutMillis = it } }
            if (request.body !is kotlinx.serialization.json.JsonNull) {
                val encoded = when (request.bodyType) {
                    ActionHttpBodyType.FORM_URL_ENCODED -> Parameters.build {
                        val form = request.body as? JsonObject ?: error("formUrlEncoded 请求体必须是 JSON 对象")
                        form.forEach { (key, value) ->
                            append(key, value.toString().trim('"'))
                        }
                    }.formUrlEncode()

                    ActionHttpBodyType.TEXT -> request.body.toString().trim('"')
                    else -> request.body.toString()
                }
                contentType(
                    ContentType.parse(
                        request.contentType ?: when (request.bodyType) {
                            ActionHttpBodyType.FORM_URL_ENCODED -> ContentType.Application.FormUrlEncoded.toString()
                            ActionHttpBodyType.TEXT -> ContentType.Text.Plain.toString()
                            else -> ContentType.Application.Json.toString()
                        },
                    ),
                )
                setBody(encoded)
            }
        }
        val rawBody: String = response.body()
        val responseContentType = response.headers[HttpHeaders.ContentType].orEmpty()
        val body = when {
            responseContentType.contains("json", ignoreCase = true) -> {
                runCatching { defaultJson.parseToJsonElement(rawBody) }.getOrElse { JsonPrimitive(rawBody) }
            }

            responseContentType.contains("html", ignoreCase = true) || rawBody.trimStart().startsWith("<") -> {
                val document = Ksoup.parse(rawBody)
                buildJsonObject {
                    put(ActionHttpResponseKey.HTML, JsonPrimitive(rawBody))
                    put(ActionHttpResponseKey.TEXT, JsonPrimitive(document.text()))
                    put(ActionHttpResponseKey.TITLE, JsonPrimitive(document.title()))
                }
            }

            else -> JsonPrimitive(rawBody)
        }

        return buildJsonObject {
            put(ActionHttpResponseKey.STATUS_CODE, JsonPrimitive(response.status.value))
            put(ActionHttpResponseKey.IS_SUCCESS, JsonPrimitive(response.status.value in 200..299))
            put(ActionHttpResponseKey.CONTENT_TYPE, JsonPrimitive(responseContentType))
            put(ActionHttpResponseKey.RAW_BODY, JsonPrimitive(rawBody))
            put(ActionHttpResponseKey.BODY, body)
        }
    }
}
