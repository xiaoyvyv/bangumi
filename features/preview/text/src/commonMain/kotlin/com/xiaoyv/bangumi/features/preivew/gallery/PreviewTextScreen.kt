package com.xiaoyv.bangumi.features.preivew.gallery

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.rounded.Article
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_detail
import com.xiaoyv.bangumi.features.preivew.gallery.business.PreviewTextEvent
import com.xiaoyv.bangumi.features.preivew.gallery.business.PreviewTextState
import com.xiaoyv.bangumi.features.preivew.gallery.business.PreviewTextViewModel
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.types.LoadingState
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.component.text.BgmLinkedText
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.theme.BgmIconsMirrored
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun PreviewTextRoute(
    viewModel: PreviewTextViewModel = koinViewModel<PreviewTextViewModel>(),
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    PreviewTextScreen(
        baseState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is PreviewTextEvent.UI.OnNavUp -> onNavUp()
                is PreviewTextEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun PreviewTextScreen(
    baseState: BaseState<PreviewTextState>,
    onUiEvent: (PreviewTextEvent.UI) -> Unit,
    onActionEvent: (PreviewTextEvent.Action) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                title = stringResource(Res.string.global_detail),
                actions = {
                    baseState.content {
                        IconButton(
                            enabled = loading != LoadingState.Loading,
                            onClick = { onActionEvent(PreviewTextEvent.Action.OnToggleTranslate) }
                        ) {
                            if (loading == LoadingState.Loading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(
                                    imageVector = if (showOrigin) BgmIcons.Translate else BgmIconsMirrored.Article,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                },
                onNavigationClick = { onUiEvent(PreviewTextEvent.UI.OnNavUp) }
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            onRefresh = { onActionEvent(PreviewTextEvent.Action.OnRefresh(it)) },
            baseState = baseState,
        ) { state ->
            PreviewTextScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}


@Composable
private fun PreviewTextScreenContent(
    state: PreviewTextState,
    onUiEvent: (PreviewTextEvent.UI) -> Unit,
    onActionEvent: (PreviewTextEvent.Action) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = LayoutPadding)
    ) {
        BgmLinkedText(
            modifier = Modifier.fillMaxWidth(),
            text = if (state.showOrigin) state.originText else state.translateText
        )
    }
}

