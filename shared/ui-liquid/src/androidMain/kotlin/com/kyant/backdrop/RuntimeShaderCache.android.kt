package com.kyant.backdrop

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi

actual typealias RuntimeShader = RuntimeShader

private object AndroidRuntimeShaderCache : RuntimeShaderCache {

    private val runtimeShaders = mutableMapOf<String, RuntimeShader>()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun obtainRuntimeShader(key: String, string: String): RuntimeShader {
        return runtimeShaders.getOrPut(key) { RuntimeShader(string) }
    }

    override fun clear() {
        runtimeShaders.clear()
    }
}

actual fun getRuntimeShaderCache(): RuntimeShaderCache {
    return AndroidRuntimeShaderCache
}