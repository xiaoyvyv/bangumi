package com.kyant.backdrop.utils

import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.asSkiaColorFilter
import org.jetbrains.skia.ImageFilter

actual fun createColorFilterEffect(
    renderEffect: RenderEffect?,
    colorFilter: ColorFilter
): RenderEffect {
    return if (renderEffect != null) {
        ImageFilter.makeColorFilter(colorFilter.asSkiaColorFilter(), renderEffect.asSkiaImageFilter(), null)
    } else {
        ImageFilter.makeColorFilter(colorFilter.asSkiaColorFilter(), null, null)
    }.asComposeRenderEffect()
}