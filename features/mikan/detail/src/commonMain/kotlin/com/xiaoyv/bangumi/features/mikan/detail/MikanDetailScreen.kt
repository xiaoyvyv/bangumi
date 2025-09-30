package com.xiaoyv.bangumi.features.mikan.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Deselect
import androidx.compose.material.icons.rounded.SelectAll
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_copy
import com.xiaoyv.bangumi.core_resource.resources.global_share
import com.xiaoyv.bangumi.core_resource.resources.mikan_resource_detail
import com.xiaoyv.bangumi.features.mikan.detail.business.MikanDetailEvent
import com.xiaoyv.bangumi.features.mikan.detail.business.MikanDetailSideEffect
import com.xiaoyv.bangumi.features.mikan.detail.business.MikanDetailState
import com.xiaoyv.bangumi.features.mikan.detail.business.MikanDetailViewModel
import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmLargeTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.view.magnet.MikanMagnetItem
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun MikanDetailRoute(
    viewModel: MikanDetailViewModel = koinViewModel<MikanDetailViewModel>(),
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()
    val clipboard = LocalClipboard.current
    val uriHandler = LocalUriHandler.current
    viewModel.collectBaseSideEffect {
        when (it) {
            is MikanDetailSideEffect.OnCopyText -> {
                clipboard.setClipEntry(System.createClipEntry(it.data))
            }

            is MikanDetailSideEffect.OnOpenUri -> {
                runCatching { uriHandler.openUri(it.uri) }
            }
        }
    }

    MikanDetailScreen(
        baseState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is MikanDetailEvent.UI.OnNavUp -> onNavUp()
                is MikanDetailEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun MikanDetailScreen(
    baseState: BaseState<MikanDetailState>,
    onUiEvent: (MikanDetailEvent.UI) -> Unit,
    onActionEvent: (MikanDetailEvent.Action) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            BgmLargeTopAppBar(
                title = baseState.payload?.groupName
                    ?: stringResource(Res.string.mikan_resource_detail),
                scrollBehavior = scrollBehavior,
                actions = {
                    baseState.content {
                        if (checkMode) {
                            IconButton(onClick = { onActionEvent(MikanDetailEvent.Action.OnToggleSelectAll) }) {
                                Icon(
                                    imageVector = if (checkItems.size == resources.size) BgmIcons.Deselect else BgmIcons.SelectAll,
                                    contentDescription = null
                                )
                            }
                        }
                        IconButton(onClick = { onActionEvent(MikanDetailEvent.Action.OnToggleCheckMode) }) {
                            Icon(
                                imageVector = if (checkMode) BgmIcons.Close else BgmIcons.Checklist,
                                contentDescription = null
                            )
                        }
                    }
                },
                onNavigationClick = { onUiEvent(MikanDetailEvent.UI.OnNavUp) }
            )
        },
        bottomBar = {
            baseState.content {
                AnimatedVisibility(modifier = Modifier.fillMaxWidth(), visible = checkMode) {
                    BottomAppBar(
                        modifier = Modifier.fillMaxWidth(),
                        actions = {
                            TextButton(onClick = { onActionEvent(MikanDetailEvent.Action.OnCopy) }) {
                                Icon(BgmIcons.ContentCopy, stringResource(Res.string.global_copy))
                                Spacer(modifier = Modifier.width(LayoutPaddingHalf / 2))
                                Text(text = stringResource(Res.string.global_copy))
                            }
                        },
                        floatingActionButton = {
                            FloatingActionButton(onClick = { onActionEvent(MikanDetailEvent.Action.OnShare) }) {
                                Icon(
                                    imageVector = BgmIcons.Share,
                                    contentDescription = stringResource(Res.string.global_share)
                                )
                            }
                        }
                    )
                }
            }
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            onRefresh = { onActionEvent(MikanDetailEvent.Action.OnRefresh(it)) },
            baseState = baseState,
        ) { state ->
            MikanDetailScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}


@Composable
private fun MikanDetailScreenContent(
    state: MikanDetailState,
    onUiEvent: (MikanDetailEvent.UI) -> Unit,
    onActionEvent: (MikanDetailEvent.Action) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(state.resources) { index, item ->
            MikanMagnetItem(
                modifier = Modifier.fillMaxWidth(),
                item = item,
                checked = state.checkItems.contains(index),
                checkMode = state.checkMode,
                onCheckedChange = {
                    onActionEvent(MikanDetailEvent.Action.OnToggleItem(index))
                }
            )
        }
    }
}
