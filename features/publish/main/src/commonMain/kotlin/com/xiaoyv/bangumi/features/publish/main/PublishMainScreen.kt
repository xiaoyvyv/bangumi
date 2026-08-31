package com.xiaoyv.bangumi.features.publish.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_add
import com.xiaoyv.bangumi.core_resource.resources.global_private
import com.xiaoyv.bangumi.core_resource.resources.global_public
import com.xiaoyv.bangumi.core_resource.resources.global_title
import com.xiaoyv.bangumi.core_resource.resources.publish_add_subject
import com.xiaoyv.bangumi.core_resource.resources.publish_add_tag
import com.xiaoyv.bangumi.core_resource.resources.publish_tag_tip
import com.xiaoyv.bangumi.core_resource.resources.timeline_add
import com.xiaoyv.bangumi.core_resource.resources.timeline_add_placeholder
import com.xiaoyv.bangumi.core_resource.resources.timeline_add_publish
import com.xiaoyv.bangumi.features.publish.main.business.PublishMainEvent
import com.xiaoyv.bangumi.features.publish.main.business.PublishMainSideEffect
import com.xiaoyv.bangumi.features.publish.main.business.PublishMainState
import com.xiaoyv.bangumi.features.publish.main.business.PublishMainViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.types.PublishPostType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.utils.ImePanelColumn
import com.xiaoyv.bangumi.shared.core.utils.clickWithoutRipped
import com.xiaoyv.bangumi.shared.core.utils.rememberImePanelState
import com.xiaoyv.bangumi.shared.core.utils.resetSize
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.BgmAlertInputDialog
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.rememberAlertInputDialogState
import com.xiaoyv.bangumi.shared.ui.component.dialog.comment.CommentDialogPanel
import com.xiaoyv.bangumi.shared.ui.component.dialog.comment.InputActionBar
import com.xiaoyv.bangumi.shared.ui.component.dialog.sheet.rememberSheetDialogState
import com.xiaoyv.bangumi.shared.ui.component.dialog.subject.SearchSubjectDialog
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.text.BmgTextField
import com.xiaoyv.bangumi.shared.ui.component.text.textFieldTransparentColors
import com.xiaoyv.bangumi.shared.ui.component.turnstile.BgmTurnstile
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import com.xiaoyv.bangumi.shared.ui.theme.colorCollectionDoneContainer
import com.xiaoyv.bangumi.shared.ui.theme.colorCollectionDoneText
import com.xiaoyv.bangumi.shared.ui.theme.colorCollectionOnHoldContainer
import com.xiaoyv.bangumi.shared.ui.theme.colorCollectionOnHoldText
import com.xiaoyv.bangumi.shared.ui.theme.colorCollectionWishContainer
import com.xiaoyv.bangumi.shared.ui.theme.colorCollectionWishText
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
    val searchSubjectDialogState = rememberSheetDialogState()

    SearchSubjectDialog(
        state = searchSubjectDialogState,
        onSelectSubject = { onActionEvent(PublishMainEvent.Action.OnAddSubject(it)) },
    )

    val inputDialogState = rememberAlertInputDialogState()

    BgmAlertInputDialog(
        state = inputDialogState,
        confirm = stringResource(Res.string.global_add),
        onConfirm = {
            onActionEvent(PublishMainEvent.Action.OnAddTags(it.value))
        }
    )


    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 56.dp)
        ) {
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
                maxLines = 10,
                minLines = 4,
                contentPadding = PaddingValues(ContentMargin),
                onValueChange = { onActionEvent(PublishMainEvent.Action.OnContentChange(it)) },
                placeholder = { Text(text = stringResource(Res.string.timeline_add_placeholder)) },
            )

            if (state.type == PublishPostType.BLOG) {
                val title = stringResource(Res.string.publish_add_tag)
                val subtitle = stringResource(Res.string.publish_tag_tip)

                PublishBlogOptions(
                    state = state,
                    onActionEvent = onActionEvent,
                    onShowSubjectDialog = {
                        searchSubjectDialogState.show()
                    },
                    onShowTagDialog = {
                        inputDialogState.show {
                            it.copy(title = title, subtitle = subtitle)
                        }
                    }
                )
            }

            BgmTurnstile(
                modifier = Modifier
                    .padding(horizontal = ContentMarginHalf, vertical = ContentMargin)
                    .fillMaxWidth(),
                refreshKey = state.turnstileRefreshKey,
                onToken = {
                    onActionEvent(PublishMainEvent.Action.OnReceiveTurnstileToken(it))
                }
            )
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

/**
 * 日志的附加发布信息。
 *
 * @param state 当前发布状态
 * @param onActionEvent 发布操作回调
 */
@Composable
private fun PublishBlogOptions(
    state: PublishMainState,
    onActionEvent: (PublishMainEvent.Action) -> Unit,
    onShowSubjectDialog: () -> Unit,
    onShowTagDialog: () -> Unit,
) {
    val addSubjectTitle = stringResource(Res.string.publish_add_subject)
    val addTagTitle = stringResource(Res.string.publish_add_tag)

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ContentMargin),
        horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
        verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
        itemVerticalAlignment = Alignment.CenterVertically,
    ) {
        PublishBlogOptionTag(
            text = stringResource(if (state.public) Res.string.global_public else Res.string.global_private),
            containerColor = colorCollectionOnHoldContainer,
            contentColor = colorCollectionOnHoldText,
            onClick = { onActionEvent(PublishMainEvent.Action.OnPublicChange(!state.public)) },
        )

        state.attachSubjects.forEach { subject ->
            PublishBlogOptionTag(
                text = stringResource(SubjectType.string(subject.type)) + " " + subject.displayName,
                containerColor = colorCollectionWishContainer,
                contentColor = colorCollectionWishText,
                onClick = { onActionEvent(PublishMainEvent.Action.OnRemoveSubject(subject.id)) },
            )
        }

        state.attachTags.forEach { tag ->
            PublishBlogOptionTag(
                text = "# $tag",
                containerColor = colorCollectionDoneContainer,
                contentColor = colorCollectionDoneText,
                onClick = { onActionEvent(PublishMainEvent.Action.OnRemoveTag(tag)) },
            )
        }

        if (state.attachSubjects.size < PublishMainState.MAX_ATTACH_SUBJECT_COUNT) {
            PublishBlogOptionTag(
                text = addSubjectTitle,
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                onClick = onShowSubjectDialog,
            )
        }

        PublishBlogOptionTag(
            text = addTagTitle,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            onClick = onShowTagDialog,
        )
    }
}

@Composable
private fun PublishBlogOptionTag(
    text: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
) {
    Text(
        modifier = Modifier
            .widthIn(max = 120.dp)
            .background(containerColor, MaterialTheme.shapes.extraSmall)
            .clickWithoutRipped(onClick)
            .padding(horizontal = ContentMarginHalf, vertical = 4.dp),
        text = text,
        color = contentColor,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.labelMedium,
    )
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
                PublishMainState(
                    title = Res.string.timeline_add,
                    type = PublishPostType.BLOG
                )
            ),
            onUiEvent = {},
            onActionEvent = {}
        )
    }
}
