package com.xiaoyv.bangumi.features.timeline.add.business

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue

/**
 * [TimelineAddState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class TimelineAddState(
    val content: TextFieldValue = TextFieldValue(""),
    val turnstileToken: String = ""
) {
    val canPublish: Boolean
        get() = content.text.isNotBlank() && turnstileToken.isNotBlank()
}


