package com.xiaoyv.bangumi.features.detect.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import io.github.vinceglb.filekit.PlatformFile

/**
 * [ReceiveEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class ReceiveEvent {
    sealed class UI : ReceiveEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : ReceiveEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data object OnDismissResultDialog : Action()
        data object OnRecognizingImageSource : Action()
        data class OnChangeModel(val model: String) : Action()
        data class OnSelectedFile(val file: PlatformFile) : Action()
    }
}