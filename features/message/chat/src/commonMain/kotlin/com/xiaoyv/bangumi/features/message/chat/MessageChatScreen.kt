package com.xiaoyv.bangumi.features.message.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_message
import com.xiaoyv.bangumi.core_resource.resources.reply_comment_send
import com.xiaoyv.bangumi.core_resource.resources.reply_message_hint_disable
import com.xiaoyv.bangumi.core_resource.resources.reply_message_hint_normal
import com.xiaoyv.bangumi.core_resource.resources.reply_message_warn
import com.xiaoyv.bangumi.features.message.chat.business.MessageChatEvent
import com.xiaoyv.bangumi.features.message.chat.business.MessageChatState
import com.xiaoyv.bangumi.features.message.chat.business.MessageChatViewModel
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.types.LoadingState
import com.xiaoyv.bangumi.shared.core.utils.resetSize
import com.xiaoyv.bangumi.shared.data.manager.shared.currentUser
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMessage
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.text.BgmLinkedText
import com.xiaoyv.bangumi.shared.ui.component.text.BmgTextField
import com.xiaoyv.bangumi.shared.ui.component.text.textFieldTransparentColors
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

private const val CONTENT_TYPE_MESSAGE_ITEM = "CONTENT_TYPE_MESSAGE_ITEM"

@Composable
fun MessageChatRoute(
    viewModel: MessageChatViewModel = koinViewModel<MessageChatViewModel>(),
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    MessageChatScreen(
        baseState = baseState,
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
    baseState: BaseState<MessageChatState>,
    onUiEvent: (MessageChatEvent.UI) -> Unit,
    onActionEvent: (MessageChatEvent.Action) -> Unit,
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                title = stringResource(Res.string.global_message),
                onNavigationClick = { onUiEvent(MessageChatEvent.UI.OnNavUp) }
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding()),
            onRefresh = { onActionEvent(MessageChatEvent.Action.OnRefresh(it)) },
            baseState = baseState,
        ) { state ->
            MessageChatScreenContent(state, onUiEvent, onActionEvent)
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(12.dp)
        ) {
            item {
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        text = stringResource(Res.string.reply_message_warn),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
            items(
                items = state.message.messages,
                key = { it.id },
                contentType = { CONTENT_TYPE_MESSAGE_ITEM }
            ) {
                MessageChatScreenPageItem(
                    item = it,
                    onUiEvent = onUiEvent,
                    onActionEvent = onActionEvent
                )
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
private fun MessageChatBottomBar(
    state: MessageChatState,
    onUiEvent: (MessageChatEvent.UI) -> Unit,
    onActionEvent: (MessageChatEvent.Action) -> Unit,
) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(LayoutPaddingHalf)
            .background(MaterialTheme.colorScheme.surfaceBright, MaterialTheme.shapes.small),
        horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
    ) {
        BmgTextField(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(LayoutPaddingHalf),
            value = state.input,
            onValueChange = { onActionEvent(MessageChatEvent.Action.OnTextChange(it)) },
            shape = MaterialTheme.shapes.small,
            maxLines = 6,
            minLines = 3,
            autoFocus = state.message.canReply,
            enabled = state.message.canReply,
            placeholder = {
                if (state.message.canReply) {
                    Text(text = stringResource(Res.string.reply_message_hint_normal))
                } else {
                    Text(text = stringResource(Res.string.reply_message_hint_disable))
                }
            },
            colors = textFieldTransparentColors(),
            textStyle = MaterialTheme.typography.bodyLarge,
        )

        if (state.message.canReply) Button(
            modifier = Modifier
                .align(Alignment.Bottom)
                .padding(bottom = LayoutPaddingHalf, end = LayoutPaddingHalf)
                .resetSize(),
            enabled = state.input.text.isNotBlank() && state.sending != LoadingState.Loading,
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


@Composable
private fun MessageChatScreenPageItem(
    item: ComposeMessage,
    onUiEvent: (MessageChatEvent.UI) -> Unit,
    onActionEvent: (MessageChatEvent.Action) -> Unit,
) {
    OutlinedCard(modifier = Modifier.fillMaxWidth(), onClick = {}) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
        ) {
            val currentUser = currentUser()
            val isSelf = item.user.username == currentUser.username
            if (!isSelf) StateImage(
                modifier = Modifier.size(44.dp),
                model = item.user.displayAvatar,
                shape = MaterialTheme.shapes.small,
            )

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
                ) {
                    if (isSelf) Text(
                        text = item.time,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    ) else {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = item.user.nickname,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = if (isSelf) TextAlign.End else TextAlign.Start
                        )
                        Text(
                            text = item.time,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        )
                    }
                }

                if (item.title.isNotBlank()) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        fontWeight = FontWeight.SemiBold,
                        text = item.title,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }

                BgmLinkedText(
                    modifier = Modifier.fillMaxWidth(),
                    text = item.contentHtml,
                )
            }

            if (isSelf) StateImage(
                modifier = Modifier.size(44.dp),
                model = item.user.displayAvatar,
                shape = MaterialTheme.shapes.small,
            )
        }
    }
}

