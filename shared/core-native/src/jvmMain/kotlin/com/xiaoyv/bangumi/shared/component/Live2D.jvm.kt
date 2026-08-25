package com.xiaoyv.bangumi.shared.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.isActive
import java.awt.image.BufferedImage

@Stable
actual class Live2DState actual constructor(actual var workDir: String) {
    val bridge = Live2DNativeBridge()

    private var currentModelInfo: Pair<String, String>? = null
    var isSurfaceCreated = true
        private set

    actual fun loadModel(zipFilePath: String, modelName: String) {
        val cleanZip = zipFilePath.trim()
        val cleanName = modelName.trim()

        currentModelInfo = Pair(cleanZip, cleanName)
        checkAndExecutePendingLoad()
    }

    private fun checkAndExecutePendingLoad() {
        val info = currentModelInfo ?: return
        val (zip, name) = info
        bridge.loadModelFromZip(zip, workDir, name)
    }

    actual fun setMotion(group: String, index: Int) {
        bridge.setMotion(group, index)
    }

    actual fun setExpression(expressionId: String) {
        bridge.setExpression(expressionId)
    }

    actual fun getMotions(): List<String> {
        return bridge.getMotions()
    }

    actual fun getExpressions(): List<String> {
        return bridge.getExpressions()
    }

    fun onTouch(x: Float, y: Float, phase: Int) {
        bridge.onTouch(x, y, phase)
    }

    fun release() {
        bridge.destroy()
    }
}

@Composable
actual fun Live2D(
    modifier: Modifier,
    state: Live2DState
) {
    val density = LocalDensity.current
    var frameBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    DisposableEffect(state) {
        onDispose {
            state.release()
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .pointerInput(state) {
                detectTapGestures(
                    onPress = { offset ->
                        state.onTouch(offset.x, offset.y, 0)
                        tryAwaitRelease()
                        state.onTouch(offset.x, offset.y, 2)
                    }
                )
            }
            .pointerInput(state) {
                detectDragGestures(
                    onDrag = { change, _ ->
                        state.onTouch(change.position.x, change.position.y, 1)
                    }
                )
            }
    ) {
        val widthPx = with(density) { constraints.maxWidth.coerceAtLeast(1) }
        val heightPx = with(density) { constraints.maxHeight.coerceAtLeast(1) }

        LaunchedEffect(state, widthPx, heightPx) {
            val pixelCount = widthPx * heightPx
            val pixelBuffer = IntArray(pixelCount)

            val currentImage = BufferedImage(widthPx, heightPx, BufferedImage.TYPE_INT_ARGB_PRE)

            while (isActive) {
                val ok = state.bridge.renderPixels(widthPx, heightPx, pixelBuffer)
                if (ok) {
                    currentImage.setRGB(0, 0, widthPx, heightPx, pixelBuffer, 0, widthPx)
                    frameBitmap = currentImage.toComposeImageBitmap()
                }
                withFrameNanos { }
            }
        }

        frameBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap,
                contentDescription = "Live2D Canvas",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
