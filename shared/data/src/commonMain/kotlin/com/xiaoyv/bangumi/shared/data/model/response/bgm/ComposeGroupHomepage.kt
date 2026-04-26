package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.topic.ComposeTopic
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class ComposeGroupHomepage(
    @SerialName("hotGroups") val hotGroups: SerializeList<ComposeGroup> = persistentListOf(),
    @SerialName("newestGroups") val newestGroups: SerializeList<ComposeGroup> = persistentListOf(),
    @SerialName("newestTopics") val newestTopics: SerializeList<ComposeTopic> = persistentListOf(),
)