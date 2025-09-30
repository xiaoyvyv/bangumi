package com.xiaoyv.bangumi.features.web.business

import com.multiplatform.webview.request.WebRequest
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [WebEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class WebEvent {
    sealed class UI : WebEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : WebEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data class OnHandleProtocol(val request: WebRequest) : Action()
        data class OnTitleChange(val title: String?) : Action()
    }
}