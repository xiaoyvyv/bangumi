@file:OptIn(ExperimentalStdlibApi::class)
@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.api.client.plugin

import com.xiaoyv.bangumi.shared.data.constant.WebConstant
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.cookies.cookies
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import io.ktor.utils.io.KtorDsl

@KtorDsl
class BgmProxyCookiePluginConfig(
    var bgmUrl: String = WebConstant.URL_BASE_WEB,
    var proxyUrl: String = WebConstant.URL_BGM_PROXY,
)

/**
 * [AuthProxyCookiePlugin]
 *
 * 将 bgmUrl 的本地 cookie 透传给 proxyUrl
 */
val AuthProxyCookiePlugin: ClientPlugin<BgmProxyCookiePluginConfig> =
    createClientPlugin("AuthProxyCookiePlugin", ::BgmProxyCookiePluginConfig) {
        val config = pluginConfig
        val httpClient = this.client

        onRequest { request, _ ->
            val url = request.url.toString()

            if (url.startsWith(config.proxyUrl, true)) {
                val bgmCookies = httpClient.cookies(Url(config.bgmUrl))
                if (bgmCookies.isNotEmpty()) {
                    val bgmCookieHeader = bgmCookies.joinToString("; ") { "${it.name}=${it.value}" }
                    val existingCookie = request.headers[HttpHeaders.Cookie]
                    if (!existingCookie.isNullOrBlank()) {
                        request.headers[HttpHeaders.Cookie] = "$existingCookie; $bgmCookieHeader"
                    } else {
                        request.headers[HttpHeaders.Cookie] = bgmCookieHeader
                    }
                }
            }
        }
    }
