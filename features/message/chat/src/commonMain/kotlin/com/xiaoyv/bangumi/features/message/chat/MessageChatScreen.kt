package com.xiaoyv.bangumi.features.message.chat

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.rounded.ModeEdit
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.img_background
import com.xiaoyv.bangumi.core_resource.resources.message_topic_hint
import com.xiaoyv.bangumi.core_resource.resources.reply_comment_send
import com.xiaoyv.bangumi.core_resource.resources.reply_message_hint_normal
import com.xiaoyv.bangumi.core_resource.resources.reply_message_warn
import com.xiaoyv.bangumi.features.message.chat.business.MessageChatEvent
import com.xiaoyv.bangumi.features.message.chat.business.MessageChatState
import com.xiaoyv.bangumi.features.message.chat.business.MessageChatViewModel
import com.xiaoyv.bangumi.shared.core.mvi.PageStatus
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.types.LoadingState
import com.xiaoyv.bangumi.shared.core.utils.resetSize
import com.xiaoyv.bangumi.shared.data.manager.shared.currentUser
import com.xiaoyv.bangumi.shared.data.model.response.bgm.pm.ComposePmMessage
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.text.BgmLinkedText
import com.xiaoyv.bangumi.shared.ui.component.text.BmgTextField
import com.xiaoyv.bangumi.shared.ui.component.text.textFieldTransparentColors
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.theme.BorderStrokeVariant
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginGrid
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.orbitmvi.orbit.compose.collectAsState

private const val CONTENT_TYPE_MESSAGE_ITEM = "CONTENT_TYPE_MESSAGE_ITEM"
private const val CONTENT_TYPE_SUBJECT_TIP = "CONTENT_TYPE_SUBJECT_TIP"
private const val KEY_PREFIX_SUBJECT_TIP = "KEY_PREFIX_SUBJECT_TIP"
private const val KEY_TOPIC_INPUT = "KEY_TOPIC_INPUT"
private const val KEY_MESSAGE_INPUT = "KEY_MESSAGE_INPUT"
private val MessageChatAvatarSize = 40.dp

@Composable
fun MessageChatRoute(
    viewModel: MessageChatViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    MessageChatScreen(
        uiState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is MessageChatEvent.UI.OnNavUp -> onNavUp()
                is MessageChatEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun MessageChatScreen(
    uiState: UiState<MessageChatState>,
    onUiEvent: (MessageChatEvent.UI) -> Unit,
    onActionEvent: (MessageChatEvent.Action) -> Unit,
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                titleContent = {
                    if (uiState.status != PageStatus.Loading) Column(
                        modifier = Modifier.padding(vertical = ContentMarginHalf),
                        verticalArrangement = Arrangement.spacedBy(ContentMarginHalf / 2)
                    ) {
                        Text(
                            text = uiState.data.nickname,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        MessageChatThreadBar(
                            state = uiState.data,
                            onActionEvent = onActionEvent,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                ),
                actions = {
                    IconButton(
                        onClick = {
                            onActionEvent(
                                MessageChatEvent.Action.OnEnableTopicInput(!uiState.data.topicEnable)
                            )
                        },
                    ) {
                        Icon(
                            imageVector = BgmIcons.ModeEdit,
                            contentDescription = stringResource(Res.string.message_topic_hint),
                            tint = if (uiState.data.topicEnable) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        )
                    }
                },
                onNavigationClick = { onUiEvent(MessageChatEvent.UI.OnNavUp) }
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = it.calculateBottomPadding())
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                            )
                        )
                    )
                    .padding(it)
                    .alpha(0.4f),
                painter = painterResource(Res.drawable.img_background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )

            StateLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = it.calculateTopPadding()),
                onRefresh = { loading -> onActionEvent(MessageChatEvent.Action.OnRefresh(loading)) },
                containerColor = Color.Transparent,
                uiState = uiState,
            ) { state ->
                MessageChatScreenContent(state, onUiEvent, onActionEvent)
            }
        }
    }
}


@Composable
private fun MessageChatScreenContent(
    state: MessageChatState,
    onUiEvent: (MessageChatEvent.UI) -> Unit,
    onActionEvent: (MessageChatEvent.Action) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .imePadding()
    ) {
        val listState = rememberLazyListState(
            initialFirstVisibleItemIndex = state.message.messages.lastIndex.coerceAtLeast(0)
        )

        LaunchedEffect(state.message.messages.size) {
            listState.animateScrollToItem(state.message.messages.lastIndex.coerceAtLeast(0))
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(ContentMarginGrid),
            contentPadding = PaddingValues(ContentMarginGrid)
        ) {
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(ContentMarginHalf),
                        text = stringResource(Res.string.reply_message_warn),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            state.message.messages.forEachIndexed { index, item ->
                if (item.isSubjectTip) {
                    stickyHeader(
                        key = "${KEY_PREFIX_SUBJECT_TIP}_${item.title}_$index",
                        contentType = CONTENT_TYPE_SUBJECT_TIP,
                    ) {
                        MessageChatSubjectTip(item = item)
                    }
                } else if (item.isContent) {
                    item(
                        key = item.msgId,
                        contentType = CONTENT_TYPE_MESSAGE_ITEM,
                    ) {
                        MessageChatScreenPageItem(
                            item = item,
                            onAvatarClick = {
                                onUiEvent(
                                    MessageChatEvent.UI.OnNavScreen(Screen.UserDetail(item.user.username))
                                )
                            },
                        )
                    }
                }
            }
        }


        val bottom = WindowInsets.ime.getBottom(LocalDensity.current)

        LaunchedEffect(bottom, listState) {
            if (bottom > 0) {
                listState.animateScrollToItem(state.message.messages.lastIndex.coerceAtLeast(0))
            }
        }

        MessageChatBottomBar(
            state = state,
            onUiEvent = onUiEvent,
            onActionEvent = onActionEvent
        )
    }
}

@Composable
private fun MessageChatThreadBar(
    state: MessageChatState,
    onActionEvent: (MessageChatEvent.Action) -> Unit,
) {
    if (state.message.threads.isEmpty()) return

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
    ) {
        items(
            items = state.message.threads,
            key = { it.id },
        ) { thread ->
            FilterChip(
                modifier = Modifier.alpha(0.7f),
                selected = state.thread.id == thread.id,
                onClick = { onActionEvent(MessageChatEvent.Action.OnThreadChange(thread)) },
                label = {
                    Text(
                        text = thread.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    labelColor = MaterialTheme.colorScheme.onSurface,
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                ),
            )
        }
    }
}


@Composable
private fun MessageChatScreenPageItem(
    item: ComposePmMessage,
    onAvatarClick: () -> Unit,
) {
    val isSelf = item.user.username == currentUser().username
    val bubbleColor = if (isSelf) {
        MaterialTheme.colorScheme.surfaceContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainer
    }
    val contentColor = MaterialTheme.colorScheme.onSurface

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val maxBubbleWidth = maxWidth - MessageChatAvatarSize * 2 - ContentMargin - ContentMarginHalf * 2

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf / 2, if (isSelf) Alignment.End else Alignment.Start),
            verticalAlignment = Alignment.Top,
        ) {
            if (!isSelf) {
                MessageChatAvatar(item = item, onClick = onAvatarClick)
            }

            MessageChatBubble(
                modifier = Modifier
                    .padding(start = if (isSelf) 0.dp else ContentMarginHalf)
                    .padding(end = if (isSelf) ContentMarginHalf else 0.dp)
                    .width(IntrinsicSize.Max)
                    .widthIn(max = maxBubbleWidth),
                isSelf = isSelf,
                color = bubbleColor,
            ) {
                Column(
                    modifier = Modifier.padding(
                        start = ContentMargin,
                        top = ContentMarginHalf,
                        end = ContentMargin,
                        bottom = ContentMarginHalf,
                    ),
                    verticalArrangement = Arrangement.spacedBy(ContentMarginHalf / 2),
                ) {
                    if (!isSelf) {
                        Text(
                            text = item.user.nickname,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }

                    BgmLinkedText(
                        text = item.content,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = contentColor),
                    )

                    Text(
                        modifier = Modifier.align(Alignment.End),
                        text = item.time,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = contentColor.copy(alpha = 0.7f),
                        ),
                    )
                }
            }

            if (isSelf) {
                MessageChatAvatar(item = item, onClick = onAvatarClick)
            }
        }
    }
}

@Composable
private fun MessageChatAvatar(
    item: ComposePmMessage,
    onClick: () -> Unit,
) {
    StateImage(
        modifier = Modifier
            .size(MessageChatAvatarSize)
            .clickable(onClick = onClick)
            .shadow(1.dp),
        border = BorderStrokeVariant,
        model = item.user.avatar.displayMediumImage,
        shape = MaterialTheme.shapes.medium,
    )
}

@Composable
private fun MessageChatBubble(
    modifier: Modifier,
    isSelf: Boolean,
    color: Color,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        Surface(
            shape = RoundedCornerShape(
                topStart = if (isSelf) 16.dp else 0.dp,
                topEnd = if (isSelf) 0.dp else 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp,
            ),
            color = color,
            content = content,
            shadowElevation = 1.dp
        )
        Canvas(
            modifier = Modifier
                .align(if (isSelf) Alignment.TopEnd else Alignment.TopStart)
                .offset(
                    x = if (isSelf) 8.dp else (-8).dp,
                    y = 0.dp,
                )
                .size(12.dp),
        ) {
            val tail = Path().apply {
                if (isSelf) {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(0f, size.height)
                } else {
                    moveTo(size.width, 0f)
                    lineTo(0f, 0f)
                    lineTo(size.width, size.height)
                }
                close()
            }
            drawPath(path = tail, color = color)
        }

    }
}

@Composable
private fun MessageChatSubjectTip(item: ComposePmMessage) {
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        ElevatedCard(
            modifier = Modifier.padding(vertical = ContentMarginHalf),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
        ) {
            Text(
                modifier = Modifier.padding(horizontal = ContentMargin, vertical = ContentMarginHalf),
                text = item.title,
                style = MaterialTheme.typography.labelLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )
        }
    }
}

@Composable
private fun MessageChatBottomBar(
    state: MessageChatState,
    onUiEvent: (MessageChatEvent.UI) -> Unit,
    onActionEvent: (MessageChatEvent.Action) -> Unit,
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(ContentMarginHalf)
            .background(MaterialTheme.colorScheme.surfaceBright, MaterialTheme.shapes.small),
        verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
    ) {
        if (state.topicEnable) {
            key(KEY_TOPIC_INPUT) {
                BmgTextField(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(ContentMarginHalf),
                    value = state.topic,
                    onValueChange = { onActionEvent(MessageChatEvent.Action.OnTopicInputChange(it)) },
                    shape = MaterialTheme.shapes.small,
                    maxLines = 1,
                    placeholder = { Text(text = stringResource(Res.string.message_topic_hint)) },
                    colors = textFieldTransparentColors(),
                    textStyle = MaterialTheme.typography.bodyLarge,
                )
            }
        }

        key(KEY_MESSAGE_INPUT) {
            Row(horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf)) {
                BmgTextField(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(ContentMarginHalf),
                    value = state.input,
                    onValueChange = { onActionEvent(MessageChatEvent.Action.OnTextChange(it)) },
                    shape = MaterialTheme.shapes.small,
                    maxLines = 5,
                    minLines = 2,
                    placeholder = { Text(text = stringResource(Res.string.reply_message_hint_normal)) },
                    colors = textFieldTransparentColors(),
                    textStyle = MaterialTheme.typography.bodyLarge,
                )

                Button(
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .padding(bottom = ContentMarginHalf, end = ContentMarginHalf)
                        .resetSize(),
                    enabled = state.input.text.isNotBlank()
                            && (!state.topicEnable || state.topic.text.isNotBlank())
                            && state.sending != LoadingState.Loading,
                    onClick = { onActionEvent(MessageChatEvent.Action.OnSendReply(state.input.text)) },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = MaterialTheme.shapes.small
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            modifier = Modifier.alpha(if (state.sending == LoadingState.Loading) 0f else 1f),
                            text = stringResource(Res.string.reply_comment_send),
                            style = MaterialTheme.typography.labelLarge
                        )

                        if (state.sending == LoadingState.Loading) CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
        }
    }
}
