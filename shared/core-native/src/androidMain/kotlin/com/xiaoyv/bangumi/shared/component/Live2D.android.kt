package com.xiaoyv.bangumi.shared.component

import android.content.Context
import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.MotionEvent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

private const val TAG = "Live2DState"

@Stable
actual class Live2DState actual constructor() {
    val bridge = Live2DNativeBridge()
    var glSurfaceView: Live2DGLSurfaceView? = null

    private var pendingLoad: Pair<String, String>? = null
    var isSurfaceCreated = false
        private set

    actual fun loadModel(modelDir: String, modelJsonName: String) {
        var cleanDir = modelDir.trim()
        var cleanJson = modelJsonName.trim()

        if (cleanDir.endsWith(".json", ignoreCase = true)) {
            val lastSlash = cleanDir.lastIndexOf('/')
            if (lastSlash != -1) {
                cleanJson = cleanDir.substring(lastSlash + 1)
                cleanDir = cleanDir.substring(0, lastSlash)
            }
        }

        if (cleanJson.isEmpty()) {
            val lastSlash = cleanDir.lastIndexOf('/')
            if (lastSlash != -1) {
                val folderName = cleanDir.substring(lastSlash + 1)
                cleanJson = "$folderName.model3.json"
            }
        }

        Log.d(TAG, "loadModel requested: dir=$cleanDir, json=$cleanJson, isSurfaceCreated=$isSurfaceCreated")
        pendingLoad = Pair(cleanDir, cleanJson)
        checkAndExecutePendingLoad()
    }

    fun attachView(view: Live2DGLSurfaceView) {
        glSurfaceView = view
        checkAndExecutePendingLoad()
    }

    fun onSurfaceCreatedOnGLThread() {
        isSurfaceCreated = true
        Log.d(TAG, "onSurfaceCreatedOnGLThread triggered")
        checkAndExecutePendingLoad()
    }

    private fun checkAndExecutePendingLoad() {
        val view = glSurfaceView ?: return
        val pending = pendingLoad ?: return
        if (!isSurfaceCreated) {
            Log.d(TAG, "Postponing model load until GL Surface is fully created")
            return
        }

        val (dir, json) = pending
        pendingLoad = null
        Log.d(TAG, "Executing queued bridge.loadModel: dir=$dir, json=$json")
        view.queueEvent {
            bridge.loadModel(dir, json)
        }
    }

    fun detachView() {
        glSurfaceView = null
        isSurfaceCreated = false
    }

    actual fun setMotion(group: String, index: Int) {
        glSurfaceView?.queueEvent {
            bridge.setMotion(group, index)
        }
    }

    actual fun setExpression(expressionId: String) {
        glSurfaceView?.queueEvent {
            bridge.setExpression(expressionId)
        }
    }

    actual fun getMotions(): List<String> {
        return bridge.getMotions()
    }

    actual fun getExpressions(): List<String> {
        return bridge.getExpressions()
    }

    fun release() {
        glSurfaceView?.queueEvent {
            bridge.destroy()
        }
        glSurfaceView = null
        isSurfaceCreated = false
    }
}

class Live2DGLSurfaceView(context: Context, private val state: Live2DState) : GLSurfaceView(context) {
    init {
        state.bridge.setAssetManager(context.assets)

        setEGLContextClientVersion(2)
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        holder.setFormat(PixelFormat.TRANSLUCENT)
        setZOrderOnTop(true)

        setRenderer(object : Renderer {
            override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
                Log.d(TAG, "GLSurfaceView Renderer onSurfaceCreated")
                state.bridge.onSurfaceCreated()
                state.onSurfaceCreatedOnGLThread()
            }

            override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
                Log.d(TAG, "GLSurfaceView Renderer onSurfaceChanged: ${width}x${height}")
                state.bridge.onSurfaceChanged(width, height)
            }

            override fun onDrawFrame(gl: GL10?) {
                state.bridge.onDrawFrame()
            }
        })
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> queueEvent { state.bridge.onTouch(event.x, event.y, 0) }
            MotionEvent.ACTION_MOVE -> queueEvent { state.bridge.onTouch(event.x, event.y, 1) }
            MotionEvent.ACTION_UP -> queueEvent { state.bridge.onTouch(event.x, event.y, 2) }
        }
        return true
    }
}

@Composable
actual fun Live2D(
    modifier: Modifier,
    state: Live2DState
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            Live2DGLSurfaceView(context, state).also { view ->
                state.attachView(view)
            }
        },
        onRelease = {
            state.detachView()
            state.release()
        }
    )
}
