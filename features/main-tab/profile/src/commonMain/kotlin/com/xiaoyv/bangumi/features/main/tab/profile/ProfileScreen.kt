package com.xiaoyv.bangumi.features.main.tab.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_edit
import com.xiaoyv.bangumi.core_resource.resources.global_image
import com.xiaoyv.bangumi.core_resource.resources.global_message
import com.xiaoyv.bangumi.core_resource.resources.global_notification
import com.xiaoyv.bangumi.core_resource.resources.global_settings
import com.xiaoyv.bangumi.core_resource.resources.login_first_tip
import com.xiaoyv.bangumi.features.main.tab.profile.business.ProfileEvent
import com.xiaoyv.bangumi.features.main.tab.profile.business.ProfileState
import com.xiaoyv.bangumi.features.main.tab.profile.business.ProfileViewModel
import com.xiaoyv.bangumi.features.main.tab.profile.page.ProfileBlogScreen
import com.xiaoyv.bangumi.features.main.tab.profile.page.ProfileCollectionScreen
import com.xiaoyv.bangumi.features.main.tab.profile.page.ProfileFriendScreen
import com.xiaoyv.bangumi.features.main.tab.profile.page.ProfileGroupScreen
import com.xiaoyv.bangumi.features.main.tab.profile.page.ProfileIndexScreen
import com.xiaoyv.bangumi.features.main.tab.profile.page.ProfileMonoScreen
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.types.ProfileMenu
import com.xiaoyv.bangumi.shared.core.types.ProfileTab
import com.xiaoyv.bangumi.shared.core.utils.clickWithoutRipped
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalSharedState
import com.xiaoyv.bangumi.shared.data.manager.shared.currentUser
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.chip.DropMenuActionButton
import com.xiaoyv.bangumi.shared.ui.component.image.BlurImage
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.layout.BgmCollapsingScaffold
import com.xiaoyv.bangumi.shared.ui.component.layout.BgmRequireLoginLayout
import com.xiaoyv.bangumi.shared.ui.component.layout.LocalCollapsingPullRefresh
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.pager.BgmTabHorizontalPager
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun ProfileRoute(
    viewModel: ProfileViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    ProfileScreen(
        baseState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is ProfileEvent.UI.OnNavUp -> onNavUp()
                is ProfileEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        }
    )
}

@Composable
private fun ProfileScreen(
    baseState: BaseState<ProfileState>,
    onUiEvent: (ProfileEvent.UI) -> Unit,
    onActionEvent: (ProfileEvent.Action) -> Unit,
) {
    val scrollState = rememberScrollState()
    val user = currentUser()
    val sharedState = LocalSharedState.current

    BgmCollapsingScaffold(
        modifier = Modifier.fillMaxSize(),
        state = scrollState,
        topBar = {
            BgmTopAppBar(
                title = currentUser().nickname,
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = it)),
                navigationIcon = {
                    DropMenuActionButton(
                        imageVector = BgmIcons.Menu,
                        options = baseState.payload?.topBarMenu ?: persistentListOf()
                    ) { item ->
                        when (item.type) {
                            ProfileMenu.TIME_MACHINE -> {
                                if (sharedState.isLogin) {
                                    onUiEvent(ProfileEvent.UI.OnNavScreen(Screen.UserDetail(user.username)))
                                } else {
                                    onUiEvent(ProfileEvent.UI.OnNavScreen(Screen.SignIn))
                                }
                            }
                        }
                    }
                },
                actions = {
                    BadgedBox(
                        badge = {
                            if (sharedState.unreadNotification > 0) {
                                Badge { Text(text = sharedState.unreadNotification.toString()) }
                            }
                        },
                        content = {
                            IconButton(onClick = { onUiEvent(ProfileEvent.UI.OnNavScreen(Screen.Notification)) }) {
                                Icon(
                                    imageVector = BgmIcons.NotificationsActive,
                                    contentDescription = stringResource(Res.string.global_notification)
                                )
                            }
                        }
                    )
                    Spacer(Modifier.width(LayoutPaddingHalf))
                    BadgedBox(
                        badge = {
                            if (sharedState.unreadMessage > 0) {
                                Badge { Text(text = sharedState.unreadMessage.toString()) }
                            }
                        },
                        content = {
                            IconButton(onClick = { onUiEvent(ProfileEvent.UI.OnNavScreen(Screen.MessageMain)) }) {
                                Icon(
                                    imageVector = BgmIcons.Email,
                                    contentDescription = stringResource(Res.string.global_message)
                                )
                            }
                        }
                    )
                    Spacer(Modifier.width(LayoutPaddingHalf))
                    IconButton(onClick = { onUiEvent(ProfileEvent.UI.OnNavScreen(Screen.SettingsMain)) }) {
                        Icon(
                            imageVector = BgmIcons.Settings,
                            contentDescription = stringResource(Res.string.global_settings)
                        )
                    }
                },
            )
        },
        collapse = {
            baseState.content {
                ProfileScreenHeader(state = this, it, onUiEvent, onActionEvent)
            }
        }
    ) {
        StateLayout(
            modifier = Modifier.fillMaxSize(),
            baseState = baseState,
        ) { state ->
            CompositionLocalProvider(LocalCollapsingPullRefresh provides (it == 0f)) {
                val scope = rememberCoroutineScope()
                ProfileScreenContent(state, onUiEvent, onActionEvent) {
                    scope.launch { scrollState.animateScrollTo(scrollState.maxValue) }
                }
            }
        }
    }
}

@Composable
private fun ProfileScreenHeader(
    state: ProfileState,
    padding: PaddingValues,
    onUiEvent: (ProfileEvent.UI) -> Unit,
    onActionEvent: (ProfileEvent.Action) -> Unit,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        val currentUser = currentUser()
        val avatar = currentUser.avatar.displayMediumImage

        BlurImage(
            modifier = Modifier.fillMaxSize(),
            model = avatar,
            contentDescription = stringResource(Res.string.global_image),
            androidSampling = if (avatar.isBlank()) 20f else 5f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf, Alignment.CenterVertically)
        ) {
            Box(modifier = Modifier.clickWithoutRipped { onUiEvent(ProfileEvent.UI.OnNavScreen(Screen.SettingsAccount)) }) {
                StateImage(
                    modifier = Modifier
                        .size(80.dp)
                        .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape),
                    model = avatar,
                    shape = CircleShape,
                )
                Icon(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(24.dp)
                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                        .padding(4.dp),
                    imageVector = BgmIcons.Edit,
                    contentDescription = stringResource(Res.string.global_edit)
                )
            }
            Text(
                modifier = Modifier.padding(top = LayoutPaddingHalf),
                text = currentUser.nickname.ifBlank { stringResource(Res.string.login_first_tip) },
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.surface
                )
            )
            Text(
                text = "@" + currentUser.username.ifBlank { "bangumi" },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}

@Composable
private fun ProfileScreenContent(
    state: ProfileState,
    onUiEvent: (ProfileEvent.UI) -> Unit,
    onActionEvent: (ProfileEvent.Action) -> Unit,
    onTabSelected: (Int) -> Unit,
) {
    BgmRequireLoginLayout(
        modifier = Modifier.fillMaxSize(),
        isLogin = LocalSharedState.current.isLogin,
        onLogin = { onUiEvent(ProfileEvent.UI.OnNavScreen(Screen.SignIn)) }
    ) {
        BgmTabHorizontalPager(
            modifier = Modifier.fillMaxSize(),
            onTabSelected = onTabSelected,
            tabs = state.tabs
        ) {
            when (state.tabs[it].type) {
                ProfileTab.COLLECTION -> ProfileCollectionScreen(state, onUiEvent, onActionEvent)
                ProfileTab.MONO -> ProfileMonoScreen(state, onUiEvent, onActionEvent)
                ProfileTab.BLOG -> ProfileBlogScreen(state, onUiEvent, onActionEvent)
                ProfileTab.INDEX -> ProfileIndexScreen(state, onUiEvent, onActionEvent)
                ProfileTab.GROUP -> ProfileGroupScreen(state, onUiEvent, onActionEvent)
                ProfileTab.FRIEND -> ProfileFriendScreen(state, onUiEvent, onActionEvent)
            }
        }
    }
}

@Preview
@Composable
private fun PreviewProfileScreen() {
    PreviewColumn(modifier = Modifier.fillMaxSize()) {
        ProfileScreen(
            baseState = BaseState.Success(
                ProfileState()
            ),
            onUiEvent = {},
            onActionEvent = {}
        )
    }
}