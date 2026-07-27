package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [ComposeMonoCast]
 *
 * 角色出演条目中的CV（声优）信息
 * 对应 next.bgm.tv API 中 character casts 的嵌套结构
 *
 * @since 2025/7/27
 */
@Immutable
@Serializable
data class ComposeMonoCast(
    @SerialName("person") val person: ComposeMono = ComposeMono.Empty,
    @SerialName("relation") val relation: String = "",
    @SerialName("summary") val summary: String = "",
) {
    companion object {
        val Empty = ComposeMonoCast()
    }
}
