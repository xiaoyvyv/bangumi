package com.xiaoyv.bangumi.features.article

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_comments
import com.xiaoyv.bangumi.core_resource.resources.global_image
import com.xiaoyv.bangumi.core_resource.resources.global_reaction
import com.xiaoyv.bangumi.core_resource.resources.global_topic
import com.xiaoyv.bangumi.features.article.business.ArticleEvent
import com.xiaoyv.bangumi.features.article.business.ArticleState
import com.xiaoyv.bangumi.features.article.business.ArticleViewModel
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.types.ButtonType
import com.xiaoyv.bangumi.shared.core.types.CommentType
import com.xiaoyv.bangumi.shared.core.types.RakuenIdType
import com.xiaoyv.bangumi.shared.core.utils.animateScrollToItem
import com.xiaoyv.bangumi.shared.core.utils.nodesIndexed
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeComment
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeTopicDetail
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUser
import com.xiaoyv.bangumi.shared.ui.component.action.LocalActionHandler
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.chip.DropMenuActionButton
import com.xiaoyv.bangumi.shared.ui.component.chip.DropMenuChip
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.rememberAlertDialogState
import com.xiaoyv.bangumi.shared.ui.component.dialog.comment.CommentDialog
import com.xiaoyv.bangumi.shared.ui.component.dialog.comment.CommentDialogAnchor
import com.xiaoyv.bangumi.shared.ui.component.divider.BgmHorizontalDivider
import com.xiaoyv.bangumi.shared.ui.component.emoji.PopupReaction
import com.xiaoyv.bangumi.shared.ui.component.emoji.ReactionGroup
import com.xiaoyv.bangumi.shared.ui.component.emoji.rememberPopupReactionState
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.layout.BgmCharacterPlaceHolder
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.layout.state.rememberCacheWindowLazyListState
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.tab.rememberButtonTypeMenu
import com.xiaoyv.bangumi.shared.ui.component.text.BgmLinkedText
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import com.xiaoyv.bangumi.shared.ui.view.comment.CommentItem
import com.xiaoyv.bangumi.shared.ui.view.comment.LocalCommentTargetAuthorUsername
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

private var stickHeaderHeight = 0

private const val CONTENT_TYPE_ARTICLE = "CONTENT_TYPE_ARTICLE"
private const val CONTENT_TYPE_COMMENT_HEADER = "CONTENT_TYPE_COMMENT_HEADER"
private const val CONTENT_TYPE_COMMENT_ITEM = "CONTENT_TYPE_COMMENT_ITEM"
private const val CONTENT_TYPE_BOTTOM_CHARACTER = "CONTENT_TYPE_BOTTOM_CHARACTER"

@Composable
fun ArticleRoute(
    viewModel: ArticleViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    viewModel.collectBaseSideEffect {

    }

    ArticleScreen(
        baseState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is ArticleEvent.UI.OnNavUp -> onNavUp()
                is ArticleEvent.UI.OnNavScreen -> onNavScreen(it.screen)
                is ArticleEvent.UI.OnCollapseHeader -> scope.launch { scrollState.scrollTo(scrollState.maxValue) }
            }
        }
    )
}

@Composable
private fun ArticleScreen(
    baseState: BaseState<ArticleState>,
    onUiEvent: (ArticleEvent.UI) -> Unit,
    onActionEvent: (ArticleEvent.Action) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                title = stringResource(Res.string.global_topic),
                actions = {
                    baseState.content {
                        val actionHandler = LocalActionHandler.current

                        DropMenuActionButton(
                            options = rememberButtonTypeMenu(article.type) {
                                add(ButtonType.OpenInBrowser)
                                add(ButtonType.CopyLink)
                                add(ButtonType.Share)

                                // 仅以下几种话题才显示举报
                                when (article.type) {
                                    RakuenIdType.TYPE_SUBJECT,
                                    RakuenIdType.TYPE_BLOG,
                                    RakuenIdType.TYPE_GROUP,
                                        -> add(ButtonType.Report)
                                }
                            },
                            onOptionClick = {
                                when (it.type) {
                                    ButtonType.Share -> actionHandler.shareContent(article.shareUrl)
                                    ButtonType.CopyLink -> actionHandler.copyContent(article.shareUrl)
                                    ButtonType.OpenInBrowser -> actionHandler.openInBrowser(article.shareUrl)
                                    ButtonType.Report -> {

                                    }

                                    else -> Unit
                                }
                            }
                        )
                    }
                },
                onNavigationClick = { onUiEvent(ArticleEvent.UI.OnNavUp) }
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            onRefresh = { onActionEvent(ArticleEvent.Action.OnRefresh(it)) },
            baseState = baseState,
        ) { state ->
            ArticleScreenContent(state, onUiEvent, onActionEvent)

            val commentDialogState = rememberAlertDialogState()

            CommentDialog(
                dialogState = commentDialogState,
                anchor = remember(state.article) {
                    CommentDialogAnchor(
                        article = state.article,
                        lastViewedInMillis = state.lastViewed
                    )
                },
                onSendCommentSuccess = { comment ->
                    onActionEvent(ArticleEvent.Action.OnAppendComment(comment))
                }
            )

            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(LayoutPadding),
                onClick = { commentDialogState.show() }
            ) {
                Icon(imageVector = BgmIcons.EditNote, contentDescription = null)
            }
        }
    }
}

@Composable
private fun ArticleScreenContent(
    state: ArticleState,
    onUiEvent: (ArticleEvent.UI) -> Unit,
    onActionEvent: (ArticleEvent.Action) -> Unit,
) {
    val listState = rememberCacheWindowLazyListState()
    val scope = rememberCoroutineScope()

    // 修改评论过滤项目，重新回到顶部
    LaunchedEffect(Unit) {
        var isFirst = true
        snapshotFlow { state.selectedCommentSortFilter to state.selectedCommentTypeFilter }
            .collect {
                if (isFirst) {
                    isFirst = false
                } else {
                    delay(100)
                    listState.scrollToItem(1)
                }
            }
    }

    CompositionLocalProvider(LocalCommentTargetAuthorUsername provides state.article.user.username) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth(),
            state = listState,
            userScrollEnabled = listState.canScrollForward || listState.canScrollBackward,
        ) {
            item(key = CONTENT_TYPE_ARTICLE, contentType = CONTENT_TYPE_ARTICLE) {
                ArticleScreenContentHeader(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .fillMaxWidth()
                        .padding(vertical = LayoutPadding)
                        .padding(start = LayoutPadding, end = LayoutPaddingHalf),
                    state = state,
                    onUiEvent = onUiEvent,
                    onActionEvent = onActionEvent
                )
            }

            stickyHeader(key = CONTENT_TYPE_COMMENT_HEADER, contentType = CONTENT_TYPE_COMMENT_HEADER) {
                ArticleScreenCommentHeader(
                    state = state,
                    onActionEvent = onActionEvent
                )
            }

            nodesIndexed(
                nodes = state.article.comments,
                key = { it.id },
                contentType = { CONTENT_TYPE_COMMENT_ITEM }
            ) { item, level, index ->
                if (index > 0 && item.parent == null) BgmHorizontalDivider()
                val commentDialogState = rememberAlertDialogState()
                val density = LocalDensity.current

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
                )

                CommentItem(
                    modifier = Modifier.fillMaxWidth(),
                    item = item,
                    reactions = state.article.reactions[item.id],
                    onClickUser = { onUiEvent(ArticleEvent.UI.OnNavScreen(Screen.UserDetail(it))) },
                    onClick = {
                        onUiEvent(ArticleEvent.UI.OnCollapseHeader)
                        scope.launch {
                            listState.animateScrollToItem(index + 2, -stickHeaderHeight, density)
                        }
                        scope.launch {
                            commentDialogState.show()
                        }
                    },
                    onClickReaction = {
                        onActionEvent(ArticleEvent.Action.OnReactionClick(item.type, item.id, it.value))
                    }
                )
            }

            item(key = CONTENT_TYPE_BOTTOM_CHARACTER, contentType = CONTENT_TYPE_BOTTOM_CHARACTER) {
                BgmCharacterPlaceHolder(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ArticleScreenContentHeader(
    modifier: Modifier,
    state: ArticleState,
    onUiEvent: (ArticleEvent.UI) -> Unit,
    onActionEvent: (ArticleEvent.Action) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
    ) {
        Text(
            text = state.article.title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )

        when (state.article.type) {
            // 小组贴
            RakuenIdType.TYPE_GROUP -> {
                ArticleScreenAttachBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    avatar = state.article.group.images.displayGridImage,
                    title = state.article.group.title,
                    time = state.article.time,
                    onClick = {
                        onUiEvent(ArticleEvent.UI.OnNavScreen(Screen.GroupDetail(state.article.group.name)))
                    }
                )
            }
            // 条目贴
            RakuenIdType.TYPE_SUBJECT -> {
                ArticleScreenAttachBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    avatar = state.article.firstSubject.images.displayGridImage,
                    title = state.article.firstSubject.name,
                    time = state.article.time,
                    onClick = {
                        onUiEvent(ArticleEvent.UI.OnNavScreen(Screen.SubjectDetail(state.article.firstSubject.id)))
                    }
                )
            }
            // 章节贴
            RakuenIdType.TYPE_EP -> {
                ArticleScreenAttachBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    avatar = state.article.firstSubject.images.displayGridImage,
                    title = state.article.firstSubject.name,
                    avatarSize = 44.dp,
                    onClick = {
                        onUiEvent(ArticleEvent.UI.OnNavScreen(Screen.SubjectDetail(state.article.firstSubject.id)))
                    }
                )
            }
            // 人物贴
            RakuenIdType.TYPE_PERSON, RakuenIdType.TYPE_CRT -> {
                ArticleScreenAttachBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    avatar = state.article.mono.mono.images.displayGridImage,
                    title = state.article.mono.mono.name,
                    avatarSize = 44.dp,
                    onClick = {
                        onUiEvent(ArticleEvent.UI.OnNavScreen(Screen.MonoDetail(state.article.mono.id, state.article.mono.type)))
                    }
                )
            }
        }

        if (state.article.user != ComposeUser.Empty) {
            ArticleScreenUserBar(
                modifier = Modifier.fillMaxWidth(),
                state = state,
                onUiEvent = onUiEvent
            )
        }

        BgmLinkedText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = LayoutPaddingHalf),
            text = state.article.contentHtml,
        )

        // 支持贴贴表情的话题
        if (RakuenIdType.isSupportRection(state.article.type)) {
            state.article.reactions[state.article.contentId]?.let {
                ReactionGroup(
                    modifier = Modifier.fillMaxWidth(),
                    reactions = it,
                    onClick = { reaction ->
                        // 针对日志内容部分贴贴，type = 20
                        onActionEvent(
                            ArticleEvent.Action.OnReactionClick(
                                if (state.article.type == RakuenIdType.TYPE_BLOG) 20 else CommentType.fromRakuenIdType(state.article.type),
                                state.article.contentId,
                                reaction.value
                            )
                        )
                    }
                )
            }

            ArticleScreenRecationButton(
                modifier = Modifier.align(Alignment.End),
                state = state,
                onActionEvent = onActionEvent,
            )
        }
    }
}

@Composable
private fun ArticleScreenUserBar(
    modifier: Modifier,
    state: ArticleState,
    onUiEvent: (ArticleEvent.UI) -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StateImage(
            modifier = Modifier.size(44.dp),
            shape = MaterialTheme.shapes.small,
            model = state.article.user.avatar.displayMediumImage
        )

        Column(verticalArrangement = Arrangement.Center) {
            Text(
                text = state.article.user.nickname,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = buildAnnotatedString {
                    append("@")
                    append(state.article.user.username)
                    if (state.article.user.sign.isNotBlank()) {
                        append(" ")
                        append(state.article.user.sign)
                    }
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )
        }
    }
}

@Composable
private fun ArticleScreenRecationButton(
    modifier: Modifier,
    state: ArticleState,
    onActionEvent: (ArticleEvent.Action) -> Unit,
) {
    val reactionState = rememberPopupReactionState()

    Box(modifier = modifier) {
        PopupReaction(
            state = reactionState,
            onClick = { value ->
                // 针对日志内容部分贴贴，type = 20
                onActionEvent(
                    ArticleEvent.Action.OnReactionClick(
                        if (state.article.type == RakuenIdType.TYPE_BLOG) 20 else CommentType.fromRakuenIdType(state.article.type),
                        state.article.contentId,
                        value
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

@Composable
private fun ArticleScreenAttachBar(
    modifier: Modifier,
    avatar: String,
    title: String,
    time: String? = null,
    avatarSize: Dp = 24.dp,
    onClick: () -> Unit = {},
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
        verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
        itemVerticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .clickable(onClick = onClick)
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(LayoutPaddingHalf),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
        ) {
            StateImage(
                modifier = Modifier.size(avatarSize),
                model = avatar,
                contentDescription = stringResource(Res.string.global_image),
                shape = MaterialTheme.shapes.small,
                alignment = Alignment.TopCenter
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )
        }

        if (time != null) Text(
            text = time,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        )
    }
}

@Composable
private fun ArticleScreenCommentHeader(
    state: ArticleState,
    onActionEvent: (ArticleEvent.Action) -> Unit,
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
            text = buildAnnotatedString {
                append(stringResource(Res.string.global_comments))
                withStyle(MaterialTheme.typography.bodyLarge.toSpanStyle()) {
                    append(" (${state.article.commentCount})")
                }
            },
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
                onActionEvent(ArticleEvent.Action.OnCommentTypeChange(it.type))
            }
        )

        DropMenuChip(
            options = state.commentSortFilters,
            current = state.selectedCommentSortFilter,
            trailingIcon = { Icon(BgmIcons.ArrowDropDown, contentDescription = null) },
            onOptionClick = {
                onActionEvent(ArticleEvent.Action.OnCommentSortChange(it.type))
            }
        )
    }
}


@Preview
@Composable
private fun ArticleScreenPreview() {
    PreviewColumn {
        ArticleScreen(
            baseState = BaseState.Success(
                ArticleState(
                    title = "Sample Article Title",
                    article = ComposeTopicDetail(
                        comments = persistentListOf(
                            ComposeComment.Empty.copy(id = "1"),
                            ComposeComment.Empty.copy(id = "2"),
                        )
                    )
                )
            ),
            onUiEvent = {},
            onActionEvent = {},
        )
    }
}

