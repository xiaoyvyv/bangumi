@file:OptIn(ExperimentalStdlibApi::class)
@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.api.client.plugin

import com.appmattus.crypto.Algorithm
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSetting
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.url
import io.ktor.utils.io.KtorDsl

/**
 * imp.pximg.net
 * i-f.pximg.net
 * source.pximg.net
 */
@KtorDsl
class PixivImagePluginConfig(
    var os: String = "android",
    var userAgent: String = "PixivAndroidApp/6.141.1 (Android 15; Google Pixel 7);",
    var network: ComposeSetting.NetworkConfig = ComposeSetting.NetworkConfig.Default,
)

/**
 * [PixivProxyPlugin]
 */
val PixivProxyPlugin: ClientPlugin<PixivImagePluginConfig> =
    createClientPlugin("PixivProxyPlugin", ::PixivImagePluginConfig) {
        val config = pluginConfig

        onRequest { request, _ ->
            val url = request.url.toString()
            if (url.contains("i.pximg.net")) {
                val newUrl = config.network.pixivImageHost + url.substringAfter("i.pximg.net").trimStart('/')
                request.headers["Referer"] = "https://www.pixiv.net/"
                request.url(newUrl)
            }

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
