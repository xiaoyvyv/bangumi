package com.xiaoyv.bangumi.shared.data.api.client.plugin

import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.util.appendIfNameAbsent

/**
 * 自动为 POST/PUT/PATCH 请求按需添加 application/json Content-Type
 * 避免影响没有 Body 的 DELETE/GET 请求引发 Fastify 415 报错
 */
val JsonContentTypePlugin = createClientPlugin("JsonContentTypePlugin") {
    onRequest { request, _ ->
        if (request.method == HttpMethod.Post || request.method == HttpMethod.Put || request.method == HttpMethod.Patch) {
            request.headers.appendIfNameAbsent(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }
    }
}