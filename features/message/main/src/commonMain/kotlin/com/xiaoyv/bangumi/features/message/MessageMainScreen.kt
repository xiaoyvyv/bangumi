package com.xiaoyv.bangumi.features.message

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_delete
import com.xiaoyv.bangumi.core_resource.resources.global_done
import com.xiaoyv.bangumi.core_resource.resources.global_edit
import com.xiaoyv.bangumi.core_resource.resources.global_message
import com.xiaoyv.bangumi.features.friend.FriendRoute
import com.xiaoyv.bangumi.features.message.business.MessageMainEvent
import com.xiaoyv.bangumi.features.message.business.MessageMainSideEffect
import com.xiaoyv.bangumi.features.message.business.MessageMainState
import com.xiaoyv.bangumi.features.message.business.MessageMainViewModel
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.types.MessageBoxType
import com.xiaoyv.bangumi.shared.core.types.list.ListUserType
import com.xiaoyv.bangumi.shared.data.manager.shared.currentUser
import com.xiaoyv.bangumi.shared.data.model.request.list.user.ListUserParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMessage
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.divider.BgmHorizontalDivider
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLazyColumn
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.pager.BgmTabHorizontalPager
import com.xiaoyv.bangumi.shared.ui.component.pager.rememberBgmPagerState
import com.xiaoyv.bangumi.shared.ui.component.paging.LazyPagingItems
import com.xiaoyv.bangumi.shared.ui.component.paging.collectAsLazyPagingItems
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.text.StarColor
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

const val TAB_FRIEND = "TAB_FRIEND"

private const val CONTENT_TYPE_MESSAGE_ITEM = "CONTENT_TYPE_MESSAGE_ITEM"

@Composable
fun MessageMainRoute(
    viewModel: MessageMainViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()
    val inboxLazyItems = viewModel.messageInbox.collectAsLazyPagingItems()
    val outboxLazyItems = viewModel.messageOutbox.collectAsLazyPagingItems()

    viewModel.collectBaseSideEffect {
        when (it) {
            is MessageMainSideEffect.OnRefreshList -> {
                when (it.type) {
                    MessageBoxType.TYPE_INBOX -> inboxLazyItems.refresh()
                    MessageBoxType.TYPE_OUTBOX -> outboxLazyItems.refresh()
                }
            }
        }
    }

    MessageMainScreen(
        baseState = baseState,
        inboxLazyItems = inboxLazyItems,
        outboxLazyItems = outboxLazyItems,
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
    baseState: BaseState<MessageMainState>,
    inboxLazyItems: LazyPagingItems<ComposeMessage>,
    outboxLazyItems: LazyPagingItems<ComposeMessage>,
    onUiEvent: (MessageMainEvent.UI) -> Unit,
    onActionEvent: (MessageMainEvent.Action) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                title = stringResource(Res.string.global_message),
                actions = {
                    baseState.content {
                        if (selectedTabType != TAB_FRIEND) {
                            TextButton(onClick = { onActionEvent(MessageMainEvent.Action.OnToggleEditMode) }) {
                                if (editMode) {
                                    Text(text = stringResource(Res.string.global_done))
                                } else {
                                    Text(text = stringResource(Res.string.global_edit))
                                }
                            }
                        }
                    }
                },
                onNavigationClick = { onUiEvent(MessageMainEvent.UI.OnNavUp) }
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            baseState = baseState,
        ) { state ->
            MessageMainScreenContent(
                state = state,
                inboxLazyItems = inboxLazyItems,
                outboxLazyItems = outboxLazyItems,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )
        }
    }
}


@Composable
private fun MessageMainScreenContent(
    state: MessageMainState,
    inboxLazyItems: LazyPagingItems<ComposeMessage>,
    outboxLazyItems: LazyPagingItems<ComposeMessage>,
    onUiEvent: (MessageMainEvent.UI) -> Unit,
    onActionEvent: (MessageMainEvent.Action) -> Unit,
) {
    val currentUser = currentUser()

    BgmTabHorizontalPager(
        modifier = Modifier.fillMaxSize(),
        tabs = state.tabs,
        pagerState = rememberBgmPagerState(
            onPageChange = { onActionEvent(MessageMainEvent.Action.OnTabSelected(state.tabs[it].type)) },
            pageCount = { state.tabs.size }
        )
    ) {
        when (state.tabs[it].type) {
            MessageBoxType.TYPE_INBOX -> MessageMainScreenPage(
                type = MessageBoxType.TYPE_INBOX,
                state = state,
                pagingItems = inboxLazyItems,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )

            MessageBoxType.TYPE_OUTBOX -> MessageMainScreenPage(
                type = MessageBoxType.TYPE_OUTBOX,
                state = state,
                pagingItems = outboxLazyItems,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )

            TAB_FRIEND -> FriendRoute(
                param = remember { ListUserParam(type = ListUserType.USER_FRIEND, username = currentUser.username) },
                onNavScreen = { screen -> onUiEvent(MessageMainEvent.UI.OnNavScreen(screen)) }
            )
        }
    }
}


@Composable
private fun MessageMainScreenPage(
    @MessageBoxType type: String,
    state: MessageMainState,
    pagingItems: LazyPagingItems<ComposeMessage>,
    onUiEvent: (MessageMainEvent.UI) -> Unit,
    onActionEvent: (MessageMainEvent.Action) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        StateLazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            pagingItems = pagingItems,
            key = { item, _ -> item.id },
            contentType = { CONTENT_TYPE_MESSAGE_ITEM }
        ) { item, index ->
            MessageMainScreenPageItem(
                state = state,
                type = type,
                item = item,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )
            BgmHorizontalDivider()
        }

        val selectedCount = when (type) {
            MessageBoxType.TYPE_INBOX -> state.selectedInboxIds.size
            MessageBoxType.TYPE_OUTBOX -> state.selectedOutboxIds.size
            else -> 0
        }
        if (state.editMode && selectedCount > 0) Text(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onActionEvent(MessageMainEvent.Action.OnDeleteMessage(type)) }
                .padding(LayoutPadding),
            text = stringResource(Res.string.global_delete),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        )
    }
}


@Composable
private fun MessageMainScreenPageItem(
    state: MessageMainState,
    @MessageBoxType type: String,
    item: ComposeMessage,
    onUiEvent: (MessageMainEvent.UI) -> Unit,
    onActionEvent: (MessageMainEvent.Action) -> Unit,
) {
    val checked = if (type == MessageBoxType.TYPE_INBOX) {
        state.selectedInboxIds.contains(item.id)
    } else {
        state.selectedOutboxIds.contains(item.id)
    }

    ListItem(
        modifier = Modifier
            .clickable {
                if (state.editMode) {
                    onActionEvent(MessageMainEvent.Action.OnItemCheckChanged(type, item.id, !checked))
                } else {
                    onUiEvent(MessageMainEvent.UI.OnNavScreen(Screen.MessageChat(item.id, item.user.username)))
                }
            }
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        leadingContent = {
            BadgedBox(
                badge = {
                    if (item.unread && type == MessageBoxType.TYPE_INBOX) {
                        Badge(content = { Text(text = "1") })
                    }
                },
                content = {
                    StateImage(
                        modifier = Modifier.size(44.dp),
                        model = item.user.avatar.displayMediumImage,
                        shape = MaterialTheme.shapes.small,
                    )
                }
            )
        },
        overlineContent = {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = item.user.nickname,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(text = item.time)
            }
        },
        headlineContent = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 4.dp),
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge,
                )
                if (item.unread && type == MessageBoxType.TYPE_OUTBOX) Badge(
                    containerColor = StarColor,
                    content = { Text(text = "对方未读", color = MaterialTheme.colorScheme.onPrimary) }
                )
            }
        },
        supportingContent = {
            Text(
                text = item.content,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        trailingContent = if (!state.editMode) null else {
            {
                Checkbox(
                    checked = checked,
                    onCheckedChange = {
                        onActionEvent(MessageMainEvent.Action.OnItemCheckChanged(type, item.id, it))
                    }
                )
            }
        }
    )
}