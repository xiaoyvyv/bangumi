@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
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
import com.xiaoyv.bangumi.features.detect.navigateReceive
import com.xiaoyv.bangumi.shared.avif.AvifDecoderFactory
import com.xiaoyv.bangumi.shared.component.BgmLive2DView
import com.xiaoyv.bangumi.shared.component.DetectType
import com.xiaoyv.bangumi.shared.component.LaunchReceiveShareImageEffect
import com.xiaoyv.bangumi.shared.component.rememberBgmLive2DState
import com.xiaoyv.bangumi.shared.data.api.client.BgmApiClient
import com.xiaoyv.bangumi.shared.data.manager.app.LocalPersonalState
import com.xiaoyv.bangumi.shared.data.manager.app.PersonalStateStore
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalSharedModelStoreOwner
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalSharedState
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalSharedViewModel
import com.xiaoyv.bangumi.shared.data.manager.shared.SharedViewModel
import com.xiaoyv.bangumi.shared.gif.addPlatformGifSupport
import com.xiaoyv.bangumi.shared.ui.component.action.LocalActionHandler
import com.xiaoyv.bangumi.shared.ui.component.action.rememberAppActionHandler
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.AlertOptionDialog
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.rememberAlertDialogState
import com.xiaoyv.bangumi.shared.ui.component.image.ImageInterceptor
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.popup.LocalPopupLoadingState
import com.xiaoyv.bangumi.shared.ui.component.popup.LocalPopupTipState
import com.xiaoyv.bangumi.shared.ui.component.popup.PopupLoadingScreen
import com.xiaoyv.bangumi.shared.ui.component.popup.rememberPopupLoadingState
import com.xiaoyv.bangumi.shared.ui.component.popup.rememberPopupTipState
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import com.xiaoyv.bangumi.shared.ui.theme.BgmAppTheme
import kotlinx.collections.immutable.persistentListOf
import okio.FileSystem
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState


@Composable
fun App() = KoinApplication(application = { initializeKoin() }) {
    val apiClient: BgmApiClient = koinInject()
    val personalStateStore: PersonalStateStore = koinInject()
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .crossfade(true)
            .components {
                add(KtorNetworkFetcherFactory(apiClient.client))
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

    val navController = rememberNavController()
    val popupTipState = rememberPopupTipState()
    val popupLoadingState = rememberPopupLoadingState()
    val actionHandler = rememberAppActionHandler {
        navController.navigateToScreen(it)
    }

    // 全局状态
    val sharedViewModelStoreOwner = LocalViewModelStoreOwner.current
    val sharedViewModel = koinViewModel<SharedViewModel>(viewModelStoreOwner = requireNotNull(sharedViewModelStoreOwner))
    val sharedState by sharedViewModel.collectAsState()
    val personState by personalStateStore.state.collectAsStateWithLifecycle()

    CompositionLocalProvider(
        LocalPopupTipState provides popupTipState,
        LocalPopupLoadingState provides popupLoadingState,
        LocalSharedViewModel provides sharedViewModel,
        LocalSharedModelStoreOwner provides sharedViewModelStoreOwner,
        LocalSharedState provides sharedState,
        LocalActionHandler provides actionHandler,
        LocalPersonalState provides personState
    ) {
        BgmAppTheme(modifier = Modifier.fillMaxSize()) {
            BgmScreenNavGraph(
                navController = navController,
                startDestination = Screen.Splash
            )

            // Loading
            PopupLoadingScreen(popupLoadingState)

            // Toast
            SnackbarHost(
                modifier = Modifier
                    .padding(bottom = 60.dp)
                    .align(Alignment.BottomCenter),
                hostState = popupTipState.state
            )

            if (LocalSharedState.current.settings.live2d.enable) {
                val live2DState = rememberBgmLive2DState()

                BgmLive2DView(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .systemBarsPadding()
                        .padding(bottom = 80.dp)
                        .width(100.dp)
                        .aspectRatio(202 / 308f),
                    state = live2DState
                )

                LaunchedEffect(Unit) {
                    val modelName = "bangumi_musume_2d"
                    val modelPath = Res.getUri("files/live2d/$modelName/$modelName.moc3")
                    val modelDir = modelPath.substringBeforeLast("/")
                    live2DState.loadModel(modelName, modelDir)
                }
            }
        }
    }

    HandleShareContent(navController)
}

@Composable
private fun HandleShareContent(
    navController: NavHostController,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
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
                DetectType.SOURCE -> navController.navigateReceive(
                    Screen.DetectImage(DetectType.SOURCE, currentPath)
                )

                DetectType.CHARACTER -> navController.navigateReceive(
                    Screen.DetectImage(DetectType.CHARACTER, currentPath)
                )
            }
        }
    )

    LaunchReceiveShareImageEffect(
        enable = currentDestination != null && currentDestination.route != Screen.Splash.route,
        onReceiveImagePath = { path, type ->
            if (type == DetectType.CHARACTER || type == DetectType.SOURCE) {
                navController.navigateReceive(Screen.DetectImage(type, path))
            } else {
                currentPath = path
                dialogState.show()
            }
        }
    )
}

