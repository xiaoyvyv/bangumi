package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class ComposeCharacterSubjectRelation(
    @SerialName("type") val type: Int = 0,
    @SerialName("subject") val subject: ComposeSubject = ComposeSubject.Empty,
) {
    companion object {
        val Empty = ComposeCharacterSubjectRelation()
    }
}