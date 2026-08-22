package com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray

/**
 * Pixiv API 响应基础结构
 * @param T 响应体类型
 */
@Immutable
@Serializable
data class ComposePixivResponse<T>(
    @SerialName("body") val body: T? = null,
    @SerialName("error") val error: Boolean = false,
    @SerialName("message") val message: String = ""
) {
    companion object {
        val Empty = ComposePixivResponse<Nothing>()
    }
}

/**
 * 用于接收空数组响应的类型
 */
typealias ComposePixivEmptyArrayResponse = ComposePixivResponse<JsonArray>
