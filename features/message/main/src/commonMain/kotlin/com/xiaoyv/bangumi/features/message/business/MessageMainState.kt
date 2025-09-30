package com.xiaoyv.bangumi.features.message.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf

/**
 * [MessageMainState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class MessageMainState(
    val tabs: SerializeList<ComposeTextTab<String>> = persistentListOf(),
    val selectedTabType: String = "",

    val editMode: Boolean = false,
    val selectedInboxIds: SerializeList<Long> = persistentListOf(),
    val selectedOutboxIds: SerializeList<Long> = persistentListOf(),
)
