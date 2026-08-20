package com.xiaoyv.bangumi.features.timeline.add.business

import androidx.compose.ui.text.input.TextFieldValue
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import io.github.vinceglb.filekit.PlatformFile

/**
 * [TimelineAddEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class TimelineAddEvent {
    sealed class UI : TimelineAddEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : TimelineAddEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data object OnPublish : Action()
        data class OnReceiveTurnstileToken(val token: String) : Action()
        data class OnImagePickResult(val path: PlatformFile) : Action()
        data class OnContentChange(val content: TextFieldValue) : Action()
    }
}