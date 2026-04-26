package com.kyant.backdrop.effects

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect
import com.kyant.backdrop.RoundedRectRefractionShaderString
import com.kyant.backdrop.RoundedRectRefractionWithDispersionShaderString
import com.kyant.backdrop.getRuntimeShaderCache

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
internal actual fun createRefractionRuntimeShaderEffect(
    size: Size,
    offset: Offset,
    cornerRadii: FloatArray,
    refractionHeight: Float,
    refractionAmount: Float,
    depthEffect: Float,
    chromaticAberration: Float?
): RenderEffect {
    val shader = if (chromaticAberration == null) {
        getRuntimeShaderCache().obtainRuntimeShader(
            "Refraction",
            RoundedRectRefractionShaderString
        )
    } else {
        getRuntimeShaderCache().obtainRuntimeShader(
            "RefractionWithDispersion",
            RoundedRectRefractionWithDispersionShaderString
        )
    }.apply {
        setFloatUniform("size", size.width, size.height)
        setFloatUniform("offset", offset.x, offset.y)
        setFloatUniform("cornerRadii", cornerRadii)
        setFloatUniform("refractionHeight", refractionHeight)
        setFloatUniform("refractionAmount", -refractionAmount)
        setFloatUniform("depthEffect", depthEffect)
        if (chromaticAberration != null) {
            setFloatUniform("chromaticAberration", 1f)
        }
    }
    return android.graphics.RenderEffect.createRuntimeShaderEffect(shader, "content").asComposeRenderEffect()
}
