package com.xiaoyv.bangumi.shared.data.model.request.list.mono

import androidx.compose.runtime.Immutable
import com.appmattus.crypto.Algorithm
import com.xiaoyv.bangumi.shared.core.types.list.ListMonoType
import com.xiaoyv.bangumi.shared.data.model.ui.PageUI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [com.xiaoyv.bangumi.shared.data.model.request.list.subject.SubjectSearchBody]
 *
 * @author why
 * @since 2025/1/25
 */
@Immutable
@Serializable
data class ListMonoParam(
    @SerialName("ui") val ui: PageUI = PageUI(),

    @field:ListMonoType
    @SerialName("type")
    val type: Int = ListMonoType.UNKNOWN,

    /**
     * [ListMonoType.BROWSER] 浏览人物的参数
     */
    @SerialName("browser")
    val browser: MonoBrowserBody = MonoBrowserBody.Empty,

    /**
     * [ListMonoType.USER_COLLECTION] 用户收藏人物的参数
     */
    @SerialName("collection")
    val collection: MonoCollectionBody = MonoCollectionBody.Empty,

    /**
     * - [ListMonoType.SUBJECT_CHARACTER] 条目的角色
     * - [ListMonoType.SUBJECT_PERSON]    条目的人物
     */
    @SerialName("subject")
    val subject: MonoSubjectBody = MonoSubjectBody.Empty,

    /**
     * [ListMonoType.SEARCH_PERSON] 搜索的人物
     * [ListMonoType.SEARCH_CHARACTER] 搜索的角色
     */
    @SerialName("search")
    val search: MonoSearchBody = MonoSearchBody.Empty,
) {
    val uniqueKey = Algorithm.SHA_1.hash(toString().encodeToByteArray()).toHexString()

    companion object {
        val Empty = ListMonoParam()
    }
}