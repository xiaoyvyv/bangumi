package com.xiaoyv.bangumi.features.friend

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import com.xiaoyv.bangumi.features.friend.business.FriendEvent
import com.xiaoyv.bangumi.features.friend.business.FriendItem
import com.xiaoyv.bangumi.features.friend.business.FriendState
import com.xiaoyv.bangumi.features.friend.business.FriendViewModel
import com.xiaoyv.bangumi.features.friend.business.koinFriendViewModel
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.data.constant.userImage
import com.xiaoyv.bangumi.shared.data.model.request.list.user.ListUserParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUserDisplay
import com.xiaoyv.bangumi.shared.ui.component.divider.BgmHorizontalDivider
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLazyColumn
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.paging.LazyPagingItems
import com.xiaoyv.bangumi.shared.ui.component.paging.collectAsLazyPagingItems
import com.xiaoyv.bangumi.shared.ui.component.text.StarColor
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState

private const val CONTENT_TYPE_FRIEND_HEADER = "CONTENT_TYPE_FRIEND_HEADER"
private const val CONTENT_TYPE_FRIEND_ITEM = "CONTENT_TYPE_FRIEND_ITEM"

@Composable
fun FriendRoute(
    param: ListUserParam,
    onNavScreen: (Screen) -> Unit,
) {
    if (LocalInspectionMode.current) return
    val viewModel: FriendViewModel = koinFriendViewModel(param)
    val baseState by viewModel.collectAsState()
    val pagingItems = viewModel.users.collectAsLazyPagingItems()

    viewModel.collectBaseSideEffect {

    }

    FriendScreen(
        baseState = baseState,
        pagingItems = pagingItems,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is FriendEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun FriendScreen(
    baseState: BaseState<FriendState>,
    pagingItems: LazyPagingItems<ComposeUserDisplay>,
    onUiEvent: (FriendEvent.UI) -> Unit,
    onActionEvent: (FriendEvent.Action) -> Unit,
) {
    StateLayout(
        modifier = Modifier.fillMaxSize(),
        onRefresh = { onActionEvent(FriendEvent.Action.OnRefresh(it)) },
        enablePullRefresh = true,
        baseState = baseState,
    ) { state ->
        if (state.param.ui.pageMode) {
            FriendScreenPage(pagingItems, onUiEvent, onActionEvent)
        } else {
            FriendScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}


@Composable
private fun FriendScreenPage(
    pagingItems: LazyPagingItems<ComposeUserDisplay>,
    onUiEvent: (FriendEvent.UI) -> Unit,
    onActionEvent: (FriendEvent.Action) -> Unit,
) {
    StateLazyColumn(
        modifier = Modifier.fillMaxSize(),
        pagingItems = pagingItems,
        showScrollUpBtn = true
    ) { item, index ->
        FriendScreenItem(
            item = item,
            onUiEvent = onUiEvent,
            onActionEvent = onActionEvent
        )
        BgmHorizontalDivider()
    }
}

@Composable
private fun FriendScreenContent(
    state: FriendState,
    onUiEvent: (FriendEvent.UI) -> Unit,
    onActionEvent: (FriendEvent.Action) -> Unit,
) {
    val listState = rememberLazyListState()
    var selectedKey by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 左侧好友列表
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState
        ) {
            state.friends.fastForEach { item ->
                when (item) {
                    is FriendItem.Header -> {
                        stickyHeader(key = item.title, contentType = CONTENT_TYPE_FRIEND_HEADER) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                text = item.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            BgmHorizontalDivider()
                        }
                    }

                    is FriendItem.Friend -> {
                        item(key = item.friend.user.username, contentType = CONTENT_TYPE_FRIEND_ITEM) {
                            FriendScreenItem(
                                item = item.friend,
                                onUiEvent = onUiEvent,
                                onActionEvent = onActionEvent
                            )
                            BgmHorizontalDivider()
                        }
                    }
                }
            }
        }

        // 右侧字母条
        val scope = rememberCoroutineScope()
        val feedback = LocalHapticFeedback.current

        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp)
                .width(28.dp)
                .background(MaterialTheme.colorScheme.surfaceContainer, CircleShape)
                .pointerInput(state.keys) {
                    detectTapGestures(onTap = { offset ->
                        val index = ((offset.y) / (size.height / state.keys.size)).toInt()
                        state.keys.getOrNull(index)?.let { key ->
                            scope.launch {
                                feedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                scrollToKey(listState, state, key)
                            }
                        }
                    })
                }
                .pointerInput(state.keys) {
                    detectVerticalDragGestures(
                        onVerticalDrag = { change, _ ->
                            val index = ((change.position.y) / (size.height / state.keys.size)).toInt()
                            state.keys.getOrNull(index)?.let { key ->
                                if (selectedKey != key) {
                                    feedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    selectedKey = key
                                    scope.launch { scrollToKey(listState, state, key) }
                                }
                            }
                        },
                        onDragEnd = { selectedKey = null }
                    )
                }
                .padding(vertical = 8.dp)
        ) {
            state.keys.fastForEachIndexed { index, it ->
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    text = it,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }

        // Popup 显示当前字母
        selectedKey?.let { key ->
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(80.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = key,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
    }
}


@Composable
private fun FriendScreenItem(
    item: ComposeUserDisplay,
    onUiEvent: (FriendEvent.UI) -> Unit,
    onActionEvent: (FriendEvent.Action) -> Unit,
) {
    ListItem(
        modifier = Modifier
            .semantics { contentDescription = "user_item" }
            .clickable { onUiEvent(FriendEvent.UI.OnNavScreen(Screen.UserDetail(item.user.username))) }
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        leadingContent = {
            StateImage(
                modifier = Modifier.size(44.dp),
                model = item.user.avatar.displayMediumImage.ifBlank { userImage(item.user.username) },
                shape = MaterialTheme.shapes.small,
            )
        },
        headlineContent = {
            Text(
                text = item.user.nickname,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        supportingContent = if (item.user.sign.isBlank()) null else {
            {
                Text(
                    modifier = Modifier.padding(vertical = 4.dp),
                    text = item.user.sign,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        },
        trailingContent = if (item.user.id == 0L) null else {
            {
                Text(
                    text = "UID: ${item.user.id}",
                    color = StarColor,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    )
}


private suspend fun scrollToKey(
    listState: LazyListState,
    state: FriendState,
    key: String,
) {
    val index = state.friends.indexOfFirst {
        it is FriendItem.Header && it.title == key
    }
    if (index != -1) {
        listState.scrollToItem(index)
    }
}
