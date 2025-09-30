package com.xiaoyv.bangumi.features.main.tab.topic.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.RakuenTab
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf

/**
 * [TopicState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class TopicState(
    @field:RakuenTab
    val type: String = RakuenTab.ALL,
    val tabs: SerializeList<ComposeTextTab<String>> = persistentListOf(),
    val actions: SerializeList<ComposeTextTab<String>> = persistentListOf(),
)
