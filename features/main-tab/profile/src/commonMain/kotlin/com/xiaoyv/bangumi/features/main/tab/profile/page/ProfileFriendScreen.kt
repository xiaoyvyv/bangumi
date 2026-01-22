package com.xiaoyv.bangumi.features.main.tab.profile.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.xiaoyv.bangumi.features.friend.FriendRoute
import com.xiaoyv.bangumi.features.main.tab.profile.business.ProfileEvent
import com.xiaoyv.bangumi.features.main.tab.profile.business.ProfileState
import com.xiaoyv.bangumi.shared.core.types.list.ListUserType
import com.xiaoyv.bangumi.shared.data.manager.shared.currentUser
import com.xiaoyv.bangumi.shared.data.model.request.list.user.ListUserParam


@Composable
fun ProfileFriendScreen(
    state: ProfileState,
    onUiEvent: (ProfileEvent.UI) -> Unit,
    onActionEvent: (ProfileEvent.Action) -> Unit,
) {
    val currentUser = currentUser()
    FriendRoute(
        param = remember { ListUserParam(type = ListUserType.USER_FRIEND, username = currentUser.username) },
        onNavScreen = { screen -> onUiEvent(ProfileEvent.UI.OnNavScreen(screen)) }
    )
}