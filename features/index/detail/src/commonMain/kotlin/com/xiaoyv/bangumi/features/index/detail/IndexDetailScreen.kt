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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_all
import com.xiaoyv.bangumi.core_resource.resources.global_bookmark
import com.xiaoyv.bangumi.features.index.detail.business.IndexDetailEvent
import com.xiaoyv.bangumi.features.index.detail.business.IndexDetailState
import com.xiaoyv.bangumi.features.index.detail.business.IndexDetailViewModel
import com.xiaoyv.bangumi.features.index.detail.page.IndexDetailPageScreen
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.types.ButtonType
import com.xiaoyv.bangumi.shared.core.types.IndexCatWebTabType
import com.xiaoyv.bangumi.shared.core.utils.formatDate
import com.xiaoyv.bangumi.shared.data.model.request.list.index.ListIndexRelatedParam
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
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import com.xiaoyv.bangumi.shared.ui.component.tab.rememberButtonTypeMenu
import com.xiaoyv.bangumi.shared.ui.component.text.StarColor
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
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
        baseState = baseState,
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
    baseState: BaseState<IndexDetailState>,
    onUiEvent: (IndexDetailEvent.UI) -> Unit,
    onActionEvent: (IndexDetailEvent.Action) -> Unit,
) {
    BgmCollapsingScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                title = baseState.content { index.title },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = it),
                    titleContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = it),
                ),
                actions = {
                    baseState.content {
                        val actionHandler = LocalActionHandler.current
                        IconButton(onClick = { onActionEvent(IndexDetailEvent.Action.OnToggleBookmarkIndex) }) {
                            Icon(
                                imageVector = if (index.isBookmarked) BgmIcons.BookmarkAdded else BgmIcons.BookmarkAdd,
                                contentDescription = stringResource(Res.string.global_bookmark),
                                tint = if (index.isBookmarked) StarColor else LocalContentColor.current
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
                                ButtonType.Share -> actionHandler.shareContent(index.shareUrl)
                                ButtonType.OpenInBrowser -> actionHandler.openInBrowser(index.shareUrl)
                                ButtonType.CopyLink -> actionHandler.copyContent(index.shareUrl)
                                else -> Unit
                            }
                        }
                    }
                },
                onNavigationClick = { onUiEvent(IndexDetailEvent.UI.OnNavUp) }
            )
        },
        collapse = {
            baseState.content {
                IndexDetailScreenHeader(
                    state = this,
                    padding = it,
                    onUiEvent = onUiEvent,
                    onActionEvent = onActionEvent
                )
            }
        }
    ) {
        StateLayout(
            modifier = Modifier.fillMaxSize(),
            onRefresh = { loading -> onActionEvent(IndexDetailEvent.Action.OnRefresh(loading)) },
            baseState = baseState,
        ) { state ->
            CompositionLocalProvider(LocalCollapsingPullRefresh provides (it == 0f)) {
                IndexDetailScreenContent(state, onUiEvent, onActionEvent)
            }
        }
    }
}

@Composable
private fun IndexDetailScreenHeader(
    state: IndexDetailState,
    padding: PaddingValues,
    onUiEvent: (IndexDetailEvent.UI) -> Unit,
    onActionEvent: (IndexDetailEvent.Action) -> Unit,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        BlurImage(
            modifier = Modifier.matchParentSize(),
            model = state.index.creator.avatar.displayGridImage,
            contentDescription = state.index.title,
            contentScale = ContentScale.Crop
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
        ListItem(
            modifier = Modifier
                .padding(padding)
                .padding(vertical = LayoutPaddingHalf),
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent,
                headlineColor = MaterialTheme.colorScheme.onPrimary,
                supportingColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
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
                    text = state.index.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            supportingContent = {
                Column {
                    Text(
                        modifier = Modifier.padding(vertical = LayoutPaddingHalf),
                        text = buildString {
                            append(state.index.total)
                            append("个收录 ")
                            append(state.index.collects)
                            append("人收藏")
                        }
                    )
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onPrimary)) {
                                append(state.index.creator.nickname)
                            }
                            append(" 创建 ")
                            append(state.index.createdAt.formatDate("yyyy-MM-dd"))
                            append(" 更新 ")
                            append(state.index.updatedAt.formatDate("yyyy-MM-dd"))
                        }
                    )
                }
            }
        )
    }
}


@Composable
private fun IndexDetailScreenContent(
    state: IndexDetailState,
    onUiEvent: (IndexDetailEvent.UI) -> Unit,
    onActionEvent: (IndexDetailEvent.Action) -> Unit,
) {
    BgmTabHorizontalPager(
        modifier = Modifier.fillMaxSize(),
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
            baseState = BaseState.Success(
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

