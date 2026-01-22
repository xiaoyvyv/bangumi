package com.xiaoyv.bangumi.features.main.tab.topic.page

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.RakuenTab

@Immutable
data class TopicPageState(
    @field:RakuenTab
    val type: String = RakuenTab.ALL,
)