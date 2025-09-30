package com.xiaoyv.bangumi.shared.data.model.request.list.subject

import androidx.compose.runtime.Immutable
import com.appmattus.crypto.Algorithm
import com.xiaoyv.bangumi.shared.core.types.list.ListSubjectType
import com.xiaoyv.bangumi.shared.data.model.ui.PageUI
import kotlinx.serialization.Serializable

/**
 * [SubjectSearchBody]
 *
 * @author why
 * @since 2025/1/25
 */
@Immutable
@Serializable
data class ListSubjectParam(
    val ui: PageUI = PageUI(),

    /**
     * 列表条目请求数据类型
     */
    @field:ListSubjectType
    val type: Int = ListSubjectType.UNKNOWN,

    /**
     * [ListSubjectType.SEARCH] 搜索条目的参数
     */
    val search: SubjectSearchBody = SubjectSearchBody.Empty,

    /**
     * [ListSubjectType.BROWSER] 浏览条目的参数
     */
    val browser: SubjectBrowserBody = SubjectBrowserBody.Empty,

    /**
     * [ListSubjectType.USER_COLLECTION] 用户收藏条目的参数
     */
    val collection: SubjectCollectionBody = SubjectCollectionBody.Empty,

    /**
     * [ListSubjectType.PERSON_WORK] 人员作品条目的参数
     */
    val personWork: SubjectPersonWorkBody = SubjectPersonWorkBody.Empty,

    /**
     * [ListSubjectType.SUBJECT_RELATED] 条目相关联的条目
     */
    val related: SubjectRelatedBody = SubjectRelatedBody.Empty,
) {
    val uniqueKey = Algorithm.SHA_1.hash(toString().encodeToByteArray()).toHexString()

    companion object {
        val Empty = ListSubjectParam()
    }
}