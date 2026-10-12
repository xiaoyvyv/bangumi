package com.xiaoyv.bangumi.shared.data

import com.xiaoyv.bangumi.shared.core.exception.ApiHttpException
import com.xiaoyv.bangumi.shared.core.utils.defaultJson
import com.xiaoyv.bangumi.shared.data.api.client.createApiKtorfit
import com.xiaoyv.bangumi.shared.data.api.client.createDocumentKtorfit
import com.xiaoyv.bangumi.shared.data.api.client.plugin.JsonContentTypePlugin
import com.xiaoyv.bangumi.shared.data.api.createBgmWebApi
import com.xiaoyv.bangumi.shared.data.api.next.createRelationshipApi
import com.xiaoyv.bangumi.shared.data.api.next.createUserApi
import com.xiaoyv.bangumi.shared.data.parser.bgm.UserParser
import com.xiaoyv.bangumi.shared.data.repository.impl.UserBlocklistUpdater
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.http.parseQueryString
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UserBlocklistUpdaterTest {
    @Test
    fun successfulApiDoesNotRequestTheWebsite() = runBlocking {
        Harness(primaryStatus = HttpStatusCode.OK).use { harness ->
            val list = harness.updater.update("target", blocked = true)
            assertEquals(listOf(42L), list.blocklist)
            assertEquals(listOf("next.bgm.tv"), harness.requests.map { it.url.host })
        }
    }

    @Test
    fun serverErrorUsesFreshWebsiteBlockFormAndReadsBackTheResult() = runBlocking {
        Harness().use { harness ->
            val list = harness.updater.update("target", blocked = true)
            assertEquals(listOf(42L), list.blocklist)
            val submit = harness.requests.single { it.url.host == "bgm.tv" && it.method == HttpMethod.Post }
            val fields = parseQueryString(submit.body.toByteArray().decodeToString())
            assertEquals("target", fields["ignore_user"])
            assertEquals("fresh-hash", fields["formhash"])
            assertEquals("submit_ignore", fields["submit_ignore"])
            assertEquals(3, fields.names().size)
            assertTrue(submit.body.contentType.toString().startsWith(ContentType.Application.FormUrlEncoded.toString()))
            assertEquals("/p1/blocklist", harness.requests.last().url.encodedPath)
        }
    }

    @Test
    fun serverErrorUnblocksOnlyTheTargetRowUsingItsDecodedLink() = runBlocking {
        Harness(initiallyBlocked = true).use { harness ->
            val list = harness.updater.update("target", blocked = false)
            assertTrue(list.blocklist.isEmpty())
            val cancel = harness.requests.single { it.url.encodedPath == "/settings/privacy/unblock" }
            assertEquals(HttpMethod.Get, cancel.method)
            assertEquals("42", cancel.url.parameters["id"])
            assertEquals("fresh-hash", cancel.url.parameters["gh"])
            assertFalse(harness.requests.any { it.url.parameters["id"] == "99" })
        }
    }

    @Test
    fun nestedTablesDoNotSelectAnotherUsersCancellationLink() = runBlocking {
        val nestedPage = PRIVACY_PAGE
            .replace("<table class=\"settings\"><tbody>", "<table class=\"settings\"><tbody><tr><td><table><tbody>")
            .replace("</tbody></table>", "</tbody></table></td></tr></tbody></table>")
        Harness(initiallyBlocked = true, privacyPage = nestedPage).use { harness ->
            assertTrue(harness.updater.update("target", false).blocklist.isEmpty())
            val cancel = harness.requests.single { it.url.encodedPath == "/settings/privacy/unblock" }
            assertEquals("42", cancel.url.parameters["id"])
        }
    }

    @Test
    fun serverErrorAfterACompletedWriteDoesNotRepeatTheOperation() = runBlocking {
        for (blocked in listOf(true, false)) {
            Harness(initiallyBlocked = blocked).use { harness ->
                val list = harness.updater.update("target", blocked)
                assertEquals(blocked, 42L in list.blocklist)
                assertFalse(harness.requests.any { it.url.host == "bgm.tv" })
            }
        }
    }

    @Test
    fun loginPermissionNotFoundAndRateLimitErrorsDoNotFallBack() = runBlocking {
        for (status in listOf(HttpStatusCode.Unauthorized, HttpStatusCode.Forbidden, HttpStatusCode.NotFound, HttpStatusCode.TooManyRequests)) {
            Harness(primaryStatus = status).use { harness ->
                val error = assertFailsWith<ApiHttpException> { harness.updater.update("target", true) }
                assertEquals(status.value, error.code)
                assertEquals(1, harness.requests.size)
            }
        }
    }

    @Test
    fun expiredWebsiteLoginDoesNotSubmitOrReportSuccess() = runBlocking {
        Harness(privacyPage = "<div class=guest><a href=/login>登录</a></div>").use { harness ->
            val error = assertFailsWith<ApiHttpException> { harness.updater.update("target", true) }
            assertEquals(401, error.code)
            assertFalse(harness.requests.any { it.url.host == "bgm.tv" && it.method == HttpMethod.Post })
        }
    }

    @Test
    fun missingFormHashDoesNotSubmit() = runBlocking {
        Harness(privacyPage = PRIVACY_PAGE.replace("fresh-hash", "")).use { harness ->
            assertFailsWith<IllegalArgumentException> { harness.updater.update("target", true) }
            assertFalse(harness.requests.any { it.url.host == "bgm.tv" && it.method == HttpMethod.Post })
        }
    }

    @Test
    fun missingTargetCancellationLinkDoesNotCancelAnotherUser() = runBlocking {
        Harness(initiallyBlocked = true, privacyPage = PRIVACY_PAGE.replace("/user/42", "/user/77")).use { harness ->
            assertFailsWith<IllegalArgumentException> { harness.updater.update("target", false) }
            assertFalse(harness.requests.any { it.url.encodedPath == "/settings/privacy/unblock" })
        }
    }

    @Test
    fun foreignCancellationLinkIsRejectedBeforeRequestingIt() = runBlocking {
        Harness(initiallyBlocked = true, privacyPage = PRIVACY_PAGE.replace("/settings/privacy/unblock?id=42", "https://example.org/unblock?id=42")).use { harness ->
            assertFailsWith<IllegalArgumentException> { harness.updater.update("target", false) }
            assertFalse(harness.requests.any { it.url.host == "example.org" })
        }
    }

    @Test
    fun unchangedServerListDoesNotReportSuccessAfterWebsiteSubmission() = runBlocking {
        for (blocked in listOf(true, false)) {
            Harness(initiallyBlocked = !blocked, websiteWrites = false).use { harness ->
                assertFailsWith<IllegalStateException> { harness.updater.update("target", blocked) }
                assertEquals("/p1/blocklist", harness.requests.last().url.encodedPath)
            }
        }
    }

    @Test
    fun cancellationDoesNotStartAWebsiteFallback() = runBlocking {
        Harness(cancelPrimary = true).use { harness ->
            assertFailsWith<CancellationException> { harness.updater.update("target", true) }
            assertEquals(1, harness.requests.size)
        }
    }

    private class Harness(
        private val primaryStatus: HttpStatusCode = HttpStatusCode.InternalServerError,
        initiallyBlocked: Boolean = false,
        private val privacyPage: String = PRIVACY_PAGE,
        private val websiteWrites: Boolean = true,
        private val cancelPrimary: Boolean = false,
    ) : AutoCloseable {
        val requests = mutableListOf<HttpRequestData>()
        private var serverBlocked = initiallyBlocked
        private val client = HttpClient(MockEngine { request ->
            requests += request
            when {
                request.url.host == "next.bgm.tv" && request.method != HttpMethod.Get -> {
                    if (cancelPrimary) throw CancellationException("Cancelled")
                    if (primaryStatus == HttpStatusCode.OK) {
                        serverBlocked = request.method == HttpMethod.Put
                        respond(blocklistJson(), primaryStatus, JSON_HEADERS)
                    } else {
                        respond("""{"message":"Server failure","statusCode":${primaryStatus.value}}""", primaryStatus, JSON_HEADERS)
                    }
                }
                request.url.encodedPath == "/p1/users/target" -> respond("""{"id":42,"username":"target"}""", headers = JSON_HEADERS)
                request.url.encodedPath == "/p1/blocklist" -> respond(blocklistJson(), headers = JSON_HEADERS)
                request.url.host == "bgm.tv" && request.method == HttpMethod.Post -> {
                    if (websiteWrites) serverBlocked = true
                    respond(privacyPage, headers = HTML_HEADERS)
                }
                request.url.encodedPath == "/settings/privacy/unblock" -> {
                    if (websiteWrites) serverBlocked = false
                    respond(privacyPage, headers = HTML_HEADERS)
                }
                request.url.host == "bgm.tv" && request.url.encodedPath == "/settings/privacy" -> respond(privacyPage, headers = HTML_HEADERS)
                else -> error("Unexpected request: ${request.method} ${request.url}")
            }
        }) {
            install(ContentNegotiation) { json(defaultJson) }
            install(JsonContentTypePlugin)
        }
        private val nextApi = createApiKtorfit(client, "https://next.bgm.tv/")
        val updater = UserBlocklistUpdater(
            nextApi.createRelationshipApi(),
            nextApi.createUserApi(),
            createDocumentKtorfit(client, "https://bgm.tv/").createBgmWebApi(),
            UserParser(),
        )

        private fun blocklistJson() = if (serverBlocked) """{"blocklist":[42]}""" else """{"blocklist":[]}"""

        override fun close() = client.close()
    }

    private companion object {
        val JSON_HEADERS = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        val HTML_HEADERS = headersOf(HttpHeaders.ContentType, ContentType.Text.Html.toString())
        const val PRIVACY_PAGE = """
            <html><body><div id="dock"><a href="/user/me">我的时光机</a></div>
            <form action="/settings/privacy" method="post">
              <input name="formhash" value="fresh-hash"><input name="ignore_user">
              <input type="submit" name="submit_ignore" value="保存">
            </form>
            <table class="settings"><tbody>
              <tr><td valign="top"><a href="/user/other">Other</a></td>
                <td><a class="tip_i" href="/settings/privacy/unblock?id=99&amp;gh=other-hash">取消绝交</a></td></tr>
              <tr><td valign="top"><a href="/user/42">Target</a></td>
                <td><a class="tip_i" href="/settings/privacy/unblock?id=42&amp;gh=fresh-hash">取消绝交</a></td></tr>
            </tbody></table></body></html>
        """
    }
}
