package com.xiaoyv.bangumi.shared.data.model.request.list.group

import androidx.compose.runtime.Immutable
import com.appmattus.crypto.Algorithm
import com.xiaoyv.bangumi.shared.core.types.list.ListGroupType
import com.xiaoyv.bangumi.shared.data.model.ui.PageUI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [ListGroupParam]
 *
 * @author why
 * @since 2025/1/25
 */
@Immutable
@Serializable
data class ListGroupParam(
    @SerialName("ui") val ui: PageUI = PageUI(),

    @field:ListGroupType
    @SerialName("type")
    val type: Int = ListGroupType.UNKNOWN,

    /**
     * [ListGroupType.USER] 用户加入的小组
     */
    @SerialName("username")
    val username: String = "",

    /**
     * [ListGroupType.BROWSER] 浏览全站小组，分类
     */
    val browser: ListGroupBrowserParam = ListGroupBrowserParam.Empty,
) {
    val uniqueKey = Algorithm.SHA_1.hash(toString().encodeToByteArray()).toHexString()

    companion object {
        val Empty = ListGroupParam()
    }
}
