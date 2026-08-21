@file:OptIn(ExperimentalStdlibApi::class)
@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.api.client.plugin

import com.xiaoyv.bangumi.shared.data.constant.WebConstant
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.cookies.cookies
import io.ktor.client.request.HttpSendPipeline
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import io.ktor.utils.io.KtorDsl

@KtorDsl
class AuthProxyCookiePluginConfig(
    var bgmUrl: String = WebConstant.URL_BASE_WEB,
    var proxyUrl: String = WebConstant.URL_BGM_PROXY,
)

/**
 * [AuthProxyCookiePlugin]
 */
val AuthProxyCookiePlugin: ClientPlugin<AuthProxyCookiePluginConfig> =
    createClientPlugin("AuthProxyCookiePlugin", ::AuthProxyCookiePluginConfig) {
        val config = pluginConfig
        val client = client

        client.sendPipeline.intercept(HttpSendPipeline.State) {
            val request = context
            val url = request.url.toString()

            if (url.startsWith(config.proxyUrl, true)) {
                val cookies = client.cookies(Url(config.bgmUrl))
                if (cookies.isNotEmpty()) {
                    val bgmCookieHeader = cookies.joinToString("; ") { "${it.name}=${it.value}" }
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
