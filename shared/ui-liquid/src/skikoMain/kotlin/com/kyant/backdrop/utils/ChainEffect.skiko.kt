package com.kyant.backdrop.utils

import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.skiaImageFilter
import org.jetbrains.skia.ImageFilter

actual fun createChainEffect(outer: RenderEffect, inner: RenderEffect): RenderEffect {
    val outerFilter = outer.skiaImageFilter
    val innerFilter = inner.skiaImageFilter

    return ImageFilter.makeCompose(outerFilter, innerFilter).asComposeRenderEffect()
}