package com.xiaoyv.bangumi.features.main.tab.home.page.mono

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.base.ComposeSection
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoDisplay
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class HomeMonoState(
    val sections: SerializeList<ComposeSection<ComposeMonoDisplay>> = persistentListOf(),
)


