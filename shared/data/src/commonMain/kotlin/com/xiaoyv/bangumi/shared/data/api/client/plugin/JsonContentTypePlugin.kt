package com.xiaoyv.bangumi.shared.data.api.client.plugin

import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.utils.EmptyContent
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.content.NullBody
import io.ktor.util.appendIfNameAbsent

/**
 * 自动为带有 Request Body 的 POST/PUT/PATCH 请求按需添加 application/json Content-Type
 * 避免影响没有 Body 的请求引发 Fastify 400 (FST_ERR_CTP_EMPTY_JSON_BODY) 或 415 报错
 *
 * 插件只在调用方尚未指定 Content-Type 且存在 Request Body 时补充请求头，
 * 因此表单、文件上传等显式声明的内容类型不会被覆盖。
 */
val JsonContentTypePlugin = createClientPlugin("JsonContentTypePlugin") {
    onRequest { request, _ ->
        val hasBody = request.body != NullBody && request.body != EmptyContent
        if (hasBody && (request.method == HttpMethod.Post || request.method == HttpMethod.Put || request.method == HttpMethod.Patch)) {
            request.headers.appendIfNameAbsent(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }
    }
}
