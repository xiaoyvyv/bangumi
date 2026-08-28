package com.xiaoyv.bangumi.shared.data.model.response.bgm.home

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeEpisode
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubjectInterest
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class ComposeHomeProgress(
    @SerialName(value = "subject")
    val subject: ComposeSubject = ComposeSubject.Empty,

    @SerialName(value = "interest")
    val interest: ComposeSubjectInterest = ComposeSubjectInterest.Empty,

    @SerialName(value = "percent")
    val percent: Double = 0.0,

    @SerialName(value = "todayOnAir")
    val todayOnAir: Boolean = false,

    @SerialName(value = "lastUnwatchedEp")
    val lastUnwatchedEp: ComposeEpisode = ComposeEpisode.Empty,

    @SerialName(value = "eps")
    val eps: SerializeList<ComposeEpisode> = persistentListOf(),
) {
    companion object {
        val Empty = ComposeHomeProgress()
    }
}