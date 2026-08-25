package com.xiaoyv.bangumi.shared.component

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.*
import platform.EAGL.*
import platform.GLKit.*
import platform.UIKit.*
import platform.CoreGraphics.*
import platform.darwin.NSObject
import platform.Foundation.*
import platform.QuartzCore.CADisplayLink
import com.xiaoyv.bangumi.shared.component.live2d.*

@Stable
actual class Live2DState actual constructor(actual var workDir: String) {
    var nativeHandle: Live2DHandle? = null
        private set

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
    internal var glkDelegate: Live2DGLKViewDelegate? = null

    init {
        ensureNativeCreated()
    }

    fun ensureNativeCreated() {
        if (nativeHandle == null) {
            val handle = live2d_create()
            nativeHandle = handle
            currentModelInfo?.let { (zip, name) ->
                if (handle != null) {
                    val ok = live2d_load_model_from_zip(handle, zip, workDir, name)
                    this._isLoading = false
                    this._isLoaded = ok
                    this._availableMotions = getMotionsInternal(handle)
                    this._availableExpressions = getExpressionsInternal(handle)
                }
            }
        }
    }

    actual fun loadModel(zipFilePath: String, modelName: String) {
        val cleanZip = zipFilePath.trim()
        val cleanName = modelName.trim()
        this._modelName = cleanName
        this._isLoading = true
        this._isLoaded = false
        currentModelInfo = Pair(cleanZip, cleanName)

        ensureNativeCreated()
        val handle = nativeHandle ?: return
        val ok = live2d_load_model_from_zip(handle, cleanZip, workDir, cleanName)
        this._isLoading = false
        this._isLoaded = ok
        this._availableMotions = getMotionsInternal(handle)
        this._availableExpressions = getExpressionsInternal(handle)
    }

    actual fun setMotion(group: String, index: Int) {
        _currentMotion = group
        val handle = nativeHandle ?: return
        live2d_set_motion(handle, group, index)
    }

    actual fun setExpression(expressionId: String) {
        _currentExpression = expressionId
        val handle = nativeHandle ?: return
        live2d_set_expression(handle, expressionId)
    }

    actual fun hitTest(x: Float, y: Float): String? {
        val handle = nativeHandle ?: return null
        val ptr = live2d_hit_test(handle, x, y)
        val hit = ptr?.toKString()
        return if (hit.isNullOrEmpty()) null else hit
    }

    fun onTouch(x: Float, y: Float, phase: Int) {
        val handle = nativeHandle ?: return
        live2d_on_touch(handle, x, y, phase)
    }

    fun onDrag(x: Float, y: Float) {
        val handle = nativeHandle ?: return
        live2d_on_drag(handle, x, y)
    }

    fun resetDrag() {
        val handle = nativeHandle ?: return
        live2d_reset_drag(handle)
    }

    private fun getMotionsInternal(handle: Live2DHandle): List<String> {
        val count = live2d_get_motion_count(handle)
        val list = mutableListOf<String>()
        for (i in 0 until count) {
            val ptr = live2d_get_motion_group_at(handle, i)
            ptr?.toKString()?.let { list.add(it) }
        }
        return list
    }

    private fun getExpressionsInternal(handle: Live2DHandle): List<String> {
        val count = live2d_get_expression_count(handle)
        val list = mutableListOf<String>()
        for (i in 0 until count) {
            val ptr = live2d_get_expression_id_at(handle, i)
            ptr?.toKString()?.let { list.add(it) }
        }
        return list
    }

    actual fun getMotions(): List<String> {
        return availableMotions.ifEmpty {
            val handle = nativeHandle ?: return emptyList()
            getMotionsInternal(handle)
        }
    }

    actual fun getExpressions(): List<String> {
        return availableExpressions.ifEmpty {
            val handle = nativeHandle ?: return emptyList()
            getExpressionsInternal(handle)
        }
    }

    fun release() {
        glkDelegate = null
        nativeHandle?.let {
            live2d_destroy(it)
            nativeHandle = null
        }
        _isLoading = false
        _isLoaded = false
    }
}

internal class Live2DGLKViewDelegate(private val state: Live2DState) : NSObject(), GLKViewDelegateProtocol {
    override fun glkView(view: GLKView, drawInRect: CValue<CGRect>) {
        val handle = state.nativeHandle ?: return
        val width = view.drawableWidth.toInt()
        val height = view.drawableHeight.toInt()
        if (width > 0 && height > 0) {
            live2d_on_surface_changed(handle, width, height)
        }
        live2d_on_draw_frame(handle)
    }
}

@Composable
actual fun Live2D(
    modifier: Modifier,
    state: Live2DState,
    onHitAreaClick: ((hitArea: String) -> Unit)?
) {
    SideEffect {
        if (onHitAreaClick != null) {
            state.onHitAreaClick = onHitAreaClick
        }
    }

    Box(
        modifier = modifier
            .pointerInput(state) {
                detectTapGestures(
                    onPress = { offset ->
                        state.onTouch(offset.x, offset.y, 0)
                        state.onDrag(offset.x, offset.y)
                        val released = tryAwaitRelease()
                        if (released) {
                            state.resetDrag()
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
                        state.onDrag(change.position.x, change.position.y)
                    },
                    onDragEnd = {
                        state.resetDrag()
                    },
                    onDragCancel = {
                        state.resetDrag()
                    }
                )
            }
    ) {
        UIKitView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                state.ensureNativeCreated()
                val eaglContext = EAGLContext(kEAGLRenderingAPIOpenGLES2)
                val glkView = GLKView(frame = CGRectZero.readValue(), context = eaglContext)
                glkView.backgroundColor = UIColor.clearColor
                glkView.drawableDepthFormat = GLKViewDrawableDepthFormat16

                EAGLContext.setCurrentContext(eaglContext)
                state.nativeHandle?.let { handle ->
                    live2d_on_surface_created(handle)
                }

                val delegate = Live2DGLKViewDelegate(state)
                state.glkDelegate = delegate
                glkView.delegate = delegate

                val displayLink = CADisplayLink.displayLinkWithTarget(
                    target = glkView,
                    selector = NSSelectorFromString("setNeedsDisplay")
                )
                displayLink.addToRunLoop(NSRunLoop.currentRunLoop, NSRunLoopCommonModes)

                glkView
            },
            onRelease = {
                state.release()
            }
        )
    }
}
