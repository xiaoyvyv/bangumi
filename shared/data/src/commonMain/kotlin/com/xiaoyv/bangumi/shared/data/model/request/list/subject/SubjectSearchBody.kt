package com.xiaoyv.bangumi.shared.data.model.request.list.subject

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.SubjectSortSearchType
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [SubjectSearchBody]
 *
 * @author why
 * @since 2025/1/25
 */
@Immutable
@Serializable
data class SubjectSearchBody(
    @SerialName("keyword") val keyword: String,
    @SerialName("filter") val filter: SubjectSearchFilter = SubjectSearchFilter.Empty,
    @SerialName("sort") @field:SubjectSortSearchType val sort: String = SubjectSortSearchType.MATCH,
) {

    @Immutable
    @Serializable
    data class SubjectSearchFilter(
        @SerialName("air_date") val date: SerializeList<String>? = null,
        @SerialName("meta_tags") val metaTags: SerializeList<String>? = null,
        @SerialName("nsfw") val nsfw: Boolean = true,
        @SerialName("rank") val rank: SerializeList<String>? = null,
        @SerialName("rating") val rating: SerializeList<String>? = null,
        @SerialName("tags") val tags: SerializeList<String>? = null,
        @SerialName("type") val type: SerializeList<Int>? = null,
    ) {
        companion object Companion {
            val Empty = SubjectSearchFilter()
        }
    }

    companion object Companion {
        val Empty = SubjectSearchBody(keyword = "")
    }
}