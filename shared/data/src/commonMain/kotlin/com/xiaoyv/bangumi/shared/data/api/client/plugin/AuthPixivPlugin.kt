@file:OptIn(ExperimentalStdlibApi::class)
@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.api.client.plugin

import com.appmattus.crypto.Algorithm
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSetting
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.createClientPlugin
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
            val url = request.url.toString()

            // 授权登录添加请求头
            if (url.contains("oauth.secure.pixiv.net")) {
                val formatted = kotlin.time.Clock.System.now().toString()
                val hashTime = Algorithm.MD5
                    .hash((formatted + config.network.pixivTimeHashSecret).encodeToByteArray())
                    .toHexString()

                request.headers["x-client-time"] = formatted
                request.headers["x-client-hash"] = hashTime
                request.headers["app-os"] = config.os
                request.headers["app-os-version"] = config.network.pixivVersion
                request.headers["user-agent"] = config.userAgent
            }
        }
    }
