package com.xiaoyv.bangumi.shared.data.model.response.bgm.index

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.IndexCatType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeDateLong
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeBlogEntry
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeEpisode
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMono
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.topic.ComposeTopic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeIndexRelated(
    @SerialName("award") val award: String = "",
    @SerialName("cat") @field:IndexCatType val cat: Int = IndexCatType.SUBJECT,
    @SerialName("comment") val comment: String = "",
    @SerialName("createdAt") val createdAt: SerializeDateLong = 0,
    @SerialName("id") val id: Long = 0,
    @SerialName("order") val order: Int = 0,
    @SerialName("rid") val rid: Long = 0,
    @SerialName("sid") val sid: Long = 0,
    @SerialName("type") @field:SubjectType val type: Int = SubjectType.UNKNOWN,
    @SerialName("subject") val subject: ComposeSubject = ComposeSubject.Companion.Empty,
    @SerialName("episode") val episode: ComposeEpisode = ComposeEpisode.Companion.Empty,
    @SerialName("groupTopic") val groupTopic: ComposeTopic = ComposeTopic.Companion.Empty,
    @SerialName("subjectTopic") val subjectTopic: ComposeTopic = ComposeTopic.Companion.Empty,
    @SerialName("person") val person: ComposeMono = ComposeMono.Companion.Empty,
    @SerialName("character") val character: ComposeMono = ComposeMono.Companion.Empty,
    @SerialName("blog") val blog: ComposeBlogEntry = ComposeBlogEntry.Companion.Empty,
)
