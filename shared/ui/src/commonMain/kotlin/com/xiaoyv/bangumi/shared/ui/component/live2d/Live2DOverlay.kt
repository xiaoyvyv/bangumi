package com.xiaoyv.bangumi.shared.ui.component.live2d

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.shared.component.Live2D
import com.xiaoyv.bangumi.shared.component.live2DOverlayGesture
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
fun BoxScope.Live2DOverlay(
    state: Live2DSpeechState,
    live2dConfig: ComposeSetting.Live2dConfig
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

    // 自动拉取看板娘服务端语料库
    LaunchedEffect(Unit) {
        state.fetchSpeeches(curPsn = 1L)
    }

    Box(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .systemBarsPadding()
            .padding(bottom = 80.dp, end = 12.dp)
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
    ) {
        Column(horizontalAlignment = Alignment.End) {
            // 说话气泡紧贴角色头部
            Live2DSpeechBubble(
                modifier = Modifier,
                state = state
            )

            // Live2D 角色区域
            Box(
                modifier = Modifier
                    .live2DOverlayGesture(
                        live2DState = state.state,
                        onDragOffset = { dx, dy ->
                            offsetX += dx
                            offsetY += dy
                        },
                        onClick = { _ ->
                            state.speakRandom()
                            true
                        }
                    )
                    .width(live2dConfig.size.dp)
                    .aspectRatio(202 / 308f)
            ) {
                Live2D(
                    modifier = Modifier.fillMaxSize(),
                    state = state.state
                )
            }
        }
    }

    LaunchedEffect(modelName, state) {
        val workDir = (FileKit.filesDir / "live2d").also {
            it.createDirectories()
        }

        val targetFile = Res.copyToDir(resourcePath = "files/live2d/$modelName.zip", workDir)

        state.state.workDir = workDir.absolutePath()
        state.state.loadModel(targetFile.absolutePath(), modelName)
    }
}
