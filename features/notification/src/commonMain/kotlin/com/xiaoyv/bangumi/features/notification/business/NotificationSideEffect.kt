package com.xiaoyv.bangumi.features.notification.business

/**
 * [NotificationSideEffect]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class NotificationSideEffect {
    data object OnRefreshNotificationCount : NotificationSideEffect()
}