@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.CachePolicy
import coil3.request.crossfade
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.image_detect
import com.xiaoyv.bangumi.core_resource.resources.image_detect_character
import com.xiaoyv.bangumi.core_resource.resources.image_detect_subject
import com.xiaoyv.bangumi.shared.avif.AvifDecoderFactory
import com.xiaoyv.bangumi.shared.component.DetectType
import com.xiaoyv.bangumi.shared.component.LaunchReceiveShareImageEffect
import com.xiaoyv.bangumi.shared.data.api.client.ApiClient
import com.xiaoyv.bangumi.shared.data.manager.app.LocalPersonalState
import com.xiaoyv.bangumi.shared.data.manager.app.PersonalStateStore
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalSharedModelStoreOwner
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalSharedState
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalSharedViewModel
import com.xiaoyv.bangumi.shared.data.manager.shared.SharedEvent
import com.xiaoyv.bangumi.shared.data.manager.shared.SharedViewModel
import com.xiaoyv.bangumi.shared.gif.addPlatformGifSupport
import com.xiaoyv.bangumi.shared.ui.component.action.LocalActionHandler
import com.xiaoyv.bangumi.shared.ui.component.action.rememberAppActionHandler
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.AlertOptionDialog
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.rememberAlertDialogState
import com.xiaoyv.bangumi.shared.ui.component.image.ImageInterceptor
import com.xiaoyv.bangumi.shared.ui.component.live2d.Live2DOverlay
import com.xiaoyv.bangumi.shared.ui.component.live2d.LocalLive2DSpeechController
import com.xiaoyv.bangumi.shared.ui.component.live2d.rememberLive2DSpeechState
import com.xiaoyv.bangumi.shared.ui.component.navigation.Navigator
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.popup.LocalPopupLoadingState
import com.xiaoyv.bangumi.shared.ui.component.popup.LocalPopupTipState
import com.xiaoyv.bangumi.shared.ui.component.popup.PopupLoadingScreen
import com.xiaoyv.bangumi.shared.ui.component.popup.PopupTipContent
import com.xiaoyv.bangumi.shared.ui.component.popup.PopupUpdateDialog
import com.xiaoyv.bangumi.shared.ui.component.popup.rememberPopupLoadingState
import com.xiaoyv.bangumi.shared.ui.component.popup.rememberPopupTipState
import com.xiaoyv.bangumi.shared.ui.component.scroll.LocalScrollUpState
import com.xiaoyv.bangumi.shared.ui.component.scroll.rememberScrollUpState
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import com.xiaoyv.bangumi.shared.ui.theme.BgmAppTheme
import kotlinx.collections.immutable.persistentListOf
import okio.FileSystem
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.dsl.koinConfiguration
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect


@OptIn(ExperimentalCoilApi::class)
@Composable
fun App() = KoinApplication(configuration = koinConfiguration(declaration = { initializeKoin() })) {
    val apiClient: ApiClient = koinInject()
    val personalStateStore: PersonalStateStore = koinInject()

    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .crossfade(true)
            .components {
                add(KtorNetworkFetcherFactory(apiClient.imageHttpClient))
                add(ImageInterceptor)
                add(AvifDecoderFactory.create(context))
                addPlatformGifSupport()
            }
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.3)
                    .strongReferencesEnabled(true)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "image_cache_6")
                    .maxSizeBytes(512L * 1024 * 1024)
                    .build()
            }
            .build()
    }

    val navigator = koinInject<Navigator>()
    val popupTipState = rememberPopupTipState()
    val popupLoadingState = rememberPopupLoadingState()
    val actionHandler = rememberAppActionHandler {
        navigator.navigate(it)
    }

    // 全局状态
    val sharedViewModelStoreOwner = LocalViewModelStoreOwner.current
    val sharedViewModel = koinViewModel<SharedViewModel>(viewModelStoreOwner = requireNotNull(sharedViewModelStoreOwner))
    val sharedState by sharedViewModel.collectAsState()
    val personState by personalStateStore.state.collectAsStateWithLifecycle()
    val appUpdatePopupState = rememberAlertDialogState()

    sharedViewModel.collectSideEffect { event ->
        when (event) {
            SharedEvent.OnShowAppUpdate -> appUpdatePopupState.show()
            is SharedEvent.OnRefresh -> Unit
        }
    }

    // Live2d Controller
    val live2dSpeechState = rememberLive2DSpeechState()
    val scrollUpState = rememberScrollUpState()

    CompositionLocalProvider(
        LocalPopupTipState provides popupTipState,
        LocalPopupLoadingState provides popupLoadingState,
        LocalSharedViewModel provides sharedViewModel,
        LocalSharedModelStoreOwner provides sharedViewModelStoreOwner,
        LocalSharedState provides sharedState,
        LocalActionHandler provides actionHandler,
        LocalPersonalState provides personState,
        LocalLive2DSpeechController provides live2dSpeechState,
        LocalScrollUpState provides scrollUpState,
    ) {
        BgmAppTheme(modifier = Modifier.fillMaxSize()) {
            BgmScreenNavGraph(navigator = navigator)

            // Update
            PopupUpdateDialog(
                release = sharedState.appRelease,
                state = appUpdatePopupState,
                onDownload = { actionHandler.openInBrowser(it.htmlUrl) },
            )

            // Loading
            PopupLoadingScreen(popupLoadingState)

            // Toast
            SnackbarHost(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = 56.dp)
                    .align(Alignment.BottomCenter),
                hostState = popupTipState.state,
                snackbar = { PopupTipContent(it) }
            )

            // Liv2d
            Live2DOverlay(
                state = live2dSpeechState,
                live2dConfig = LocalSharedState.current.settings.live2d
            )
        }
    }

    HandleShareContent(navigator)
}


@Composable
private fun HandleShareContent(navigator: Navigator) {
    var currentPath by remember { mutableStateOf("") }
    val dialogState = rememberAlertDialogState()

    AlertOptionDialog(
        state = dialogState,
        title = stringResource(Res.string.image_detect),
        items = remember {
            persistentListOf(
                ComposeTextTab(DetectType.SOURCE, Res.string.image_detect_subject),
                ComposeTextTab(DetectType.CHARACTER, Res.string.image_detect_character),
            )
        },
        onClick = { item, _ ->
            when (item.type) {
                DetectType.SOURCE -> navigator.navigate(
                    Screen.DetectImage(DetectType.SOURCE, currentPath)
                )

                DetectType.CHARACTER -> navigator.navigate(
                    Screen.DetectImage(DetectType.CHARACTER, currentPath)
                )
            }
        }
    )

    LaunchReceiveShareImageEffect(
        enable = navigator.backStack.lastOrNull() !is Screen.Splash,
        onReceiveImagePath = { path, type ->
            if (type == DetectType.CHARACTER || type == DetectType.SOURCE) {
                navigator.navigate(Screen.DetectImage(type, path))
            } else {
                currentPath = path
                dialogState.show()
            }
        }
    )
}
