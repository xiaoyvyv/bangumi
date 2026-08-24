package com.xiaoyv.bangumi.shared.component

import android.content.res.AssetManager

class Live2DNativeBridge {
    var nativeHandle: Long = 0
        private set

    init {
        nativeHandle = nativeCreate()
    }

    fun setAssetManager(assetManager: AssetManager) {
        nativeSetAssetManager(assetManager)
    }

    fun loadModel(modelDir: String, modelJsonName: String): Boolean {
        if (nativeHandle == 0L) return false
        return nativeLoadModel(nativeHandle, modelDir, modelJsonName)
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
        if (nativeHandle != 0L) nativeOnSurfaceCreated(nativeHandle)
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

    fun destroy() {
        if (nativeHandle != 0L) {
            nativeDestroy(nativeHandle)
            nativeHandle = 0L
        }
    }

    private external fun nativeSetAssetManager(assetManager: AssetManager)
    private external fun nativeCreate(): Long
    private external fun nativeDestroy(handle: Long)
    private external fun nativeLoadModel(handle: Long, modelDir: String, modelJsonName: String): Boolean
    private external fun nativeSetMotion(handle: Long, group: String, index: Int)
    private external fun nativeSetExpression(handle: Long, expressionId: String)
    private external fun nativeGetMotions(handle: Long): Array<String>?
    private external fun nativeGetExpressions(handle: Long): Array<String>?
    private external fun nativeOnSurfaceCreated(handle: Long)
    private external fun nativeOnSurfaceChanged(handle: Long, width: Int, height: Int)
    private external fun nativeOnDrawFrame(handle: Long)
    private external fun nativeOnTouch(handle: Long, x: Float, y: Float, phase: Int)

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
