package com.xiaoyv.bangumi.features.topic.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.PostAdd
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.comment_delete_confirm
import com.xiaoyv.bangumi.core_resource.resources.global_comments
import com.xiaoyv.bangumi.core_resource.resources.global_delete
import com.xiaoyv.bangumi.core_resource.resources.global_no_content
import com.xiaoyv.bangumi.core_resource.resources.global_reaction
import com.xiaoyv.bangumi.core_resource.resources.topic_title
import com.xiaoyv.bangumi.features.topic.detail.business.TopicDetailEvent
import com.xiaoyv.bangumi.features.topic.detail.business.TopicDetailState
import com.xiaoyv.bangumi.features.topic.detail.business.TopicDetailViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.types.ButtonType
import com.xiaoyv.bangumi.shared.core.types.TopicType
import com.xiaoyv.bangumi.shared.core.utils.animateScrollToItem
import com.xiaoyv.bangumi.shared.core.utils.nodesIndexed
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.reaction.ComposeReaction
import com.xiaoyv.bangumi.shared.data.model.response.bgm.topic.ComposeTopicDetail
import com.xiaoyv.bangumi.shared.ui.component.action.LocalActionHandler
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.chip.DropMenuActionButton
import com.xiaoyv.bangumi.shared.ui.component.chip.DropMenuChip
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.BgmAlertDialog
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.rememberAlertDialogState
import com.xiaoyv.bangumi.shared.ui.component.dialog.comment.CommentDialog
import com.xiaoyv.bangumi.shared.ui.component.dialog.comment.CommentDialogAnchor
import com.xiaoyv.bangumi.shared.ui.component.dialog.comment.CommentTarget
import com.xiaoyv.bangumi.shared.ui.component.divider.BgmHorizontalDivider
import com.xiaoyv.bangumi.shared.ui.component.emoji.PopupReaction
import com.xiaoyv.bangumi.shared.ui.component.emoji.ReactionGroup
import com.xiaoyv.bangumi.shared.ui.component.emoji.rememberPopupReactionState
import com.xiaoyv.bangumi.shared.ui.component.layout.state.CommentNoDataTip
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.layout.state.itemKey
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.tab.rememberButtonTypeMenu
import com.xiaoyv.bangumi.shared.ui.component.text.BgmLinkedText
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import com.xiaoyv.bangumi.shared.ui.view.comment.CommentReplyItem
import com.xiaoyv.bangumi.shared.ui.view.comment.LocalCommentTargetAuthorUsername
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.orbitmvi.orbit.compose.collectAsState

private var stickHeaderHeight = 0

private const val CONTENT_TYPE_ARTICLE = "CONTENT_TYPE_ARTICLE"
private const val CONTENT_TYPE_COMMENT_HEADER = "CONTENT_TYPE_COMMENT_HEADER"
private const val CONTENT_TYPE_COMMENT_ITEM = "CONTENT_TYPE_COMMENT_ITEM"
private const val CONTENT_TYPE_BOTTOM_CHARACTER = "CONTENT_TYPE_BOTTOM_CHARACTER"

@Composable
fun TopicDetailRoute(
    viewModel: TopicDetailViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    TopicDetailScreen(
        uiState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is TopicDetailEvent.UI.OnNavUp -> onNavUp()
                is TopicDetailEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        }
    )
}

@Composable
private fun TopicDetailScreen(
    uiState: UiState<TopicDetailState>,
    onUiEvent: (TopicDetailEvent.UI) -> Unit,
    onActionEvent: (TopicDetailEvent.Action) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                title = stringResource(Res.string.topic_title),
                actions = {
                    val actionHandler = LocalActionHandler.current

                    DropMenuActionButton(
                        options = rememberButtonTypeMenu(uiState.data) {
                            // EP、人物、角色 显示翻译功能
                            when (uiState.data.type) {
                                TopicType.TYPE_EP,
                                TopicType.TYPE_CRT,
                                TopicType.TYPE_PERSON,
                                TopicType.TYPE_INDEX -> {
                                    add(if (uiState.data.translateContent.isNotBlank()) ButtonType.TranslateOff else ButtonType.Translate)
                                }
                            }

                            add(ButtonType.OpenInBrowser)
                            add(ButtonType.CopyLink)
                            add(ButtonType.Share)

                            // 仅以下几种话题才显示举报
                            when (uiState.data.type) {
                                TopicType.TYPE_SUBJECT,
                                TopicType.TYPE_GROUP,
                                TopicType.TYPE_BLOG,
                                TopicType.TYPE_INDEX -> add(ButtonType.Report)
                            }
                        },
                        onOptionClick = {
                            when (it.type) {
                                ButtonType.Share -> actionHandler.shareContent(uiState.data.shareUrl)
                                ButtonType.CopyLink -> actionHandler.copyContent(uiState.data.shareUrl)
                                ButtonType.OpenInBrowser -> actionHandler.openInBrowser(uiState.data.shareUrl)
                                ButtonType.Report -> {
                                    onUiEvent(TopicDetailEvent.UI.OnNavScreen(Screen.Report(uiState.data.reportType, uiState.data.id)))
                                }

                                ButtonType.Translate -> {
                                    onActionEvent(TopicDetailEvent.Action.OnTranslateContent(true))
                                }

                                ButtonType.TranslateOff -> {
                                    onActionEvent(TopicDetailEvent.Action.OnTranslateContent(false))
                                }

                                else -> Unit
                            }
                        }
                    )
                },
                onNavigationClick = { onUiEvent(TopicDetailEvent.UI.OnNavUp) }
            )
        },
        floatingActionButton = {
            val commentDialogState = rememberAlertDialogState()

            CommentDialog(
                dialogState = commentDialogState,
                anchor = remember(uiState.data) {
                    CommentDialogAnchor(
                        target = CommentTarget.Topic(
                            type = uiState.data.type,
                            id = when (uiState.data.type) {
                                TopicType.TYPE_GROUP -> uiState.data.id
                                TopicType.TYPE_SUBJECT -> uiState.data.id
                                TopicType.TYPE_EP -> uiState.data.episode.id
                                TopicType.TYPE_PERSON -> uiState.data.mono.id
                                TopicType.TYPE_CRT -> uiState.data.mono.mono.id
                                TopicType.TYPE_INDEX -> uiState.data.index.id
                                TopicType.TYPE_BLOG -> uiState.data.blog.id
                                else -> 0
                            },
                        ),
                    )
                },
                onSendCommentSuccess = { replyId ->
                    onActionEvent(TopicDetailEvent.Action.OnAppendComment(replyId))
                }
            )

            if (uiState.data.isLoadSuccess) {
                FloatingActionButton(onClick = { commentDialogState.show() }) {
                    Icon(imageVector = BgmIcons.PostAdd, contentDescription = null)
                }
            }
        },
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            onRefresh = { loading -> onActionEvent(TopicDetailEvent.Action.OnRefresh(loading)) },
            uiState = uiState,
        ) { state ->
            TopicDetailScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}

@Composable
fun TopicDetailScreenHeader(
    modifier: Modifier,
    insets: PaddingValues,
    state: TopicDetailState,
    onUiEvent: (TopicDetailEvent.UI) -> Unit,
    onActionEvent: (TopicDetailEvent.Action) -> Unit
) {
    Column(
        modifier = modifier
            .padding(insets)
            .padding(start = ContentMargin, end = ContentMarginHalf, top = ContentMarginHalf, bottom = ContentMargin),
        verticalArrangement = Arrangement.spacedBy(ContentMarginHalf)
    ) {
        Text(
            text = state.displayTitle,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )

        // 时间信息
        TopicDetailScreenSubtitle(state)

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf)
        ) {
            when (state.type) {
                // 小组贴
                TopicType.TYPE_GROUP -> {
                    item {
                        TopicDetailScreenUserBar(
                            user = state.topic.creator,
                            onClickUser = {
                                onUiEvent(TopicDetailEvent.UI.OnNavScreen(Screen.UserDetail(it.username)))
                            }
                        )
                    }
                    item {
                        TopicDetailScreenGroupBar(
                            group = state.topic.group,
                            onClick = {
                                onUiEvent(TopicDetailEvent.UI.OnNavScreen(Screen.GroupDetail(it.name)))
                            }
                        )
                    }
                }
                // 条目贴
                TopicType.TYPE_SUBJECT -> {
                    item {
                        TopicDetailScreenUserBar(
                            user = state.topic.creator,
                            onClickUser = {
                                onUiEvent(TopicDetailEvent.UI.OnNavScreen(Screen.UserDetail(it.username)))
                            }
                        )
                    }
                    item {
                        TopicDetailScreenSubjectBar(
                            subject = state.topic.subject,
                            onClick = {
                                onUiEvent(TopicDetailEvent.UI.OnNavScreen(Screen.SubjectDetail(it.id)))
                            }
                        )
                    }
                }
                // 章节贴
                TopicType.TYPE_EP -> {
                    item {
                        TopicDetailScreenSubjectBar(
                            subject = state.episode.subject,
                            onClick = {
                                onUiEvent(TopicDetailEvent.UI.OnNavScreen(Screen.SubjectDetail(it.id)))
                            }
                        )
                    }
                }
                // 人物贴
                TopicType.TYPE_PERSON,
                TopicType.TYPE_CRT -> {
                    item {
                        TopicDetailScreenMonoBar(
                            mono = state.mono,
                            onClick = { mono, type ->
                                onUiEvent(TopicDetailEvent.UI.OnNavScreen(Screen.MonoDetail(mono.id, type)))
                            }
                        )
                    }
                }

                TopicType.TYPE_INDEX -> {
                    item {
                        TopicDetailScreenUserBar(
                            user = state.index.creator,
                            onClickUser = {
                                onUiEvent(TopicDetailEvent.UI.OnNavScreen(Screen.UserDetail(it.username)))
                            }
                        )
                    }
                }

                TopicType.TYPE_BLOG -> {
                    item {
                        TopicDetailScreenUserBar(
                            user = state.blog.user,
                            onClickUser = {
                                onUiEvent(TopicDetailEvent.UI.OnNavScreen(Screen.UserDetail(it.username)))
                            }
                        )
                    }
                    items(state.blog.subjects) { subject ->
                        TopicDetailScreenSubjectBar(
                            subject = subject,
                            onClick = {
                                onUiEvent(TopicDetailEvent.UI.OnNavScreen(Screen.SubjectDetail(it.id)))
                            }
                        )
                    }
                }
            }
        }

        BgmLinkedText(
            modifier = Modifier.fillMaxWidth(),
            text = state.translateContent
                .ifBlank { state.displayContentText }
                .ifBlank { stringResource(Res.string.global_no_content) },
        )

        // 支持贴贴表情的话题
        if (TopicType.isSupportRection(state.type)) {
            val displayReactions = state.displayReactions
            if (displayReactions.isNotEmpty()) {
                ReactionGroup(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = ContentMargin),
                    reactions = displayReactions,
                    onClick = { reaction ->
                        onActionEvent(
                            TopicDetailEvent.Action.OnReactionClick(
                                commentId = if (state.type == TopicType.TYPE_BLOG) state.id else state.topic.contentPostId,
                                reaction = reaction
                            )
                        )
                    }
                )
            }

            TopicDetailScreenRecationButton(
                modifier = Modifier.align(Alignment.End),
                state = state,
                onActionEvent = onActionEvent,
            )
        }
    }
}


@Composable
private fun TopicDetailScreenContent(
    state: TopicDetailState,
    onUiEvent: (TopicDetailEvent.UI) -> Unit,
    onActionEvent: (TopicDetailEvent.Action) -> Unit,
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val deleteDialogState = rememberAlertDialogState()
    var deletingCommentId by remember { mutableStateOf<Long?>(null) }

    BgmAlertDialog(
        state = deleteDialogState,
        text = stringResource(Res.string.comment_delete_confirm),
        confirm = stringResource(Res.string.global_delete),
        onConfirm = {
            deletingCommentId?.let {
                onActionEvent(TopicDetailEvent.Action.OnDeleteComment(it))
            }
            deletingCommentId = null
        },
    )

    CompositionLocalProvider(LocalCommentTargetAuthorUsername provides state.topic.creator.username) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
        ) {
            item(key = CONTENT_TYPE_ARTICLE, contentType = CONTENT_TYPE_ARTICLE) {
                TopicDetailScreenHeader(
                    modifier = Modifier.fillMaxWidth(),
                    insets = PaddingValues(),
                    state = state,
                    onUiEvent = onUiEvent,
                    onActionEvent = onActionEvent
                )
            }

            stickyHeader(key = CONTENT_TYPE_COMMENT_HEADER, contentType = CONTENT_TYPE_COMMENT_HEADER) {
                TopicDetailScreenCommentHeader(
                    state = state,
                    onUiEvent = onUiEvent,
                    onActionEvent = onActionEvent
                )
            }

            nodesIndexed(
                nodes = state.displayReplies,
                key = { it.id },
                contentType = { CONTENT_TYPE_COMMENT_ITEM }
            ) { item, level, index ->
                if (index > 0 && item.relatedID == 0L) {
                    BgmHorizontalDivider(
                        modifier = Modifier.padding(
                            start = if (level == 0) 0.dp else 76.dp
                        )
                    )
                }

                val commentDialogState = rememberAlertDialogState()
                val density = LocalDensity.current

                CommentDialog(
                    dialogState = commentDialogState,
                    anchor = remember(state) {
                        CommentDialogAnchor(
                            target = CommentTarget.Topic(
                                type = state.type,
                                id = when (state.type) {
                                    TopicType.TYPE_GROUP -> state.id
                                    TopicType.TYPE_SUBJECT -> state.id
                                    TopicType.TYPE_EP -> state.episode.id
                                    TopicType.TYPE_PERSON -> state.mono.id
                                    TopicType.TYPE_CRT -> state.mono.mono.id
                                    TopicType.TYPE_INDEX -> state.index.id
                                    TopicType.TYPE_BLOG -> state.blog.id
                                    else -> 0
                                },
                            ),
                            reply = item
                        )
                    },
                    onSendCommentSuccess = {
                        onActionEvent(TopicDetailEvent.Action.OnAppendComment(it))
                    }
                )

                CommentReplyItem(
                    modifier = Modifier.fillMaxWidth(),
                    item = item,
                    level = level,
                    isDeletable = (state.currentUserId != 0L &&
                            (item.creatorID == state.currentUserId || item.user.id == state.currentUserId)) ||
                            (state.currentUsername.isNotBlank() && item.user.username == state.currentUsername),
                    isLikeable = state.type == TopicType.TYPE_GROUP ||
                            state.type == TopicType.TYPE_SUBJECT ||
                            state.type == TopicType.TYPE_EP ||
                            state.type == TopicType.TYPE_BLOG,
                    onClickUser = { onUiEvent(TopicDetailEvent.UI.OnNavScreen(Screen.UserDetail(it))) },
                    onClick = {
                        scope.launch {
                            listState.animateScrollToItem(index + 2, -stickHeaderHeight, density)
                            commentDialogState.show()
                        }
                    },
                    onClickReport = {
                        onUiEvent(TopicDetailEvent.UI.OnNavScreen(Screen.Report(state.reportCommentType, item.id)))
                    },
                    onClickDelete = {
                        deletingCommentId = item.id
                        deleteDialogState.show()
                    },
                    onClickReaction = {
                        onActionEvent(TopicDetailEvent.Action.OnReactionClick(item.id, it))
                    }
                )
            }

            itemKey(CONTENT_TYPE_BOTTOM_CHARACTER) {
                CommentNoDataTip(isEmpty = state.displayReplies.isEmpty())
            }
        }
    }
}


@Composable
private fun TopicDetailScreenCommentHeader(
    state: TopicDetailState,
    onUiEvent: (TopicDetailEvent.UI) -> Unit,
    onActionEvent: (TopicDetailEvent.Action) -> Unit,
) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .onGloballyPositioned { stickHeaderHeight = it.size.height }
            .padding(ContentMargin, ContentMarginHalf),
        horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(Res.string.global_comments),
            style = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.onSurface
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        DropMenuChip(
            options = state.commentTypeFilters,
            current = state.selectedCommentTypeFilter,
            trailingIcon = { Icon(BgmIcons.ArrowDropDown, contentDescription = null) },
            onOptionClick = {
                onActionEvent(TopicDetailEvent.Action.OnCommentTypeChange(it.type))
            }
        )

        DropMenuChip(
            options = state.commentSortFilters,
            current = state.selectedCommentSortFilter,
            trailingIcon = { Icon(BgmIcons.ArrowDropDown, contentDescription = null) },
            onOptionClick = {
                onActionEvent(TopicDetailEvent.Action.OnCommentSortChange(it.type))
            }
        )
    }
}


@Composable
private fun TopicDetailScreenRecationButton(
    modifier: Modifier,
    state: TopicDetailState,
    onActionEvent: (TopicDetailEvent.Action) -> Unit,
) {
    val reactionState = rememberPopupReactionState()

    Box(modifier = modifier) {
        PopupReaction(
            state = reactionState,
            onClick = { value ->
                // 针对日志内容部分贴贴，type = 20
                onActionEvent(
                    TopicDetailEvent.Action.OnReactionClick(
                        if (state.type == TopicType.TYPE_BLOG) state.id else state.topic.contentPostId,
                        ComposeReaction(value = value)
                    )
                )
            }
        )

        TextButton(
            onClick = { reactionState.show() },
            colors = ButtonDefaults.textButtonColors(
                contentColor = Color(0xFFFF80AB),
            )
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = BgmIcons.Favorite,
                contentDescription = stringResource(Res.string.global_reaction),
            )
            Spacer(Modifier.width(4.dp))
            Text(stringResource(Res.string.global_reaction))
        }
    }
}


@Preview
@Composable
private fun ArticleScreenPreview() {
    PreviewColumn {
        TopicDetailScreen(
            uiState = UiState(
                TopicDetailState(
                    id = 111,
                    topic = ComposeTopicDetail(
                        replies = persistentListOf(ComposeReply(content = ""))
                    )
                )
            ),
            onUiEvent = {},
            onActionEvent = {},
        )
    }
}
