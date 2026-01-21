package com.xiaoyv.bangumi.features.pixiv.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.login_title
import com.xiaoyv.bangumi.features.pixiv.login.business.PixivLoginEvent
import com.xiaoyv.bangumi.features.pixiv.login.business.PixivLoginState
import com.xiaoyv.bangumi.features.pixiv.login.business.PixivLoginViewModel
import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.data.repository.PixivRepository
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

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
        baseState = baseState,
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
    baseState: BaseState<PixivLoginState>,
    onUiEvent: (PixivLoginEvent.UI) -> Unit,
    onActionEvent: (PixivLoginEvent.Action) -> Unit,
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                title = stringResource(Res.string.login_title),
                onNavigationClick = { onUiEvent(PixivLoginEvent.UI.OnNavUp) }
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            onRefresh = { onActionEvent(PixivLoginEvent.Action.OnRefresh(it)) },
            baseState = baseState,
        ) { state ->
            PixivLoginScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}


@Composable
private fun PixivLoginScreenContent(
    state: PixivLoginState,
    onUiEvent: (PixivLoginEvent.UI) -> Unit,
    onActionEvent: (PixivLoginEvent.Action) -> Unit,
) {

    val uriHandler = LocalUriHandler.current
    val repo = koinInject<PixivRepository>()
    val scope = rememberCoroutineScope()

    Column {
        Button(
            onClick = {
                val loginStart = "https://app-api.pixiv.net/web/v1/login?code_challenge="
                val loginEnd =
                    "&code_challenge_method=S256&client=pixiv-android&source=pixiv-android"

                scope.launch {
                    val login = repo.fetchLoginChallenge().getOrThrow()
                    val challenge = login.codeChallenge
                    val loginUrl = loginStart + challenge + loginEnd
                    debugLog { "Repo:$loginUrl" }

                    onUiEvent(PixivLoginEvent.UI.OnNavScreen(Screen.Web(loginUrl)))
//                    uriHandler.openUri(loginUrl)
                }
            }
        ) {
            Text(text = "生成Url")
        }

        Button(
            onClick = {
                val loginStart = "https://app-api.pixiv.net/web/v1/login?code_challenge="
                val loginEnd =
                    "&code_challenge_method=S256&client=pixiv-android&source=pixiv-android"

                scope.launch {
                    val login = repo.fetchLoginChallenge().getOrThrow()
                    val challenge = login.codeChallenge
                    val loginUrl = loginStart + challenge + loginEnd
                    debugLog { "Repo:$loginUrl" }

                    uriHandler.openUri(loginUrl)
                }
            }
        ) {
            Text(text = "外部登录")
        }


        Button(
            onClick = {
                System.launchDeeplinkSettings()
            }
        ) {
            Text(text = "Deeplink 配置")
        }

        Button(
            onClick = {
                scope.launch {
//                    val result = koinInject.requestGardenApi { fetchGardenResource(1) }
//                    debugLog { "动漫花园:${result.getOrNull()?.text()}" }
                }
            }
        ) {
            Text(text = "动漫花园测试")
        }

        val manager = koinInject<UserManager>()

        Button(
            onClick = {
                scope.launch {
                    manager.setToken(manager.userToken.copy(accessToken = "a"))
                }
            }
        ) {
            Text(text = "设置过期")
        }
    }
}

