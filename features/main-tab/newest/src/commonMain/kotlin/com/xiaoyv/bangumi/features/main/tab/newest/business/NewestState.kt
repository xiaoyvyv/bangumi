package com.xiaoyv.bangumi.features.main.tab.newest.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf

/**
 * [NewestState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class NewestState(
    val tabs: SerializeList<ComposeTextTab<Int>> = persistentListOf(),
    val year: Int = 0,
    val defaultMonth: Int = 0,
)
