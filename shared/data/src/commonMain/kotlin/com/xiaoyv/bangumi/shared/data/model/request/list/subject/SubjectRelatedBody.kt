package com.xiaoyv.bangumi.shared.data.model.request.list.subject

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class SubjectRelatedBody(
    @SerialName("subjectId") val subjectId: Long = 0,
) {
    companion object {
        val Empty = SubjectRelatedBody()
    }
}
