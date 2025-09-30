package com.xiaoyv.bangumi.features.mono.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.BookmarkAdded
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_image
import com.xiaoyv.bangumi.core_resource.resources.ic_add_index
import com.xiaoyv.bangumi.core_resource.resources.subject_action_more
import com.xiaoyv.bangumi.features.index.page.dialog.IndexDialog
import com.xiaoyv.bangumi.features.mono.detail.business.MonoDetailEvent
import com.xiaoyv.bangumi.features.mono.detail.business.MonoDetailState
import com.xiaoyv.bangumi.features.mono.detail.business.MonoDetailViewModel
import com.xiaoyv.bangumi.features.mono.detail.page.MonoDetailAlbumScreen
import com.xiaoyv.bangumi.features.mono.detail.page.MonoDetailCastsScreen
import com.xiaoyv.bangumi.features.mono.detail.page.MonoDetailCollabsScreen
import com.xiaoyv.bangumi.features.mono.detail.page.MonoDetailCollectionsScreen
import com.xiaoyv.bangumi.features.mono.detail.page.MonoDetailIndexScreen
import com.xiaoyv.bangumi.features.mono.detail.page.MonoDetailMainScreen
import com.xiaoyv.bangumi.features.mono.detail.page.MonoDetailPicturesScreen
import com.xiaoyv.bangumi.features.mono.detail.page.MonoDetailWorksScreen
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.mvi.interceptEvent
import com.xiaoyv.bangumi.shared.core.types.ButtonType
import com.xiaoyv.bangumi.shared.core.types.IndexCatType
import com.xiaoyv.bangumi.shared.core.types.MonoDetailTab
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalSharedState
import com.xiaoyv.bangumi.shared.data.model.request.IndexTarget
import com.xiaoyv.bangumi.shared.data.model.response.image.ComposeGallery
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
fun MonoDetailRoute(
    viewModel: MonoDetailViewModel = koinViewModel<MonoDetailViewModel>(),
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()
    val animePicImageItems = viewModel.animePicImages.collectAsLazyPagingItems()
    val pixivImageItems = viewModel.pixivImages.collectAsLazyPagingItems()

    viewModel.collectBaseSideEffect {

    }

    MonoDetailScreen(
        baseState = baseState,
        pixivImageItems = pixivImageItems,
        animePicImageItems = animePicImageItems,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is MonoDetailEvent.UI.OnNavUp -> onNavUp()
                is MonoDetailEvent.UI.OnNavScreen -> onNavScreen(it.screen)
                else -> Unit
            }
        },
    )
}

@Composable
private fun MonoDetailScreen(
    baseState: BaseState<MonoDetailState>,
    pixivImageItems: LazyPagingItems<ComposeGallery>,
    animePicImageItems: LazyPagingItems<ComposeGallery>,
    onUiEvent: (MonoDetailEvent.UI) -> Unit,
    onActionEvent: (MonoDetailEvent.Action) -> Unit,
) {
    BgmCollapsingScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = baseState.payload?.mono?.displayName,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = it),
                    titleContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = it)
                ),
                actions = {
                    baseState.content {
                        val actionHandler = LocalActionHandler.current
                        val sharedState = LocalSharedState.current
                        val sheetDialogState = rememberSheetDialogState()

                        IndexDialog(
                            state = sheetDialogState,
                            target = remember(mono) {
                                IndexTarget(
                                    cat = if (type == MonoType.PERSON) IndexCatType.PERSON else IndexCatType.CHARACTER,
                                    displayName = mono.displayName,
                                    relateId = mono.id
                                )
                            }
                        )

                        IconButton(
                            onClick = {
                                if (sharedState.isLogin) sheetDialogState.show() else {
                                    onUiEvent(MonoDetailEvent.UI.OnNavScreen(Screen.SignIn))
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_add_index),
                                contentDescription = stringResource(Res.string.subject_action_more)
                            )
                        }

                        IconButton(onClick = { onActionEvent(MonoDetailEvent.Action.OnToggleBookmarkMono) }) {
                            Icon(
                                imageVector = if (mono.collectedAt > 0) BgmIcons.BookmarkAdded else BgmIcons.BookmarkAdd,
                                contentDescription = stringResource(Res.string.global_image),
                                tint = if (mono.collectedAt > 0) StarColor else LocalContentColor.current
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
                                ButtonType.Share -> actionHandler.shareContent(mono.shareUrl(type))
                                ButtonType.OpenInBrowser -> actionHandler.openInBrowser(mono.shareUrl(type))
                                ButtonType.CopyLink -> actionHandler.copyContent(mono.shareUrl(type))
                                else -> Unit
                            }
                        }
                    }
                },
                onNavigationClick = { onUiEvent(MonoDetailEvent.UI.OnNavUp) }
            )
        },
        collapse = {
            baseState.content {
                MonoDetailScreenHeader(
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
                onRefresh = { onActionEvent(MonoDetailEvent.Action.OnRefresh(it)) },
                baseState = baseState,
            ) { state ->
                CompositionLocalProvider(LocalCollapsingPullRefresh provides (it == 0f)) {
                    MonoDetailScreenContent(
                        state = state,
                        pixivImageItems = pixivImageItems,
                        animePicImageItems = animePicImageItems,
                        onUiEvent = onUiEvent,
                        onActionEvent = onActionEvent
                    )
                }
            }
        }
    )
}

@Composable
private fun MonoDetailScreenHeader(
    modifier: Modifier,
    insets: PaddingValues,
    state: MonoDetailState,
    onUiEvent: (MonoDetailEvent.UI) -> Unit,
    onActionEvent: (MonoDetailEvent.Action) -> Unit,
) {
    Box(modifier = modifier) {
        BlurImage(
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.FillBounds,
            model = state.mono.images.displayGridImage,
            contentDescription = state.mono.displayName
        )

        Row(
            modifier = Modifier
                .matchParentSize()
                .padding(insets)
        ) {
            StateImage(
                modifier = Modifier
                    .padding(
                        top = LayoutPaddingHalf,
                        bottom = LayoutPadding,
                        start = LayoutPadding,
                        end = LayoutPaddingHalf
                    )
                    .fillMaxHeight()
                    .aspectRatio(3 / 4f)
                    .clickable { onUiEvent(MonoDetailEvent.UI.OnNavScreen(Screen.PreviewMain(state.mono.images.displayLargeImage))) },
                shape = MaterialTheme.shapes.small,
                model = state.mono.images.displayLargeImage,
                loadingThumb = state.mono.images.displayGridImage,
                alignment = Alignment.TopCenter,
                transparent = true,
                contentDescription = stringResource(Res.string.global_image)
            )

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
                    text = state.mono.displayName,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    modifier = Modifier.basicMarquee(),
                    text = state.mono.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    maxLines = 1,
                )

                // 职业
                Text(
                    text = "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f),
                    maxLines = 1,
                    overflow = TextOverflow.MiddleEllipsis,
                )

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun MonoDetailScreenContent(
    state: MonoDetailState,
    pixivImageItems: LazyPagingItems<ComposeGallery>,
    animePicImageItems: LazyPagingItems<ComposeGallery>,
    onUiEvent: (MonoDetailEvent.UI) -> Unit,
    onActionEvent: (MonoDetailEvent.Action) -> Unit,
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
            MonoDetailTab.OVERVIEW -> MonoDetailMainScreen(
                state = state,
                onUiEvent = interceptEvent(onUiEvent) { event ->
                    if (event is MonoDetailEvent.UI.OnSelectedPageType) {
                        scope.launch {
                            pagerState.animateScrollToPage(tabs.indexOfFirst { tab -> tab.type == event.page })
                        }
                    }
                    false
                },
                onActionEvent = onActionEvent
            )

            MonoDetailTab.WORKS -> MonoDetailWorksScreen(
                state = state,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )

            MonoDetailTab.CASTS -> MonoDetailCastsScreen(
                state = state,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )

            MonoDetailTab.COLLABS -> MonoDetailCollabsScreen(
                state = state,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )

            MonoDetailTab.ALBUM -> MonoDetailAlbumScreen(
                state = state,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )

            MonoDetailTab.COLLECTIONS -> MonoDetailCollectionsScreen(
                state = state,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )

            MonoDetailTab.ANIME_PICTURES -> MonoDetailPicturesScreen(
                state = state,
                imageItems = animePicImageItems,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )

            MonoDetailTab.PIXIV -> MonoDetailPicturesScreen(
                state = state,
                imageItems = pixivImageItems,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )

            MonoDetailTab.INDEX -> MonoDetailIndexScreen(
                state = state,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )
        }
    }
}

