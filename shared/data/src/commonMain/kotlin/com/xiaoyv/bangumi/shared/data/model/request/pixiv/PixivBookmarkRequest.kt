package com.xiaoyv.bangumi.shared.data.model.request.pixiv

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 添加插画作品收藏请求体
 */
@Immutable
@Serializable
data class PixivBookmarkRequest(
    @SerialName("illust_id") val illustId: Long = 0,
    @SerialName("restrict") val restrict: Int = 0,
    @SerialName("comment") val comment: String = "",
    @SerialName("tags") val tags: SerializeList<String> = persistentListOf()
) {
    companion object {
        val Empty = PixivBookmarkRequest()
    }
}
