package com.kyant.backdrop.effects

import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect
import com.kyant.backdrop.GammaAdjustmentShaderString
import com.kyant.backdrop.getRuntimeShaderCache
import org.jetbrains.skia.ImageFilter
import org.jetbrains.skia.RuntimeShaderBuilder

internal actual fun createGammaAdjustmentRuntimeShaderEffect(power: Float): RenderEffect {
    val shaderBuilder = RuntimeShaderBuilder(
        getRuntimeShaderCache().obtainRuntimeShader("GammaAdjustment", GammaAdjustmentShaderString)
    ).apply {
        uniform("power", power)
    }
    return ImageFilter.makeRuntimeShader(
        runtimeShaderBuilder = shaderBuilder,
        shaderNames = arrayOf("content"),
        inputs = arrayOf(null),
    ).asComposeRenderEffect()
}
