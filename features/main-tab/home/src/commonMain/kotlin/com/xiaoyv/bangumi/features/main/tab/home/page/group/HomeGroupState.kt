package com.xiaoyv.bangumi.features.main.tab.home.page.group

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeGroup
import com.xiaoyv.bangumi.shared.data.model.response.bgm.topic.ComposeTopic
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class HomeGroupState(
    val hotGroups: SerializeList<ComposeGroup> = persistentListOf(),
    val newestGroups: SerializeList<ComposeGroup> = persistentListOf(),
    val newestTopics: SerializeList<ComposeTopic> = persistentListOf(),
)


