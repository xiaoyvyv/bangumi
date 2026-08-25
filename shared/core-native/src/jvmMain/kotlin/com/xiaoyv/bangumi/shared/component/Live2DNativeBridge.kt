package com.xiaoyv.bangumi.shared.component

import java.io.File

class Live2DNativeBridge {
    var nativeHandle: Long = 0
        private set

    init {
        ensureNativeCreated()
    }

    fun ensureNativeCreated() {
        if (nativeHandle == 0L) {
            try {
                nativeHandle = nativeCreate()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    private external fun nativeCreate(): Long

    fun loadModel(modelDir: String, modelJsonName: String): Boolean {
        ensureNativeCreated()
        if (nativeHandle == 0L) return false
        return try {
            nativeLoadModel(nativeHandle, modelDir, modelJsonName)
        } catch (e: Throwable) {
            e.printStackTrace()
            false
        }
    }

    fun loadModelFromZip(zipFilePath: String, workDir: String, modelName: String): Boolean {
        ensureNativeCreated()
        if (nativeHandle == 0L) return false
        return try {
            nativeLoadModelFromZip(nativeHandle, zipFilePath, workDir, modelName)
        } catch (e: Throwable) {
            e.printStackTrace()
            false
        }
    }

    fun setMotion(group: String, index: Int) {
        if (nativeHandle != 0L) {
            try { nativeSetMotion(nativeHandle, group, index) } catch (e: Throwable) { e.printStackTrace() }
        }
    }

    fun setExpression(expressionId: String) {
        if (nativeHandle != 0L) {
            try { nativeSetExpression(nativeHandle, expressionId) } catch (e: Throwable) { e.printStackTrace() }
        }
    }

    fun getMotions(): List<String> {
        if (nativeHandle == 0L) return emptyList()
        return try {
            nativeGetMotions(nativeHandle)?.toList() ?: emptyList()
        } catch (e: Throwable) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun getExpressions(): List<String> {
        if (nativeHandle == 0L) return emptyList()
        return try {
            nativeGetExpressions(nativeHandle)?.toList() ?: emptyList()
        } catch (e: Throwable) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun onSurfaceCreated() {
        ensureNativeCreated()
        if (nativeHandle != 0L) {
            try { nativeOnSurfaceCreated(nativeHandle) } catch (e: Throwable) { e.printStackTrace() }
        }
    }

    fun onSurfaceChanged(width: Int, height: Int) {
        if (nativeHandle != 0L) {
            try { nativeOnSurfaceChanged(nativeHandle, width, height) } catch (e: Throwable) { e.printStackTrace() }
        }
    }

    fun onDrawFrame() {
        if (nativeHandle != 0L) {
            try { nativeOnDrawFrame(nativeHandle) } catch (e: Throwable) { e.printStackTrace() }
        }
    }

    fun onTouch(x: Float, y: Float, phase: Int) {
        if (nativeHandle != 0L) {
            try { nativeOnTouch(nativeHandle, x, y, phase) } catch (e: Throwable) { e.printStackTrace() }
        }
    }

    fun onDrag(x: Float, y: Float) {
        if (nativeHandle != 0L) {
            try { nativeOnDrag(nativeHandle, x, y) } catch (e: Throwable) { e.printStackTrace() }
        }
    }

    fun resetDrag() {
        if (nativeHandle != 0L) {
            try { nativeResetDrag(nativeHandle) } catch (e: Throwable) { e.printStackTrace() }
        }
    }

    fun hitTest(x: Float, y: Float): String? {
        if (nativeHandle == 0L) return null
        return try {
            nativeHitTest(nativeHandle, x, y)
        } catch (e: Throwable) {
            e.printStackTrace()
            null
        }
    }

    fun destroy() {
        if (nativeHandle != 0L) {
            try { nativeDestroy(nativeHandle) } catch (e: Throwable) { e.printStackTrace() }
            nativeHandle = 0L
        }
    }

    fun renderPixels(width: Int, height: Int, outPixels: IntArray): Boolean {
        ensureNativeCreated()
        if (nativeHandle == 0L) return false
        return try {
            nativeRenderPixels(nativeHandle, width, height, outPixels)
        } catch (e: Throwable) {
            e.printStackTrace()
            false
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
    private external fun nativeRenderPixels(handle: Long, width: Int, height: Int, outPixels: IntArray): Boolean
    private external fun nativeOnTouch(handle: Long, x: Float, y: Float, phase: Int)
    private external fun nativeOnDrag(handle: Long, x: Float, y: Float)
    private external fun nativeResetDrag(handle: Long)
    private external fun nativeHitTest(handle: Long, x: Float, y: Float): String?

    companion object {
        private var isLoaded = false

        init {
            loadNativeLibrary()
        }

        @Synchronized
        fun loadNativeLibrary() {
            if (isLoaded) return

            // 1. Try standard System.loadLibrary
            try {
                System.loadLibrary("live2d_native")
                isLoaded = true
                return
            } catch (_: Throwable) {}

            // 2. Try loading from classpath resources (src/jvmMain/resources/native/macos/liblive2d_native.dylib)
            val osName = System.getProperty("os.name")?.lowercase() ?: ""
            val (resourcePath, libName, ext) = when {
                osName.contains("mac") -> Triple("/native/macos/liblive2d_native.dylib", "liblive2d_native", ".dylib")
                osName.contains("win") -> Triple("/native/windows/live2d_native.dll", "live2d_native", ".dll")
                else -> Triple("/native/linux/liblive2d_native.so", "liblive2d_native", ".so")
            }

            try {
                val resourceStream = Live2DNativeBridge::class.java.getResourceAsStream(resourcePath)
                if (resourceStream != null) {
                    val tempFile = File.createTempFile(libName, ext)
                    tempFile.deleteOnExit()
                    tempFile.outputStream().use { out ->
                        resourceStream.copyTo(out)
                    }
                    System.load(tempFile.absolutePath)
                    isLoaded = true
                    return
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }

            // 3. Fallback to candidate file paths
            val userDir = System.getProperty("user.dir") ?: ""
            val candidatePaths = listOf(
                File(userDir, "shared/core-native/native/macos/liblive2d_native.dylib"),
                File(userDir, "native/macos/liblive2d_native.dylib"),
                File(System.getProperty("compose.application.resources.dir") ?: "", "liblive2d_native.dylib")
            )
            for (file in candidatePaths) {
                if (file.exists()) {
                    try {
                        System.load(file.absolutePath)
                        isLoaded = true
                        return
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}
