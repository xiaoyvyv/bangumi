package com.xiaoyv.bangumi.features.subject.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.rounded.RssFeed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_image
import com.xiaoyv.bangumi.core_resource.resources.global_rank_no
import com.xiaoyv.bangumi.core_resource.resources.ic_add_index
import com.xiaoyv.bangumi.core_resource.resources.subject_action_more
import com.xiaoyv.bangumi.core_resource.resources.subject_locked
import com.xiaoyv.bangumi.features.index.page.dialog.IndexDialog
import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailEvent
import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailState
import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailViewModel
import com.xiaoyv.bangumi.features.subject.detail.page.SubjectDetailBlogScreen
import com.xiaoyv.bangumi.features.subject.detail.page.SubjectDetailCharacterScreen
import com.xiaoyv.bangumi.features.subject.detail.page.SubjectDetailChartScreen
import com.xiaoyv.bangumi.features.subject.detail.page.SubjectDetailEpisodeScreen
import com.xiaoyv.bangumi.features.subject.detail.page.SubjectDetailIndexScreen
import com.xiaoyv.bangumi.features.subject.detail.page.SubjectDetailMainScreen
import com.xiaoyv.bangumi.features.subject.detail.page.SubjectDetailPersonScreen
import com.xiaoyv.bangumi.features.subject.detail.page.SubjectDetailPreviewScreen
import com.xiaoyv.bangumi.features.subject.detail.page.SubjectDetailRelatedScreen
import com.xiaoyv.bangumi.features.subject.detail.page.SubjectDetailTopicScreen
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.mvi.interceptEvent
import com.xiaoyv.bangumi.shared.core.types.ButtonType
import com.xiaoyv.bangumi.shared.core.types.IndexCatType
import com.xiaoyv.bangumi.shared.core.types.SubjectDetailTab
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalSharedState
import com.xiaoyv.bangumi.shared.data.manager.shared.currentMikanId
import com.xiaoyv.bangumi.shared.data.model.request.IndexTarget
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeComment
import com.xiaoyv.bangumi.shared.ui.component.action.LocalActionHandler
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.chip.DropMenuActionButton
import com.xiaoyv.bangumi.shared.ui.component.dialog.sheet.rememberSheetDialogState
import com.xiaoyv.bangumi.shared.ui.component.image.BlurImage
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.layout.BgmCollapsingScaffold
import com.xiaoyv.bangumi.shared.ui.component.layout.LocalCollapsingPullRefresh
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.pager.BgmTabHorizontalPager
import com.xiaoyv.bangumi.shared.ui.component.pager.rememberBgmPagerState
import com.xiaoyv.bangumi.shared.ui.component.paging.LazyPagingItems
import com.xiaoyv.bangumi.shared.ui.component.paging.collectAsLazyPagingItems
import com.xiaoyv.bangumi.shared.ui.component.space.BrushVerticalTransparentToHalfRed
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.tab.rememberButtonTypeMenu
import com.xiaoyv.bangumi.shared.ui.component.text.StarColor
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState


@Composable
fun SubjectDetailRoute(
    viewModel: SubjectDetailViewModel = koinViewModel<SubjectDetailViewModel>(),
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()
    val commentPagingItems = viewModel.subjectComments.collectAsLazyPagingItems()

    viewModel.collectBaseSideEffect {
    }

    SubjectDetailScreen(
        baseState = baseState,
        commentPagingItems = commentPagingItems,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is SubjectDetailEvent.UI.OnNavUp -> onNavUp()
                is SubjectDetailEvent.UI.OnNavScreen -> onNavScreen(it.screen)
                else -> Unit
            }
        }
    )
}

@Composable
private fun SubjectDetailScreen(
    baseState: BaseState<SubjectDetailState>,
    commentPagingItems: LazyPagingItems<ComposeComment>,
    onUiEvent: (SubjectDetailEvent.UI) -> Unit,
    onActionEvent: (SubjectDetailEvent.Action) -> Unit,
) {
    BgmCollapsingScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = baseState.payload?.subject?.displayName.orEmpty(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = it),
                    titleContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = it)
                ),
                actions = {
                    baseState.content {
                        val mikanId by currentMikanId(id)

                        IconButton(
                            onClick = {
                                if (mikanId.isNotBlank()) {
                                    onUiEvent(SubjectDetailEvent.UI.OnNavScreen(Screen.MikanStudio(id, mikanId)))
                                } else {
                                    onUiEvent(SubjectDetailEvent.UI.OnNavScreen(Screen.Garden(magnetQuery())))
                                }
                            },
                            content = {
                                Icon(
                                    imageVector = BgmIcons.RssFeed,
                                    contentDescription = stringResource(Res.string.subject_action_more)
                                )
                            }
                        )

                        val sheetDialogState = rememberSheetDialogState()
                        val sharedState = LocalSharedState.current
                        val actionHandler = LocalActionHandler.current

                        IndexDialog(
                            state = sheetDialogState,
                            target = remember(subject) {
                                IndexTarget(
                                    cat = IndexCatType.SUBJECT,
                                    displayName = subject.displayName,
                                    relateId = subject.id
                                )
                            }
                        )

                        IconButton(
                            onClick = {
                                if (sharedState.isLogin) sheetDialogState.show() else {
                                    onUiEvent(SubjectDetailEvent.UI.OnNavScreen(Screen.SignIn))
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_add_index),
                                contentDescription = stringResource(Res.string.subject_action_more)
                            )
                        }

                        DropMenuActionButton(
                            options = rememberButtonTypeMenu {
                                add(ButtonType.Netabare)
                                add(ButtonType.Share)
                                add(ButtonType.CopyLink)
                                add(ButtonType.OpenInBrowser)
                            }
                        ) { item ->
                            when (item.type) {
                                ButtonType.Netabare -> actionHandler.openInBrowser("https://netaba.re/subject/$id")
                                ButtonType.Share -> actionHandler.shareContent(subject.shareUrl)
                                ButtonType.OpenInBrowser -> actionHandler.openInBrowser(subject.shareUrl)
                                ButtonType.CopyLink -> actionHandler.copyContent(subject.shareUrl)
                                else -> Unit
                            }
                        }
                    }
                },
                onNavigationClick = { onUiEvent(SubjectDetailEvent.UI.OnNavUp) }
            )
        },
        collapse = {
            baseState.content {
                SubjectDetailScreenHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    insets = it,
                    state = this,
                    onUiEvent = onUiEvent,
                    onActionEvent = onActionEvent
                )
            }
        },
        content = {
            StateLayout(
                modifier = Modifier.fillMaxSize(),
                onRefresh = { onActionEvent(SubjectDetailEvent.Action.OnRefresh(it)) },
                baseState = baseState,
            ) { state ->
                CompositionLocalProvider(LocalCollapsingPullRefresh provides (it == 0f)) {
                    SubjectDetailScreenContent(
                        state = state,
                        commentPagingItems = commentPagingItems,
                        onUiEvent = onUiEvent,
                        onActionEvent = onActionEvent
                    )
                }
            }
        }
    )
}

@Composable
private fun SubjectDetailScreenHeader(
    modifier: Modifier,
    insets: PaddingValues,
    state: SubjectDetailState,
    onUiEvent: (SubjectDetailEvent.UI) -> Unit,
    onActionEvent: (SubjectDetailEvent.Action) -> Unit,
) {
    Box(modifier = modifier) {
        BlurImage(
            modifier = Modifier.matchParentSize(),
            model = state.subject.images.displayGridImage,
            contentScale = ContentScale.FillBounds,
            filterQuality = FilterQuality.High,
            contentDescription = state.subject.displayName,
            androidSampling = 2f
        )

        Row(
            modifier = Modifier
                .matchParentSize()
                .padding(insets)
        ) {
            Box(
                modifier = Modifier
                    .padding(
                        top = LayoutPaddingHalf,
                        bottom = LayoutPadding,
                        start = LayoutPadding,
                        end = LayoutPaddingHalf
                    )
                    .fillMaxHeight()
                    .aspectRatio(3 / 4f),
            ) {
                StateImage(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable {
                            onUiEvent(SubjectDetailEvent.UI.OnNavScreen(Screen.PreviewMain(state.subject.images.displayLargeImage)))
                        },
                    shape = MaterialTheme.shapes.small,
                    contentScale = ContentScale.FillBounds,
                    model = state.subject.images.displayLargeImage,
                    contentDescription = stringResource(Res.string.global_image),
                )

                // 排名
                if (state.subject.rating.rank != 0) Text(
                    modifier = Modifier
                        .padding(top = LayoutPaddingHalf)
                        .align(Alignment.TopStart)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                            RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp),
                        )
                        .padding(LayoutPaddingHalf, 4.dp),
                    text = stringResource(Res.string.global_rank_no) + " " + state.subject.rating.rank,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                // 锁定
                if (state.subject.locked) Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            BrushVerticalTransparentToHalfRed,
                            MaterialTheme.shapes.small.copy(
                                topStart = CornerSize(0.dp),
                                topEnd = CornerSize(0.dp)
                            )
                        )
                        .padding(bottom = LayoutPaddingHalf, top = LayoutPadding * 2)
                        .padding(horizontal = LayoutPaddingHalf),
                    text = stringResource(Res.string.subject_locked),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onError,
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(
                        top = LayoutPaddingHalf,
                        bottom = LayoutPadding,
                        start = LayoutPaddingHalf,
                        end = LayoutPadding
                    ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = state.subject.displayName,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (state.subject.name.isNotBlank()
                    && state.subject.displayName != state.subject.name
                ) Text(
                    modifier = Modifier.basicMarquee(),
                    text = state.subject.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    maxLines = 1,
                )
                // 日期
                Text(
                    text = state.subject.rememberDisplayDateAndType(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f),
                    maxLines = 1,
                    overflow = TextOverflow.MiddleEllipsis,
                )
                Spacer(modifier = Modifier.weight(1f))

                // 评分
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (state.subject.rating.score > 0) {
                        Text(
                            modifier = Modifier.align(Alignment.Bottom),
                            text = state.subject.rememberDisplayScoreText(StarColor),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = StarColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SubjectDetailScreenContent(
    state: SubjectDetailState,
    commentPagingItems: LazyPagingItems<ComposeComment>,
    onUiEvent: (SubjectDetailEvent.UI) -> Unit,
    onActionEvent: (SubjectDetailEvent.Action) -> Unit,
) {
    val tabs = state.rememberTabs()
    val pagerState = rememberBgmPagerState { tabs.size }
    val scope = rememberCoroutineScope()

    BgmTabHorizontalPager(
        modifier = Modifier.fillMaxSize(),
        pagerState = pagerState,
        tabs = tabs
    ) {
        when (tabs[it].type) {
            SubjectDetailTab.OVERVIEW -> SubjectDetailMainScreen(
                state = state,
                commentPagingItems = commentPagingItems,
                onUiEvent = interceptEvent(onUiEvent) { event ->
                    if (event is SubjectDetailEvent.UI.OnSelectedPageType) {
                        scope.launch { pagerState.animateScrollToPage(tabs.indexOfFirst { tab -> tab.type == event.tab }) }
                    }
                    false
                },
                onActionEvent = onActionEvent
            )

            SubjectDetailTab.PREVIEW -> SubjectDetailPreviewScreen(
                state = state,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )

            SubjectDetailTab.EPISODE -> SubjectDetailEpisodeScreen(
                state = state,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )

            SubjectDetailTab.CHARACTER -> SubjectDetailCharacterScreen(
                state = state,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )

            SubjectDetailTab.PERSON -> SubjectDetailPersonScreen(
                state = state,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )

            SubjectDetailTab.BLOG -> SubjectDetailBlogScreen(
                state = state,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )

            SubjectDetailTab.TOPIC -> SubjectDetailTopicScreen(
                state = state,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )

            SubjectDetailTab.CHART -> SubjectDetailChartScreen(
                state = state,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )

            SubjectDetailTab.INDEX -> SubjectDetailIndexScreen(
                state = state,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )

            SubjectDetailTab.RELATED -> SubjectDetailRelatedScreen(
                state = state,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )
        }
    }
}