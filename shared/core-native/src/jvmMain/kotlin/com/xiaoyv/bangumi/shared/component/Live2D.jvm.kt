package com.xiaoyv.bangumi.shared.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.awt.image.BufferedImage

@Stable
actual class Live2DState actual constructor(actual var workDir: String) {
    val bridge = Live2DNativeBridge()

    private var _isLoading by mutableStateOf(false)
    actual val isLoading: Boolean get() = _isLoading

    private var _isLoaded by mutableStateOf(false)
    actual val isLoaded: Boolean get() = _isLoaded

    private var _modelName by mutableStateOf("")
    actual val modelName: String get() = _modelName

    private var _currentMotion by mutableStateOf("")
    actual val currentMotion: String get() = _currentMotion

    private var _currentExpression by mutableStateOf("")
    actual val currentExpression: String get() = _currentExpression

    private var _availableMotions by mutableStateOf(emptyList<String>())
    actual val availableMotions: List<String> get() = _availableMotions

    private var _availableExpressions by mutableStateOf(emptyList<String>())
    actual val availableExpressions: List<String> get() = _availableExpressions

    actual var onHitAreaClick: ((hitArea: String) -> Unit)? = null

    private var currentModelInfo: Pair<String, String>? = null
    var isSurfaceCreated = true
        private set

    actual fun loadModel(zipFilePath: String, modelName: String) {
        val cleanZip = zipFilePath.trim()
        val cleanName = modelName.trim()

        this._modelName = cleanName
        this._isLoading = true
        this._isLoaded = false
        currentModelInfo = Pair(cleanZip, cleanName)
        checkAndExecutePendingLoad()
    }

    private fun checkAndExecutePendingLoad() {
        val info = currentModelInfo ?: return
        val (zip, name) = info
        val success = bridge.loadModelFromZip(zip, workDir, name)
        this._isLoading = false
        this._isLoaded = success
        this._availableMotions = bridge.getMotions()
        this._availableExpressions = bridge.getExpressions()
    }

    actual fun setMotion(group: String, index: Int) {
        _currentMotion = group
        bridge.setMotion(group, index)
    }

    actual fun setExpression(expressionId: String) {
        _currentExpression = expressionId
        bridge.setExpression(expressionId)
    }

    actual fun hitTest(x: Float, y: Float): String? {
        return bridge.hitTest(x, y)
    }

    actual fun getMotions(): List<String> {
        return availableMotions.ifEmpty { bridge.getMotions() }
    }

    actual fun getExpressions(): List<String> {
        return availableExpressions.ifEmpty { bridge.getExpressions() }
    }

    fun onTouch(x: Float, y: Float, phase: Int) {
        bridge.onTouch(x, y, phase)
    }

    fun release() {
        bridge.destroy()
        _isLoading = false
        _isLoaded = false
    }
}

@Composable
actual fun Live2D(
    modifier: Modifier,
    state: Live2DState,
    onHitAreaClick: ((hitArea: String) -> Unit)?
) {
    val density = LocalDensity.current
    var frameBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    SideEffect {
        if (onHitAreaClick != null) {
            state.onHitAreaClick = onHitAreaClick
        }
    }

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
                        state.bridge.onDrag(offset.x, offset.y)
                        val released = tryAwaitRelease()
                        if (released) {
                            state.bridge.resetDrag()
                            state.onTouch(offset.x, offset.y, 2)
                            val hit = state.hitTest(offset.x, offset.y)
                            if (!hit.isNullOrEmpty()) {
                                state.onHitAreaClick?.invoke(hit)
                            }
                        }
                    }
                )
            }
            .pointerInput(state) {
                detectDragGestures(
                    onDrag = { change, _ ->
                        state.onTouch(change.position.x, change.position.y, 1)
                        state.bridge.onDrag(change.position.x, change.position.y)
                    },
                    onDragEnd = {
                        state.bridge.resetDrag()
                    },
                    onDragCancel = {
                        state.bridge.resetDrag()
                    }
                )
            }
            .onPointerEvent(PointerEventType.Move) { event ->
                val position = event.changes.firstOrNull()?.position
                if (position != null) {
                    state.bridge.onDrag(position.x, position.y)
                }
            }
            .onPointerEvent(PointerEventType.Exit) {
                state.bridge.resetDrag()
            }
    ) {
        val widthPx = with(density) { constraints.maxWidth.coerceAtLeast(1) }
        val heightPx = with(density) { constraints.maxHeight.coerceAtLeast(1) }

        LaunchedEffect(state, widthPx, heightPx) {
            val pixelCount = widthPx * heightPx
            val pixelBuffer = IntArray(pixelCount)
            val currentImage = BufferedImage(widthPx, heightPx, BufferedImage.TYPE_INT_ARGB_PRE)

            while (isActive) {
                val ok = withContext(Dispatchers.Default) {
                    state.bridge.renderPixels(widthPx, heightPx, pixelBuffer)
                }
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
