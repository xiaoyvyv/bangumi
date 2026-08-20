package com.xiaoyv.bangumi.features.notification.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeNotice
import kotlinx.collections.immutable.persistentListOf

/**
 * [NotificationState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class NotificationState(
    val pageUrl: String = "",
    val notifications: SerializeList<ComposeNotice> = persistentListOf(),
)
