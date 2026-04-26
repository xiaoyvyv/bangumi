package com.xiaoyv.bangumi.shared.data.model.response.bgm.index

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeIndexStats(
    @SerialName("blog") val blog: Int = 0,
    @SerialName("character") val character: Int = 0,
    @SerialName("episode") val episode: Int = 0,
    @SerialName("groupTopic") val groupTopic: Int = 0,
    @SerialName("person") val person: Int = 0,
    @SerialName("subject") val subject: StatsSubject = StatsSubject(),
    @SerialName("subjectTopic") val subjectTopic: Int = 0,
) {
    @Immutable
    @Serializable
    data class StatsSubject(
        @SerialName("anime") val anime: Int = 0,
        @SerialName("book") val book: Int = 0,
        @SerialName("game") val game: Int = 0,
        @SerialName("music") val music: Int = 0,
        @SerialName("real") val real: Int = 0,
    )
}