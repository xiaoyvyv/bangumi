package com.xiaoyv.bangumi.features.user.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.xiaoyv.bangumi.features.friend.FriendRoute
import com.xiaoyv.bangumi.features.user.business.UserEvent
import com.xiaoyv.bangumi.features.user.business.UserState
import com.xiaoyv.bangumi.shared.core.types.list.ListUserType
import com.xiaoyv.bangumi.shared.data.model.request.list.user.ListUserParam

@Composable
fun UserFriendScreen(
    state: UserState,
    onUiEvent: (UserEvent.UI) -> Unit,
    onActionEvent: (UserEvent.Action) -> Unit,
) {
    FriendRoute(
        param = remember {
            ListUserParam(
                type = ListUserType.USER_FRIEND,
                username = state.username
            )
        },
        onNavScreen = {
            onUiEvent(UserEvent.UI.OnNavScreen(it))
        }
    )
}

