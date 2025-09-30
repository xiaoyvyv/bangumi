package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeEpisodeRelated(
    @SerialName("airdate") val airdate: String = "",
    @SerialName("collection") val collection: ComposeEpisode.EpCollection = ComposeEpisode.EpCollection.Empty,
    @SerialName("comment") val comment: Int = 0,
    @SerialName("desc") val desc: String = "",
    @SerialName("disc") val disc: Int = 0,
    @SerialName("duration") val duration: String = "",
    @SerialName("id") val id: Int = 0,
    @SerialName("name") val name: String = "",
    @SerialName("nameCN") val nameCN: String = "",
    @SerialName("sort") val sort: Int = 0,
    @SerialName("subject") val subject: ComposeSubject = ComposeSubject.Empty,
    @SerialName("subjectID") val subjectID: Long = 0,
    @SerialName("type") val type: Int = 0,
) {
    companion object Companion {
        val Empty = ComposeEpisodeRelated()
    }
}