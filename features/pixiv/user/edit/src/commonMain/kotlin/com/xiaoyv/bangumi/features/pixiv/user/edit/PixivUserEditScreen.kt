package com.xiaoyv.bangumi.features.pixiv.user.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_edit
import com.xiaoyv.bangumi.core_resource.resources.pixiv_user_edit_intro
import com.xiaoyv.bangumi.core_resource.resources.pixiv_user_edit_intro_placeholder
import com.xiaoyv.bangumi.core_resource.resources.pixiv_user_edit_name
import com.xiaoyv.bangumi.core_resource.resources.pixiv_user_edit_name_placeholder
import com.xiaoyv.bangumi.core_resource.resources.pixiv_user_edit_profile
import com.xiaoyv.bangumi.core_resource.resources.pixiv_user_edit_website
import com.xiaoyv.bangumi.core_resource.resources.pixiv_user_edit_website_placeholder
import com.xiaoyv.bangumi.features.pixiv.user.edit.business.PixivUserEditEvent
import com.xiaoyv.bangumi.features.pixiv.user.edit.business.PixivUserEditState
import com.xiaoyv.bangumi.features.pixiv.user.edit.business.PixivUserEditViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import org.jetbrains.compose.resources.stringResource
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun PixivUserEditRoute(
    viewModel: PixivUserEditViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    PixivUserEditScreen(
        uiState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is PixivUserEditEvent.UI.OnNavUp -> onNavUp()
                is PixivUserEditEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

/**
 * Hosts editable Pixiv profile fields in the module's MVI state.
 */
@Composable
private fun PixivUserEditScreen(
    uiState: UiState<PixivUserEditState>,
    onUiEvent: (PixivUserEditEvent.UI) -> Unit,
    onActionEvent: (PixivUserEditEvent.Action) -> Unit,
) {
    StateLayout(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        onRefresh = { onActionEvent(PixivUserEditEvent.Action.OnRefresh(loading = true)) }
    ) { state ->
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                BgmTopAppBar(
                    title = stringResource(Res.string.global_edit),
                    onNavigationClick = { onUiEvent(PixivUserEditEvent.UI.OnNavUp) }
                )
            }
        ) { paddingValues ->
            PixivUserEditScreenContent(
                modifier = Modifier.padding(paddingValues),
                state = state,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )
        }
    }
}

/**
 * Renders the Pixiv profile form with fields grouped as a single profile card.
 */
@Composable
private fun PixivUserEditScreenContent(
    modifier: Modifier,
    state: PixivUserEditState,
    onUiEvent: (PixivUserEditEvent.UI) -> Unit,
    onActionEvent: (PixivUserEditEvent.Action) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(ContentMargin),
        verticalArrangement = Arrangement.spacedBy(ContentMargin),
    ) {
        item {
            Text(
                text = stringResource(Res.string.pixiv_user_edit_profile),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        item {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.displayName,
                onValueChange = { onActionEvent(PixivUserEditEvent.Action.OnDisplayNameChanged(it)) },
                label = { Text(stringResource(Res.string.pixiv_user_edit_name), style = MaterialTheme.typography.labelMedium) },
                placeholder = { Text(stringResource(Res.string.pixiv_user_edit_name_placeholder), style = MaterialTheme.typography.bodyMedium) },
                textStyle = MaterialTheme.typography.bodyMedium,
                singleLine = true,
                shape = MaterialTheme.shapes.large,
            )
        }
        item {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.introduction,
                onValueChange = { onActionEvent(PixivUserEditEvent.Action.OnIntroductionChanged(it)) },
                label = { Text(stringResource(Res.string.pixiv_user_edit_intro), style = MaterialTheme.typography.labelMedium) },
                placeholder = { Text(stringResource(Res.string.pixiv_user_edit_intro_placeholder), style = MaterialTheme.typography.bodyMedium) },
                textStyle = MaterialTheme.typography.bodyMedium,
                minLines = 4,
                shape = MaterialTheme.shapes.large,
            )
        }
        item {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.website,
                onValueChange = { onActionEvent(PixivUserEditEvent.Action.OnWebsiteChanged(it)) },
                label = { Text(stringResource(Res.string.pixiv_user_edit_website), style = MaterialTheme.typography.labelMedium) },
                placeholder = { Text(stringResource(Res.string.pixiv_user_edit_website_placeholder), style = MaterialTheme.typography.bodyMedium) },
                textStyle = MaterialTheme.typography.bodyMedium,
                singleLine = true,
                shape = MaterialTheme.shapes.large,
            )
        }
    }
}

@Composable
@Preview
private fun PreviewPixivUserEditScreen() {
    PreviewColumn(modifier = Modifier.fillMaxSize()) {
        PixivUserEditScreen(
            uiState = UiState(PixivUserEditState()),
            onUiEvent = {},
            onActionEvent = {}
        )
    }
}
