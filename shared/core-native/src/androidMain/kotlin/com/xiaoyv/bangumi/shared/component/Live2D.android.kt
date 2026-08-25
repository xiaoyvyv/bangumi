package com.xiaoyv.bangumi.shared.component

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.EGLContext
import android.opengl.EGLDisplay
import android.opengl.EGLSurface
import android.opengl.GLES20
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.TextureView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean

private const val TAG = "Live2DState"

@Stable
actual class Live2DState actual constructor(actual var workDir: String) {
    val bridge = Live2DNativeBridge()
    var textureView: Live2DTextureView? = null
    private val mainHandler = Handler(Looper.getMainLooper())

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
    var isSurfaceCreated = false
        private set

    actual fun loadModel(zipFilePath: String, modelName: String) {
        val cleanZip = zipFilePath.trim()
        val cleanName = modelName.trim()

        Log.d(TAG, "loadModel requested: zipFilePath=$cleanZip, modelName=$cleanName, workDir=$workDir, isSurfaceCreated=$isSurfaceCreated")
        this._modelName = cleanName
        this._isLoading = true
        this._isLoaded = false
        currentModelInfo = Pair(cleanZip, cleanName)
        checkAndExecutePendingLoad()
    }

    fun attachView(view: Live2DTextureView) {
        textureView = view
        checkAndExecutePendingLoad()
    }

    fun onSurfaceCreatedOnGLThread() {
        isSurfaceCreated = true
        Log.d(TAG, "onSurfaceCreatedOnGLThread triggered")
        checkAndExecutePendingLoad()
    }

    private fun checkAndExecutePendingLoad() {
        val view = textureView ?: return
        val info = currentModelInfo ?: return
        if (!isSurfaceCreated) {
            Log.d(TAG, "Postponing model load until TextureView surface is fully created")
            return
        }

        val (zip, name) = info
        Log.d(TAG, "Executing queued bridge.loadModelFromZip: zip=$zip, workDir=$workDir, modelName=$name")
        view.queueEvent {
            val success = bridge.loadModelFromZip(zip, workDir, name)
            val motions = bridge.getMotions()
            val expressions = bridge.getExpressions()
            mainHandler.post {
                this._isLoading = false
                this._isLoaded = success
                this._availableMotions = motions
                this._availableExpressions = expressions
            }
        }
    }

    fun detachView() {
        textureView = null
        isSurfaceCreated = false
    }

    actual fun setMotion(group: String, index: Int) {
        _currentMotion = group
        textureView?.queueEvent {
            bridge.setMotion(group, index)
        }
    }

    actual fun setExpression(expressionId: String) {
        _currentExpression = expressionId
        textureView?.queueEvent {
            bridge.setExpression(expressionId)
        }
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

    fun release() {
        textureView?.queueEvent {
            bridge.destroy()
        }
        textureView = null
        isSurfaceCreated = false
        _isLoading = false
        _isLoaded = false
    }
}

class Live2DTextureView(
    context: Context,
    private val state: Live2DState
) : TextureView(context), TextureView.SurfaceTextureListener {

    private var renderThread: Live2DRenderThread? = null

    init {
        isOpaque = false
        surfaceTextureListener = this
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        Log.d(TAG, "onSurfaceTextureAvailable: ${width}x${height}")
        renderThread = Live2DRenderThread(surface, width, height, state).apply {
            start()
        }
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        Log.d(TAG, "onSurfaceTextureSizeChanged: ${width}x${height}")
        renderThread?.onSizeChanged(width, height)
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        Log.d(TAG, "onSurfaceTextureDestroyed")
        renderThread?.stopRendering()
        renderThread = null
        return true
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
    }

    fun queueEvent(runnable: () -> Unit) {
        renderThread?.queueEvent(runnable)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> queueEvent { state.bridge.onTouch(x, y, 0) }
            MotionEvent.ACTION_MOVE -> queueEvent { state.bridge.onTouch(x, y, 1) }
            MotionEvent.ACTION_UP -> {
                queueEvent {
                    state.bridge.onTouch(x, y, 2)
                    val hit = state.bridge.hitTest(x, y)
                    if (!hit.isNullOrEmpty()) {
                        post { state.onHitAreaClick?.invoke(hit) }
                    }
                }
            }
        }
        return true
    }
}

private class Live2DRenderThread(
    private val surfaceTexture: SurfaceTexture,
    private var width: Int,
    private var height: Int,
    private val state: Live2DState
) : Thread("Live2DRenderThread") {

    private val isRunning = AtomicBoolean(true)
    private val eventQueue = ConcurrentLinkedQueue<Runnable>()

    private var eglDisplay: EGLDisplay = EGL14.EGL_NO_DISPLAY
    private var eglContext: EGLContext = EGL14.EGL_NO_CONTEXT
    private var eglSurface: EGLSurface = EGL14.EGL_NO_SURFACE

    private var pendingWidth = -1
    private var pendingHeight = -1

    fun queueEvent(block: () -> Unit) {
        eventQueue.add(Runnable { block() })
    }

    fun onSizeChanged(newWidth: Int, newHeight: Int) {
        synchronized(this) {
            pendingWidth = newWidth
            pendingHeight = newHeight
        }
    }

    fun stopRendering() {
        isRunning.set(false)
        try {
            join()
        } catch (e: InterruptedException) {
            currentThread().interrupt()
        }
    }

    override fun run() {
        if (!initEGL()) {
            Log.e(TAG, "Failed to initialize EGL for Live2DTextureView")
            releaseEGL()
            return
        }

        Log.d(TAG, "TextureView GL thread onSurfaceCreated")
        state.bridge.onSurfaceCreated()
        state.onSurfaceCreatedOnGLThread()
        state.bridge.onSurfaceChanged(width, height)

        val targetFrameDurationNs = 1_000_000_000L / 60L

        while (isRunning.get()) {
            val frameStartTime = System.nanoTime()

            var newW = -1
            var newH = -1
            synchronized(this) {
                if (pendingWidth > 0 && pendingHeight > 0) {
                    newW = pendingWidth
                    newH = pendingHeight
                    pendingWidth = -1
                    pendingHeight = -1
                }
            }
            if (newW > 0 && newH > 0 && (newW != width || newH != height)) {
                width = newW
                height = newH
                state.bridge.onSurfaceChanged(width, height)
            }

            while (!eventQueue.isEmpty()) {
                eventQueue.poll()?.run()
            }

            if (eglDisplay != EGL14.EGL_NO_DISPLAY && eglSurface != EGL14.EGL_NO_SURFACE) {
                GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_STENCIL_BUFFER_BIT)

                state.bridge.onDrawFrame()

                EGL14.eglSwapBuffers(eglDisplay, eglSurface)
            }

            val elapsedNs = System.nanoTime() - frameStartTime
            val sleepMs = ((targetFrameDurationNs - elapsedNs) / 1_000_000L)
            if (sleepMs > 0) {
                try {
                    sleep(sleepMs)
                } catch (e: InterruptedException) {
                    break
                }
            }
        }

        // Drain any remaining events (such as bridge.destroy()) before releasing EGL context
        while (!eventQueue.isEmpty()) {
            eventQueue.poll()?.run()
        }

        releaseEGL()
    }

    private fun initEGL(): Boolean {
        eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
        if (eglDisplay == EGL14.EGL_NO_DISPLAY) {
            Log.e(TAG, "eglGetDisplay failed: ${EGL14.eglGetError()}")
            return false
        }

        val version = IntArray(2)
        if (!EGL14.eglInitialize(eglDisplay, version, 0, version, 1)) {
            Log.e(TAG, "eglInitialize failed: ${EGL14.eglGetError()}")
            return false
        }

        val attribList = intArrayOf(
            EGL14.EGL_RED_SIZE, 8,
            EGL14.EGL_GREEN_SIZE, 8,
            EGL14.EGL_BLUE_SIZE, 8,
            EGL14.EGL_ALPHA_SIZE, 8,
            EGL14.EGL_DEPTH_SIZE, 16,
            EGL14.EGL_STENCIL_SIZE, 8,
            EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
            EGL14.EGL_NONE
        )

        val configs = arrayOfNulls<EGLConfig>(1)
        val numConfigs = IntArray(1)
        if (!EGL14.eglChooseConfig(eglDisplay, attribList, 0, configs, 0, configs.size, numConfigs, 0) || numConfigs[0] == 0) {
            Log.e(TAG, "eglChooseConfig failed: ${EGL14.eglGetError()}")
            return false
        }
        val eglConfig = configs[0]

        val contextAttribs = intArrayOf(
            EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
            EGL14.EGL_NONE
        )
        eglContext = EGL14.eglCreateContext(eglDisplay, eglConfig, EGL14.EGL_NO_CONTEXT, contextAttribs, 0)
        if (eglContext == EGL14.EGL_NO_CONTEXT) {
            Log.e(TAG, "eglCreateContext failed: ${EGL14.eglGetError()}")
            return false
        }

        val surfaceAttribs = intArrayOf(
            EGL14.EGL_NONE
        )
        eglSurface = EGL14.eglCreateWindowSurface(eglDisplay, eglConfig, surfaceTexture, surfaceAttribs, 0)
        if (eglSurface == EGL14.EGL_NO_SURFACE) {
            Log.e(TAG, "eglCreateWindowSurface failed: ${EGL14.eglGetError()}")
            return false
        }

        if (!EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
            Log.e(TAG, "eglMakeCurrent failed: ${EGL14.eglGetError()}")
            return false
        }

        return true
    }

    private fun releaseEGL() {
        if (eglDisplay != EGL14.EGL_NO_DISPLAY) {
            EGL14.eglMakeCurrent(eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)
            if (eglSurface != EGL14.EGL_NO_SURFACE) {
                EGL14.eglDestroySurface(eglDisplay, eglSurface)
                eglSurface = EGL14.EGL_NO_SURFACE
            }
            if (eglContext != EGL14.EGL_NO_CONTEXT) {
                EGL14.eglDestroyContext(eglDisplay, eglContext)
                eglContext = EGL14.EGL_NO_CONTEXT
            }
            EGL14.eglTerminate(eglDisplay)
            eglDisplay = EGL14.EGL_NO_DISPLAY
        }
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

    AndroidView(
        modifier = modifier,
        factory = { context ->
            Live2DTextureView(context, state).also { view ->
                state.attachView(view)
            }
        },
        onRelease = {
            state.release()
            state.detachView()
        }
    )
}
