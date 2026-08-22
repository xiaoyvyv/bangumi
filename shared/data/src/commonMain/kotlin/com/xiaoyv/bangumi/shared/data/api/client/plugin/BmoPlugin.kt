package com.xiaoyv.bangumi.shared.data.api.client.plugin

import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.shared.core.bmo.BmoAssetManager
import io.ktor.client.call.HttpClientCall
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.InternalAPI

private val assetManager by lazy {
    BmoAssetManager(
        loadManifest = {
            Res.readBytes("files/bmo/manifest.local.json").decodeToString()
        },
        loadAsset = { rawPath ->
            val relativePath = rawPath.removePrefix("./").removePrefix("/")
            runCatching { Res.readBytes("files/bmo/$relativePath") }
                .getOrNull()
                ?.takeIf { it.isNotEmpty() }
        },
    )
}

class BmoConfig {
    var url: String = "http://localhost/bmo"
    var canvasWidth: Int = 63
    var canvasHeight: Int = 63
}


/**
 * 拦截本地 BMO 表情生成请求，根据编码合成 PNG 二进制数据返回
 *
 * 请求命中配置的本地地址且包含 `code` 参数时，插件从 Compose Resources 读取 BMO 清单与图层，
 * 在内存中合成为指定尺寸的 PNG，并直接构造成功响应，不会发起网络请求。
 * 编码为空、资源缺失或无法生成图片时，请求会继续交给原有网络管线处理。
 */
@OptIn(InternalAPI::class)
val BmoPlugin: ClientPlugin<BmoConfig> = createClientPlugin("BmoPlugin", ::BmoConfig) {
    val config = pluginConfig
    on(Send) { builder ->
        val url = builder.url
        val isBmoHost = url.toString().startsWith(config.url, true)
        if (isBmoHost) {
            val code = url.parameters["code"].orEmpty()
            if (code.isNotBlank()) {
                val pngBytes = assetManager.getOrGenerateCompositeImage(
                    code = code,
                    width = config.canvasWidth,
                    height = config.canvasHeight
                )

                if (pngBytes != null) {
                    val responseData = HttpResponseData(
                        statusCode = HttpStatusCode.OK,
                        requestTime = GMTDate(),
                        headers = headersOf(
                            HttpHeaders.ContentType to listOf(ContentType.Image.PNG.toString()),
                            HttpHeaders.ContentLength to listOf(pngBytes.size.toString())
                        ),
                        version = HttpProtocolVersion.HTTP_1_1,
                        body = ByteReadChannel(pngBytes),
                        callContext = builder.executionContext
                    )

                    return@on HttpClientCall(client, builder.build(), responseData)
                }
            }
        }
        proceed(builder)
    }
}
