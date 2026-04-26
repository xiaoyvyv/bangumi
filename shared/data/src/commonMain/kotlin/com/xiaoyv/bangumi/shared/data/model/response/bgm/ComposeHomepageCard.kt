package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeHomepageCard(
    @SerialName("title") val title: String = "",
    @field:SubjectType
    @SerialName("type") val type: Int = SubjectType.UNKNOWN,
    @SerialName("subjects") val subjects: SerializeList<ComposeSubject> = persistentListOf(),
)