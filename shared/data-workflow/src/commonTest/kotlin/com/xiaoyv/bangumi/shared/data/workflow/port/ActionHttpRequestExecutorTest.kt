package com.xiaoyv.bangumi.shared.data.workflow.port

import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionHttpRequestEffect
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.cookies.fillDefaults
import io.ktor.client.plugins.cookies.matches
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.Cookie
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.headersOf
import io.ktor.util.appendIfNameAbsent
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * HTTP 请求执行器的回归测试。
 */
class ActionHttpRequestExecutorTest {
    /**
     * 启用本地 CookieStorage 时，节点声明的 Cookie Header 应由 Ktor 原生 Cookie 插件发送。
     */
    @Test
    fun requestPreservesConfiguredCookieHeaderWithLocalCookieStorage() = runBlocking {
        val cookieHeaders = mutableListOf<String>()
        val executor = DefaultActionHttpRequestExecutor(
            httpClient = HttpClient(MockEngine { request ->
                cookieHeaders += request.headers[HttpHeaders.Cookie].orEmpty()
                respond(content = "{}", status = HttpStatusCode.OK)
            }) {
                install(HttpCookies) {
                    storage = defaultingCookiesStorage(
                        Cookie(
                            name = "SESSDATA",
                            value = "local-session",
                            domain = "example.com",
                            path = "/",
                        ),
                    )
                }
            },
            hostCookieStorge = emptyCookiesStorage,
        )

        executor.execute(
            ActionHttpRequestEffect(
                url = "https://example.com/resource",
                headers = buildJsonObject {
                    put(HttpHeaders.Cookie, JsonPrimitive("buvid4=workflow-buvid4"))
                },
                useLocalCookieStorage = true,
            ),
        )

        executor.execute(
            ActionHttpRequestEffect(
                url = "https://example.com/another-resource",
                useLocalCookieStorage = true,
            ),
        )

        assertEquals("SESSDATA=local-session; buvid4=workflow-buvid4", cookieHeaders[0])
        assertEquals("SESSDATA=local-session; buvid4=workflow-buvid4", cookieHeaders[1])
    }

    /**
     * 普通请求与下载请求共享请求构建逻辑，必须保留节点声明的 Header。
     */
    @Test
    fun requestPreservesConfiguredHeadersWithLocalCookieStorage() = runBlocking {
        var userAgent = ""
        val executor = DefaultActionHttpRequestExecutor(
            httpClient = HttpClient(MockEngine { request ->
                userAgent = request.headers[HttpHeaders.UserAgent].orEmpty()
                respond(
                    content = "{\"ok\":true}",
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            }),
            hostCookieStorge = emptyCookiesStorage,
        )

        executor.execute(
            ActionHttpRequestEffect(
                url = "https://example.com/resource",
                headers = buildJsonObject {
                    put(HttpHeaders.UserAgent, JsonPrimitive("workflow-test-agent"))
                },
                useLocalCookieStorage = true,
            ),
        )

        assertEquals("workflow-test-agent", userAgent)
    }

    /**
     * 节点声明的 Header 必须覆盖客户端默认 Header，避免默认 User-Agent 抢占节点配置。
     */
    @Test
    fun requestConfiguredHeaderOverridesClientDefaultHeader() = runBlocking {
        var userAgent = ""
        val executor = DefaultActionHttpRequestExecutor(
            httpClient = HttpClient(MockEngine { request ->
                userAgent = request.headers[HttpHeaders.UserAgent].orEmpty()
                respond(content = "{}", status = HttpStatusCode.OK)
            }) {
                defaultRequest {
                    headers.appendIfNameAbsent(HttpHeaders.UserAgent, "default-agent")
                }
            },
            hostCookieStorge = emptyCookiesStorage,
        )

        executor.execute(
            ActionHttpRequestEffect(
                url = "https://example.com/resource",
                headers = buildJsonObject {
                    put(HttpHeaders.UserAgent, JsonPrimitive("workflow-test-agent"))
                },
                useLocalCookieStorage = true,
            ),
        )

        assertEquals("workflow-test-agent", userAgent)
    }

    /**
     * 空 Header 值不能被序列化为字符串 \"null\" 并发送给服务端。
     */
    @Test
    fun requestRejectsNullHeaderValue() = runBlocking {
        val executor = DefaultActionHttpRequestExecutor(
            httpClient = HttpClient(MockEngine {
                error("空 Header 值不应发起网络请求")
            }),
            hostCookieStorge = emptyCookiesStorage,
        )

        val error = assertFailsWith<IllegalStateException> {
            executor.execute(
                ActionHttpRequestEffect(
                    url = "https://example.com/resource",
                    headers = buildJsonObject {
                        put(HttpHeaders.UserAgent, JsonPrimitive(null as String?))
                    },
                    useLocalCookieStorage = true,
                ),
            )
        }

        assertEquals("HTTP 请求头 [User-Agent] 的值不能为空", error.message)
    }

    private companion object {
        val emptyCookiesStorage = object : CookiesStorage {
            override suspend fun addCookie(requestUrl: Url, cookie: Cookie) = Unit

            override suspend fun get(requestUrl: Url): List<Cookie> = emptyList()

            override fun close() = Unit
        }

        fun defaultingCookiesStorage(vararg initialCookies: Cookie) = object : CookiesStorage {
            private val cookies = initialCookies.toMutableList()

            override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
                cookies.removeAll { item -> item.name == cookie.name && item.domain == requestUrl.host }
                cookies += cookie.copy(path = cookie.path ?: "/").fillDefaults(requestUrl)
            }

            override suspend fun get(requestUrl: Url): List<Cookie> = cookies.filter { it.matches(requestUrl) }

            override fun close() = Unit
        }
    }
}
