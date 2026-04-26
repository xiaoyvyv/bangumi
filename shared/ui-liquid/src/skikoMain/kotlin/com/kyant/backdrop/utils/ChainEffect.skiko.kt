package com.kyant.backdrop.utils

import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect
import org.jetbrains.skia.ImageFilter

actual fun createChainEffect(outer: RenderEffect, inner: RenderEffect): RenderEffect {
    val outerFilter = outer.asSkiaImageFilter()
    val innerFilter = inner.asSkiaImageFilter()

    return ImageFilter.makeCompose(outerFilter, innerFilter).asComposeRenderEffect()
}