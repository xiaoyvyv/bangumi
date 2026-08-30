package com.xiaoyv.bangumi.features.timeline.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.rounded.PostAdd
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_comments
import com.xiaoyv.bangumi.core_resource.resources.timeline_title
import com.xiaoyv.bangumi.features.timeline.detail.business.TimelineDetailEvent
import com.xiaoyv.bangumi.features.timeline.detail.business.TimelineDetailSideEffect
import com.xiaoyv.bangumi.features.timeline.detail.business.TimelineDetailState
import com.xiaoyv.bangumi.features.timeline.detail.business.TimelineDetailViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.types.ReportType
import com.xiaoyv.bangumi.shared.core.utils.nodesIndexed
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.rememberAlertDialogState
import com.xiaoyv.bangumi.shared.ui.component.dialog.comment.CommentDialog
import com.xiaoyv.bangumi.shared.ui.component.dialog.comment.CommentDialogAnchor
import com.xiaoyv.bangumi.shared.ui.component.dialog.comment.CommentTarget
import com.xiaoyv.bangumi.shared.ui.component.divider.BgmHorizontalDivider
import com.xiaoyv.bangumi.shared.ui.component.layout.state.CommentNoDataTip
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.scroll.rememberScrollUpLazyListState
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import com.xiaoyv.bangumi.shared.ui.view.comment.CommentReplyItem
import com.xiaoyv.bangumi.shared.ui.view.comment.LocalCommentTargetAuthorUsername
import com.xiaoyv.bangumi.shared.ui.view.timeline.TimelinePageItem
import org.jetbrains.compose.resources.stringResource
import org.orbitmvi.orbit.compose.collectAsState

private const val CONTENT_TYPE_TIMELINE = "timeline"
private const val CONTENT_TYPE_COMMENT_HEADER = "comment_header"
private const val CONTENT_TYPE_COMMENT_ITEM = "comment_item"
private const val CONTENT_TYPE_COMMENT_END = "comment_end"

@Composable
fun TimelineDetailRoute(
    viewModel: TimelineDetailViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val uiState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect { sideEffect ->
        when (sideEffect) {
            TimelineDetailSideEffect.OnNavUp -> onNavUp()
        }
    }

    TimelineDetailScreen(
        uiState = uiState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is TimelineDetailEvent.UI.OnNavUp -> onNavUp()
                is TimelineDetailEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun TimelineDetailScreen(
    uiState: UiState<TimelineDetailState>,
    onUiEvent: (TimelineDetailEvent.UI) -> Unit,
    onActionEvent: (TimelineDetailEvent.Action) -> Unit
) {
    var replyTarget by remember { mutableStateOf(ComposeReply.Empty) }
    val commentDialogState = rememberAlertDialogState()

    CommentDialog(
        dialogState = commentDialogState,
        anchor = remember(uiState.data.timeline.id, replyTarget) {
            CommentDialogAnchor(
                target = CommentTarget.Timeline(uiState.data.timeline.id),
                reply = replyTarget,
            )
        },
        onSendCommentSuccess = {
            replyTarget = ComposeReply.Empty
            onActionEvent(TimelineDetailEvent.Action.OnAppendComment)
        },
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                title = stringResource(Res.string.timeline_title),
                onNavigationClick = { onUiEvent(TimelineDetailEvent.UI.OnNavUp) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    replyTarget = ComposeReply.Empty
                    commentDialogState.show()
                },
            ) {
                Icon(imageVector = BgmIcons.PostAdd, contentDescription = null)
            }
        },
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            onRefresh = { loading -> onActionEvent(TimelineDetailEvent.Action.OnRefresh(loading)) },
            uiState = uiState,
        ) { state ->
            TimelineDetailScreenContent(
                state = state,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent,
                onReplyClick = { reply ->
                    replyTarget = reply
                    commentDialogState.show()
                }
            )
        }
    }
}


@Composable
private fun TimelineDetailScreenContent(
    state: TimelineDetailState,
    onUiEvent: (TimelineDetailEvent.UI) -> Unit,
    onActionEvent: (TimelineDetailEvent.Action) -> Unit,
    onReplyClick: (ComposeReply) -> Unit,
) {
    CompositionLocalProvider(LocalCommentTargetAuthorUsername provides state.timeline.user.username) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = rememberScrollUpLazyListState()
        ) {
            item(key = CONTENT_TYPE_TIMELINE, contentType = CONTENT_TYPE_TIMELINE) {
                TimelinePageItem(
                    modifier = Modifier.fillMaxWidth(),
                    item = state.timeline,
                    enableDetailNavigation = false,
                    onNavigate = { onUiEvent(TimelineDetailEvent.UI.OnNavScreen(it)) },
                    onReactionClick = { timeline, reaction ->
                        onActionEvent(TimelineDetailEvent.Action.OnClickReaction(timeline, reaction))
                    },
                    onDeleteClick = { timeline ->
                        onActionEvent(TimelineDetailEvent.Action.OnDeleteTimeline(timeline))
                    },
                )
            }

            stickyHeader(key = CONTENT_TYPE_COMMENT_HEADER, contentType = CONTENT_TYPE_COMMENT_HEADER) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(androidx.compose.material3.MaterialTheme.colorScheme.surface)
                        .padding(horizontal = ContentMargin, vertical = ContentMarginHalf),
                    text = stringResource(Res.string.global_comments),
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                )
            }

            nodesIndexed(
                nodes = state.replies,
                key = { it.id },
                contentType = { CONTENT_TYPE_COMMENT_ITEM },
            ) { reply, level, index ->
                if (index > 0 && reply.relatedID == 0L) {
                    BgmHorizontalDivider(
                        modifier = Modifier.padding(start = if (level == 0) 0.dp else 76.dp),
                    )
                }

                CommentReplyItem(
                    modifier = Modifier.fillMaxWidth(),
                    item = reply,
                    level = level,
                    onClickUser = { username ->
                        onUiEvent(TimelineDetailEvent.UI.OnNavScreen(Screen.UserDetail(username)))
                    },
                    onClickReport = {
                        onUiEvent(
                            TimelineDetailEvent.UI.OnNavScreen(
                                Screen.Report(ReportType.USER, reply.user.id)
                            )
                        )
                    },
                    onClick = { onReplyClick(reply) },
                )
            }

            item(key = CONTENT_TYPE_COMMENT_END, contentType = CONTENT_TYPE_COMMENT_END) {
                CommentNoDataTip(isEmpty = state.replies.isEmpty())
            }
        }
    }
}


@Preview
@Composable
private fun PreviewTimelineDetailScreenScreen() {
    PreviewColumn(modifier = Modifier.fillMaxSize()) {
        TimelineDetailScreen(
            uiState = UiState(
                TimelineDetailState()
            ),
            onUiEvent = {},
            onActionEvent = {}
        )
    }
}
