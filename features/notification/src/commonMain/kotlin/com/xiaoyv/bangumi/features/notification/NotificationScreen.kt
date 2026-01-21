package com.xiaoyv.bangumi.features.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_notification
import com.xiaoyv.bangumi.features.notification.business.NotificationEvent
import com.xiaoyv.bangumi.features.notification.business.NotificationSideEffect
import com.xiaoyv.bangumi.features.notification.business.NotificationState
import com.xiaoyv.bangumi.features.notification.business.NotificationViewModel
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.utils.resetSize
import com.xiaoyv.bangumi.shared.data.manager.shared.shareViewModel
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeNotification
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.divider.BgmHorizontalDivider
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.text.BgmLinkedText
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun NotificationRoute(
    viewModel: NotificationViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()
    val shareViewModel = shareViewModel()

    viewModel.collectBaseSideEffect {
        when (it) {
            NotificationSideEffect.OnRefreshNotificationCount -> shareViewModel.onRefreshUserUnreadNotification()
        }
    }

    NotificationScreen(
        baseState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is NotificationEvent.UI.OnNavUp -> onNavUp()
                is NotificationEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun NotificationScreen(
    baseState: BaseState<NotificationState>,
    onUiEvent: (NotificationEvent.UI) -> Unit,
    onActionEvent: (NotificationEvent.Action) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                title = stringResource(Res.string.global_notification),
                onNavigationClick = { onUiEvent(NotificationEvent.UI.OnNavUp) }
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            onRefresh = { onActionEvent(NotificationEvent.Action.OnRefresh(it)) },
            baseState = baseState,
        ) { state ->
            NotificationScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}


@Composable
private fun NotificationScreenContent(
    state: NotificationState,
    onUiEvent: (NotificationEvent.UI) -> Unit,
    onActionEvent: (NotificationEvent.Action) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(state.notifications) {
            ListItem(
                modifier = Modifier
                    .clickable {}
                    .padding(vertical = 4.dp),
                leadingContent = {
                    StateImage(
                        modifier = Modifier.size(44.dp),
                        model = it.user.avatar.displayMediumImage,
                        shape = MaterialTheme.shapes.small
                    )
                },
                overlineContent = {
                    Text(
                        text = it.user.nickname,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                },
                headlineContent = {
                    BgmLinkedText(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        text = it.messageHtml,
                    )
                },
                supportingContent = {
                    if (it.unread) {
                        Text(
                            text = "未读",
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Start,
                        )
                    } else {
                        Text(
                            text = "已读",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start,
                            color = Color.Green.copy(green = 0.8f)
                        )
                    }
                },
                trailingContent = if (!it.unread) null else {
                    { NotificationActionButton(it, onActionEvent) }
                }
            )
            BgmHorizontalDivider()
        }
    }
}

@Composable
private fun NotificationActionButton(
    item: ComposeNotification,
    onActionEvent: (NotificationEvent.Action) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)) {
        if (item.message.contains("请求与你成为好友")) {
            OutlinedButton(
                modifier = Modifier.resetSize(),
                contentPadding = PaddingValues(LayoutPadding, LayoutPaddingHalf),
                shape = MaterialTheme.shapes.small,
                onClick = { onActionEvent(NotificationEvent.Action.OnMarkRead(item)) }
            ) {
                Text(text = "忽略")
            }
            OutlinedButton(
                modifier = Modifier.resetSize(),
                contentPadding = PaddingValues(LayoutPadding, LayoutPaddingHalf),
                shape = MaterialTheme.shapes.small,
                onClick = { onActionEvent(NotificationEvent.Action.OnAgreeFriendRequest(item)) }
            ) {
                Text(text = "同意")
            }
        } else {
            OutlinedButton(
                modifier = Modifier.resetSize(),
                contentPadding = PaddingValues(LayoutPadding, LayoutPaddingHalf),
                shape = MaterialTheme.shapes.small,
                onClick = { onActionEvent(NotificationEvent.Action.OnMarkRead(item)) }
            ) {
                if (item.count == null || item.count == 0) {
                    Text(text = "我已读")
                } else {
                    Text(text = "我已读 (${item.count})")
                }
            }
        }
    }
}

