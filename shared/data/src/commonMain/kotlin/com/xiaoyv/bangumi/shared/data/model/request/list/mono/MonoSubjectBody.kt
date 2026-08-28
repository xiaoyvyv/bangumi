package com.xiaoyv.bangumi.shared.data.model.request.list.mono

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.MonoCastType
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.types.PersonType
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

    @field:PersonType
    @SerialName("personPosition")
    val personPosition: Int = PersonType.UNKNOWN,
) {
    companion object {
        val Empty = MonoSubjectBody(subjectId = 0)
    }
}