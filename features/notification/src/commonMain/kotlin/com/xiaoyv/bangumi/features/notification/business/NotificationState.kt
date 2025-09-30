package com.xiaoyv.bangumi.features.notification.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeNotification
import kotlinx.collections.immutable.persistentListOf

/**
 * [NotificationState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class NotificationState(
    val notifications: SerializeList<ComposeNotification> = persistentListOf(),
)
