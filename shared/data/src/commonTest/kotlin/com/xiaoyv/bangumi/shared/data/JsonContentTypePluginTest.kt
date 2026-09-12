package com.xiaoyv.bangumi.shared.data

import com.xiaoyv.bangumi.shared.data.model.request.bgm.LikeCommentParam
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.setBody
import io.ktor.client.utils.EmptyContent
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.content.NullBody
import io.ktor.util.appendIfNameAbsent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class JsonContentTypePluginTest {

    private fun HttpRequestBuilder.applyJsonContentTypePlugin() {
        val hasBody = body != NullBody && body != EmptyContent
        if (hasBody && (method == HttpMethod.Post || method == HttpMethod.Put || method == HttpMethod.Patch)) {
            headers.appendIfNameAbsent(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }
    }

    @Test
    fun testJsonContentTypeHeaderAddedOnlyWhenBodyIsPresent() {
        // PUT with body -> should have application/json
        val putWithBody = HttpRequestBuilder().apply {
            method = HttpMethod.Put
            setBody(LikeCommentParam(1))
        }
        putWithBody.applyJsonContentTypePlugin()
        assertEquals(ContentType.Application.Json.toString(), putWithBody.headers[HttpHeaders.ContentType])

        // PUT without body -> should NOT have application/json
        val putWithoutBody = HttpRequestBuilder().apply {
            method = HttpMethod.Put
        }
        putWithoutBody.applyJsonContentTypePlugin()
        assertNull(putWithoutBody.headers[HttpHeaders.ContentType])

        // POST with body -> should have application/json
        val postWithBody = HttpRequestBuilder().apply {
            method = HttpMethod.Post
            setBody(LikeCommentParam(1))
        }
        postWithBody.applyJsonContentTypePlugin()
        assertEquals(ContentType.Application.Json.toString(), postWithBody.headers[HttpHeaders.ContentType])

        // POST without body -> should NOT have application/json
        val postWithoutBody = HttpRequestBuilder().apply {
            method = HttpMethod.Post
        }
        postWithoutBody.applyJsonContentTypePlugin()
        assertNull(postWithoutBody.headers[HttpHeaders.ContentType])

        // GET without body -> should NOT have application/json
        val getRequest = HttpRequestBuilder().apply { method = HttpMethod.Get }
        getRequest.applyJsonContentTypePlugin()
        assertNull(getRequest.headers[HttpHeaders.ContentType])

        // DELETE without body -> should NOT have application/json
        val deleteRequest = HttpRequestBuilder().apply { method = HttpMethod.Delete }
        deleteRequest.applyJsonContentTypePlugin()
        assertNull(deleteRequest.headers[HttpHeaders.ContentType])
    }
}
