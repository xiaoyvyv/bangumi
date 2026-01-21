package com.xiaoyv.bangumi.features.user

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_image
import com.xiaoyv.bangumi.features.user.business.UserEvent
import com.xiaoyv.bangumi.features.user.business.UserState
import com.xiaoyv.bangumi.features.user.business.UserViewModel
import com.xiaoyv.bangumi.features.user.page.UserBioScreen
import com.xiaoyv.bangumi.features.user.page.UserCollectionScreen
import com.xiaoyv.bangumi.features.user.page.UserFriendScreen
import com.xiaoyv.bangumi.features.user.page.UserMainScreen
import com.xiaoyv.bangumi.features.user.page.UserStateScreen
import com.xiaoyv.bangumi.features.user.page.UserTimelineScreen
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.types.ButtonType
import com.xiaoyv.bangumi.shared.core.types.ProfileMenu
import com.xiaoyv.bangumi.shared.ui.component.action.LocalActionHandler
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.chip.DropMenuActionButton
import com.xiaoyv.bangumi.shared.ui.component.image.BlurImage
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.layout.BgmCollapsingScaffold
import com.xiaoyv.bangumi.shared.ui.component.layout.LocalCollapsingPullRefresh
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.pager.BgmTabHorizontalPager
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.tab.rememberButtonTypeMenu
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun UserRoute(
    viewModel: UserViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    UserScreen(
        baseState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is UserEvent.UI.OnNavUp -> onNavUp()
                is UserEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun UserScreen(
    baseState: BaseState<UserState>,
    onUiEvent: (UserEvent.UI) -> Unit,
    onActionEvent: (UserEvent.Action) -> Unit,
) {
    val scrollState = rememberScrollState()

    BgmCollapsingScaffold(
        modifier = Modifier.fillMaxSize(),
        state = scrollState,
        topBar = {
            BgmTopAppBar(
                title = baseState.payload?.user?.nickname,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = it),
                    titleContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = it)
                ),
                actions = {
                    baseState.content {
                        val actionHandler = LocalActionHandler.current

                        DropMenuActionButton(
                            options = rememberButtonTypeMenu {
                                add(ButtonType.Share)
                                add(ButtonType.CopyLink)
                                add(ButtonType.OpenInBrowser)
                            }
                        ) { item ->
                            when (item.type) {
                                ButtonType.Share -> actionHandler.shareContent(user.shareUrl)
                                ButtonType.OpenInBrowser -> actionHandler.openInBrowser(user.shareUrl)
                                ButtonType.CopyLink -> actionHandler.copyContent(user.shareUrl)
                                else -> Unit
                            }
                        }
                    }
                },
                onNavigationClick = { onUiEvent(UserEvent.UI.OnNavUp) }
            )
        },
        collapse = {
            baseState.content {
                UserScreenHeader(state = this, it, onUiEvent, onActionEvent)
            }
        }
    ) {
        StateLayout(
            modifier = Modifier.fillMaxSize(),
            baseState = baseState,
        ) { state ->
            CompositionLocalProvider(LocalCollapsingPullRefresh provides (it == 0f)) {
                UserScreenContent(state, onUiEvent, onActionEvent)
            }
        }
    }
}


@Composable
private fun UserScreenHeader(
    state: UserState,
    padding: PaddingValues,
    onUiEvent: (UserEvent.UI) -> Unit,
    onActionEvent: (UserEvent.Action) -> Unit,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        BlurImage(
            modifier = Modifier.fillMaxSize(),
            model = state.user.avatar.displayGridImage,
            contentDescription = stringResource(Res.string.global_image)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf, Alignment.CenterVertically)
        ) {
            Box {
                StateImage(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(80.dp)
                        .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        .clickable { onUiEvent(UserEvent.UI.OnNavScreen(Screen.PreviewMain(state.user.avatar.displayLargeImage))) },
                    model = state.user.avatar.displayMediumImage,
                    shape = CircleShape,
                )
            }

            Text(
                modifier = Modifier.padding(top = LayoutPaddingHalf),
                text = state.user.nickname + "@" + state.user.username,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}

@Composable
private fun UserScreenContent(
    state: UserState,
    onUiEvent: (UserEvent.UI) -> Unit,
    onActionEvent: (UserEvent.Action) -> Unit,
) {
    val tabs = state.rememberTabs()

    BgmTabHorizontalPager(modifier = Modifier.fillMaxSize(), tabs = tabs) {
        when (tabs[it].type) {
            ProfileMenu.TIME_MACHINE -> UserMainScreen(state, onUiEvent, onActionEvent)
            ProfileMenu.BIO -> UserBioScreen(state, onUiEvent, onActionEvent)
            ProfileMenu.TIMELINE -> UserTimelineScreen(state, onUiEvent, onActionEvent)
            ProfileMenu.COLLECTION -> UserCollectionScreen(state, onUiEvent, onActionEvent)
            ProfileMenu.STATE -> UserStateScreen(state, onUiEvent, onActionEvent)
            ProfileMenu.FRIEND -> UserFriendScreen(state, onUiEvent, onActionEvent)
            else -> Unit
        }
    }
}

