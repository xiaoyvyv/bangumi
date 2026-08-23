package com.xiaoyv.bangumi.features.pixiv.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_pixiv
import com.xiaoyv.bangumi.core_resource.resources.pixiv_login_action
import com.xiaoyv.bangumi.core_resource.resources.pixiv_login_description
import com.xiaoyv.bangumi.core_resource.resources.pixiv_login_privacy
import com.xiaoyv.bangumi.core_resource.resources.pixiv_login_title
import com.xiaoyv.bangumi.features.pixiv.login.business.PixivLoginEvent
import com.xiaoyv.bangumi.features.pixiv.login.business.PixivLoginState
import com.xiaoyv.bangumi.features.pixiv.login.business.PixivLoginViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.data.repository.PixivRepository
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.button.LoadingButton
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.orbitmvi.orbit.compose.collectAsState

private const val PIXIV_LOGIN_START = "https://app-api.pixiv.net/web/v1/login?code_challenge="
private const val PIXIV_LOGIN_END = "&code_challenge_method=S256&client=pixiv-android&source=pixiv-android"

@Composable
fun PixivLoginRoute(
    viewModel: PixivLoginViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    PixivLoginScreen(
        uiState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is PixivLoginEvent.UI.OnNavUp -> onNavUp()
                is PixivLoginEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun PixivLoginScreen(
    uiState: UiState<PixivLoginState>,
    onUiEvent: (PixivLoginEvent.UI) -> Unit,
    onActionEvent: (PixivLoginEvent.Action) -> Unit,
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                title = stringResource(Res.string.pixiv_login_title),
                onNavigationClick = { onUiEvent(PixivLoginEvent.UI.OnNavUp) }
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            onRefresh = { onActionEvent(PixivLoginEvent.Action.OnRefresh(it)) },
            uiState = uiState,
        ) {
            PixivLoginScreenContent(onUiEvent)
        }
    }
}


@Composable
private fun PixivLoginScreenContent(onUiEvent: (PixivLoginEvent.UI) -> Unit) {
    val repo = koinInject<PixivRepository>()
    val scope = rememberCoroutineScope()
    var isOpening by remember { mutableStateOf(false) }

    PixivLoginContent(
        isOpening = isOpening,
        onOpenLogin = {
            scope.launch {
                isOpening = true
                runCatching {
                    repo.fetchLoginChallenge().getOrThrow().codeChallenge
                }.onSuccess { challenge ->
                    onUiEvent(PixivLoginEvent.UI.OnNavScreen(Screen.Web(PIXIV_LOGIN_START + challenge + PIXIV_LOGIN_END)))
                }
                isOpening = false
            }
        },
    )
}

@Composable
private fun PixivLoginContent(
    isOpening: Boolean,
    onOpenLogin: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = ContentMargin),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(Res.string.global_pixiv),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(ContentMarginHalf))
        Text(
            text = stringResource(Res.string.pixiv_login_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(ContentMargin))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.extraLarge)
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = MaterialTheme.shapes.extraLarge,
                )
                .padding(ContentMargin),
            verticalArrangement = Arrangement.spacedBy(ContentMargin),
        ) {
            Text(
                text = stringResource(Res.string.pixiv_login_description),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            LoadingButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                loading = isOpening,
                shape = MaterialTheme.shapes.large,
                onClick = onOpenLogin,
            ) {
                Text(
                    text = stringResource(Res.string.pixiv_login_action),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }

        Spacer(modifier = Modifier.height(ContentMargin))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.pixiv_login_privacy),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
@Preview
private fun PreviewPixivLoginContent() {
    PreviewColumn(modifier = Modifier.fillMaxSize()) {
        PixivLoginContent(
            isOpening = false,
            onOpenLogin = {},
        )
    }
}
