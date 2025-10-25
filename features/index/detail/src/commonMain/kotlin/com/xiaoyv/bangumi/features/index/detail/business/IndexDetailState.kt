package com.xiaoyv.bangumi.features.index.detail.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeIndex
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf

/**
 * [IndexDetailState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class IndexDetailState(
    val index: ComposeIndex = ComposeIndex.Empty,
    val tabs: SerializeList<ComposeTextTab<String>> = persistentListOf(),
)
