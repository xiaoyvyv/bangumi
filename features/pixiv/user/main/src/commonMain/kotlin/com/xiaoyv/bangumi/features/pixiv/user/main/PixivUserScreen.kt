package com.xiaoyv.bangumi.features.pixiv.user.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_edit
import com.xiaoyv.bangumi.core_resource.resources.pixiv_user_bio
import com.xiaoyv.bangumi.core_resource.resources.pixiv_user_location
import com.xiaoyv.bangumi.core_resource.resources.pixiv_user_no_bio
import com.xiaoyv.bangumi.core_resource.resources.pixiv_user_website
import com.xiaoyv.bangumi.features.pixiv.user.main.business.PixivUserEvent
import com.xiaoyv.bangumi.features.pixiv.user.main.business.PixivUserMoreAction
import com.xiaoyv.bangumi.features.pixiv.user.main.business.PixivUserState
import com.xiaoyv.bangumi.features.pixiv.user.main.business.PixivUserViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.chip.DropMenuActionButton
import com.xiaoyv.bangumi.shared.ui.component.layout.BgmCollapsingScaffold
import com.xiaoyv.bangumi.shared.ui.component.layout.rememberBgmCollapsingScaffoldState
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import com.xiaoyv.bangumi.shared.ui.view.pixiv.PixivUserProfileHeader
import org.jetbrains.compose.resources.stringResource
import org.orbitmvi.orbit.compose.collectAsState
import kotlinx.collections.immutable.toPersistentList

@Composable
fun PixivUserRoute(
    viewModel: PixivUserViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    PixivUserScreen(
        uiState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is PixivUserEvent.UI.OnNavUp -> onNavUp()
                is PixivUserEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

/**
 * 承载 Pixiv 用户页及其刷新状态。
 */
@Composable
private fun PixivUserScreen(
    uiState: UiState<PixivUserState>,
    onUiEvent: (PixivUserEvent.UI) -> Unit,
    onActionEvent: (PixivUserEvent.Action) -> Unit,
) {
    val scrollState = rememberScrollState()
    val collapsingState = rememberBgmCollapsingScaffoldState()

    StateLayout(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        onRefresh = { onActionEvent(PixivUserEvent.Action.OnRefresh(loading = true)) }
    ) { state ->
        BgmCollapsingScaffold(
            modifier = Modifier.fillMaxSize(),
            state = scrollState,
            collapsingState = collapsingState,
            topBar = { progressProvider ->
                BgmTopAppBar(
                    title = state.userInfo.name,
                    onNavigationClick = { onUiEvent(PixivUserEvent.UI.OnNavUp) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = progressProvider()),
                        titleContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = progressProvider()),
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    actions = {
                        if (state.isCurrentUser) {
                            IconButton(
                                onClick = { onUiEvent(PixivUserEvent.UI.OnNavScreen(Screen.PixivUserEdit)) }
                            ) {
                                Icon(
                                    imageVector = BgmIcons.Edit,
                                    contentDescription = stringResource(Res.string.global_edit),
                                )
                            }
                            DropMenuActionButton(
                                options = state.actions.map { option ->
                                    if (option.type == PixivUserMoreAction.Logout) {
                                        option.copy(contentColor = MaterialTheme.colorScheme.error)
                                    } else {
                                        option
                                    }
                                }.toPersistentList(),
                                onOptionClick = {
                                    when (it.type) {
                                        PixivUserMoreAction.Settings -> {
                                            onUiEvent(PixivUserEvent.UI.OnNavScreen(Screen.PixivUserSetting))
                                        }

                                        PixivUserMoreAction.Logout -> {
                                            onActionEvent(PixivUserEvent.Action.OnLogout)
                                        }
                                    }
                                },
                            )
                        }
                    },
                )
            },
            collapse = { topPadding ->
                PixivUserProfileHeader(
                    user = state.userInfo,
                    topPadding = topPadding,
                )
            },
        ) {
            PixivUserScreenContent(
                modifier = Modifier,
                state = state,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )
        }
    }
}

/**
 * Shows public Pixiv profile information returned by the user endpoint.
 */
@Composable
private fun PixivUserScreenContent(
    modifier: Modifier,
    state: PixivUserState,
    onUiEvent: (PixivUserEvent.UI) -> Unit,
    onActionEvent: (PixivUserEvent.Action) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(ContentMargin),
        verticalArrangement = Arrangement.spacedBy(ContentMargin),
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                shape = MaterialTheme.shapes.extraLarge,
            ) {
                androidx.compose.foundation.layout.Column(
                    modifier = Modifier.padding(ContentMargin),
                    verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
                ) {
                    Text(stringResource(Res.string.pixiv_user_bio), style = MaterialTheme.typography.bodyLarge)
                    SelectionContainer {
                        Text(
                            text = state.userInfo.comment.ifBlank { stringResource(Res.string.pixiv_user_no_bio) },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
        if (state.userInfo.region.name.isNotBlank() || state.userInfo.webpage.isNotBlank()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    shape = MaterialTheme.shapes.extraLarge,
                ) {
                    androidx.compose.foundation.layout.Column(
                        modifier = Modifier.padding(ContentMargin),
                        verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
                    ) {
                        if (state.userInfo.region.name.isNotBlank()) {
                            Text(stringResource(Res.string.pixiv_user_location), style = MaterialTheme.typography.labelMedium)
                            SelectionContainer {
                                Text(state.userInfo.region.name, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        if (state.userInfo.webpage.isNotBlank()) {
                            Spacer(Modifier.height(ContentMarginHalf))
                            Text(stringResource(Res.string.pixiv_user_website), style = MaterialTheme.typography.labelMedium)
                            SelectionContainer {
                                Text(
                                    state.userInfo.webpage,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun PreviewPixivUserScreen() {
    PreviewColumn(modifier = Modifier.fillMaxSize()) {
        PixivUserScreen(
            uiState = UiState(PixivUserState()),
            onUiEvent = {},
            onActionEvent = {}
        )
    }
}
