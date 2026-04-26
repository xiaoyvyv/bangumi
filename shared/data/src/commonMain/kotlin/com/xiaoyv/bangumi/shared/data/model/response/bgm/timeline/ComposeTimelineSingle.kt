package com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeEpisodeRelated
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeTimelineSingle(
    @SerialName("episode") val episode: ComposeEpisodeRelated = ComposeEpisodeRelated.Empty,
    @SerialName("subject") val subject: ComposeSubject = ComposeSubject.Empty,
) {
    companion object {
        val Empty = ComposeTimelineSingle()
    }
}

