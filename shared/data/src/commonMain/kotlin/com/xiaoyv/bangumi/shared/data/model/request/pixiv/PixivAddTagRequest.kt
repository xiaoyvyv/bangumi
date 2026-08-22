package com.xiaoyv.bangumi.shared.data.model.request.pixiv

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 添加/删除标签请求体
 */
@Immutable
@Serializable
data class PixivAddTagRequest(
    @SerialName("tag") val tag: String = ""
) {
    companion object {
        val Empty = PixivAddTagRequest()
    }
}

/**
 * 批量删除收藏请求体
 */
@Immutable
@Serializable
data class PixivDeleteIllustsBookmarkRequest(
    @SerialName("bookmarkIds") val bookmarkIds: SerializeList<Long> = persistentListOf()
) {
    companion object {
        val Empty = PixivDeleteIllustsBookmarkRequest()
    }
}
