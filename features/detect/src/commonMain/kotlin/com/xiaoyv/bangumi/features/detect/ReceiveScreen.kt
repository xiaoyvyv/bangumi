package com.xiaoyv.bangumi.features.detect

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import com.attafitamim.krop.core.crop.AspectRatio
import com.attafitamim.krop.core.crop.LocalCropperStyle
import com.attafitamim.krop.core.crop.cropperStyle
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_close
import com.xiaoyv.bangumi.core_resource.resources.image_detect_character_change_model
import com.xiaoyv.bangumi.core_resource.resources.image_detect_character_choose_model
import com.xiaoyv.bangumi.core_resource.resources.image_detect_engine
import com.xiaoyv.bangumi.core_resource.resources.image_detect_pick
import com.xiaoyv.bangumi.core_resource.resources.image_detect_running
import com.xiaoyv.bangumi.core_resource.resources.image_detect_start
import com.xiaoyv.bangumi.features.detect.business.ReceiveEvent
import com.xiaoyv.bangumi.features.detect.business.ReceiveState
import com.xiaoyv.bangumi.features.detect.business.ReceiveViewModel
import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.utils.clickWithoutRipped
import com.xiaoyv.bangumi.shared.core.utils.formatHMS
import com.xiaoyv.bangumi.shared.core.utils.toFixed
import com.xiaoyv.bangumi.shared.data.model.response.trace.ComposeTraceCharacter
import com.xiaoyv.bangumi.shared.data.model.response.trace.ComposeTraceMoe
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.AlertOptionDialog
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.rememberAlertDialogState
import com.xiaoyv.bangumi.shared.ui.component.image.ClippedImage
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.image.cropper.CropperImage
import com.xiaoyv.bangumi.shared.ui.component.layout.AdaptiveTwoPanel
import com.xiaoyv.bangumi.shared.ui.component.layout.TransparentBackground
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import com.xiaoyv.bangumi.shared.ui.composition.LocalCropperState
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import kotlin.math.roundToLong

@Composable
fun ReceiveRoute(
    viewModel: ReceiveViewModel = koinViewModel<ReceiveViewModel>(),
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()
    val cropState = viewModel.imageCropper.cropState

    viewModel.collectBaseSideEffect {

    }

    CompositionLocalProvider(LocalCropperState provides cropState) {
        ReceiveScreen(
            baseState = baseState,
            onActionEvent = viewModel::onEvent,
            onUiEvent = {
                when (it) {
                    is ReceiveEvent.UI.OnNavUp -> onNavUp()
                    is ReceiveEvent.UI.OnNavScreen -> onNavScreen(it.screen)
                }
            }
        )
    }
}

@Composable
private fun ReceiveScreen(
    baseState: BaseState<ReceiveState>,
    onUiEvent: (ReceiveEvent.UI) -> Unit,
    onActionEvent: (ReceiveEvent.Action) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            baseState.content {
                val launcher = rememberFilePickerLauncher(type = FileKitType.Image) {
                    if (it != null) {
                        onActionEvent(ReceiveEvent.Action.OnSelectedFile(it))
                    }
                }

                BgmTopAppBar(
                    title = stringResource(title),
                    actions = {
                        TextButton(onClick = { launcher.launch() }) {
                            Text(text = stringResource(Res.string.image_detect_pick))
                        }
                    },
                    onNavigationClick = { onUiEvent(ReceiveEvent.UI.OnNavUp) }
                )
            }
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            onRefresh = { onActionEvent(ReceiveEvent.Action.OnRefresh(it)) },
            baseState = baseState,
        ) { state ->
            ReceiveScreenContent(
                state = state,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )

            ReceiveScreenSubjectDialog(
                state = state,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )

            ReceiveScreenCharacterDialog(
                state = state,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )
        }
    }
}


@Composable
private fun ReceiveScreenContent(
    state: ReceiveState,
    onUiEvent: (ReceiveEvent.UI) -> Unit,
    onActionEvent: (ReceiveEvent.Action) -> Unit,
) {
    val modelDialogState = rememberAlertDialogState()

    AdaptiveTwoPanel(
        modifier = Modifier.fillMaxSize(),
        bias = 0.6f,
        start = {
            TransparentBackground(Modifier.matchParentSize())

            val cropState = LocalCropperState.current
            if (cropState != null) {
                val cropperStyle = remember {
                    cropperStyle(
                        backgroundColor = Color.Transparent,
                        touchRad = 20.dp,
                        aspects = listOf(
                            AspectRatio(16, 9),
                            AspectRatio(4, 3),
                        ),
                        secondaryHandles = true
                    )
                }

                CompositionLocalProvider(LocalCropperStyle provides cropperStyle) {
                    CropperImage(
                        modifier = Modifier.matchParentSize(),
                        state = cropState
                    )

                    LaunchedEffect(cropState) {
                        cropState.setInitialState(cropperStyle)
                    }
                }
            }
        },
        end = {
            Column(
                modifier = Modifier
                    .matchParentSize()
                    .padding(vertical = LayoutPaddingHalf, horizontal = LayoutPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(state.subtitle),
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 28.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                state.models[state.currentModel]?.let {
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        modifier = Modifier
                            .align(Alignment.Start)
                            .clickWithoutRipped { modelDialogState.show() },
                        text = stringResource(
                            Res.string.image_detect_character_change_model,
                            stringResource(it)
                        ),
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 28.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                RecognizingButton(
                    modifier = Modifier.fillMaxWidth(),
                    isRecognizing = state.isRecognizing,
                    enabled = !state.isRecognizing && LocalCropperState.current != null,
                    onClick = { onActionEvent(ReceiveEvent.Action.OnRecognizingImageSource) }
                )

                Spacer(modifier = Modifier.weight(1f))

                val uriHandler = LocalUriHandler.current

                Text(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .clickable { uriHandler.openUri(state.helpLink) }
                        .padding(vertical = LayoutPaddingHalf),
                    text = stringResource(Res.string.image_detect_engine, state.helpLink),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )

    AlertOptionDialog(
        state = modelDialogState,
        title = stringResource(Res.string.image_detect_character_choose_model),
        items = remember(state.models) {
            state.models.map { ComposeTextTab(it.key, it.value) }.toPersistentList()
        },
        onClick = { tab, _ ->
            onActionEvent(ReceiveEvent.Action.OnChangeModel(tab.type))
        }
    )
}

/**
 * {
 *   "frameCount": 1520187104,
 *   "error": "",
 *   "result": [
 *     {
 *       "anilist": 2136,
 *       "filename": "フロントイノセント.mp4",
 *       "episode": null,
 *       "from": 514.4473,
 *       "at": 514.6808,
 *       "to": 514.7476,
 *       "duration": 1668.2666,
 *       "similarity": 0.6467470356062347,
 *       "video": "https://api.trace.moe/video/NfKMBIFuQDAeHpurcJPAMbR",
 *       "image": "https://api.trace.moe/image/NfKMBIFuQDAeHpurcJPAMbR"
 *     },
 *     {
 *       "anilist": 99088,
 *       "filename": "Pluto.S01E06.1080p.WEB.h264-QUiNTESSENCE.mp4",
 *       "episode": 6,
 *       "from": 1776.191,
 *       "at": 1776.191,
 *       "to": 1777.3589,
 *       "duration": 3602.72,
 *       "similarity": 0.6461268406288296,
 *       "video": "https://api.trace.moe/video/IhFJYbQ7qlJ0nXhUPMSFdY9L",
 *       "image": "https://api.trace.moe/image/IhFJYbQ7qlJ0nXhUPMSFdY9L"
 *     },
 *     {
 *       "anilist": 4720,
 *       "filename": "[SumiSora][White_Album][BDRip][03][GB][1920x1080][x264_aac].mp4",
 *       "episode": 3,
 *       "from": 287.3957,
 *       "at": 287.5292,
 *       "to": 287.5626,
 *       "duration": 1476.053,
 *       "similarity": 0.5924404069489124,
 *       "video": "https://api.trace.moe/video/oQQJiFiqjs8ugIQyCvOVVhd",
 *       "image": "https://api.trace.moe/image/oQQJiFiqjs8ugIQyCvOVVhd"
 *     },
 *     {
 *       "anilist": 5114,
 *       "filename": "[WLGO&PPX][Fullmetal_Alchemist_Brotherhood][01v3][BIG5][720P][BA0CD359].mp4",
 *       "episode": 1,
 *       "from": 438.2705,
 *       "at": 438.2705,
 *       "to": 438.2705,
 *       "duration": 1469.866,
 *       "similarity": 0.5815584070542279,
 *       "video": "https://api.trace.moe/video/15rryvEz9mpGxMUzuc5TxiM",
 *       "image": "https://api.trace.moe/image/15rryvEz9mpGxMUzuc5TxiM"
 *     },
 *     {
 *       "anilist": 21175,
 *       "filename": "[HYSUB]Dragon Ball Super[06][BIG5_MP4][1280X720].mp4",
 *       "episode": 6,
 *       "from": 490.4896,
 *       "at": 490.6981,
 *       "to": 491.4906,
 *       "duration": 1404.877,
 *       "similarity": 0.5776130227481617,
 *       "video": "https://api.trace.moe/video/CTVLGqSmfx2fgK4zrPJEOW1",
 *       "image": "https://api.trace.moe/image/CTVLGqSmfx2fgK4zrPJEOW1"
 *     },
 *     {
 *       "anilist": 5114,
 *       "filename": "[WLGO][Fullmetal_Alchemist_Brotherhood][16][BIG5][720P][151963FF].mp4",
 *       "episode": 16,
 *       "from": 843.5086,
 *       "at": 843.5503,
 *       "to": 843.5503,
 *       "duration": 1469.994,
 *       "similarity": 0.5742897781671262,
 *       "video": "https://api.trace.moe/video/ZwRtfPvZueKqjDs6oXhuiz0",
 *       "image": "https://api.trace.moe/image/ZwRtfPvZueKqjDs6oXhuiz0"
 *     },
 *     {
 *       "anilist": 114340,
 *       "filename": "[Ohys-Raws] Kuma Kuma Kuma Bear - 01 (AT-X 1280x720 x264 AAC).mp4",
 *       "episode": 1,
 *       "from": 114.9065,
 *       "at": 115.1567,
 *       "to": 115.1984,
 *       "duration": 1440.128,
 *       "similarity": 0.5667937035654106,
 *       "video": "https://api.trace.moe/video/px9D5OmPt2bVTedjmvELF89",
 *       "image": "https://api.trace.moe/image/px9D5OmPt2bVTedjmvELF89"
 *     },
 *     {
 *       "anilist": 20954,
 *       "filename": "[ReinForce] Eiga Koe no Katachi (BDRip 1920x1036 x264 FLAC).mp4",
 *       "episode": null,
 *       "from": 7041.2007,
 *       "at": 7043.3696,
 *       "to": 7048.7085,
 *       "duration": 7782.775,
 *       "similarity": 0.5630115883023131,
 *       "video": "https://api.trace.moe/video/TyTpkppluj1ZgUiotjREjPbD",
 *       "image": "https://api.trace.moe/image/TyTpkppluj1ZgUiotjREjPbD"
 *     },
 *     {
 *       "anilist": 406,
 *       "filename": "[ktxp][BUZZER BEATER][07][jap_chn].mp4",
 *       "episode": 7,
 *       "from": 456.3333,
 *       "at": 456.4667,
 *       "to": 456.6,
 *       "duration": 1515.798,
 *       "similarity": 0.5618631101122089,
 *       "video": "https://api.trace.moe/video/aGQKNKUHEsaGQB7SsxwqSq2",
 *       "image": "https://api.trace.moe/image/aGQKNKUHEsaGQB7SsxwqSq2"
 *     },
 *     {
 *       "anilist": 10033,
 *       "filename": "[Dymy][Toriko][51SP][BIG5][1024x576].mp4",
 *       "episode": null,
 *       "from": 2179.8916,
 *       "at": 2180.3921,
 *       "to": 2180.3921,
 *       "duration": 2794.893,
 *       "similarity": 0.5449687284581801,
 *       "video": "https://api.trace.moe/video/mKedZsnjdATP7N3jhol5zmBi",
 *       "image": "https://api.trace.moe/image/mKedZsnjdATP7N3jhol5zmBi"
 *     }
 *   ]
 * }
 */
@Composable
private fun ReceiveScreenSubjectDialog(
    state: ReceiveState,
    onUiEvent: (ReceiveEvent.UI) -> Unit,
    onActionEvent: (ReceiveEvent.Action) -> Unit,
) {
    val bottomSheetState = rememberModalBottomSheetState(true) { it != SheetValue.Hidden }
    val scope = rememberCoroutineScope()
    if (state.resultSubject.isNotEmpty()) {
        ModalBottomSheet(
            onDismissRequest = { onActionEvent(ReceiveEvent.Action.OnDismissResultDialog) },
            sheetState = bottomSheetState,
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(LayoutPadding),
                    contentPadding = PaddingValues(LayoutPadding)
                ) {
                    items(state.resultSubject) {
                        DetectSubjectItem(it, state, onUiEvent, onActionEvent)
                    }
                }

                FloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(LayoutPadding)
                        .navigationBarsPadding(),
                    onClick = {
                        scope.launch {
                            bottomSheetState.hide()
                            onActionEvent(ReceiveEvent.Action.OnDismissResultDialog)
                        }
                    }
                ) {
                    Icon(BgmIcons.Close, stringResource(Res.string.global_close))
                }
            }
        }
    }
}


@Composable
private fun ReceiveScreenCharacterDialog(
    state: ReceiveState,
    onUiEvent: (ReceiveEvent.UI) -> Unit,
    onActionEvent: (ReceiveEvent.Action) -> Unit,
) {
    val bottomSheetState = rememberModalBottomSheetState(true) { it != SheetValue.Hidden }
    val scope = rememberCoroutineScope()
    if (state.resultCharacter.isNotEmpty()) {
        ModalBottomSheet(
            modifier = Modifier
                .fillMaxHeight()
                .statusBarsPadding()
                .padding(top = 100.dp),
            sheetState = bottomSheetState,
            onDismissRequest = { onActionEvent(ReceiveEvent.Action.OnDismissResultDialog) },
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(LayoutPadding),
                    contentPadding = PaddingValues(LayoutPadding)
                ) {
                    items(state.resultCharacter) {
                        DetectCharacterItem(it, state, onUiEvent, onActionEvent)
                    }
                }

                FloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(LayoutPadding)
                        .navigationBarsPadding(),
                    onClick = {
                        scope.launch {
                            bottomSheetState.hide()
                            onActionEvent(ReceiveEvent.Action.OnDismissResultDialog)
                        }
                    }
                ) {
                    Icon(BgmIcons.Close, stringResource(Res.string.global_close))
                }
            }
        }
    }
}

@Composable
private fun DetectSubjectItem(
    item: ComposeTraceMoe.Result,
    state: ReceiveState,
    onUiEvent: (ReceiveEvent.UI) -> Unit,
    onActionEvent: (ReceiveEvent.Action) -> Unit,
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onUiEvent(ReceiveEvent.UI.OnNavScreen(Screen.SearchResult(item.anilist.title.displayName))) }
    ) {
        Spacer(Modifier.height(LayoutPadding))

        Text(
            modifier = Modifier.padding(horizontal = LayoutPadding),
            text = item.anilist.title.displayName,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(LayoutPaddingHalf))
        Text(
            modifier = Modifier.padding(horizontal = LayoutPadding),
            text = item.filename,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(LayoutPadding))

        HorizontalDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LayoutPadding)
        ) {
            StateImage(
                modifier = Modifier
                    .width(150.dp)
                    .aspectRatio(16 / 9f),
                model = item.image,
                shape = MaterialTheme.shapes.small
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = LayoutPadding, vertical = LayoutPaddingHalf),
                verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf, Alignment.CenterVertically)
            ) {
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(text = "Ep." + item.episode)
                    Text(text = "匹配度：" + (item.similarity * 100).toFixed(1) + "%")
                    Text(text = item.from.roundToLong().formatHMS() + " - " + item.to.roundToLong().formatHMS())
                }
            }
        }
    }
}


@Composable
private inline fun DetectCharacterItem(
    item: ComposeTraceCharacter.Data,
    state: ReceiveState,
    crossinline onUiEvent: (ReceiveEvent.UI) -> Unit,
    crossinline onActionEvent: (ReceiveEvent.Action) -> Unit,
) {
    Row(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.surfaceContainer,
                MaterialTheme.shapes.small
            )
            .padding(horizontal = LayoutPadding)
            .padding(bottom = LayoutPadding, top = LayoutPaddingHalf),
        horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
    ) {
        ClippedImage(
            modifier = Modifier
                .padding(top = 12.dp)
                .clip(MaterialTheme.shapes.small)
                .size(56.dp)
                .background(MaterialTheme.colorScheme.surfaceContainer),
            source = state.currentImageBitmap,
            rect = item.rememberRect(state.currentImageBitmap)
        )
        Column(verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)) {
            val spanStyle = SpanStyle(color = MaterialTheme.colorScheme.primary)

            item.character?.fastForEach { character ->
                var expanded by remember { mutableStateOf(false) }
                val clipboard = LocalClipboard.current
                val scope = rememberCoroutineScope()

                Box {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.small)
                            .clickable { expanded = true }
                            .padding(LayoutPaddingHalf),
                        text = buildAnnotatedString {
                            withStyle(spanStyle) { append("人物：") }
                            append(character.character.orEmpty())
                            appendLine()
                            withStyle(spanStyle) { append("动画：") }
                            append(character.work.orEmpty())
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Normal
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        state.itemActions.forEachIndexed { index, title ->
                            DropdownMenuItem(
                                onClick = {
                                    when (state.itemActions[index].type) {
                                        0 -> onUiEvent(
                                            ReceiveEvent.UI.OnNavScreen(
                                                Screen.SearchResult(character.character.orEmpty())
                                            )
                                        )

                                        1 -> onUiEvent(
                                            ReceiveEvent.UI.OnNavScreen(
                                                Screen.SearchResult(character.work.orEmpty())
                                            )
                                        )

                                        2 -> scope.launch {
                                            clipboard.setClipEntry(
                                                System.createClipEntry(character.character.orEmpty())
                                            )
                                        }

                                        3 -> scope.launch {
                                            clipboard.setClipEntry(
                                                System.createClipEntry(character.work.orEmpty())
                                            )
                                        }
                                    }
                                    expanded = false
                                },
                                text = { Text(title.displayText()) }
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun RecognizingButton(
    isRecognizing: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Button(
        modifier = modifier.alpha(if (isRecognizing) alpha else 1f),
        contentPadding = PaddingValues(vertical = 12.dp),
        onClick = onClick,
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isRecognizing) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.primary
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (isRecognizing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 1.dp
                )
                Text(
                    text = stringResource(Res.string.image_detect_running),
                    style = MaterialTheme.typography.bodyLarge,
                )
            } else {
                Text(
                    text = stringResource(Res.string.image_detect_start),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}


@Composable
@Preview
fun PreView() {
    MaterialTheme {
        Text(text = "Test")
    }
}




