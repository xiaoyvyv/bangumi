package com.kyant.backdrop

import org.jetbrains.skia.RuntimeEffect
import org.jetbrains.skia.Shader

actual typealias RuntimeShader = RuntimeEffect

private object SkikoRuntimeShaderCache : RuntimeShaderCache {
    private val runtimeShaders = mutableMapOf<String, RuntimeEffect>()

    override fun obtainRuntimeShader(
        key: String,
        string: String
    ): RuntimeShader {
        return runtimeShaders.getOrPut(key) { RuntimeEffect.makeForShader(string) }
    }

    override fun clear() {
        runtimeShaders.clear()
    }
}

actual fun getRuntimeShaderCache(): RuntimeShaderCache {
    return SkikoRuntimeShaderCache
}