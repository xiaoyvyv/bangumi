package com.xiaoyv.bangumi.features.mono.browser.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.request.list.mono.MonoBrowserBody
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.StringResource

/**
 * [MonoBrowserState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class MonoBrowserState(
    val title: StringResource? = null,
    val param: MonoBrowserBody = MonoBrowserBody.Empty,
    val typeFilters: SerializeList<ComposeTextTab<String>> = persistentListOf(),
    val genderFilters: SerializeList<ComposeTextTab<String>> = persistentListOf(),
    val bloodFilters: SerializeList<ComposeTextTab<String>> = persistentListOf(),
    val sortFilters: SerializeList<ComposeTextTab<String>> = persistentListOf(),
    val monthFilters: SerializeList<ComposeTextTab<String>> = persistentListOf(),
    val dayFilters: SerializeList<ComposeTextTab<String>> = persistentListOf(),
)
