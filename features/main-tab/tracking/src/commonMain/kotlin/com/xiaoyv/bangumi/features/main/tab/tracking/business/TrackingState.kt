package com.xiaoyv.bangumi.features.main.tab.tracking.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf

/**
 * [TrackingState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class TrackingState(
    val tabs: SerializeList<ComposeTextTab<Int>> = persistentListOf(),
)
