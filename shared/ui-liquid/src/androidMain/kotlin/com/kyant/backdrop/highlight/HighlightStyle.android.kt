package com.kyant.backdrop.highlight

import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.geometry.Size
import com.kyant.backdrop.AmbientHighlightShaderString
import com.kyant.backdrop.DefaultHighlightShaderString
import com.kyant.backdrop.getRuntimeShaderCache

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
internal actual fun createDefaultAmbientShader(
    size: Size,
    cornerRadii: FloatArray,
    angle: Float,
    falloff: Float
): Shader {
    return getRuntimeShaderCache().obtainRuntimeShader(
        "Default",
        DefaultHighlightShaderString
    ).apply {
        setFloatUniform("size", size.width, size.height)
        setFloatUniform("cornerRadii", cornerRadii)
        setFloatUniform("angle", angle)
        setFloatUniform("falloff", falloff)
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
internal actual fun createHighlightAmbientShader(
    size: Size,
    cornerRadii: FloatArray,
    angle: Float,
    falloff: Float
): Shader {
    return getRuntimeShaderCache().obtainRuntimeShader(
        "Ambient",
        AmbientHighlightShaderString
    ).apply {
        setFloatUniform("size", size.width, size.height)
        setFloatUniform("cornerRadii", cornerRadii)
        setFloatUniform("angle", angle)
        setFloatUniform("falloff", falloff)
    }
}
