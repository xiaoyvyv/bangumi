package com.kyant.backdrop.catalog.utils

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.util.fastCoerceIn
import com.kyant.backdrop.getRuntimeShaderCache
import org.intellij.lang.annotations.Language

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
internal actual fun createInteractiveHighlightShader(
    size: Size,
    color: Color,
    radius: Float,
    position: Offset
): Shader {
    return getRuntimeShaderCache().obtainRuntimeShader("InteractiveHighlight", InteractiveHighlightShaderString).apply {
        setFloatUniform("size", size.width, size.height)
        setColorUniform("color", color.toArgb())
        setFloatUniform("radius", radius)
        setFloatUniform(
            "position",
            position.x.fastCoerceIn(0f, size.width),
            position.y.fastCoerceIn(0f, size.height)
        )
    }
}

@Language("AGSL")
internal const val InteractiveHighlightShaderString = """
    uniform float2 size;
    layout(color) uniform half4 color;
    uniform float radius;
    uniform float2 position;
    
    half4 main(float2 coord) {
        float dist = distance(coord, position);
        float intensity = smoothstep(radius, radius * 0.5, dist);
        return color * intensity;
    }
"""
