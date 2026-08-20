package com.xiaoyv.bangumi.features.settings.privacy

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.settings_privacy
import com.xiaoyv.bangumi.core_resource.resources.settings_privacy_comment_notify
import com.xiaoyv.bangumi.core_resource.resources.settings_privacy_follow
import com.xiaoyv.bangumi.core_resource.resources.settings_privacy_friend_notify
import com.xiaoyv.bangumi.core_resource.resources.settings_privacy_interactive
import com.xiaoyv.bangumi.core_resource.resources.settings_privacy_mention_notify
import com.xiaoyv.bangumi.core_resource.resources.settings_privacy_notification
import com.xiaoyv.bangumi.core_resource.resources.settings_privacy_nsfw_allow
import com.xiaoyv.bangumi.core_resource.resources.settings_privacy_nsfw_show
import com.xiaoyv.bangumi.core_resource.resources.settings_privacy_nsfw_show_desc
import com.xiaoyv.bangumi.core_resource.resources.settings_privacy_pm
import com.xiaoyv.bangumi.core_resource.resources.settings_privacy_preference
import com.xiaoyv.bangumi.core_resource.resources.settings_privacy_timeline_collect_reply
import com.xiaoyv.bangumi.core_resource.resources.settings_privacy_timeline_reply
import com.xiaoyv.bangumi.features.settings.privacy.business.SettingsPrivacyEvent
import com.xiaoyv.bangumi.features.settings.privacy.business.SettingsPrivacyState
import com.xiaoyv.bangumi.features.settings.privacy.business.SettingsPrivacyViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUserPrivacy
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmLargeTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.settings.SettingContainer
import com.xiaoyv.bangumi.shared.ui.component.settings.SettingOptionItem
import com.xiaoyv.bangumi.shared.ui.component.settings.SettingSwitchItem
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import org.jetbrains.compose.resources.stringResource
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun SettingsPrivacyRoute(
    viewModel: SettingsPrivacyViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    SettingsPrivacyScreen(
        uiState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is SettingsPrivacyEvent.UI.OnNavUp -> onNavUp()
                is SettingsPrivacyEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun SettingsPrivacyScreen(
    uiState: UiState<SettingsPrivacyState>,
    onUiEvent: (SettingsPrivacyEvent.UI) -> Unit,
    onActionEvent: (SettingsPrivacyEvent.Action) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            BgmLargeTopAppBar(
                title = stringResource(Res.string.settings_privacy),
                scrollBehavior = scrollBehavior,
                onNavigationClick = { onUiEvent(SettingsPrivacyEvent.UI.OnNavUp) }
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(it),
            onRefresh = { loading -> onActionEvent(SettingsPrivacyEvent.Action.OnRefresh(loading)) },
            uiState = uiState,
        ) { state ->
            SettingsPrivacyScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}

@Composable
private fun SettingsPrivacyScreenContent(
    state: SettingsPrivacyState,
    onUiEvent: (SettingsPrivacyEvent.UI) -> Unit,
    onActionEvent: (SettingsPrivacyEvent.Action) -> Unit,
) {
    val privacy = state.privacy
    val settings = privacy.settings
    val preferences = privacy.preferences

    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        // 内容偏好
        SettingContainer(label = { Text(text = stringResource(Res.string.settings_privacy_preference)) }) {
            SettingSwitchItem(
                title = stringResource(Res.string.settings_privacy_nsfw_show),
                description = stringResource(Res.string.settings_privacy_nsfw_show_desc),
                shape = ListItemDefaults.segmentedShapes(0, 2),
                value = preferences.showNsfwSubject,
                onValueChange = { checked ->
                    val updated = privacy.copy(
                        preferences = preferences.copy(showNsfwSubject = checked)
                    )
                    onActionEvent(SettingsPrivacyEvent.Action.OnUpdatePrivacy(updated))
                }
            )
            SettingSwitchItem(
                title = stringResource(Res.string.settings_privacy_nsfw_allow),
                shape = ListItemDefaults.segmentedShapes(1, 2),
                value = preferences.allowNsfw,
                onValueChange = { checked ->
                    val updated = privacy.copy(
                        preferences = preferences.copy(allowNsfw = checked)
                    )
                    onActionEvent(SettingsPrivacyEvent.Action.OnUpdatePrivacy(updated))
                }
            )
        }

        // 互动与隐私
        SettingContainer(label = { Text(text = stringResource(Res.string.settings_privacy_interactive)) }) {
            // 接收站内私信
            SettingOptionItem(
                title = stringResource(Res.string.settings_privacy_pm),
                shape = ListItemDefaults.segmentedShapes(0, 4),
                value = TabTokens.privacyScope3Tabs.find { it.type == settings.privateMessage.value }?.displayText().orEmpty(),
                items = TabTokens.privacyScope3Tabs,
                onClick = { value ->
                    val enumVal = ComposeUserPrivacy.ComposeUserPrivacySettings.PrivacyPrivateMessage.entries.find { it.value == value }
                        ?: ComposeUserPrivacy.ComposeUserPrivacySettings.PrivacyPrivateMessage.ALL
                    val updated = privacy.copy(
                        settings = settings.copy(privateMessage = enumVal)
                    )
                    onActionEvent(SettingsPrivacyEvent.Action.OnUpdatePrivacy(updated))
                }
            )

            // 吐槽动向回复
            SettingOptionItem(
                title = stringResource(Res.string.settings_privacy_timeline_reply),
                shape = ListItemDefaults.segmentedShapes(1, 4),
                value = TabTokens.privacyScope3Tabs.find { it.type == settings.timelineReply.value }?.displayText().orEmpty(),
                items = TabTokens.privacyScope3Tabs,
                onClick = { value ->
                    val enumVal = ComposeUserPrivacy.ComposeUserPrivacySettings.PrivacyTimelineReply.entries.find { it.value == value }
                        ?: ComposeUserPrivacy.ComposeUserPrivacySettings.PrivacyTimelineReply.ALL
                    val updated = privacy.copy(
                        settings = settings.copy(timelineReply = enumVal)
                    )
                    onActionEvent(SettingsPrivacyEvent.Action.OnUpdatePrivacy(updated))
                }
            )

            // 收藏动向回复
            SettingOptionItem(
                title = stringResource(Res.string.settings_privacy_timeline_collect_reply),
                shape = ListItemDefaults.segmentedShapes(2, 4),
                value = TabTokens.privacyScope3Tabs.find { it.type == settings.timelineCollectReply.value }?.displayText().orEmpty(),
                items = TabTokens.privacyScope3Tabs,
                onClick = { value ->
                    val enumVal = ComposeUserPrivacy.ComposeUserPrivacySettings.PrivacyTimelineCollectReply.entries.find { it.value == value }
                        ?: ComposeUserPrivacy.ComposeUserPrivacySettings.PrivacyTimelineCollectReply.ALL
                    val updated = privacy.copy(
                        settings = settings.copy(timelineCollectReply = enumVal)
                    )
                    onActionEvent(SettingsPrivacyEvent.Action.OnUpdatePrivacy(updated))
                }
            )

            // 允许关注
            SettingOptionItem(
                title = stringResource(Res.string.settings_privacy_follow),
                shape = ListItemDefaults.segmentedShapes(3, 4),
                value = TabTokens.privacyScope2Tabs.find { it.type == settings.follow.value }?.displayText().orEmpty(),
                items = TabTokens.privacyScope2Tabs,
                onClick = { value ->
                    val enumVal = ComposeUserPrivacy.ComposeUserPrivacySettings.PrivacyFollow.entries.find { it.value == value }
                        ?: ComposeUserPrivacy.ComposeUserPrivacySettings.PrivacyFollow.ALL
                    val updated = privacy.copy(
                        settings = settings.copy(follow = enumVal)
                    )
                    onActionEvent(SettingsPrivacyEvent.Action.OnUpdatePrivacy(updated))
                }
            )
        }

        // 通知与提醒
        SettingContainer(label = { Text(text = stringResource(Res.string.settings_privacy_notification)) }) {
            // @ 提醒通知
            SettingOptionItem(
                title = stringResource(Res.string.settings_privacy_mention_notify),
                shape = ListItemDefaults.segmentedShapes(0, 3),
                value = TabTokens.privacyScope3Tabs.find { it.type == settings.mentionNotification.value }?.displayText().orEmpty(),
                items = TabTokens.privacyScope3Tabs,
                onClick = { value ->
                    val enumVal = ComposeUserPrivacy.ComposeUserPrivacySettings.PrivacyMentionNotification.entries.find { it.value == value }
                        ?: ComposeUserPrivacy.ComposeUserPrivacySettings.PrivacyMentionNotification.ALL
                    val updated = privacy.copy(
                        settings = settings.copy(mentionNotification = enumVal)
                    )
                    onActionEvent(SettingsPrivacyEvent.Action.OnUpdatePrivacy(updated))
                }
            )

            // 评论回复通知
            SettingOptionItem(
                title = stringResource(Res.string.settings_privacy_comment_notify),
                shape = ListItemDefaults.segmentedShapes(1, 3),
                value = TabTokens.privacyScope3Tabs.find { it.type == settings.commentNotification.value }?.displayText().orEmpty(),
                items = TabTokens.privacyScope3Tabs,
                onClick = { value ->
                    val enumVal = ComposeUserPrivacy.ComposeUserPrivacySettings.PrivacyCommentNotification.entries.find { it.value == value }
                        ?: ComposeUserPrivacy.ComposeUserPrivacySettings.PrivacyCommentNotification.ALL
                    val updated = privacy.copy(
                        settings = settings.copy(commentNotification = enumVal)
                    )
                    onActionEvent(SettingsPrivacyEvent.Action.OnUpdatePrivacy(updated))
                }
            )

            // 接收好友申请
            SettingOptionItem(
                title = stringResource(Res.string.settings_privacy_friend_notify),
                shape = ListItemDefaults.segmentedShapes(2, 3),
                value = TabTokens.privacyScope2Tabs.find { it.type == settings.friendNotification.value }?.displayText().orEmpty(),
                items = TabTokens.privacyScope2Tabs,
                onClick = { value ->
                    val enumVal = ComposeUserPrivacy.ComposeUserPrivacySettings.PrivacyFriendNotification.entries.find { it.value == value }
                        ?: ComposeUserPrivacy.ComposeUserPrivacySettings.PrivacyFriendNotification.ALL
                    val updated = privacy.copy(
                        settings = settings.copy(friendNotification = enumVal)
                    )
                    onActionEvent(SettingsPrivacyEvent.Action.OnUpdatePrivacy(updated))
                }
            )
        }
    }
}

@Composable
@Preview
private fun PreviewSettingsPrivacyScreen() {
    PreviewColumn(modifier = Modifier.fillMaxSize()) {
        SettingsPrivacyScreen(
            uiState = UiState(
                SettingsPrivacyState()
            ),
            onUiEvent = {},
            onActionEvent = {}
        )
    }
}
