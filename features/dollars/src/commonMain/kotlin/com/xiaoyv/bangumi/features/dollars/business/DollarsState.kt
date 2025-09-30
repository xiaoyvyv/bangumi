package com.xiaoyv.bangumi.features.dollars.business

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue
import com.xiaoyv.bangumi.shared.core.types.LoadingState
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeDollarItem
import kotlinx.collections.immutable.persistentListOf

/**
 * [DollarsState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class DollarsState(
    val value: TextFieldValue = TextFieldValue(),
    val items: SerializeList<ComposeDollarItem> = persistentListOf(),
    val sending: LoadingState = LoadingState.NotLoading,
)
