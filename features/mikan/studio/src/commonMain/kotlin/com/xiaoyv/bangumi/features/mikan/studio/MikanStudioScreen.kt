package com.xiaoyv.bangumi.features.mikan.studio

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.rounded.RssFeed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_copy_success
import com.xiaoyv.bangumi.core_resource.resources.mikan_title
import com.xiaoyv.bangumi.features.mikan.studio.business.MikanStudioEvent
import com.xiaoyv.bangumi.features.mikan.studio.business.MikanStudioState
import com.xiaoyv.bangumi.features.mikan.studio.business.MikanStudioViewModel
import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmLargeTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.divider.BgmHorizontalDivider
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.popup.LocalPopupTipState
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun MikanStudioRoute(
    viewModel: MikanStudioViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    MikanStudioScreen(
        baseState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is MikanStudioEvent.UI.OnNavUp -> onNavUp()
                is MikanStudioEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun MikanStudioScreen(
    baseState: BaseState<MikanStudioState>,
    onUiEvent: (MikanStudioEvent.UI) -> Unit,
    onActionEvent: (MikanStudioEvent.Action) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            BgmLargeTopAppBar(
                title = stringResource(Res.string.mikan_title),
                scrollBehavior = scrollBehavior,
                onNavigationClick = { onUiEvent(MikanStudioEvent.UI.OnNavUp) }
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            onRefresh = { onActionEvent(MikanStudioEvent.Action.OnRefresh(it)) },
            baseState = baseState,
        ) { state ->
            MikanStudioScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}


@Composable
private fun MikanStudioScreenContent(
    state: MikanStudioState,
    onUiEvent: (MikanStudioEvent.UI) -> Unit,
    onActionEvent: (MikanStudioEvent.Action) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(state.groupInfo) {
            ListItem(
                modifier = Modifier
                    .clickable {
                        onUiEvent(
                            MikanStudioEvent.UI.OnNavScreen(
                                Screen.MikanResources(
                                    state.mikanId,
                                    it.id.orEmpty(),
                                    it.name.orEmpty()
                                )
                            )
                        )
                    }
                    .padding(vertical = LayoutPaddingHalf / 2f),
                headlineContent = {
                    Text(text = it.name.orEmpty())
                },
                leadingContent = {
                    StateImage(
                        modifier = Modifier.size(44.dp),
                        model = it.poster.orEmpty(),
                        shape = CircleShape
                    )
                },
                supportingContent = {
                    Text(
                        modifier = Modifier.padding(vertical = LayoutPaddingHalf / 2f),
                        text = it.time.orEmpty()
                    )
                },
                trailingContent = {
                    val clipboard = LocalClipboard.current
                    val scope = rememberCoroutineScope()
                    val popupTipState = LocalPopupTipState.current
                    IconButton(
                        onClick = {
                            scope.launch {
                                clipboard.setClipEntry(System.createClipEntry(it.rssUrl))
                                popupTipState.showToast(getString(Res.string.global_copy_success))
                            }
                        }
                    ) {
                        Icon(
                            tint = MaterialTheme.colorScheme.primary,
                            imageVector = BgmIcons.RssFeed,
                            contentDescription = stringResource(Res.string.mikan_title)
                        )
                    }
                }
            )
            BgmHorizontalDivider()
        }
    }
}

