package com.xiaoyv.bangumi.shared.data.manager.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSetting
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUnRead
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import com.xiaoyv.bangumi.shared.data.model.response.chore.ComposeAppRelease
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ComposePixivToken

/**
 * [SharedState]
 *
 * @author why
 * @since 2025/1/15
 */
@Immutable
data class SharedState(
    val user: ComposeUser = ComposeUser.Empty,
    val pixivToken: ComposePixivToken = ComposePixivToken.Empty,
    val settings: ComposeSetting = ComposeSetting.Default,
    val appRelease: ComposeAppRelease = ComposeAppRelease.Empty,
    val mikanIdMap: Map<String, String> = emptyMap(),
    val unread: ComposeUnRead = ComposeUnRead.Empty,
) {
    val isLogin get() = user != ComposeUser.Empty
    val isPixivLogin get() = pixivToken.refreshToken.isNotBlank() || pixivToken.accessToken.isNotBlank()
    val pixivUserId get() = pixivToken.currentUser.id.toLongOrNull() ?: 0
    val pixivUserAvatar get() = pixivToken.currentUser.profileImageUrls.maxUrl
}

@Composable
fun currentUser() = LocalSharedState.current.user

@Composable
fun currentLogin() = LocalSharedState.current.isLogin
