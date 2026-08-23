package com.xiaoyv.bangumi.features.pixiv.user.main.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_settings
import com.xiaoyv.bangumi.core_resource.resources.pixiv_logout
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivUserInfoBody
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf

sealed interface PixivUserMoreAction {
    data object Settings : PixivUserMoreAction
    data object Logout : PixivUserMoreAction
}

/**
 * [PixivUserState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class PixivUserState(
    val userId: Long = 0,
    val isCurrentUser: Boolean = false,
    val userInfo: ComposePixivUserInfoBody = ComposePixivUserInfoBody.Empty,
    val actions: SerializeList<ComposeTextTab<PixivUserMoreAction>> = persistentListOf(
        ComposeTextTab(
            type = PixivUserMoreAction.Settings,
            label = Res.string.global_settings,
        ),
        ComposeTextTab(
            type = PixivUserMoreAction.Logout,
            label = Res.string.pixiv_logout,
        ),
    ),
)
