@file:OptIn(ExperimentalStdlibApi::class)
@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.api.client.plugin

import com.appmattus.crypto.Algorithm
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSetting
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.HttpSendPipeline
import io.ktor.http.HttpHeaders
import io.ktor.utils.io.KtorDsl

/**
 * Pixiv OAuth 请求头配置。
 *
 * @param os 上报给 Pixiv 的客户端操作系统。
 * @param userAgent Pixiv Android 客户端 User-Agent。
 * @param network 提供客户端版本和时间哈希密钥的网络配置。
 */
@KtorDsl
class AuthPixivPluginConfig(
    var os: String = "android",
    var userAgent: String = "PixivAndroidApp/6.141.1 (Android 15; Google Pixel 7);",
    var network: ComposeSetting.NetworkConfig = ComposeSetting.NetworkConfig.Default,
)

/**
 * [AuthPixivPlugin]
 *
 * 仅在请求 Pixiv OAuth 服务时添加官方客户端所需的时间、MD5 校验值、系统版本和 User-Agent，
 * 使登录及 Refresh Token 请求符合 Pixiv 客户端校验规则。其它 Pixiv API 请求保持不变。
 */
val AuthPixivPlugin: ClientPlugin<AuthPixivPluginConfig> =
    createClientPlugin("AuthPixivPlugin", ::AuthPixivPluginConfig) {
        val config = pluginConfig

        onRequest { request, _ ->
            // 授权登录添加请求头
            if (request.url.host == "oauth.secure.pixiv.net") {
                val formatted = kotlin.time.Clock.System.now().toString()
                val hashTime = Algorithm.MD5
                    .hash((formatted + config.network.pixivTimeHashSecret).encodeToByteArray())
                    .toHexString()

                request.headers["x-client-time"] = formatted
                request.headers["x-client-hash"] = hashTime
                request.headers["app-os"] = config.os
                request.headers["app-os-version"] = config.network.pixivVersion
                request.headers[HttpHeaders.UserAgent] = config.userAgent
            }
        }
    }

@KtorDsl
data class AuthPixivAjaxPluginConfig(
    var referer: String = ""
)

/**
 * [AuthPixivAjaxPlugin]
 *
 * 针对 Pixiv Web/Ajax 请求在 sendPipeline (HttpSendPipeline.State) 阶段注入 Desktop 浏览器请求头，
 * 彻底替换 defaultRequest 中追加的默认 APP/Mobile User-Agent，
 * 避免移动端 User-Agent 触发 Pixiv 服务端将 /ranking.php 等端点重定向至 H5 HTML 页面 (touch.pixiv.net)。
 */
val AuthPixivAjaxPlugin: ClientPlugin<AuthPixivAjaxPluginConfig> =
    createClientPlugin("AuthPixivAjaxPlugin", ::AuthPixivAjaxPluginConfig) {
        val config = pluginConfig
        val client = client

        client.sendPipeline.intercept(HttpSendPipeline.State) {
            val request = context
            val host = request.url.host
            if (host == "pixiv.net" || host.endsWith(".pixiv.net")) {
                request.headers[HttpHeaders.Referrer] = config.referer.ifEmpty { "https://www.pixiv.net/" }
                request.headers[HttpHeaders.UserAgent] = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36"
                request.headers[HttpHeaders.Accept] = "*/*"
                request.headers[HttpHeaders.AcceptLanguage] = "zh-CN,zh;q=0.9,en;q=0.8,ja;q=0.5"
                request.headers["Sec-Ch-Ua"] = "\"Chromium\";v=\"142\", \"Google Chrome\";v=\"142\", \"Not_A Brand\";v=\"99\""
                request.headers["Sec-Ch-Ua-Mobile"] = "?0"
                request.headers["Sec-Ch-Ua-Platform"] = "\"Windows\""
                request.headers["Sec-Fetch-Dest"] = "empty"
                request.headers["Sec-Fetch-Mode"] = "cors"
                request.headers["Sec-Fetch-Site"] = "same-origin"
            }
        }
    }
