package com.xiaoyv.bangumi.shared.ui.component.turnstile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.multiplatform.webview.request.RequestInterceptor
import com.multiplatform.webview.request.WebRequest
import com.multiplatform.webview.request.WebRequestInterceptResult
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_turnstile
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.core.utils.toUrl
import com.xiaoyv.bangumi.shared.data.constant.WebConstant
import com.xiaoyv.bangumi.shared.data.manager.shared.currentSettings
import com.xiaoyv.bangumi.shared.sni.AntiSniWebProxy
import com.xiaoyv.bangumi.shared.ui.component.layout.state.BgmProgressIndicator
import com.xiaoyv.bangumi.shared.ui.kts.HideInPreview
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import org.jetbrains.compose.resources.stringResource


@Composable
fun BgmTurnstile(
    onToken: (String) -> Unit,
    modifier: Modifier = Modifier,
    refreshKey: Long = 0,
    url: String = WebConstant.URL_BGM_TURNSTILE,
    callback: String = "bangumi://"
) = HideInPreview {
    Column(modifier) {
        key(refreshKey) {
            val settings = currentSettings()
            val webViewState = rememberWebViewState(url)

            val requestInterceptor = remember(onToken, webViewState) {
                object : RequestInterceptor {
                    override fun onInterceptUrlRequest(request: WebRequest, navigator: WebViewNavigator): WebRequestInterceptResult {
                        if (request.url.startsWith(callback)) {
                            onToken(request.url.toUrl().parameters["token"].orEmpty())
                            return WebRequestInterceptResult.Reject
                        } else {
                            return WebRequestInterceptResult.Allow
                        }
                    }
                }
            }

            val webViewNavigator = rememberWebViewNavigator(
                requestInterceptor = requestInterceptor
            )

            DisposableEffect(settings) {
                val proxy = AntiSniWebProxy(
                    settings.network.configHosts,
                    settings.network.tlsFragmentationDomains,
                    connectTimeoutMillis = 10000,
                    headerTimeoutMillis = 10000,
                    errorHandler = {
                        debugLog { it.stackTraceToString() }
                    }
                )
                proxy.start()
                onDispose { proxy.close() }
            }

            Text(
                modifier = Modifier.padding(horizontal = ContentMarginHalf),
                text = stringResource(Res.string.global_turnstile),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Box(contentAlignment = Alignment.Center) {
                BgmProgressIndicator()


                WebView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    state = webViewState,
                    navigator = webViewNavigator,
                )
            }
        }
    }
}