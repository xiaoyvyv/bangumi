package com.xiaoyv.bangumi.shared.data.model.response.bgm.home

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeMap
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeGroup
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeHomeSection
import com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline.ComposeTimeline
import com.xiaoyv.bangumi.shared.data.model.response.bgm.topic.ComposeTopicDetail
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class ComposeHome(
    @SerialName(value = "progress")
    val progress: SerializeList<ComposeHomeProgress> = persistentListOf(),

    @SerialName(value = "timeline")
    val timeline: SerializeList<ComposeTimeline> = persistentListOf(),

    @SerialName(value = "groupTopics")
    val groupTopics: SerializeList<ComposeTopicDetail> = persistentListOf(),

    @SerialName(value = "famousGroups")
    val famousGroups: SerializeList<ComposeGroup> = persistentListOf(),

    @SerialName(value = "hotSubjectTopics")
    val hotSubjectTopics: SerializeList<ComposeTopicDetail> = persistentListOf(),

    @SerialName(value = "calendar")
    val calendar: SerializeMap<String, SerializeList<ComposeHomeSection>> = persistentMapOf()
) {
    companion object {
        val Empty = ComposeHome()
    }
}