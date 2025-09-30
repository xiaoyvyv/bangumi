package com.xiaoyv.bangumi.shared.data.model.request.list.blog

import androidx.compose.runtime.Immutable
import com.appmattus.crypto.Algorithm
import com.xiaoyv.bangumi.shared.core.types.SubjectWebPath
import com.xiaoyv.bangumi.shared.core.types.list.ListBlogType
import com.xiaoyv.bangumi.shared.data.model.ui.PageUI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [ListBlogParam]
 *
 * @author why
 * @since 2025/1/25
 */
@Immutable
@Serializable
data class ListBlogParam(
    @SerialName("ui") val ui: PageUI = PageUI(),

    @field:ListBlogType
    @SerialName("type")
    val type: Int = ListBlogType.UNKNOWN,

    /**
     * [ListBlogType.USER_CREATE] 用户的日志
     */
    @SerialName("username")
    val username: String = "",

    /**
     * [ListBlogType.SUBJECT_RELATED] 条目相关的日志
     */
    @SerialName("subject_id")
    val subjectId: Long = 0,

    /**
     * [ListBlogType.BROWSER] 浏览全站日志
     */
    @field:SubjectWebPath
    @SerialName("browser")
    val browser: String = "",
) {
    val uniqueKey = Algorithm.SHA_1.hash(toString().encodeToByteArray()).toHexString()

    companion object {
        val Empty = ListBlogParam()
    }
}