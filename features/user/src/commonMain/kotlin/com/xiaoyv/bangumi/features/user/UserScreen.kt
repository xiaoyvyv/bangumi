package com.xiaoyv.bangumi.features.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.types.ButtonType
import com.xiaoyv.bangumi.shared.core.types.ProfileMenu
import com.xiaoyv.bangumi.shared.ui.component.action.LocalActionHandler
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.chip.DropMenuActionButton
import com.xiaoyv.bangumi.shared.ui.component.image.BlurImage
import com.xiaoyv.bangumi.shared.ui.component.image.ImageColorState
import com.xiaoyv.bangumi.shared.ui.component.image.rememberImageColorState
import com.xiaoyv.bangumi.shared.ui.component.layout.BgmCollapsingScaffold
import com.xiaoyv.bangumi.shared.ui.component.layout.rememberBgmCollapsingScaffoldState
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.pager.BgmTabHorizontalPager
import com.xiaoyv.bangumi.shared.ui.component.pager.rememberBgmPagerState
import com.xiaoyv.bangumi.shared.ui.component.tab.rememberButtonTypeMenu
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import com.xiaoyv.bangumi.shared.ui.view.user.UserProfileHeroCard
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun UserRoute(
    args: Screen.UserDetail,
    viewModel: UserViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    UserScreen(
        uiState = baseState,
        initialTab = args.tab,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is UserEvent.UI.OnNavUp -> onNavUp()
                is UserEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

/**
 * 用户主页整体入口，负责折叠头图与分页内容的组合。
 */
@Composable
private fun UserScreen(
    uiState: UiState<UserState>,
    @ProfileMenu initialTab: Int,
    onUiEvent: (UserEvent.UI) -> Unit,
    onActionEvent: (UserEvent.Action) -> Unit,
) {
    val scrollState = rememberScrollState()
    val imageColorState = rememberImageColorState()
    val collapsingState = rememberBgmCollapsingScaffoldState()
    val tabs = uiState.data.rememberTabs()
    val initialPage = remember(initialTab, tabs) {
        val idx = tabs.indexOfFirst { it.type == initialTab }
        if (idx >= 0) idx else 0
    }
    val pagerState = rememberBgmPagerState(
        initialPage = initialPage.coerceAtLeast(0),
        pageCount = { tabs.size }
    )

    LaunchedEffect(pagerState, collapsingState) {
        snapshotFlow { pagerState.currentPage }
            .drop(1)
            .collect { page ->
                if (page != 0) {
                    collapsingState.collapse()
                }
            }
    }

    BgmCollapsingScaffold(
        modifier = Modifier.fillMaxSize(),
        state = scrollState,
        collapsingState = collapsingState,
        topBar = { progressProvider ->
            val progress = progressProvider()
            val iconColor = androidx.compose.ui.graphics.lerp(
                imageColorState.contentColor,
                MaterialTheme.colorScheme.onSurface,
                progress
            )
            BgmTopAppBar(
                title = uiState.data.user.nickname,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = progress),
                    titleContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = progress),
                    navigationIconContentColor = iconColor,
                    actionIconContentColor = iconColor.copy(alpha = 0.75f)
                ),
                actions = {
                    uiState.data.run {
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
            uiState.data.run {
                UserScreenHeader(state = this, it, imageColorState, onUiEvent, onActionEvent)
            }
        }
    ) { _ ->
        StateLayout(
            modifier = Modifier.fillMaxSize(),
            uiState = uiState,
        ) { state ->
            UserScreenContent(state, initialTab, pagerState, onUiEvent, onActionEvent)
        }
    }
}


/**
 * 顶部沉浸式头图与用户信息卡。
 */
@Composable
private fun UserScreenHeader(
    state: UserState,
    padding: PaddingValues,
    imageColorState: ImageColorState,
    onUiEvent: (UserEvent.UI) -> Unit,
    onActionEvent: (UserEvent.Action) -> Unit,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(360.dp)
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        BlurImage(
            modifier = Modifier.fillMaxSize(),
            model = state.user.avatar.displayGridImage,
            contentDescription = org.jetbrains.compose.resources.stringResource(Res.string.global_image),
            onState = imageColorState.onImageState
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.scrim.copy(alpha = 0.12f),
                            MaterialTheme.colorScheme.scrim.copy(alpha = 0.58f)
                        )
                    )
                )
        )

        CompositionLocalProvider(
            LocalContentColor provides imageColorState.contentColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = ContentMarginHalf, vertical = ContentMarginHalf),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(ContentMarginHalf, Alignment.CenterVertically)
            ) {
                UserProfileHeroCard(
                    user = state.user,
                    onAvatarClick = {
                        onUiEvent(UserEvent.UI.OnNavScreen(Screen.PreviewMain(state.user.avatar.displayOriginalUrl)))
                    }
                )
            }
        }
    }
}

@Composable
private fun UserScreenContent(
    state: UserState,
    @ProfileMenu initialTab: Int,
    pagerState: androidx.compose.foundation.pager.PagerState,
    onUiEvent: (UserEvent.UI) -> Unit,
    onActionEvent: (UserEvent.Action) -> Unit,
) {
    val tabs = state.rememberTabs()
    val initialPage = remember(initialTab, tabs) {
        val idx = tabs.indexOfFirst { it.type == initialTab }
        if (idx >= 0) idx else 0
    }
    val scope = rememberCoroutineScope()
    val collectionPageIndex = remember(tabs) { tabs.indexOfFirst { it.type == ProfileMenu.COLLECTION } }

    BgmTabHorizontalPager(
        modifier = Modifier.fillMaxSize(),
        tabs = tabs,
        initialPage = initialPage,
        pagerState = pagerState
    ) {
        when (tabs[it].type) {
            ProfileMenu.TIME_MACHINE -> UserMainScreen(
                state = state,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent,
                onOpenCollection = { subjectType ->
                    onActionEvent(UserEvent.Action.OnChangeSubjectTypeFilter(subjectType))
                    if (collectionPageIndex >= 0) {
                        scope.launch { pagerState.animateScrollToPage(collectionPageIndex) }
                    }
                }
            )
            ProfileMenu.BIO -> UserBioScreen(state, onUiEvent, onActionEvent)
            ProfileMenu.TIMELINE -> UserTimelineScreen(state, onUiEvent, onActionEvent)
            ProfileMenu.COLLECTION -> UserCollectionScreen(state, onUiEvent, onActionEvent)
            ProfileMenu.STATE -> UserStateScreen(state, onUiEvent, onActionEvent)
            ProfileMenu.FRIEND -> UserFriendScreen(state, onUiEvent, onActionEvent)
            else -> Unit
        }
    }
}
