package com.xiaoyv.bangumi.shared.data.manager.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSetting
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUser

/**
 * [SharedState]
 *
 * @author why
 * @since 2025/1/15
 */
@Immutable
data class SharedState(
    val user: ComposeUser = ComposeUser.Empty,
    val settings: ComposeSetting = ComposeSetting.Default,
    val mikanIdMap: Map<String, String> = emptyMap(),
    val unreadNotification: Int = 0,
    val unreadMessage: Int = 0,
) {
    val isLogin get() = user != ComposeUser.Empty
}

@Composable
fun currentUser() = LocalSharedState.current.user

@Composable
fun currentLogin() = LocalSharedState.current.isLogin
