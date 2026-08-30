package com.xiaoyv.bangumi.features.preivew.gallery

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import com.xiaoyv.bangumi.shared.ui.component.scroll.rememberScrollUpScrollState as rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Article
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material.icons.rounded.Preview
import androidx.compose.material.icons.rounded.ToggleOff
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material.icons.rounded.Undo
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
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.types.ButtonType
import com.xiaoyv.bangumi.shared.core.types.LoadingState
import com.xiaoyv.bangumi.shared.core.types.TopicType
import com.xiaoyv.bangumi.shared.ui.component.action.LocalActionHandler
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.chip.DropMenuActionButton
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.tab.rememberButtonTypeMenu
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.component.text.BgmLinkedText
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.theme.BgmIconsMirrored
import org.jetbrains.compose.resources.stringResource
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun PreviewTextRoute(
    viewModel: PreviewTextViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    PreviewTextScreen(
        uiState = baseState,
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
    uiState: UiState<PreviewTextState>,
    onUiEvent: (PreviewTextEvent.UI) -> Unit,
    onActionEvent: (PreviewTextEvent.Action) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                title = stringResource(Res.string.global_detail),
                actions = {
                    uiState.data.run {
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

                        val actionHandler = LocalActionHandler.current

                        DropMenuActionButton(
                            options = rememberButtonTypeMenu {
                                add(ButtonType.Copy)
                                add(ButtonType.Share)
                            },
                            onOptionClick = {
                                val content = if (uiState.data.showOrigin) {
                                    uiState.data.originText
                                } else {
                                    uiState.data.translateText
                                }
                                when (it.type) {
                                    ButtonType.Share -> actionHandler.shareContent(content)
                                    ButtonType.Copy -> actionHandler.copyContent(content)
                                    else -> Unit
                                }
                            }
                        )
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
            uiState = uiState,
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
            .padding(horizontal = ContentMargin)
    ) {
        BgmLinkedText(
            modifier = Modifier.fillMaxWidth(),
            text = if (state.showOrigin) state.originText else state.translateText
        )
    }
}
