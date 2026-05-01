package com.xiaoyv.bangumi.features.preivew.gallery.business

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import com.xiaoyv.bangumi.shared.core.types.LoadingState

/**
 * [PreviewTextState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class PreviewTextState(
    val showOrigin: Boolean = true,
    val originText: String = "",
    val translateText: String = "",
    val loading: LoadingState = LoadingState.NotLoading,
)
