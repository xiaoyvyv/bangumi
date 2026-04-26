package com.kyant.backdrop.effects

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect
import com.kyant.backdrop.RoundedRectRefractionShaderString
import com.kyant.backdrop.RoundedRectRefractionWithDispersionShaderString
import com.kyant.backdrop.getRuntimeShaderCache
import org.jetbrains.skia.ImageFilter
import org.jetbrains.skia.RuntimeShaderBuilder

internal actual fun createRefractionRuntimeShaderEffect(
    size: Size,
    offset: Offset,
    cornerRadii: FloatArray,
    refractionHeight: Float,
    refractionAmount: Float,
    depthEffect: Float,
    chromaticAberration: Float?
): RenderEffect {
    val shaderBuilder = RuntimeShaderBuilder(
        if (chromaticAberration == null) {
            getRuntimeShaderCache().obtainRuntimeShader(
                "Refraction",
                RoundedRectRefractionShaderString
            )
        } else {
            getRuntimeShaderCache().obtainRuntimeShader(
                "RefractionWithDispersion",
                RoundedRectRefractionWithDispersionShaderString
            )
        }
    ).apply {
        uniform("size", size.width, size.height)
        uniform("offset", offset.x, offset.y)
        uniform("cornerRadii", cornerRadii)
        uniform("refractionHeight", refractionHeight)
        uniform("refractionAmount", -refractionAmount)
        uniform("depthEffect", depthEffect)
        if (chromaticAberration != null) {
            uniform("chromaticAberration", 1f)
        }
    }
    return ImageFilter.makeRuntimeShader(
        runtimeShaderBuilder = shaderBuilder,
        shaderNames = arrayOf("content"),
        inputs = arrayOf(null),
    ).asComposeRenderEffect()
}