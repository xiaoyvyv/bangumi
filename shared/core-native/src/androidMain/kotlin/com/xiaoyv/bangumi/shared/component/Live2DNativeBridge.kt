package com.xiaoyv.bangumi.shared.component

class Live2DNativeBridge {
    var nativeHandle: Long = 0
        private set

    init {
        ensureNativeCreated()
    }

    fun ensureNativeCreated() {
        if (nativeHandle == 0L) {
            nativeHandle = nativeCreate()
        }
    }

    private external fun nativeCreate(): Long

    fun loadModel(modelDir: String, modelJsonName: String): Boolean {
        ensureNativeCreated()
        return nativeLoadModel(nativeHandle, modelDir, modelJsonName)
    }

    fun loadModelFromZip(zipFilePath: String, workDir: String, modelName: String): Boolean {
        ensureNativeCreated()
        return nativeLoadModelFromZip(nativeHandle, zipFilePath, workDir, modelName)
    }

    fun setMotion(group: String, index: Int) {
        if (nativeHandle != 0L) nativeSetMotion(nativeHandle, group, index)
    }

    fun setExpression(expressionId: String) {
        if (nativeHandle != 0L) nativeSetExpression(nativeHandle, expressionId)
    }

    fun getMotions(): List<String> {
        if (nativeHandle == 0L) return emptyList()
        return nativeGetMotions(nativeHandle)?.toList() ?: emptyList()
    }

    fun getExpressions(): List<String> {
        if (nativeHandle == 0L) return emptyList()
        return nativeGetExpressions(nativeHandle)?.toList() ?: emptyList()
    }

    fun onSurfaceCreated() {
        ensureNativeCreated()
        nativeOnSurfaceCreated(nativeHandle)
    }

    fun onSurfaceChanged(width: Int, height: Int) {
        if (nativeHandle != 0L) nativeOnSurfaceChanged(nativeHandle, width, height)
    }

    fun onDrawFrame() {
        if (nativeHandle != 0L) nativeOnDrawFrame(nativeHandle)
    }

    fun onTouch(x: Float, y: Float, phase: Int) {
        if (nativeHandle != 0L) nativeOnTouch(nativeHandle, x, y, phase)
    }

    fun hitTest(x: Float, y: Float): String? {
        if (nativeHandle == 0L) return null
        return nativeHitTest(nativeHandle, x, y)
    }

    fun destroy() {
        if (nativeHandle != 0L) {
            nativeDestroy(nativeHandle)
            nativeHandle = 0L
        }
    }

    private external fun nativeDestroy(handle: Long)
    private external fun nativeLoadModel(handle: Long, modelDir: String, modelJsonName: String): Boolean
    private external fun nativeLoadModelFromZip(handle: Long, zipFilePath: String, workDir: String, modelName: String): Boolean
    private external fun nativeSetMotion(handle: Long, group: String, index: Int)
    private external fun nativeSetExpression(handle: Long, expressionId: String)
    private external fun nativeGetMotions(handle: Long): Array<String>?
    private external fun nativeGetExpressions(handle: Long): Array<String>?
    private external fun nativeOnSurfaceCreated(handle: Long)
    private external fun nativeOnSurfaceChanged(handle: Long, width: Int, height: Int)
    private external fun nativeOnDrawFrame(handle: Long)
    private external fun nativeOnTouch(handle: Long, x: Float, y: Float, phase: Int)
    private external fun nativeHitTest(handle: Long, x: Float, y: Float): String?

    companion object {
        init {
            try {
                System.loadLibrary("live2d_native")
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }
}
