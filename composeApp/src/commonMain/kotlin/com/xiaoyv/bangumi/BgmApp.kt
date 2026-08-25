@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
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
import com.xiaoyv.bangumi.shared.component.Live2D
import com.xiaoyv.bangumi.shared.component.rememberLive2DState
import com.xiaoyv.bangumi.shared.data.api.client.ApiClient
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSetting
import com.xiaoyv.bangumi.shared.ui.theme.currentInDarkTheme
import com.xiaoyv.bangumi.shared.data.manager.app.LocalPersonalState
import com.xiaoyv.bangumi.shared.data.manager.app.PersonalStateStore
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalSharedModelStoreOwner
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalSharedState
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalSharedViewModel
import com.xiaoyv.bangumi.shared.data.manager.shared.SharedViewModel
import com.xiaoyv.bangumi.shared.gif.addPlatformGifSupport
import com.xiaoyv.bangumi.shared.resource.copyToDir
import com.xiaoyv.bangumi.shared.ui.component.action.LocalActionHandler
import com.xiaoyv.bangumi.shared.ui.component.action.rememberAppActionHandler
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.AlertOptionDialog
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.rememberAlertDialogState
import com.xiaoyv.bangumi.shared.ui.component.image.ImageInterceptor
import com.xiaoyv.bangumi.shared.ui.component.navigation.Navigator
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.popup.LocalPopupLoadingState
import com.xiaoyv.bangumi.shared.ui.component.popup.LocalPopupTipState
import com.xiaoyv.bangumi.shared.ui.component.popup.PopupLoadingScreen
import com.xiaoyv.bangumi.shared.ui.component.popup.rememberPopupLoadingState
import com.xiaoyv.bangumi.shared.ui.component.popup.rememberPopupTipState
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import com.xiaoyv.bangumi.shared.ui.theme.BgmAppTheme
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.createDirectories
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.filesDir
import kotlinx.collections.immutable.persistentListOf
import okio.FileSystem
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.dsl.koinConfiguration
import org.orbitmvi.orbit.compose.collectAsState


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
            BgmScreenNavGraph(navigator = navigator)

            // Loading
            PopupLoadingScreen(popupLoadingState)

            // Toast
            SnackbarHost(
                modifier = Modifier
                    .padding(bottom = 60.dp)
                    .align(Alignment.BottomCenter),
                hostState = popupTipState.state
            )

            BgmLive2DOverlay(
                live2dConfig = LocalSharedState.current.settings.live2d
            )
        }
    }

    HandleShareContent(navigator)
}

@Composable
private fun BoxScope.BgmLive2DOverlay(
    live2dConfig: ComposeSetting.Live2dConfig,
) {
    if (!live2dConfig.enable) return

    val inDark = currentInDarkTheme()
    val modelName = remember(live2dConfig.shell, inDark) {
        when (live2dConfig.shell) {
            ComposeSetting.Live2dConfig.Shell.MUSUME -> "bangumi_musume_2026_parts_grouped"
            ComposeSetting.Live2dConfig.Shell.BLACK_MUSUME -> "bangumi_black_musume_2026_parts"
            else -> if (inDark) "bangumi_black_musume_2026_parts" else "bangumi_musume_2026_parts_grouped"
        }
    }
    val live2DState = rememberLive2DState()

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .systemBarsPadding()
            .padding(bottom = 80.dp)
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val downPosition = down.position
                    val downTime = down.uptimeMillis
                    val pointerId = down.id
                    var isLongPress = false

                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Main)
                        val change = event.changes.firstOrNull { it.id == pointerId } ?: break

                        if (!change.pressed) {
                            break
                        }

                        val distance = (change.position - downPosition).getDistance()
                        val duration = change.uptimeMillis - downTime

                        // 如果在长按等待时间内移动距离超过 TouchSlop，说明是 Live2D 的交互滑动而非长按拖拽
                        if (distance > viewConfiguration.touchSlop) {
                            break
                        }

                        // 保持长按静止达到 LongPress 阈值
                        if (duration >= viewConfiguration.longPressTimeoutMillis) {
                            isLongPress = true
                            break
                        }
                    }

                    if (isLongPress) {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Main)
                            val change = event.changes.firstOrNull { it.id == pointerId } ?: break
                            if (!change.pressed) break

                            val dragAmount = change.positionChange()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                            change.consume()
                        }
                    }
                }
            }
            .width(live2dConfig.size.dp)
            .aspectRatio(202 / 308f)
    ) {
        Live2D(
            modifier = Modifier.fillMaxSize(),
            state = live2DState
        )
    }

    LaunchedEffect(modelName) {
        val workDir = (FileKit.filesDir / "live2d").also {
            it.createDirectories()
        }

        val targetFile = Res.copyToDir(resourcePath = "files/live2d/$modelName.zip", workDir)

        live2DState.workDir = workDir.absolutePath()
        live2DState.loadModel(targetFile.absolutePath(), modelName)
    }
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

