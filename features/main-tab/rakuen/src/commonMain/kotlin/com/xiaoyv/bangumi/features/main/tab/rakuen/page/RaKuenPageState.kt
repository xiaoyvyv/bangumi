package com.xiaoyv.bangumi.features.main.tab.rakuen.page

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.RakuenTab

@Immutable
data class RaKuenPageState(
    @field:RakuenTab
    val type: String = RakuenTab.ALL,
)