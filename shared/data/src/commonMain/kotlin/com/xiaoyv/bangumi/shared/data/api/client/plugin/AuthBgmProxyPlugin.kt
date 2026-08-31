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

/**
 * Bangumi 代理请求的 Cookie 转发配置。
 *
 * @param bgmUrl 读取登录 Cookie 的 Bangumi 站点地址。
 * @param proxyUrl 需要接收 Bangumi Cookie 的代理服务地址。
 */
@KtorDsl
class AuthBgmProxyPluginConfig(
    var bgmUrl: String = WebConstant.URL_BASE_WEB,
    var proxyUrl: String = WebConstant.URL_BGM_PROXY,
)

/**
 * [AuthBgmProxyPlugin]
 *
 * 将当前 Bgm 的登录信息以及 BaseUrl 转发给代理域名；
 *
 * 配置代理域名仅需要更改域名即可，无需额外的 Url 处理
 */
val AuthBgmProxyPlugin: ClientPlugin<AuthBgmProxyPluginConfig> =
    createClientPlugin("AuthBgmProxyPlugin", ::AuthBgmProxyPluginConfig) {
        val config = pluginConfig
        val client = client

        client.sendPipeline.intercept(HttpSendPipeline.State) {
            val request = context
            val url = request.url.toString()

            if (url.startsWith(config.proxyUrl, true)) {
                request.headers["BaseUrl"] = config.bgmUrl

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
