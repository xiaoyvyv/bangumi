package com.xiaoyv.bangumi.features.index.page.dialog

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeIndex
import kotlinx.collections.immutable.persistentListOf

/**
 * [IndexDialogState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class IndexDialogState(
    val indexList: SerializeList<ComposeIndex> = persistentListOf(),
)
