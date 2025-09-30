package com.xiaoyv.bangumi.features.index.detail.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeIndex

/**
 * [IndexDetailState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class IndexDetailState(
    val index: ComposeIndex = ComposeIndex.Empty,
)
