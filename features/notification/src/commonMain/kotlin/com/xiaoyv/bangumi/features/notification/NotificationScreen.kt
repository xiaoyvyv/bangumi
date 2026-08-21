package com.xiaoyv.bangumi.features.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_agree
import com.xiaoyv.bangumi.core_resource.resources.global_ignore
import com.xiaoyv.bangumi.core_resource.resources.global_no_more_notice
import com.xiaoyv.bangumi.core_resource.resources.global_notification
import com.xiaoyv.bangumi.core_resource.resources.global_unread
import com.xiaoyv.bangumi.features.notification.business.NotificationEvent
import com.xiaoyv.bangumi.features.notification.business.NotificationSideEffect
import com.xiaoyv.bangumi.features.notification.business.NotificationState
import com.xiaoyv.bangumi.features.notification.business.NotificationViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.types.ButtonType
import com.xiaoyv.bangumi.shared.core.types.NoticeType
import com.xiaoyv.bangumi.shared.core.utils.clickWithoutRipped
import com.xiaoyv.bangumi.shared.core.utils.formatAgo
import com.xiaoyv.bangumi.shared.data.manager.shared.shareViewModel
import com.xiaoyv.bangumi.shared.data.model.PreviewComposeNotice
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.rememberDisplayAnnotatedString
import com.xiaoyv.bangumi.shared.ui.component.action.LocalActionHandler
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.chip.DropMenuActionButton
import com.xiaoyv.bangumi.shared.ui.component.divider.BgmHorizontalDivider
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.tab.rememberButtonTypeMenu
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
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
            is NotificationSideEffect.OnNavScreen -> onNavScreen(it.screen)
        }
    }

    NotificationScreen(
        uiState = baseState,
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
    uiState: UiState<NotificationState>,
    onUiEvent: (NotificationEvent.UI) -> Unit,
    onActionEvent: (NotificationEvent.Action) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                title = stringResource(Res.string.global_notification),
                onNavigationClick = { onUiEvent(NotificationEvent.UI.OnNavUp) },
                actions = {
                    uiState.data.run {
                        val actionHandler = LocalActionHandler.current

                        DropMenuActionButton(
                            options = rememberButtonTypeMenu { add(ButtonType.OpenInBrowser) },
                            onOptionClick = {
                                when (it.type) {
                                    ButtonType.OpenInBrowser -> actionHandler.openInBrowser(uiState.data.pageUrl)
                                    else -> Unit
                                }
                            }
                        )
                    }
                },
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            enablePullRefresh = true,
            onRefresh = { loading -> onActionEvent(NotificationEvent.Action.OnRefresh(loading = loading)) },
            uiState = uiState,
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
        items(state.notifications) { item ->
            ListItem(
                modifier = Modifier.clickable {
                    onActionEvent(NotificationEvent.Action.OnClickItem(item))
                    onActionEvent(NotificationEvent.Action.OnMarkRead(item, showLoading = false))
                },
                colors = ListItemDefaults.colors(
                    containerColor = if (item.unread) {
                        MaterialTheme.colorScheme.surfaceContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                ),
                leadingContent = {
                    StateImage(
                        modifier = Modifier
                            .size(44.dp)
                            .clickWithoutRipped { onUiEvent(NotificationEvent.UI.OnNavScreen(Screen.UserDetail(item.sender.username))) },
                        model = item.sender.avatar.displayMediumImage,
                        shape = MaterialTheme.shapes.small
                    )
                },
                overlineContent = {
                    Row(horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf)) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = item.sender.nickname,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        )

                        if (item.unread) Text(
                            text = stringResource(Res.string.global_unread),
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                },
                headlineContent = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = item.rememberDisplayAnnotatedString(),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                },
                supportingContent = {
                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(ContentMargin)) {
                        Text(
                            modifier = Modifier.padding(top = ContentMarginHalf / 2),
                            text = item.createdAt.formatAgo(),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

                        if (item.type == NoticeType.REQUEST_FRIEND && item.unread) Row(
                            horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            OutlinedButton(
                                shape = MaterialTheme.shapes.small,
                                onClick = { onActionEvent(NotificationEvent.Action.OnMarkRead(item)) }
                            ) {
                                Text(text = stringResource(Res.string.global_ignore))
                            }
                            OutlinedButton(
                                shape = MaterialTheme.shapes.small,
                                onClick = { onActionEvent(NotificationEvent.Action.OnAgreeFriendRequest(item)) }
                            ) {
                                Text(text = stringResource(Res.string.global_agree))
                            }
                        }
                    }
                }
            )
            BgmHorizontalDivider()
        }

        if (state.notifications.isNotEmpty() && state.notifications.size == 40) {
            item {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    text = stringResource(Res.string.global_no_more_notice),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
@Preview
private fun PreviewNotificationScreen() {
    PreviewColumn(modifier = Modifier.fillMaxSize()) {
        NotificationScreen(
            uiState = UiState(
                NotificationState(
                    notifications = persistentListOf(
                        PreviewComposeNotice.copy(unread = false),
                        PreviewComposeNotice.copy(unread = true)
                    )
                )
            ),
            onUiEvent = { },
            onActionEvent = {}
        )
    }
}
