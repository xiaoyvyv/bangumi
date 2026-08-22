package com.xiaoyv.bangumi.features.main.tab.timeline.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.TimelineTab
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf

/**
 * [TimelineState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class TimelineState(
    val actions: SerializeList<ComposeTextTab<Int>> = persistentListOf(),
    val selectedMode: Int = TimelineTab.TIMELINE_FRIEND
)
