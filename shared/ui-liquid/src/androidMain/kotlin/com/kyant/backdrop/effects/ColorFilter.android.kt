package com.kyant.backdrop.effects

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect
import com.kyant.backdrop.GammaAdjustmentShaderString
import com.kyant.backdrop.getRuntimeShaderCache

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
internal actual fun createGammaAdjustmentRuntimeShaderEffect(power: Float): RenderEffect {
    val shader = getRuntimeShaderCache().obtainRuntimeShader("GammaAdjustment", GammaAdjustmentShaderString).apply {
        setFloatUniform("power", power)
    }
    return android.graphics.RenderEffect.createRuntimeShaderEffect(shader, "content").asComposeRenderEffect()
}