package com.xiaoyv.bangumi.features.groups.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.group_created_at_prefix
import com.xiaoyv.bangumi.core_resource.resources.group_join
import com.xiaoyv.bangumi.core_resource.resources.group_joined
import com.xiaoyv.bangumi.core_resource.resources.group_members_suffix
import com.xiaoyv.bangumi.core_resource.resources.group_topics_suffix
import com.xiaoyv.bangumi.features.friend.FriendRoute
import com.xiaoyv.bangumi.features.groups.detail.business.GroupsDetailEvent
import com.xiaoyv.bangumi.features.groups.detail.business.GroupsDetailState
import com.xiaoyv.bangumi.features.groups.detail.business.GroupsDetailViewModel
import com.xiaoyv.bangumi.features.topic.page.TopicPageRoute
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.types.ButtonType
import com.xiaoyv.bangumi.shared.core.types.list.ListTopicType
import com.xiaoyv.bangumi.shared.core.types.list.ListUserType
import com.xiaoyv.bangumi.shared.core.utils.formatDate
import com.xiaoyv.bangumi.shared.data.manager.bbcodeToHtml
import com.xiaoyv.bangumi.shared.data.model.request.list.topic.ListTopicParam
import com.xiaoyv.bangumi.shared.data.model.request.list.user.ListUserParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMembership
import com.xiaoyv.bangumi.shared.data.model.ui.PageUI
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
import com.xiaoyv.bangumi.shared.ui.component.pager.BgmChipHorizontalPager
import com.xiaoyv.bangumi.shared.ui.component.pager.BgmTabHorizontalPager
import com.xiaoyv.bangumi.shared.ui.component.pager.rememberPagerState
import com.xiaoyv.bangumi.shared.ui.component.tab.rememberButtonTypeMenu
import com.xiaoyv.bangumi.shared.ui.component.text.BgmLinkedText
import com.xiaoyv.bangumi.shared.ui.component.text.StarColor
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import kotlinx.coroutines.flow.drop
import org.jetbrains.compose.resources.stringResource
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun GroupsDetailRoute(
    viewModel: GroupsDetailViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    GroupsDetailScreen(
        uiState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is GroupsDetailEvent.UI.OnNavUp -> onNavUp()
                is GroupsDetailEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun GroupsDetailScreen(
    uiState: UiState<GroupsDetailState>,
    onUiEvent: (GroupsDetailEvent.UI) -> Unit,
    onActionEvent: (GroupsDetailEvent.Action) -> Unit,
) {
    val imageColorState = rememberImageColorState()
    val collapsingState = rememberCollapsingScaffoldState()
    val pagerState = rememberPagerState { uiState.data.tabs.size }

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
        collapsingState = collapsingState,
        topBar = { progressProvider ->
            val progress = progressProvider()
            val iconColor = androidx.compose.ui.graphics.lerp(
                imageColorState.contentColor,
                MaterialTheme.colorScheme.onSurface,
                progress
            )
            BgmTopAppBar(
                title = uiState.data.run { group.title },
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
                                ButtonType.Share -> actionHandler.shareContent(group.shareUrl)
                                ButtonType.OpenInBrowser -> actionHandler.openInBrowser(group.shareUrl)
                                ButtonType.CopyLink -> actionHandler.copyContent(group.shareUrl)
                                else -> Unit
                            }
                        }
                    }
                },
                onNavigationClick = { onUiEvent(GroupsDetailEvent.UI.OnNavUp) }
            )
        },
        collapse = {
            uiState.data.run {
                GroupsDetailScreenHeader(
                    state = this,
                    padding = it,
                    imageColorState = imageColorState,
                    onUiEvent = onUiEvent,
                    onActionEvent = onActionEvent
                )
            }
        }
    ) { _ ->
        StateLayout(
            modifier = Modifier.fillMaxSize(),
            onRefresh = { loading -> onActionEvent(GroupsDetailEvent.Action.OnRefresh(loading)) },
            uiState = uiState,
        ) { state ->
            GroupsDetailScreenContent(state, pagerState, onUiEvent, onActionEvent)
        }
    }
}


@Composable
private fun GroupsDetailScreenHeader(
    state: GroupsDetailState,
    padding: PaddingValues,
    imageColorState: ImageColorState,
    onUiEvent: (GroupsDetailEvent.UI) -> Unit,
    onActionEvent: (GroupsDetailEvent.Action) -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        BlurImage(
            modifier = Modifier.matchParentSize(),
            model = state.group.images.displayGridImage,
            contentDescription = state.group.name,
            contentScale = ContentScale.Crop,
            onState = imageColorState.onImageState
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )
        CompositionLocalProvider(
            LocalContentColor provides imageColorState.contentColor
        ) {
            ListItem(
                modifier = Modifier
                    .padding(padding)
                    .padding(vertical = ContentMarginHalf),
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent,
                    headlineColor = LocalContentColor.current,
                    supportingColor = LocalContentColor.current.copy(alpha = 0.75f)
                ),
                leadingContent = {
                    StateImage(
                        modifier = Modifier.size(64.dp)
                            .border(2.dp, MaterialTheme.colorScheme.surface, MaterialTheme.shapes.small),
                        model = state.group.images.large,
                        shape = MaterialTheme.shapes.small,
                    )
                },
                headlineContent = {
                    Text(
                        text = state.group.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                supportingContent = {
                    Column {
                        Text(
                            modifier = Modifier.padding(vertical = ContentMarginHalf),
                            text = buildString {
                                append(state.group.members)
                                append(stringResource(Res.string.group_members_suffix))
                                append(" ")
                                append(state.group.topics)
                                append(stringResource(Res.string.group_topics_suffix))
                            }
                        )
                        Text(text = stringResource(Res.string.group_created_at_prefix) + state.group.createdAt.formatDate("yyyy-MM-dd"))
                    }
                },
                trailingContent = {
                    OutlinedButton(
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (state.group.isJoined) LocalContentColor.current else StarColor,
                        ),
                        border = BorderStroke(1.dp, if (state.group.isJoined) LocalContentColor.current else StarColor),
                        onClick = { onActionEvent(GroupsDetailEvent.Action.OnToggleJoinGroup) }
                    ) {
                        Text(
                            text = if (state.group.membership == ComposeMembership.Empty) {
                                stringResource(Res.string.group_join)
                            } else {
                                stringResource(Res.string.group_joined)
                            }
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun GroupsDetailScreenContent(
    state: GroupsDetailState,
    pagerState: androidx.compose.foundation.pager.PagerState,
    onUiEvent: (GroupsDetailEvent.UI) -> Unit,
    onActionEvent: (GroupsDetailEvent.Action) -> Unit,
) {
    BgmTabHorizontalPager(
        modifier = Modifier.fillMaxSize(),
        pagerState = pagerState,
        tabs = state.tabs
    ) {
        when (it) {
            0 -> GroupsDetailScreenSummary(state, onUiEvent, onActionEvent)
            1 -> GroupsDetailScreenTopics(state, onUiEvent, onActionEvent)
            2 -> GroupsDetailScreenMembers(state, onUiEvent, onActionEvent)
        }
    }
}

@Composable
fun GroupsDetailScreenTopics(
    state: GroupsDetailState,
    onUiEvent: (GroupsDetailEvent.UI) -> Unit,
    onActionEvent: (GroupsDetailEvent.Action) -> Unit,
) {
    TopicPageRoute(
        param = remember(state.group.name) {
            ListTopicParam(
                type = ListTopicType.GROUP_TARGET,
                groupName = state.group.name
            )
        },
        onNavScreen = { screen -> onUiEvent(GroupsDetailEvent.UI.OnNavScreen(screen)) }
    )
}

@Composable
private fun GroupsDetailScreenMembers(
    state: GroupsDetailState,
    onUiEvent: (GroupsDetailEvent.UI) -> Unit,
    onActionEvent: (GroupsDetailEvent.Action) -> Unit,
) {
    BgmChipHorizontalPager(
        modifier = Modifier.fillMaxSize(),
        tabs = state.memberFilters
    ) {
        val role = state.memberFilters[it].type
        FriendRoute(
            param = remember(state.group.name, role) {
                ListUserParam(
                    type = ListUserType.GROUP_MEMBER,
                    groupRole = role,
                    groupName = state.group.name,
                    ui = PageUI(pageMode = true)
                )
            },
            onNavScreen = { screen -> onUiEvent(GroupsDetailEvent.UI.OnNavScreen(screen)) }
        )
    }
}


@Composable
private fun GroupsDetailScreenSummary(
    state: GroupsDetailState,
    onUiEvent: (GroupsDetailEvent.UI) -> Unit,
    onActionEvent: (GroupsDetailEvent.Action) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val text by produceState("", state.group.description) {
            value = state.group.description.bbcodeToHtml()
        }

        BgmLinkedText(
            modifier = Modifier.padding(ContentMargin),
            text = text,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface)
        )
    }
}
