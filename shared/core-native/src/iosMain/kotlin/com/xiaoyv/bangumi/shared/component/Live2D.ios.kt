package com.xiaoyv.bangumi.shared.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import com.xiaoyv.bangumi.shared.component.live2d.Live2DHandle
import com.xiaoyv.bangumi.shared.component.live2d.live2d_create
import com.xiaoyv.bangumi.shared.component.live2d.live2d_destroy
import com.xiaoyv.bangumi.shared.component.live2d.live2d_get_expression_count
import com.xiaoyv.bangumi.shared.component.live2d.live2d_get_expression_id_at
import com.xiaoyv.bangumi.shared.component.live2d.live2d_get_motion_count
import com.xiaoyv.bangumi.shared.component.live2d.live2d_get_motion_group_at
import com.xiaoyv.bangumi.shared.component.live2d.live2d_hit_test
import com.xiaoyv.bangumi.shared.component.live2d.live2d_load_model_from_zip
import com.xiaoyv.bangumi.shared.component.live2d.live2d_on_drag
import com.xiaoyv.bangumi.shared.component.live2d.live2d_on_draw_frame
import com.xiaoyv.bangumi.shared.component.live2d.live2d_on_surface_changed
import com.xiaoyv.bangumi.shared.component.live2d.live2d_on_touch
import com.xiaoyv.bangumi.shared.component.live2d.live2d_reset_drag
import com.xiaoyv.bangumi.shared.component.live2d.live2d_set_expression
import com.xiaoyv.bangumi.shared.component.live2d.live2d_set_motion
import kotlinx.cinterop.CValue
import kotlinx.cinterop.readValue
import kotlinx.cinterop.toKString
import platform.CoreGraphics.CGRect
import platform.CoreGraphics.CGRectZero
import platform.EAGL.EAGLContext
import platform.EAGL.kEAGLRenderingAPIOpenGLES2
import platform.Foundation.NSRunLoop
import platform.Foundation.NSRunLoopCommonModes
import platform.Foundation.NSSelectorFromString
import platform.GLKit.GLKView
import platform.GLKit.GLKViewDelegateProtocol
import platform.GLKit.GLKViewDrawableDepthFormat16
import platform.QuartzCore.CADisplayLink
import platform.UIKit.UIColor
import platform.darwin.NSObject

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

    private var currentModelInfo: Pair<String, String>? = null
    internal var glkDelegate: Live2DGLKViewDelegate? = null
    internal var eaglContext: EAGLContext? = null
    internal var displayLink: CADisplayLink? = null

    init {
        ensureNativeCreated()
    }

    fun ensureNativeCreated() {
        if (nativeHandle == null) {
            val handle = live2d_create()
            nativeHandle = handle
            currentModelInfo?.let { (zip, name) ->
                if (handle != null) {
                    eaglContext?.let { EAGLContext.setCurrentContext(it) }
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
        eaglContext?.let { EAGLContext.setCurrentContext(it) }
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

    actual fun onTouch(x: Float, y: Float, action: Int) {
        val handle = nativeHandle ?: return
        live2d_on_touch(handle, x, y, action)
    }

    actual fun onDrag(x: Float, y: Float) {
        val handle = nativeHandle ?: return
        live2d_on_drag(handle, x, y)
    }

    actual fun resetDrag() {
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
        displayLink?.invalidate()
        displayLink = null
        glkDelegate = null
        eaglContext = null
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
        EAGLContext.setCurrentContext(view.context)
        state.ensureNativeCreated()
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
) {
    UIKitView(
        factory = {
            val eaglContext = EAGLContext(kEAGLRenderingAPIOpenGLES2)
            state.eaglContext = eaglContext
            state.ensureNativeCreated()

            val glkView = GLKView(frame = CGRectZero.readValue(), context = eaglContext)
            glkView.userInteractionEnabled = false
            glkView.backgroundColor = UIColor.clearColor
            glkView.drawableDepthFormat = GLKViewDrawableDepthFormat16

            EAGLContext.setCurrentContext(eaglContext)

            val delegate = Live2DGLKViewDelegate(state)
            state.glkDelegate = delegate
            glkView.delegate = delegate

            val displayLink = CADisplayLink.displayLinkWithTarget(
                target = glkView,
                selector = NSSelectorFromString("setNeedsDisplay")
            )
            displayLink.addToRunLoop(NSRunLoop.currentRunLoop(), NSRunLoopCommonModes)
            state.displayLink = displayLink

            glkView
        },
        modifier = modifier,
        onRelease = {
            state.release()
        },
        properties = UIKitInteropProperties(
            interactionMode = null,
            isNativeAccessibilityEnabled = false,
            placedAsOverlay = true,
        )
    )
}
