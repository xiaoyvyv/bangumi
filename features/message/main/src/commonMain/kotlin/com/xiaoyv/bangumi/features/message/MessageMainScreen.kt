package com.xiaoyv.bangumi.features.message

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_conversation
import com.xiaoyv.bangumi.features.message.business.MessageMainEvent
import com.xiaoyv.bangumi.features.message.business.MessageMainState
import com.xiaoyv.bangumi.features.message.business.MessageMainViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.utils.formatAgo
import com.xiaoyv.bangumi.shared.core.utils.formatMills
import com.xiaoyv.bangumi.shared.data.constant.userImage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.pm.ComposePmConversation
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.divider.BgmHorizontalDivider
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLazyColumn
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import org.jetbrains.compose.resources.stringResource
import org.orbitmvi.orbit.compose.collectAsState

const val TAB_INBOX = "TAB_INBOX"
const val TAB_FRIEND = "TAB_FRIEND"

private const val CONTENT_TYPE_CONVERSATION_ITEM = "CONTENT_TYPE_CONVERSATION_ITEM"

@Composable
fun MessageMainRoute(
    viewModel: MessageMainViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()
    val pagingItems = viewModel.messageInbox.collectAsLazyPagingItems()

    viewModel.collectBaseSideEffect {

    }

    var count by rememberSaveable { mutableIntStateOf(0) }

    LifecycleResumeEffect(pagingItems) {
        count++
        if (count >= 1) pagingItems.refresh()
        onPauseOrDispose { }
    }

    MessageMainScreen(
        uiState = baseState,
        pagingItems = pagingItems,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is MessageMainEvent.UI.OnNavUp -> onNavUp()
                is MessageMainEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun MessageMainScreen(
    uiState: UiState<MessageMainState>,
    pagingItems: LazyPagingItems<ComposePmConversation>,
    onUiEvent: (MessageMainEvent.UI) -> Unit,
    onActionEvent: (MessageMainEvent.Action) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                title = stringResource(Res.string.global_conversation),
                onNavigationClick = { onUiEvent(MessageMainEvent.UI.OnNavUp) },
                actions = {

                }
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            uiState = uiState,
        ) { state ->
            MessageMainScreenContent(
                state = state,
                pagingItems = pagingItems,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )
        }
    }
}


@Composable
private fun MessageMainScreenContent(
    state: MessageMainState,
    pagingItems: LazyPagingItems<ComposePmConversation>,
    onUiEvent: (MessageMainEvent.UI) -> Unit,
    onActionEvent: (MessageMainEvent.Action) -> Unit,
) {
    StateLazyColumn(
        state = com.xiaoyv.bangumi.shared.ui.component.scroll.rememberScrollUpLazyListState(),
        modifier = Modifier.fillMaxSize(),
        pagingItems = pagingItems,
        key = { item, _ -> item.id },
        contentPadding = PaddingValues(vertical = ContentMarginHalf),
        showScrollUpBtn = true,
        contentType = { CONTENT_TYPE_CONVERSATION_ITEM },
    ) { item, _ ->
        MessageMainConversationItem(
            item = item,
            onClick = {
                onUiEvent(
                    MessageMainEvent.UI.OnNavScreen(
                        Screen.MessageChat(item.id, item.user.nickname)
                    )
                )
            }
        )
        BgmHorizontalDivider(Modifier.padding(start = ContentMargin + 60.dp))
    }
}

@Composable
private fun MessageMainConversationItem(
    item: ComposePmConversation,
    onClick: () -> Unit,
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        leadingContent = {
            BadgedBox(
                badge = {
                    if (item.unread > 0) Badge { Text(text = item.unread.toString()) }
                }
            ) {
                StateImage(
                    modifier = Modifier.size(48.dp),
                    model = item.user.avatar.displayMediumImage.ifBlank { userImage(item.user.username) },
                    shape = CircleShape,
                )
            }
        },
        headlineContent = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = item.user.nickname,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = if (item.unread > 0) FontWeight.SemiBold else FontWeight.Medium,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = remember(item.time) { item.time.formatMills().formatAgo() },
                    maxLines = 1,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            }
        },
        supportingContent = {
            Text(
                modifier = Modifier.padding(top = ContentMarginHalf / 2),
                text = item.content,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )
        }
    )
}
