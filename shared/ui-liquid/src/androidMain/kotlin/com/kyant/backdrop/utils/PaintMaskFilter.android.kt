package com.kyant.backdrop.utils

import android.graphics.BlurMaskFilter
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.nativePaint

private fun FilterBlurMode.toBlurMaskFilter(): BlurMaskFilter.Blur = when (this) {
    FilterBlurMode.NORMAL -> BlurMaskFilter.Blur.NORMAL
    FilterBlurMode.SOLID -> BlurMaskFilter.Blur.SOLID
    FilterBlurMode.OUTER -> BlurMaskFilter.Blur.OUTER
    FilterBlurMode.INNER -> BlurMaskFilter.Blur.INNER
}

actual fun Paint.applyBlurMaskFilter(
    radius: Float,
    mode: FilterBlurMode
) {
    this.nativePaint.maskFilter =
        if (radius > 0f) {
            BlurMaskFilter(radius, mode.toBlurMaskFilter())
        } else {
            null
        }
}
