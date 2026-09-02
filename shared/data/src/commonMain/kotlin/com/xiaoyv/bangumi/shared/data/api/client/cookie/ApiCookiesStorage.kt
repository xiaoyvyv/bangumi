package com.xiaoyv.bangumi.shared.data.api.client.cookie

import com.xiaoyv.bangumi.shared.libnative.System

/**
 * 主业务 API 使用的持久化 CookieStorage。
 */
class ApiCookiesStorage : BaseCookiesStorage() {
    private val queries = System.database.appCookieQueries

    override fun storedCookieJsons(): List<String?> =
        queries.selectAll().executeAsList().map { it.cookie }

    override fun inTransaction(block: () -> Unit) {
        queries.transaction { block() }
    }

    override fun deleteCookie(name: String, domain: String, path: String?) {
        queries.deleteCookieByNameAndDomain(name, domain, path)
    }

    override fun insertCookie(name: String, domain: String, path: String?, expires: Long?, cookieJson: String) {
        queries.insertCookie(name, domain, path, expires, cookieJson)
    }

    override fun deleteExpiredCookies(timestamp: Long) {
        queries.deleteExpiredSqlCookie(timestamp)
    }

    override fun clearCookies() {
        queries.deleteAllCookie()
    }
}
