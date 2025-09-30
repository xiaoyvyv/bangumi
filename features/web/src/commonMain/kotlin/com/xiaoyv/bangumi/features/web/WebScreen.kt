package com.xiaoyv.bangumi.features.web

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.multiplatform.webview.cookie.Cookie
import com.multiplatform.webview.request.WebRequestInterceptResult
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.rememberSaveableWebViewState
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_loading
import com.xiaoyv.bangumi.features.web.business.WebEvent
import com.xiaoyv.bangumi.features.web.business.WebSideEffect
import com.xiaoyv.bangumi.features.web.business.WebState
import com.xiaoyv.bangumi.features.web.business.WebViewModel
import com.xiaoyv.bangumi.features.web.internal.createWebRequestInterceptor
import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.ui.component.action.LocalActionHandler
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.mp.KoinPlatform
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun WebRoute(
    viewModel: WebViewModel = koinViewModel<WebViewModel>(),
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()
    val actionHandler = LocalActionHandler.current
    val navigator = rememberWebViewNavigator(
        requestInterceptor = createWebRequestInterceptor(
            onHandleHttpUrl = {
                if (actionHandler.openBgmLink(it.url)) {
                    WebRequestInterceptResult.Reject
                } else {
                    WebRequestInterceptResult.Allow
                }
            },
            onHandleProtocol = {
                viewModel.onEvent(WebEvent.Action.OnHandleProtocol(it))
            }
        )
    )

    viewModel.collectBaseSideEffect {
        when (it) {
            is WebSideEffect.OnReload -> baseState.content { navigator.loadUrl(url) }
            is WebSideEffect.OnNavUp -> onNavUp()
        }
    }

    WebScreen(
        baseState = baseState,
        navigator = navigator,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is WebEvent.UI.OnNavUp -> onNavUp()
                is WebEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun WebScreen(
    baseState: BaseState<WebState>,
    navigator: WebViewNavigator,
    onUiEvent: (WebEvent.UI) -> Unit,
    onActionEvent: (WebEvent.Action) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                title = baseState.payload?.title ?: stringResource(Res.string.global_loading),
                onNavigationClick = { onUiEvent(WebEvent.UI.OnNavUp) }
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            onRefresh = { onActionEvent(WebEvent.Action.OnRefresh(it)) },
            baseState = baseState,
        ) { state ->
            WebScreenContent(
                state = state,
                navigator = navigator,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )
        }
    }
}


@Composable
private fun WebScreenContent(
    state: WebState,
    navigator: WebViewNavigator,
    onUiEvent: (WebEvent.UI) -> Unit,
    onActionEvent: (WebEvent.Action) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val webState = rememberSaveableWebViewState(state.url)

        LaunchedEffect(state.cookies) {
            KoinPlatform.getKoin()
            state.cookies.forEach {
                webState.cookieManager.setCookie(
                    state.url, Cookie(
                        name = it.name,
                        value = it.value,
                        domain = it.domain,
                        path = it.path,
                        expiresDate = it.expires?.timestamp,
                        isSecure = it.secure,
                        isHttpOnly = it.httpOnly,
                        maxAge = it.maxAge?.toLong(),
                    )
                )
            }

            with(webState.webSettings) {
                customUserAgentString = System.userAgent()
                supportZoom = false
                allowFileAccessFromFileURLs = true
                allowUniversalAccessFromFileURLs = true
                desktopWebSettings.disablePopupWindows = true
                androidWebSettings.allowFileAccess = true
                androidWebSettings.domStorageEnabled = true
            }
        }

        LaunchedEffect(navigator, webState) {
            val bundle = webState.viewState
            if (bundle == null) {
                navigator.loadUrl(state.url)
            }
        }

        LaunchedEffect(webState) {
            snapshotFlow { webState.pageTitle }
                .collect {
                    if (it.orEmpty().isNotBlank()) {
                        onActionEvent(WebEvent.Action.OnTitleChange(it.orEmpty()))
                    }
                }
        }

        WebView(
            modifier = Modifier.fillMaxSize(),
            captureBackPresses = false,
            state = webState,
            navigator = navigator,
        )

        val loadingState = webState.loadingState
        if (loadingState is LoadingState.Loading) {
            val progress by animateFloatAsState(
                targetValue = loadingState.progress,
                animationSpec = tween(),
                label = "WebProgressBar"
            )

            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                progress = { progress },
                gapSize = 0.dp,
                strokeCap = StrokeCap.Square,
                drawStopIndicator = {}
            )
        }
    }
}
