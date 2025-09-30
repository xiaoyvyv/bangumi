package com.xiaoyv.bangumi.shared.data.model.request.list.subject

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.PersonPositionType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class SubjectPersonWorkBody(
    val personId: Long = 0,
    @field:SubjectType val subjectType: Int = SubjectType.UNKNOWN,
    @field:PersonPositionType val position: Long = PersonPositionType.UNKNOWN,
) {
    companion object {
        val Empty = SubjectPersonWorkBody()
    }
}