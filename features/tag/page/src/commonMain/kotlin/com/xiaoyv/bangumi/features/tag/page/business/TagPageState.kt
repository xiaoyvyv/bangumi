package com.xiaoyv.bangumi.features.tag.page.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeTag
import kotlinx.collections.immutable.persistentListOf

/**
 * [TagPageState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class TagPageState(
    val tags: SerializeList<ComposeTag> = persistentListOf(),
    val keyword: String = "",
)
