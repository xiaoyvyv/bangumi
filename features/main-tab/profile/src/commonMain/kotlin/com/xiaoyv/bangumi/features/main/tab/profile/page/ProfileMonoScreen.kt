package com.xiaoyv.bangumi.features.main.tab.profile.page

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.xiaoyv.bangumi.features.main.tab.profile.business.ProfileEvent
import com.xiaoyv.bangumi.features.main.tab.profile.business.ProfileState
import com.xiaoyv.bangumi.features.mono.page.MonoPageRoute
import com.xiaoyv.bangumi.shared.data.manager.shared.currentUser
import com.xiaoyv.bangumi.shared.ui.component.pager.BgmChipHorizontalPager


@Composable
fun ProfileMonoScreen(
    state: ProfileState,
    onUiEvent: (ProfileEvent.UI) -> Unit,
    onActionEvent: (ProfileEvent.Action) -> Unit,
) {
    val currentUser = currentUser()

    BgmChipHorizontalPager(
        modifier = Modifier.fillMaxSize(),
        tabs = state.monoTypeFilters
    ) {
        MonoPageRoute(
            param = state.rememberListMonoParam(state.monoTypeFilters[it].type, currentUser.username),
            onNavScreen = { screen -> onUiEvent(ProfileEvent.UI.OnNavScreen(screen)) },
        )
    }
}