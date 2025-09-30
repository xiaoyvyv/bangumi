package com.xiaoyv.bangumi.shared.data.model.request.list.mono

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.MonoCastType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ListPersonCastParam(
    @SerialName("personId") val personId: Long,
    @field:SubjectType val subjectType: Int = SubjectType.UNKNOWN,
    @field:MonoCastType val voiceType: Int = MonoCastType.UNKNOWN,
)
