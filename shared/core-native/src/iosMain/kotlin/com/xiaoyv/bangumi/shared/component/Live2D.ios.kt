package com.xiaoyv.bangumi.shared.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
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
    var nativeHandle: Live2DHandle? = live2d_create()
        private set

    internal var glkDelegate: Live2DGLKViewDelegate? = null

    actual fun loadModel(zipFilePath: String, modelName: String) {
        val handle = nativeHandle ?: return
        live2d_load_model_from_zip(handle, zipFilePath, workDir, modelName)
    }

    actual fun setMotion(group: String, index: Int) {
        val handle = nativeHandle ?: return
        live2d_set_motion(handle, group, index)
    }

    actual fun setExpression(expressionId: String) {
        val handle = nativeHandle ?: return
        live2d_set_expression(handle, expressionId)
    }

    actual fun getMotions(): List<String> {
        val handle = nativeHandle ?: return emptyList()
        val count = live2d_get_motion_count(handle)
        val list = mutableListOf<String>()
        for (i in 0 until count) {
            val ptr = live2d_get_motion_group_at(handle, i)
            ptr?.toKString()?.let { list.add(it) }
        }
        return list
    }

    actual fun getExpressions(): List<String> {
        val handle = nativeHandle ?: return emptyList()
        val count = live2d_get_expression_count(handle)
        val list = mutableListOf<String>()
        for (i in 0 until count) {
            val ptr = live2d_get_expression_id_at(handle, i)
            ptr?.toKString()?.let { list.add(it) }
        }
        return list
    }

    fun release() {
        glkDelegate = null
        nativeHandle?.let {
            live2d_destroy(it)
            nativeHandle = null
        }
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
    state: Live2DState
) {
    UIKitView(
        modifier = modifier,
        factory = {
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
