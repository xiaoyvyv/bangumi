package com.xiaoyv.bangumi.features.index.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.BookmarkAdded
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_all
import com.xiaoyv.bangumi.core_resource.resources.global_bookmark
import com.xiaoyv.bangumi.core_resource.resources.global_loading
import com.xiaoyv.bangumi.core_resource.resources.index_detail_collect_suffix
import com.xiaoyv.bangumi.core_resource.resources.index_detail_created_label
import com.xiaoyv.bangumi.core_resource.resources.index_detail_fav_suffix
import com.xiaoyv.bangumi.core_resource.resources.index_detail_updated_label
import com.xiaoyv.bangumi.core_resource.resources.subject_action_more
import com.xiaoyv.bangumi.features.index.detail.business.IndexDetailEvent
import com.xiaoyv.bangumi.features.index.detail.business.IndexDetailState
import com.xiaoyv.bangumi.features.index.detail.business.IndexDetailViewModel
import com.xiaoyv.bangumi.features.index.detail.page.IndexDetailPageScreen
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.types.ButtonType
import com.xiaoyv.bangumi.shared.core.types.IndexCatWebTabType
import com.xiaoyv.bangumi.shared.core.types.TopicType
import com.xiaoyv.bangumi.shared.core.utils.formatDate
import com.xiaoyv.bangumi.shared.data.model.request.list.index.ListIndexRelatedParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.index.ComposeIndex
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
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import com.xiaoyv.bangumi.shared.ui.component.tab.rememberButtonTypeMenu
import com.xiaoyv.bangumi.shared.ui.component.text.StarColor
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.drop
import org.jetbrains.compose.resources.stringResource
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun IndexDetailRoute(
    viewModel: IndexDetailViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    IndexDetailScreen(
        uiState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is IndexDetailEvent.UI.OnNavUp -> onNavUp()
                is IndexDetailEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun IndexDetailScreen(
    uiState: UiState<IndexDetailState>,
    onUiEvent: (IndexDetailEvent.UI) -> Unit,
    onActionEvent: (IndexDetailEvent.Action) -> Unit,
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
                title = uiState.data.run { index.title },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = progress),
                    titleContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = progress),
                    navigationIconContentColor = iconColor,
                    actionIconContentColor = iconColor.copy(alpha = 0.75f)
                ),
                actions = {
                    if (uiState.data.index != ComposeIndex.Empty) {
                        val actionHandler = LocalActionHandler.current

                        IconButton(
                            onClick = {
                                onUiEvent(
                                    IndexDetailEvent.UI.OnNavScreen(
                                        Screen.TopicDetail(uiState.data.index.id, TopicType.TYPE_INDEX)
                                    )
                                )
                            }
                        ) {
                            Icon(
                                imageVector = BgmIcons.Edit,
                                contentDescription = stringResource(Res.string.subject_action_more)
                            )
                        }

                        IconButton(onClick = { onActionEvent(IndexDetailEvent.Action.OnToggleBookmarkIndex) }) {
                            Icon(
                                imageVector = if (uiState.data.index.isBookmarked) BgmIcons.BookmarkAdded else BgmIcons.BookmarkAdd,
                                contentDescription = stringResource(Res.string.global_bookmark),
                                tint = if (uiState.data.index.isBookmarked) StarColor else LocalContentColor.current
                            )
                        }

                        DropMenuActionButton(
                            options = rememberButtonTypeMenu {
                                add(ButtonType.Share)
                                add(ButtonType.CopyLink)
                                add(ButtonType.OpenInBrowser)
                            }
                        ) { item ->
                            when (item.type) {
                                ButtonType.Share -> actionHandler.shareContent(uiState.data.index.shareUrl)
                                ButtonType.OpenInBrowser -> actionHandler.openInBrowser(uiState.data.index.shareUrl)
                                ButtonType.CopyLink -> actionHandler.copyContent(uiState.data.index.shareUrl)
                                else -> Unit
                            }
                        }
                    }
                },
                onNavigationClick = { onUiEvent(IndexDetailEvent.UI.OnNavUp) }
            )
        },
        collapse = {
            IndexDetailScreenHeader(
                state = uiState.data,
                padding = it,
                imageColorState = imageColorState,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )
        }
    ) { _ ->
        StateLayout(
            modifier = Modifier.fillMaxSize(),
            onRefresh = { loading -> onActionEvent(IndexDetailEvent.Action.OnRefresh(loading)) },
            uiState = uiState,
        ) { state ->
            IndexDetailScreenContent(state, pagerState, onUiEvent, onActionEvent)
        }
    }
}

@Composable
private fun IndexDetailScreenHeader(
    state: IndexDetailState,
    padding: PaddingValues,
    imageColorState: ImageColorState,
    onUiEvent: (IndexDetailEvent.UI) -> Unit,
    onActionEvent: (IndexDetailEvent.Action) -> Unit,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        BlurImage(
            modifier = Modifier.matchParentSize(),
            model = state.index.creator.avatar.displayGridImage,
            contentDescription = state.index.title,
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

        CompositionLocalProvider(LocalContentColor provides imageColorState.contentColor) {
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
                        modifier = Modifier
                            .size(64.dp)
                            .clickable { onUiEvent(IndexDetailEvent.UI.OnNavScreen(Screen.UserDetail(state.index.creator.username))) }
                            .border(2.dp, MaterialTheme.colorScheme.surface, MaterialTheme.shapes.small),
                        model = state.index.creator.avatar.displayMediumImage,
                        shape = MaterialTheme.shapes.small,
                    )
                },
                headlineContent = {
                    Text(
                        text = if (state.index == ComposeIndex.Empty) stringResource(Res.string.global_loading) else state.index.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                supportingContent = {
                    Column {
                        Text(
                            modifier = Modifier.padding(vertical = ContentMarginHalf),
                            text = if (state.index == ComposeIndex.Empty) "\u3000" else buildString {
                                append(stringResource(Res.string.index_detail_collect_suffix, state.index.total))
                                append("\u3000")
                                append(stringResource(Res.string.index_detail_fav_suffix, state.index.collects))
                            }
                        )
                        Text(
                            text = if (state.index == ComposeIndex.Empty) "\u3000" else buildString {
                                append(stringResource(Res.string.index_detail_created_label, state.index.createdAt.formatDate("yyyy-MM-dd")))
                                append("\u3000")
                                append(stringResource(Res.string.index_detail_updated_label, state.index.updatedAt.formatDate("yyyy-MM-dd")))
                            }
                        )
                    }
                }
            )
        }
    }
}


@Composable
private fun IndexDetailScreenContent(
    state: IndexDetailState,
    pagerState: androidx.compose.foundation.pager.PagerState,
    onUiEvent: (IndexDetailEvent.UI) -> Unit,
    onActionEvent: (IndexDetailEvent.Action) -> Unit,
) {
    BgmTabHorizontalPager(
        modifier = Modifier.fillMaxSize(),
        pagerState = pagerState,
        tabs = state.tabs
    ) {
        val tab = state.tabs[it]

        IndexDetailPageScreen(
            param = remember(tab, state.index) {
                ListIndexRelatedParam(
                    type = tab.type,
                    indexId = state.index.id
                )
            },
            onNavScreen = { screen ->
                onUiEvent(IndexDetailEvent.UI.OnNavScreen(screen))
            }
        )
    }
}


@Composable
@Preview
private fun PreviewIndexDetailScreen() {
    PreviewColumn(modifier = Modifier.fillMaxSize()) {
        IndexDetailScreen(
            uiState = UiState(
                IndexDetailState(
                    tabs = persistentListOf(
                        ComposeTextTab(IndexCatWebTabType.ALL, Res.string.global_all)
                    )
                )
            ),
            onUiEvent = {},
            onActionEvent = {},
        )
    }
}
