package com.kyant.backdrop.utils

import androidx.compose.ui.graphics.Paint
import org.jetbrains.skia.MaskFilter

private fun FilterBlurMode.toSkiaFilterBlurMode(): org.jetbrains.skia.FilterBlurMode = when (this) {
    FilterBlurMode.NORMAL -> org.jetbrains.skia.FilterBlurMode.NORMAL
    FilterBlurMode.SOLID -> org.jetbrains.skia.FilterBlurMode.SOLID
    FilterBlurMode.OUTER -> org.jetbrains.skia.FilterBlurMode.OUTER
    FilterBlurMode.INNER -> org.jetbrains.skia.FilterBlurMode.INNER
}

actual fun Paint.applyBlurMaskFilter(
    radius: Float,
    mode: FilterBlurMode
) {
    this.asFrameworkPaint().maskFilter =
        if (radius > 0f) {
            MaskFilter.makeBlur(mode.toSkiaFilterBlurMode(), radius / 2, true)
        } else {
            null
        }
}
