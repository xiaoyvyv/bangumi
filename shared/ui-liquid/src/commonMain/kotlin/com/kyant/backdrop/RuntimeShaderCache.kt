package com.kyant.backdrop

expect class RuntimeShader

interface RuntimeShaderCache {
    fun obtainRuntimeShader(key: String, string: String): RuntimeShader
    fun clear()
}

expect fun getRuntimeShaderCache(): RuntimeShaderCache
