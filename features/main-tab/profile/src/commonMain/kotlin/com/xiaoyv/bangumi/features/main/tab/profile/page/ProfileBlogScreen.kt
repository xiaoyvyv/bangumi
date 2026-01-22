package com.xiaoyv.bangumi.features.main.tab.profile.page

import androidx.compose.runtime.Composable
import com.xiaoyv.bangumi.features.blog.page.BlogPageRoute
import com.xiaoyv.bangumi.features.main.tab.profile.business.ProfileEvent
import com.xiaoyv.bangumi.features.main.tab.profile.business.ProfileState
import com.xiaoyv.bangumi.shared.data.manager.shared.currentUser


@Composable
fun ProfileBlogScreen(
    state: ProfileState,
    onUiEvent: (ProfileEvent.UI) -> Unit,
    onActionEvent: (ProfileEvent.Action) -> Unit,
) {
    val currentUser = currentUser()

    BlogPageRoute(
        param = state.rememberListBlogParam(currentUser.username),
        onNavScreen = { onUiEvent(ProfileEvent.UI.OnNavScreen(it)) }
    )
}