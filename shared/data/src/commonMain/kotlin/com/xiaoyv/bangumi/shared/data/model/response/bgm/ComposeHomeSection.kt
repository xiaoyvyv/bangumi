package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeHomeSection(
    @SerialName("subject") val subject: ComposeSubject = ComposeSubject.Empty,
    @SerialName("watchers") val watchers: Int = 0,
)


