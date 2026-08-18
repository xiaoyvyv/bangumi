package com.xiaoyv.bangumi.features.main.tab.rakuen.page

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.RakuenType

@Immutable
data class RaKuenPageState(
    @field:RakuenType
    val type: String = RakuenType.ALL,
)