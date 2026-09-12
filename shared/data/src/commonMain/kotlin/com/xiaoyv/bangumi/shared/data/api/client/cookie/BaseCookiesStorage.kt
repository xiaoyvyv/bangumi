package com.xiaoyv.bangumi.shared.data.api.client.cookie

import com.xiaoyv.bangumi.shared.core.utils.defaultJson
import com.xiaoyv.bangumi.shared.core.utils.printTrace
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.plugins.cookies.fillDefaults
import io.ktor.client.plugins.cookies.matches
import io.ktor.http.Cookie
import io.ktor.http.Url
import io.ktor.util.date.getTimeMillis
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * 基于持久化表的 CookieStorage 公共实现。
 *
 * 子类仅负责适配各自的表查询；Cookie 的解析、过期清理、匹配与写入规则统一在这里维护。
 */
abstract class BaseCookiesStorage : CookiesStorage {
    private val mutex = Mutex()

    override suspend fun get(requestUrl: Url): List<Cookie> = mutex.withLock {
        deleteExpiredCookies(getTimeMillis())
        storedCookieJsons()
            .mapNotNull(::decodeCookie)
            .filter { it.matches(requestUrl) }
    }

    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
        if (cookie.name.isBlank()) return
        val normalizedCookie = normalizeCookie(requestUrl, cookie)

        mutex.withLock {
            inTransaction {
                deleteCookie(
                    name = normalizedCookie.name,
                    domain = normalizedCookie.domain.orEmpty(),
                    path = normalizedCookie.path,
                )
                if (normalizedCookie.domain.isNullOrBlank()) return@inTransaction
                insertCookie(
                    name = normalizedCookie.name,
                    domain = normalizedCookie.domain.orEmpty(),
                    path = normalizedCookie.path,
                    expires = normalizedCookie.maxAgeOrExpires(getTimeMillis()),
                    cookieJson = defaultJson.encodeToString(normalizedCookie),
                )
            }
        }
    }

    override fun close() = Unit

    suspend fun removeAll() = mutex.withLock { clearCookies() }

    /**
     * 清理指定站点及其父域名匹配的 Cookie。
     */
    suspend fun removeCookies(requestUrl: Url) = mutex.withLock {
        deleteExpiredCookies(getTimeMillis())
        storedCookieJsons()
            .mapNotNull(::decodeCookie)
            .filter { it.matches(requestUrl) }
            .forEach { cookie ->
                deleteCookie(
                    name = cookie.name,
                    domain = cookie.domain.orEmpty(),
                    path = cookie.path,
                )
            }
    }

    /**
     * 为不同用途的 CookieStorage 提供必要的默认属性补全策略。
     */
    protected open fun normalizeCookie(requestUrl: Url, cookie: Cookie): Cookie =
        cookie.copy(path = cookie.path ?: "/").fillDefaults(requestUrl)

    protected abstract fun storedCookieJsons(): List<String?>

    protected abstract fun inTransaction(block: () -> Unit)

    protected abstract fun deleteCookie(name: String, domain: String, path: String?)

    protected abstract fun insertCookie(
        name: String,
        domain: String,
        path: String?,
        expires: Long?,
        cookieJson: String,
    )

    protected abstract fun deleteExpiredCookies(timestamp: Long)

    protected abstract fun clearCookies()

    private fun decodeCookie(cookieJson: String?): Cookie? = runCatching {
        defaultJson.decodeFromString<Cookie>(cookieJson.orEmpty())
    }.onFailure { it.printTrace() }.getOrNull()

    private fun Cookie.maxAgeOrExpires(createdAt: Long): Long? =
        maxAge?.let { createdAt + it * 1000L } ?: expires?.timestamp
}
