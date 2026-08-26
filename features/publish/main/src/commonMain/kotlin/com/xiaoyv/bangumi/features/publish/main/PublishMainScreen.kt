package com.xiaoyv.bangumi.features.publish.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_title
import com.xiaoyv.bangumi.core_resource.resources.timeline_add
import com.xiaoyv.bangumi.core_resource.resources.timeline_add_placeholder
import com.xiaoyv.bangumi.core_resource.resources.timeline_add_publish
import com.xiaoyv.bangumi.features.publish.main.business.PublishMainEvent
import com.xiaoyv.bangumi.features.publish.main.business.PublishMainSideEffect
import com.xiaoyv.bangumi.features.publish.main.business.PublishMainState
import com.xiaoyv.bangumi.features.publish.main.business.PublishMainViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.utils.ImePanelColumn
import com.xiaoyv.bangumi.shared.core.utils.rememberImePanelState
import com.xiaoyv.bangumi.shared.core.utils.resetSize
import com.xiaoyv.bangumi.shared.data.constant.WebConstant
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.dialog.comment.CommentDialogPanel
import com.xiaoyv.bangumi.shared.ui.component.dialog.comment.InputActionBar
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.text.BmgTextField
import com.xiaoyv.bangumi.shared.ui.component.text.textFieldTransparentColors
import com.xiaoyv.bangumi.shared.ui.component.turnstile.BgmTurnstile
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import org.jetbrains.compose.resources.stringResource
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun PublishMainRoute(
    viewModel: PublishMainViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val uiState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {
        when (it) {
            is PublishMainSideEffect.OnCreatePostSuccess -> onNavUp()
        }
    }

    PublishMainScreen(
        uiState = uiState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = { event ->
            when (event) {
                is PublishMainEvent.UI.OnNavUp -> onNavUp()
                is PublishMainEvent.UI.OnNavScreen -> onNavScreen(event.screen)
            }
        },
    )
}

@Composable
private fun PublishMainScreen(
    uiState: UiState<PublishMainState>,
    onUiEvent: (PublishMainEvent.UI) -> Unit,
    onActionEvent: (PublishMainEvent.Action) -> Unit
) {
    val state = uiState.data

    val title = stringResource(state.title)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                title = title,
                onNavigationClick = { onUiEvent(PublishMainEvent.UI.OnNavUp) },
                actions = {
                    Button(
                        modifier = Modifier
                            .resetSize()
                            .padding(end = ContentMargin / 2),
                        enabled = state.canPublish,
                        shape = MaterialTheme.shapes.small,
                        contentPadding = PaddingValues(horizontal = ContentMargin, vertical = 6.dp),
                        onClick = { onActionEvent(PublishMainEvent.Action.OnPublish) },
                    ) {
                        Text(
                            text = stringResource(Res.string.timeline_add_publish),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            )
        }
    ) { padding ->
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            onRefresh = { loading -> onActionEvent(PublishMainEvent.Action.OnRefresh(loading)) },
            uiState = uiState,
        ) { currentState ->
            PublishMainScreenContent(
                state = currentState,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )
        }
    }

}

@Composable
private fun PublishMainScreenContent(
    state: PublishMainState,
    onUiEvent: (PublishMainEvent.UI) -> Unit,
    onActionEvent: (PublishMainEvent.Action) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth()) {
            if (state.needsTitle) {
                BmgTextField(
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldTransparentColors(),
                    value = state.subject,
                    autoFocus = true,
                    maxLines = 1,
                    contentPadding = PaddingValues(ContentMargin),
                    onValueChange = { onActionEvent(PublishMainEvent.Action.OnTitleChange(it)) },
                    placeholder = { Text(text = stringResource(Res.string.global_title)) },
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = ContentMargin))
            }

            BmgTextField(
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldTransparentColors(),
                value = state.content,
                autoFocus = state.needsTitle.not(),
                maxLines = 20,
                minLines = 10,
                contentPadding = PaddingValues(ContentMargin),
                onValueChange = { onActionEvent(PublishMainEvent.Action.OnContentChange(it)) },
                placeholder = { Text(text = stringResource(Res.string.timeline_add_placeholder)) },
            )

            if (!LocalInspectionMode.current) {
                BgmTurnstile(
                    modifier = Modifier
                        .padding(horizontal = ContentMarginHalf)
                        .fillMaxWidth(),
                    url = WebConstant.URL_BGM_TURNSTILE,
                    callback = "bangumi://",
                    onToken = {
                        onActionEvent(PublishMainEvent.Action.OnReceiveTurnstileToken(it))
                    }
                )
            }
        }

        PublishMainToolbar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface),
            state = state,
            onUiEvent = onUiEvent,
            onActionEvent = onActionEvent
        )
    }
}

@Composable
fun PublishMainToolbar(
    modifier: Modifier,
    state: PublishMainState,
    onUiEvent: (PublishMainEvent.UI) -> Unit,
    onActionEvent: (PublishMainEvent.Action) -> Unit
) {
    val launcher = rememberFilePickerLauncher(FileKitType.Image) {
        if (it != null) {
            onActionEvent(PublishMainEvent.Action.OnImagePickResult(it))
        }
    }

    var showEmojiPanel by remember { mutableStateOf(false) }
    var showPreviewPanel by remember { mutableStateOf(false) }

    val panelState = rememberImePanelState(
        onResetPanel = {
            showEmojiPanel = false
            showPreviewPanel = false
        }
    )

    ImePanelColumn(
        modifier = modifier,
        state = panelState,
        panelContent = {
            CommentDialogPanel(
                modifier = Modifier.fillMaxSize(),
                showEmojiPanel = showEmojiPanel,
                showPreviewPanel = showPreviewPanel,
                value = state.content,
                onValueChange = { onActionEvent(PublishMainEvent.Action.OnContentChange(it)) }
            )
        }
    ) {
        HorizontalDivider()

        InputActionBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ContentMarginHalf),
            value = state.content,
            onValueChange = { onActionEvent(PublishMainEvent.Action.OnContentChange(it)) },
            onPickImage = { launcher.launch() },
            showEmojiPanel = panelState.showPanel && showEmojiPanel,
            onToggleEmojiPanel = {
                panelState.togglePanel(it) {
                    showEmojiPanel = it
                    showPreviewPanel = false
                }
            },
            showPreviewPanel = panelState.showPanel && showPreviewPanel,
            onTogglePreviewPanel = {
                panelState.togglePanel(it) {
                    showEmojiPanel = false
                    showPreviewPanel = it
                }
            }
        )
    }
}


@Preview
@Composable
private fun PreviewPublishMainScreenScreen() {
    PreviewColumn(modifier = Modifier.fillMaxSize()) {
        PublishMainScreen(
            uiState = UiState(
                PublishMainState(title = Res.string.timeline_add)
            ),
            onUiEvent = {},
            onActionEvent = {}
        )
    }
}

