package com.kyant.backdrop.highlight

import androidx.compose.ui.geometry.Size
import com.kyant.backdrop.AmbientHighlightShaderString
import com.kyant.backdrop.DefaultHighlightShaderString
import com.kyant.backdrop.getRuntimeShaderCache
import org.jetbrains.skia.RuntimeShaderBuilder
import org.jetbrains.skia.Shader

internal actual fun createDefaultAmbientShader(
    size: Size,
    cornerRadii: FloatArray,
    angle: Float,
    falloff: Float
): Shader {
    val shaderBuilder = RuntimeShaderBuilder(
        getRuntimeShaderCache().obtainRuntimeShader(
            "Default",
            DefaultHighlightShaderString
        )
    ).apply {
        uniform("size", size.width, size.height)
        uniform("cornerRadii", cornerRadii)
        uniform("angle", angle)
        uniform("falloff", falloff)
    }
    return shaderBuilder.makeShader()
}

internal actual fun createHighlightAmbientShader(
    size: Size,
    cornerRadii: FloatArray,
    angle: Float,
    falloff: Float
): Shader {
    val shaderBuilder = RuntimeShaderBuilder(
        getRuntimeShaderCache().obtainRuntimeShader(
            "Ambient",
            AmbientHighlightShaderString
        )
    ).apply {
        uniform("size", size.width, size.height)
        uniform("cornerRadii", cornerRadii)
        uniform("angle", angle)
        uniform("falloff", falloff)
    }
    return shaderBuilder.makeShader()
}
