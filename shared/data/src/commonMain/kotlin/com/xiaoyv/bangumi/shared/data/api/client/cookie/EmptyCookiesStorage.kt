package com.xiaoyv.bangumi.shared.data.api.client.cookie

import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.http.Cookie
import io.ktor.http.Url

object EmptyCookiesStorage : CookiesStorage {
    override suspend fun get(requestUrl: Url): List<Cookie> {
        return emptyList()
    }

    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {

    }

    override fun close() {

    }
}