package com.xiaoyv.bangumi.features.notification.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [NotificationSideEffect]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class NotificationSideEffect {
    data class OnNavScreen(val screen: Screen) : NotificationSideEffect()

    data object OnRefreshNotificationCount : NotificationSideEffect()
}