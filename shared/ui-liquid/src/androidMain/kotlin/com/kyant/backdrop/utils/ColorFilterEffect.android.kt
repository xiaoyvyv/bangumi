package com.kyant.backdrop.utils

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.asAndroidColorFilter
import androidx.compose.ui.graphics.asComposeRenderEffect

@RequiresApi(Build.VERSION_CODES.S)
actual fun createColorFilterEffect(
    renderEffect: RenderEffect?,
    colorFilter: ColorFilter
): RenderEffect {
    return if (renderEffect != null) {
        android.graphics.RenderEffect.createColorFilterEffect(colorFilter.asAndroidColorFilter(), renderEffect.asAndroidRenderEffect())
    } else {
        android.graphics.RenderEffect.createColorFilterEffect(colorFilter.asAndroidColorFilter())
    }.asComposeRenderEffect()
}
