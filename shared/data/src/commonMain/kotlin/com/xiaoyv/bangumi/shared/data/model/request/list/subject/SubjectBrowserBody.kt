package com.xiaoyv.bangumi.shared.data.model.request.list.subject

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.SubjectSortBrowserType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class SubjectBrowserBody(
    @SerialName("subjectType") @field:SubjectType val subjectType: Int,
    @SerialName("sort") @field:SubjectSortBrowserType val sort: String,
    /**
     * 具体参考 https://github.com/bangumi/common 的 subject_platforms.yaml
     */
    @SerialName("cat") val cat: Int = 0,
    @SerialName("series") val series: Boolean = false,
    @SerialName("year") val year: Int = 0,
    @SerialName("month") val month: Int = 0,
    @SerialName("tags") val tags: SerializeList<String> = persistentListOf(),

    /**
     * UI 相关
     */
    @SerialName("hideSortFilter") val hideSortFilter: Boolean = false,
    @SerialName("hideDateFilter") val hideDateFilter: Boolean = false,
) {
    val uniqueKey = buildString {
        append(subjectType)
        append("-")
        append(sort)
        append("-")
        append(cat)
        append("-")
        append(series)
        append("-")
        append(year)
        append("-")
        append(month)
        append("-")
        append(tags.joinToString("|"))
        append("-")
        append(hideSortFilter)
        append("-")
        append(hideDateFilter)
    }

    companion object Companion {
        val Empty = SubjectBrowserBody(SubjectType.UNKNOWN, SubjectSortBrowserType.TRENDS)
    }
}


