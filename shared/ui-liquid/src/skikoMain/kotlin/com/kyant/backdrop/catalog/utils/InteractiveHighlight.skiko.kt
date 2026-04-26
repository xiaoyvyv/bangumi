package com.kyant.backdrop.catalog.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.util.fastCoerceIn
import com.kyant.backdrop.getRuntimeShaderCache
import org.jetbrains.skia.RuntimeShaderBuilder

internal actual fun createInteractiveHighlightShader(
    size: Size,
    color: Color,
    radius: Float,
    position: Offset
): Shader {
    val shaderBuilder = RuntimeShaderBuilder(
        getRuntimeShaderCache().obtainRuntimeShader("InteractiveHighlight", InteractiveHighlightShaderString)
    ).apply {
        uniform("size", size.width, size.height)
        uniform("color", color.red, color.green, color.blue, color.alpha)
        uniform("colorAlpha", color.alpha)
        uniform("radius", radius)
        uniform("position", position.x.fastCoerceIn(0f, size.width), position.y.fastCoerceIn(0f, size.height))
    }
    return shaderBuilder.makeShader()
}

internal const val InteractiveHighlightShaderString = """
    uniform float2 size;
    layout(color) uniform half4 color;
    uniform float colorAlpha;
    uniform float radius;
    uniform float2 position;
    
    half4 main(float2 coord) {
        float dist = distance(coord, position);
        float intensity = smoothstep(radius, radius * 0.5, dist);
        return color * colorAlpha * intensity;
    }
"""