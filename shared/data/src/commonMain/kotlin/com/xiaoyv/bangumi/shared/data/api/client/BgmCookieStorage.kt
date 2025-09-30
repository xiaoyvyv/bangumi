package com.xiaoyv.bangumi.shared.data.api.client

import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.core.utils.defaultJson
import com.xiaoyv.bangumi.shared.core.utils.printTrace
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.plugins.cookies.matches
import io.ktor.http.Cookie
import io.ktor.http.Url
import io.ktor.util.date.getTimeMillis
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * [BgmCookieStorage]
 *
 * @author why
 * @since 2025/1/15
 */
class BgmCookieStorage : CookiesStorage {
    private val mutex = Mutex()

    private val database = System.database

    override suspend fun get(requestUrl: Url): List<Cookie> = mutex.withLock {
        cleanupExpiredCookies(getTimeMillis())

        return database.appCookieQueries.selectAll().executeAsList().mapNotNull { row ->
            val cookie = runCatching {
                defaultJson.decodeFromString<Cookie>(row.cookie.orEmpty())
            }.onFailure { it.printTrace() }.getOrNull()
            val domain = cookie?.domain
            if (domain != null && cookie.matches(requestUrl)) cookie else null
        }
    }

    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
        if (cookie.name.isBlank()) return

        mutex.withLock {
            database.appCookieQueries.transaction {
                val domain = cookie.domain.orEmpty()

                // 删除旧的 Cookie
                database.appCookieQueries.deleteCookieByNameAndDomain(
                    name = cookie.name,
                    domain = domain,
                    path = cookie.path
                )

                // 插入新的 Cookie
                if (domain.isNotBlank()) database.appCookieQueries.insertCookie(
                    name = cookie.name,
                    domain = domain,
                    path = cookie.path,
                    expires = cookie.maxAgeOrExpires(getTimeMillis()),
                    cookie = defaultJson.encodeToString(cookie)
                )
            }
        }
    }

    override fun close() {}

    suspend fun removeAll() = mutex.withLock {
        database.appCookieQueries.deleteAllCookie()
    }

    private fun cleanupExpiredCookies(timestamp: Long) {
        database.appCookieQueries.deleteExpiredSqlCookie(timestamp)
    }

    private fun Cookie.maxAgeOrExpires(createdAt: Long): Long? =
        maxAge?.let { createdAt + it * 1000L } ?: expires?.timestamp
}