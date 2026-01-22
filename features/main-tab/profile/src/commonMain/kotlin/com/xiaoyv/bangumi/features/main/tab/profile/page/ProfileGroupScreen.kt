package com.xiaoyv.bangumi.features.main.tab.profile.page

import androidx.compose.runtime.Composable
import com.xiaoyv.bangumi.features.groups.page.GroupsPageRoute
import com.xiaoyv.bangumi.features.main.tab.profile.business.ProfileEvent
import com.xiaoyv.bangumi.features.main.tab.profile.business.ProfileState
import com.xiaoyv.bangumi.shared.data.manager.shared.currentUser


@Composable
fun ProfileGroupScreen(
    state: ProfileState,
    onUiEvent: (ProfileEvent.UI) -> Unit,
    onActionEvent: (ProfileEvent.Action) -> Unit,
) {
    GroupsPageRoute(
        param = state.rememberListGroupParam(currentUser().username),
        onNavScreen = {
            onUiEvent(ProfileEvent.UI.OnNavScreen(it))
        }
    )
}