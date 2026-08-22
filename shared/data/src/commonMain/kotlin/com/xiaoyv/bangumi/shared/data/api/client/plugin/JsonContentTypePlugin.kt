package com.xiaoyv.bangumi.shared.data.api.client.plugin

import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.util.appendIfNameAbsent

/**
 * 自动为 POST/PUT/PATCH 请求按需添加 application/json Content-Type
 * 避免影响没有 Body 的 DELETE/GET 请求引发 Fastify 415 报错
 *
 * 插件只在调用方尚未指定 Content-Type 时补充请求头，因此表单、文件上传等显式声明的内容类型
 * 不会被覆盖。DELETE 和 GET 请求保持原样，避免无请求体时被服务端误判为 JSON 请求。
 */
val JsonContentTypePlugin = createClientPlugin("JsonContentTypePlugin") {
    onRequest { request, _ ->
        if (request.method == HttpMethod.Post || request.method == HttpMethod.Put || request.method == HttpMethod.Patch) {
            request.headers.appendIfNameAbsent(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }
    }
}
