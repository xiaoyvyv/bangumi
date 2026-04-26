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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_comments
import com.xiaoyv.bangumi.core_resource.resources.global_reaction
import com.xiaoyv.bangumi.core_resource.resources.topic_title
import com.xiaoyv.bangumi.features.topic.detail.business.TopicDetailEvent
import com.xiaoyv.bangumi.features.topic.detail.business.TopicDetailState
import com.xiaoyv.bangumi.features.topic.detail.business.TopicDetailViewModel
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.types.ButtonType
import com.xiaoyv.bangumi.shared.core.types.TopicDetailType
import com.xiaoyv.bangumi.shared.core.utils.animateScrollToItem
import com.xiaoyv.bangumi.shared.core.utils.nodesIndexed
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReaction
import com.xiaoyv.bangumi.shared.ui.component.action.LocalActionHandler
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.chip.DropMenuActionButton
import com.xiaoyv.bangumi.shared.ui.component.chip.DropMenuChip
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.rememberAlertDialogState
import com.xiaoyv.bangumi.shared.ui.component.divider.BgmHorizontalDivider
import com.xiaoyv.bangumi.shared.ui.component.emoji.PopupReaction
import com.xiaoyv.bangumi.shared.ui.component.emoji.ReactionGroup
import com.xiaoyv.bangumi.shared.ui.component.emoji.rememberPopupReactionState
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.tab.rememberButtonTypeMenu
import com.xiaoyv.bangumi.shared.ui.component.text.BgmLinkedText
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.view.comment.CommentReplyItem
import com.xiaoyv.bangumi.shared.ui.view.comment.LocalCommentTargetAuthorUsername
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
        baseState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is TopicDetailEvent.UI.OnNavUp -> onNavUp()
                is TopicDetailEvent.UI.OnNavScreen -> onNavScreen(it.screen)
                else -> Unit
            }
        }
    )
}

@Composable
private fun TopicDetailScreen(
    baseState: BaseState<TopicDetailState>,
    onUiEvent: (TopicDetailEvent.UI) -> Unit,
    onActionEvent: (TopicDetailEvent.Action) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                title = stringResource(Res.string.topic_title),
                actions = {
                    baseState.content {
                        val actionHandler = LocalActionHandler.current

                        DropMenuActionButton(
                            options = rememberButtonTypeMenu(type) {
                                add(ButtonType.OpenInBrowser)
                                add(ButtonType.CopyLink)
                                add(ButtonType.Share)

                                // 仅以下几种话题才显示举报
                                when (type) {
                                    TopicDetailType.TYPE_SUBJECT,
                                    TopicDetailType.TYPE_BLOG,
                                    TopicDetailType.TYPE_GROUP -> add(ButtonType.Report)
                                }
                            },
                            onOptionClick = {
                                when (it.type) {
                                    ButtonType.Share -> actionHandler.shareContent(shareUrl)
                                    ButtonType.CopyLink -> actionHandler.copyContent(shareUrl)
                                    ButtonType.OpenInBrowser -> actionHandler.openInBrowser(shareUrl)
                                    ButtonType.Report -> {

                                    }

                                    else -> Unit
                                }
                            }
                        )
                    }
                },
                onNavigationClick = { onUiEvent(TopicDetailEvent.UI.OnNavUp) }
            )
        },
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            onRefresh = { onActionEvent(TopicDetailEvent.Action.OnRefresh(it)) },
            baseState = baseState,
        ) { state ->
            TopicDetailScreenContent(state, onUiEvent, onActionEvent)
        }
    }
    /*BgmCollapsingScaffold(
        modifier = Modifier.fillMaxSize(),

        collapse = {
            baseState.content {

            }
        },
        overlay = {
            Box(Modifier.fillMaxSize()) {
                val commentDialogState = rememberAlertDialogState()

                FloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(LayoutPadding),
                    onClick = { commentDialogState.show() }
                ) {
                    Icon(imageVector = BgmIcons.EditNote, contentDescription = null)
                }
            }
        },
        content = {
            StateLayout(
                modifier = Modifier.fillMaxSize(),
                onRefresh = { onActionEvent(TopicDetailEvent.Action.OnRefresh(it)) },
                baseState = baseState,
            ) { state ->
                TopicDetailScreenContent(state, onUiEvent, onActionEvent)
            }
        }
    )*/
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
            .padding(start = LayoutPadding, end = LayoutPaddingHalf, top = LayoutPaddingHalf, bottom = LayoutPadding),
        verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
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
            horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
        ) {
            when (state.type) {
                // 小组贴
                TopicDetailType.TYPE_GROUP -> {
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
                TopicDetailType.TYPE_SUBJECT -> {
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
                TopicDetailType.TYPE_EP -> {
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
                TopicDetailType.TYPE_PERSON,
                TopicDetailType.TYPE_CRT -> {
                    item {
                        TopicDetailScreenMonoBar(
                            mono = state.mono,
                            onClick = { mono, type ->
                                onUiEvent(TopicDetailEvent.UI.OnNavScreen(Screen.MonoDetail(mono.id, type)))
                            }
                        )
                    }
                }

                TopicDetailType.TYPE_INDEX -> {
                    item {
                        TopicDetailScreenUserBar(
                            user = state.index.creator,
                            onClickUser = {
                                onUiEvent(TopicDetailEvent.UI.OnNavScreen(Screen.UserDetail(it.username)))
                            }
                        )
                    }
                }

                TopicDetailType.TYPE_BLOG -> {
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
            text = state.displayContent,
        )

        // 支持贴贴表情的话题
        if (TopicDetailType.isSupportRection(state.type)) {
            val displayReactions = state.displayReactions
            if (displayReactions.isNotEmpty()) ReactionGroup(
                modifier = Modifier.fillMaxWidth(),
                reactions = displayReactions,
                onClick = { reaction ->
                    onActionEvent(
                        TopicDetailEvent.Action.OnReactionClick(
                            if (state.type == TopicDetailType.TYPE_BLOG) state.id else state.topic.contentPostId,
                            reaction
                        )
                    )
                }
            )

            ArticleScreenRecationButton(
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
                ArticleScreenCommentHeader(
                    state = state,
                    onUiEvent = onUiEvent,
                    onActionEvent = onActionEvent
                )
            }

            nodesIndexed(
                nodes = state.comments,
                key = { it.id },
                contentType = { CONTENT_TYPE_COMMENT_ITEM }
            ) { item, level, index ->
                if (index > 0 && item.relatedID == 0L) BgmHorizontalDivider()
                val commentDialogState = rememberAlertDialogState()
                val density = LocalDensity.current
                /*
                            CommentDialog(
                                dialogState = commentDialogState,
                                anchor = remember(item, state.article, state.lastViewed) {
                                    CommentDialogAnchor(
                                        article = state.article,
                                        reply = item,
                                        lastViewedInMillis = state.lastViewed
                                    )
                                },
                                onSendCommentSuccess = {
                                    onActionEvent(ArticleEvent.Action.OnAppendComment(it))
                                }
                            )*/

                CommentReplyItem(
                    modifier = Modifier.fillMaxWidth(),
                    item = item,
                    level = level,
                    isLikeable = state.type == TopicDetailType.TYPE_GROUP ||
                            state.type == TopicDetailType.TYPE_SUBJECT ||
                            state.type == TopicDetailType.TYPE_EP ||
                            state.type == TopicDetailType.TYPE_BLOG,
                    onClickUser = { onUiEvent(TopicDetailEvent.UI.OnNavScreen(Screen.UserDetail(it))) },
                    onClick = {
                        scope.launch {
                            listState.animateScrollToItem(index + 2, -stickHeaderHeight, density)
                            commentDialogState.show()
                        }
                    },
                    onClickReaction = {
                        onActionEvent(TopicDetailEvent.Action.OnReactionClick(item.id, it))
                    }
                )
            }
        }
    }
}


@Composable
private fun ArticleScreenCommentHeader(
    state: TopicDetailState,
    onUiEvent: (TopicDetailEvent.UI) -> Unit,
    onActionEvent: (TopicDetailEvent.Action) -> Unit,
) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .onGloballyPositioned { stickHeaderHeight = it.size.height }
            .padding(LayoutPadding, LayoutPaddingHalf),
        horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
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
//                onActionEvent(TopicDetailEvent.Action.OnCommentTypeChange(it.type))
            }
        )

        DropMenuChip(
            options = state.commentSortFilters,
            current = state.selectedCommentSortFilter,
            trailingIcon = { Icon(BgmIcons.ArrowDropDown, contentDescription = null) },
            onOptionClick = {
//                onActionEvent(TopicDetailEvent.Action.OnCommentSortChange(it.type))
            }
        )
    }
}


@Composable
private fun ArticleScreenRecationButton(
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
                        if (state.type == TopicDetailType.TYPE_BLOG) state.id else state.topic.contentPostId,
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



