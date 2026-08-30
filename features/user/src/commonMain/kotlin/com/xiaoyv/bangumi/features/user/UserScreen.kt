package com.xiaoyv.bangumi.features.user

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_avatar
import com.xiaoyv.bangumi.core_resource.resources.global_blog
import com.xiaoyv.bangumi.core_resource.resources.global_collection
import com.xiaoyv.bangumi.core_resource.resources.global_friend
import com.xiaoyv.bangumi.core_resource.resources.global_group
import com.xiaoyv.bangumi.core_resource.resources.profile_joined_at
import com.xiaoyv.bangumi.core_resource.resources.profile_no_sign
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
import com.xiaoyv.bangumi.shared.core.utils.formatDate
import com.xiaoyv.bangumi.shared.ui.component.action.LocalActionHandler
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.chip.DropMenuActionButton
import com.xiaoyv.bangumi.shared.ui.component.image.BlurImage
import com.xiaoyv.bangumi.shared.ui.component.image.ImageColorState
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.image.rememberImageColorState
import com.xiaoyv.bangumi.shared.ui.component.layout.BgmCollapsingScaffold
import com.xiaoyv.bangumi.shared.ui.component.layout.rememberCollapsingScaffoldState
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.pager.BgmTabHorizontalPager
import com.xiaoyv.bangumi.shared.ui.component.pager.rememberPagerState
import com.xiaoyv.bangumi.shared.ui.component.tab.rememberButtonTypeMenu
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
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

@Composable
private fun UserScreen(
    uiState: UiState<UserState>,
    @ProfileMenu initialTab: Int,
    onUiEvent: (UserEvent.UI) -> Unit,
    onActionEvent: (UserEvent.Action) -> Unit,
) {
    val scrollState = rememberScrollState()
    val imageColorState = rememberImageColorState()
    val collapsingState = rememberCollapsingScaffoldState()
    val tabs = uiState.data.rememberTabs()
    val initialPage = remember(initialTab, tabs) {
        val idx = tabs.indexOfFirst { it.type == initialTab }
        if (idx >= 0) idx else 0
    }
    val pagerState = rememberPagerState(
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
                UserScreenHeader(state = this, padding = it, imageColorState = imageColorState)
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


@Composable
private fun UserScreenHeader(
    state: UserState,
    padding: PaddingValues,
    imageColorState: ImageColorState,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 330.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        val coverImage = state.user.roomPic.ifBlank { state.user.avatar.displayLargeImage }

        BlurImage(
            modifier = Modifier.matchParentSize(),
            model = coverImage,
            contentDescription = state.user.nickname,
            contentScale = ContentScale.Crop,
            onState = imageColorState.onImageState,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
                            MaterialTheme.colorScheme.surface,
                        )
                    )
                )
        )

        CompositionLocalProvider(LocalContentColor provides imageColorState.contentColor) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(padding)
                    .padding(ContentMargin),
                verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(ContentMargin),
                ) {
                    StateImage(
                        modifier = Modifier
                            .size(84.dp)
                            .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape),
                        model = state.user.avatar.displayMediumImage,
                        contentDescription = stringResource(Res.string.global_avatar),
                        shape = CircleShape,
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
                    ) {
                        Text(
                            text = state.user.nickname,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = "@${state.user.username}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LocalContentColor.current.copy(alpha = 0.75f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                Text(
                    text = state.user.sign.ifBlank { stringResource(Res.string.profile_no_sign) },
                    style = MaterialTheme.typography.bodyMedium,
                    color = LocalContentColor.current.copy(alpha = 0.85f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    UserScreenHeaderMetric(
                        modifier = Modifier.weight(1f),
                        value = state.user.stats.subject.all.total.toString(),
                        label = stringResource(Res.string.global_collection),
                    )
                    UserScreenHeaderMetric(
                        modifier = Modifier.weight(1f),
                        value = state.user.stats.friend.toString(),
                        label = stringResource(Res.string.global_friend),
                    )
                    UserScreenHeaderMetric(
                        modifier = Modifier.weight(1f),
                        value = state.user.stats.group.toString(),
                        label = stringResource(Res.string.global_group),
                    )
                    UserScreenHeaderMetric(
                        modifier = Modifier.weight(1f),
                        value = state.user.stats.blog.toString(),
                        label = stringResource(Res.string.global_blog),
                    )
                }
                if (state.user.location.isNotBlank() || state.user.joinedAt > 0) {
                    Text(
                        text = listOfNotNull(
                            state.user.location.takeIf { it.isNotBlank() },
                            state.user.joinedAt.takeIf { it > 0 }?.let {
                                stringResource(
                                    Res.string.profile_joined_at,
                                    it.formatDate("yyyy-MM-dd"),
                                )
                            },
                        ).joinToString(" · "),
                        style = MaterialTheme.typography.labelMedium,
                        color = LocalContentColor.current.copy(alpha = 0.72f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun UserScreenHeaderMetric(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = LocalContentColor.current.copy(alpha = 0.75f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
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
        pagerState = pagerState,
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


@Composable
@Preview
private fun PreviewUserScreen() {
    PreviewColumn(modifier = Modifier.fillMaxSize()) {
        UserScreen(
            uiState = UiState(UserState()),
            initialTab = ProfileMenu.TIME_MACHINE,
            onUiEvent = {},
            onActionEvent = {}
        )
    }
}
