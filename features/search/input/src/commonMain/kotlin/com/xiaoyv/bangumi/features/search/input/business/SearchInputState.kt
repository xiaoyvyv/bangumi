package com.xiaoyv.bangumi.features.search.input.business

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue

/**
 * [SearchInputState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class SearchInputState(
    val query: TextFieldValue = TextFieldValue(),
    val suggestions: List<String> = emptyList(),
    val histories: List<String> = emptyList(),
)
