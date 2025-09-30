package com.xiaoyv.bangumi.shared.data.model.request.list.mono

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.MonoCastType
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.types.PersonPositionType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class MonoSubjectBody(
    val subjectId: Long,

    /**
     * Mono 类型
     */
    @field:MonoType
    @SerialName("monoType")
    val monoType: Int = MonoType.UNKNOWN,

    @field:MonoCastType
    @SerialName("characterType")
    val monoVoiceType: Int = MonoCastType.UNKNOWN,

    @field:PersonPositionType
    @SerialName("personPosition")
    val personPosition: Long = PersonPositionType.UNKNOWN,
) {
    companion object {
        val Empty = MonoSubjectBody(subjectId = 0)
    }
}