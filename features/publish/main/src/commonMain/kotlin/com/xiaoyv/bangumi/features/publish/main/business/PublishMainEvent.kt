package com.xiaoyv.bangumi.features.publish.main.business

import androidx.compose.ui.text.input.TextFieldValue
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import io.github.vinceglb.filekit.PlatformFile

/**
 * [PublishMainEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class PublishMainEvent {
    sealed class UI : PublishMainEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : PublishMainEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data object OnPublish : Action()
        data class OnReceiveTurnstileToken(val token: String) : Action()
        data class OnImagePickResult(val path: PlatformFile) : Action()
        data class OnTitleChange(val title: TextFieldValue) : Action()
        data class OnContentChange(val content: TextFieldValue) : Action()
    }
}