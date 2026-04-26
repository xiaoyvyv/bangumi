package com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeTimelineBatch(
    @SerialName("epsTotal") val epsTotal: String = "",
    @SerialName("epsUpdate") val epsUpdate: Int = 0,
    @SerialName("subject") val subject: ComposeSubject = ComposeSubject.Empty,
    @SerialName("volsTotal") val volsTotal: String = "",
    @SerialName("volsUpdate") val volsUpdate: Int = 0,
) {
    companion object {
        val Empty = ComposeTimelineBatch()
    }
}