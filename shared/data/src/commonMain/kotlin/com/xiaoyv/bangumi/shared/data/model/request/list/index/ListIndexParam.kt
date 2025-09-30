package com.xiaoyv.bangumi.shared.data.model.request.list.index

import androidx.compose.runtime.Immutable
import com.appmattus.crypto.Algorithm
import com.xiaoyv.bangumi.shared.core.types.list.ListIndexType
import com.xiaoyv.bangumi.shared.data.model.ui.PageUI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [ListIndexParam]
 *
 * @author why
 * @since 2025/1/25
 */
@Immutable
@Serializable
data class ListIndexParam(
    @SerialName("ui") val ui: PageUI = PageUI(),

    @field:ListIndexType
    @SerialName("type")
    val type: Int = ListIndexType.UNKNOWN,

    /**
     * - [ListIndexType.Companion.USER_CREATE]     用户 创建 的目录
     * - [ListIndexType.Companion.USER_COLLECTION] 用户 收藏 的目录
     */
    @SerialName("username")
    val username: String = "",

    /**
     * [ListIndexType.Companion.BROWSER] 浏览全部目录使用的排序方式
     */
    @SerialName("browserOrder")
    val browserOrder: String = "",

    /**
     * [ListIndexType.Companion.SEARCH] 搜索目录
     */
    @SerialName("search")
    val search: IndexSearchBody = IndexSearchBody.Empty,

    /**
     * 相关的
     */
    @SerialName("related")
    val related: IndexRelatedBody = IndexRelatedBody.Empty,
) {
    val uniqueKey = Algorithm.SHA_1.hash(toString().encodeToByteArray()).toHexString()

    companion object {
        val Empty = ListIndexParam()
    }
}