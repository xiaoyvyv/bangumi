package com.xiaoyv.bangumi.shared.data.workflow.node.effect

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpBodyType
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject

/**
 * HTTP 请求节点使用的标准请求参数。
 *
 * @param url 已解析并校验的绝对请求地址。
 * @param method 规范化后的 HTTP 请求方法。
 * @param query 请求查询参数。
 * @param headers 请求头。
 * @param body 请求体；为空时不发送请求体。
 * @param bodyType 请求体编码类型。
 * @param contentType 可选 Content-Type 覆盖值。
 * @param timeoutMillis 单次请求超时毫秒数；为空时使用客户端默认配置。
 * @param retryCount 发生可重试失败时额外尝试的次数。
 * @param retryDelayMillis 两次重试之间的等待时间，单位为毫秒。
 * @param useLocalCookieStorage 是否使用应用持久化的本地 Cookie 存储。
 */
@Immutable
data class ActionHttpRequestEffect(
    /**
     * 已解析并校验的绝对请求地址。
     */
    val url: String,
    /**
     * 规范化后的 HTTP 请求方法。
     */
    val method: String = "GET",
    /**
     * 请求查询参数。
     */
    val query: JsonObject = JsonObject(emptyMap()),
    /**
     * 请求头。
     */
    val headers: JsonObject = JsonObject(emptyMap()),
    /**
     * 请求体；为 JSON null 时不发送请求体。
     */
    val body: JsonElement = JsonNull,
    /**
     * 请求体编码类型。
     */
    val bodyType: String = ActionHttpBodyType.JSON,
    /**
     * 可选 Content-Type 覆盖值。
     */
    val contentType: String? = null,
    /**
     * 单次请求超时毫秒数；为空时使用客户端默认配置。
     */
    val timeoutMillis: Long? = null,
    /**
     * 发生可重试失败时额外尝试的次数。
     */
    val retryCount: Int = 0,
    /**
     * 两次重试之间的等待时间，单位为毫秒。
     */
    val retryDelayMillis: Long = 0,
    /**
     * 是否使用应用持久化的本地 Cookie 存储。
     *
     * 启用后仅会向 URL 域名匹配的请求附带 Cookie，同时会保存服务器返回的 Cookie；该能力
     * 需要由工作流额外声明 [com.xiaoyv.bangumi.shared.data.workflow.model.ActionCapability.NETWORK_LOCAL_COOKIE_ACCESS]。
     */
    val useLocalCookieStorage: Boolean = false,
)
