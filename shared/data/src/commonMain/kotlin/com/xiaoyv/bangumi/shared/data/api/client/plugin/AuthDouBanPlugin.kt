@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.api.client.plugin

import com.appmattus.crypto.Algorithm
import com.xiaoyv.bangumi.shared.data.constant.WebConstant
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import io.ktor.http.decodeURLPart
import io.ktor.http.encodeURLQueryComponent
import io.ktor.util.date.getTimeMillis
import io.ktor.utils.io.KtorDsl
import io.ktor.utils.io.core.toByteArray
import kotlin.io.encoding.Base64

/**
 * 豆瓣 API 请求签名配置。
 *
 * @param agent 豆瓣客户端 User-Agent。
 * @param key 生成 HMAC-SHA1 请求签名的密钥。
 */
@KtorDsl
class DouBanConfig(
    var agent: String = "",
    var key: String = "",
)

/**
 * [AuthDouBanPlugin]
 *
 * 仅处理豆瓣 API 请求：根据请求方法、编码后的路径和当前时间生成 `_sig`、`_ts` 查询参数，
 * 同时移除不适用于豆瓣的 Bearer Authorization，并写入指定 User-Agent。
 *
 * @author why
 * @since 2025/1/25
 */
val AuthDouBanPlugin: ClientPlugin<DouBanConfig> =
    createClientPlugin("AuthDouBanPlugin", ::DouBanConfig) {
        val config = pluginConfig

        onRequest { request, _ ->
            request.addDouBanHeaders(config)
        }
    }

/**
 * 添加默认请求头的扩展函数
 */
private fun HttpRequestBuilder.addDouBanHeaders(config: DouBanConfig): HttpRequestBuilder {
    if (url.toString().contains(WebConstant.URL_BASE_API_DOUBAN, true)) {
        val (sig, ts) = generateSign(url.toString(), method.value, config.key)
        url {
            parameters.append("_sig", sig)
            parameters.append("_ts", ts)
        }
        headers.remove(HttpHeaders.Authorization)
        headers[HttpHeaders.UserAgent] = config.agent
    }
    return this
}

/**
 * - 签名: encodedPath -> /api/v2/search/suggestion
 * - 签名: decode -> /api/v2/search/suggestion
 * - 签名: paramString -> GET&%2Fapi%2Fv2%2Fsearch%2Fsuggestion&1747492232
 * - 签名: sign -> eeEaxLAmJC/MpQZSVmDYeXjnuOI=
 */
private fun generateSign(
    requestUrl: String,
    method: String,
    key: String,
    time: Long = getTimeMillis(),
): Pair<String, String> {
    val encodedPath = Url(requestUrl).segments
        .joinToString("/")
        .trim('/')
        .let { "/$it" }
    val decode = encodedPath.decodeURLPart()
    val builder = StringBuilder(method.uppercase())
    builder.append("&")
    builder.append(decode.encodeURLQueryComponent(encodeFull = true))

    val currentSeconds = time / 1000
    builder.append("&")
    builder.append(currentSeconds)

    val paramString = builder.toString()
    val bytes = Algorithm.SHA_1
        .createHmac(key.toByteArray())
        .digest(paramString.encodeToByteArray())
    return Pair(Base64.Mime.encode(bytes), currentSeconds.toString())
}
