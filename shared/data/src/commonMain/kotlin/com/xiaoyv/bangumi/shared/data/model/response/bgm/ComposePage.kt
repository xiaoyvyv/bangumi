package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [ComposePage]
 *
 * @author why
 * @since 2025/1/25
 */
@Immutable
@Serializable
data class ComposePage<T>(
    @SerialName("data") val result: List<T> = emptyList(),
    @SerialName("limit") val limit: Int = 0,
    @SerialName("offset") val offset: Int = 0,
    @SerialName("total") val total: Int = 0,
)

fun <T, R> ComposePage<T>.transform(transform: (T) -> R): ComposePage<R> {
    return ComposePage(
        result = result.map(transform),
        limit = limit,
        offset = offset,
    )
}

/**
 * 循环加载分页接口，直到返回全部数据
 *
 * @param fetcher 分页数据获取函数，参数为 offset, limit
 * @param limit 每次请求条数，默认 50
 */
suspend inline fun <T> loadAllData(
    limit: Int = 100,
    crossinline fetcher: suspend (offset: Int, limit: Int) -> ComposePage<T>,
): List<T> {
    val result = mutableListOf<T>()
    var offset = 0

    while (true) {
        val page = fetcher(offset, limit)
        result.addAll(page.result)

        // 判断是否加载完
        if (result.size >= page.total || page.result.isEmpty()) {
            break
        }
        offset += limit
    }

    return result
}