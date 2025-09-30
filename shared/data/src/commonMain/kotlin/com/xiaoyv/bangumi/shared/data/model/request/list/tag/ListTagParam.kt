package com.xiaoyv.bangumi.shared.data.model.request.list.tag

import androidx.compose.runtime.Immutable
import com.appmattus.crypto.Algorithm
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.types.list.ListTagType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Immutable
@Serializable
data class ListTagParam(

    @field:ListTagType
    @SerialName("type")
    val type: Int = ListTagType.UNKNOWN,

    /**
     * [ListTagType.BROWSER] 浏览类似全部标签时使用
     */
    @SerialName("subjectType")
    val subjectType: Int = SubjectType.ANIME,

    /**
     * [ListTagType.SEARCH] 搜索标签
     */
    @SerialName("search")
    val search: TagSearchBody = TagSearchBody.Empty,
) {
    val uniqueKey = Algorithm.SHA_1.hash(toString().encodeToByteArray()).toHexString()

    companion object {
        val Empty = ListTagParam()
    }
}