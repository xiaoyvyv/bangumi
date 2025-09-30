package com.xiaoyv.bangumi.features.preivew.main.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.persistentListOf

/**
 * [PreviewMainState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class PreviewMainState(
    val index: Int,
    val items: SerializeList<String> = persistentListOf(),
)
