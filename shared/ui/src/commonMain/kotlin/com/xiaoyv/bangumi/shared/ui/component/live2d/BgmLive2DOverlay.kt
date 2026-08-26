package com.xiaoyv.bangumi.shared.ui.component.live2d

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventTimeoutCancellationException
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.shared.component.Live2D
import com.xiaoyv.bangumi.shared.component.rememberLive2DState
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSetting
import com.xiaoyv.bangumi.shared.resource.copyToDir
import com.xiaoyv.bangumi.shared.ui.theme.currentInDarkTheme
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.createDirectories
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.filesDir
import kotlin.math.roundToInt

@Composable
fun BoxScope.BgmLive2DOverlay(
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
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    val live2DState = rememberLive2DState()

    Box(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .systemBarsPadding()
            .padding(bottom = 80.dp)
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(live2DState) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                    val pointerId = down.id

                    println("[Live2D-Overlay] DOWN detected at pos=(${down.position.x}, ${down.position.y}), id=$pointerId")

                    live2DState.onTouch(down.position.x, down.position.y, 0)

                    var isLongPress = false
                    var isDrag = false

                    val longPressResult = try {
                        withTimeout(viewConfiguration.longPressTimeoutMillis) {
                            println("[Live2D-Overlay] Waiting for long press timeout (${viewConfiguration.longPressTimeoutMillis}ms)... touchSlop=${viewConfiguration.touchSlop}")
                            while (true) {
                                val event = awaitPointerEvent(PointerEventPass.Initial)
                                val change = event.changes.firstOrNull { it.id == pointerId } ?: break

                                if (!change.pressed) {
                                    println("[Live2D-Overlay] Long press CANCELLED: finger released before timeout")
                                    return@withTimeout false
                                }

                                val distance = (change.position - down.position).getDistance()
                                if (distance > viewConfiguration.touchSlop) {
                                    println("[Live2D-Overlay] Long press CANCELLED: moved distance=$distance > touchSlop=${viewConfiguration.touchSlop}")
                                    return@withTimeout false
                                }
                            }
                            false
                        }
                    } catch (e: PointerEventTimeoutCancellationException) {
                        println("[Live2D-Overlay] Long press TIMEOUT EXCEPTION caught! Long press SUCCESS!")
                        true
                    }

                    if (longPressResult) {
                        isLongPress = true
                        down.consume()
                        live2DState.resetDrag()
                        println("[Live2D-Overlay] Starting container position drag loop...")
                    }

                    if (isLongPress) {
                        try {
                            while (true) {
                                val event = awaitPointerEvent(PointerEventPass.Initial)
                                val change = event.changes.firstOrNull { it.id == pointerId } ?: break

                                if (!change.pressed) {
                                    println("[Live2D-Overlay] Container position drag FINISHED: finger released")
                                    break
                                }

                                val dragAmount = change.positionChange()
                                change.consume()

                                offsetX += dragAmount.x
                                offsetY += dragAmount.y
                                println("[Live2D-Overlay] Dragging container: dx=${dragAmount.x}, dy=${dragAmount.y} -> newOffset=($offsetX, $offsetY)")
                            }
                        } finally {
                            live2DState.resetDrag()
                        }
                    } else {
                        println("[Live2D-Overlay] Entering normal touch / model drag loop...")
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == pointerId } ?: break

                            if (!change.pressed) {
                                println("[Live2D-Overlay] Normal touch RELEASED: isDrag=$isDrag")
                                live2DState.onTouch(change.position.x, change.position.y, 2)
                                live2DState.resetDrag()

                                if (!isDrag) {
                                    val hit = live2DState.hitTest(change.position.x, change.position.y)
                                    println("[Live2D-Overlay] Hit test result: $hit")
                                    if (!hit.isNullOrEmpty()) {
                                        live2DState.onHitAreaClick?.invoke(hit)
                                    }
                                }
                                break
                            }

                            val distance = (change.position - down.position).getDistance()
                            if (distance > viewConfiguration.touchSlop) {
                                isDrag = true
                                live2DState.onTouch(change.position.x, change.position.y, 1)
                                live2DState.onDrag(change.position.x, change.position.y)
                            }
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
