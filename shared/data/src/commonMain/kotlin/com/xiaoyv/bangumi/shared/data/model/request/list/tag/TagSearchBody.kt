package com.xiaoyv.bangumi.shared.data.model.request.list.tag

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class TagSearchBody(
    @SerialName("keyword")
    val keyword: String = "",
    @field:SubjectType
    @SerialName("subjectType")
    val subjectType: Int = SubjectType.ANIME,
) {
    companion object {
        val Empty = TagSearchBody()
    }
}
