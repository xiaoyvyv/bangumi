package com.xiaoyv.bangumi.features.index.detail.page

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.IndexCatWebTabType

@Immutable
data class IndexDetailPageState(
    val type: String = IndexCatWebTabType.ALL,
)
