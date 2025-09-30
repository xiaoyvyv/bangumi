package com.xiaoyv.bangumi.features.notification.business

import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeNotification
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [NotificationEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class NotificationEvent {
    sealed class UI : NotificationEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : NotificationEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data class OnMarkRead(val item: ComposeNotification) : Action()
        data class OnAgreeFriendRequest(val item: ComposeNotification) : Action()
    }
}