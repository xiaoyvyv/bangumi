package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeDateLong
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeIndexRelated(
    @SerialName("award") val award: String = "",
    @SerialName("cat") val cat: Int = 0,
    @SerialName("character") val character: ComposeMono = ComposeMono.Empty,
    @SerialName("comment") val comment: String = "",
    @SerialName("createdAt") val createdAt: SerializeDateLong = 0,
    @SerialName("episode") val episode: ComposeEpisodeRelated = ComposeEpisodeRelated.Empty,
    @SerialName("id") val id: Long = 0,
    @SerialName("order") val order: Int = 0,
    @SerialName("person") val person: ComposeMono = ComposeMono.Empty,
    @SerialName("rid") val rid: Int = 0,
    @SerialName("sid") val sid: Int = 0,
    @SerialName("subject") val subject: ComposeSubject = ComposeSubject.Empty,
    @SerialName("type") val type: Int = 0,
)