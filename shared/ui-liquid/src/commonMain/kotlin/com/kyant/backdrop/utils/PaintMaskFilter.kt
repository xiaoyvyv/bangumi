package com.kyant.backdrop.utils

import androidx.compose.ui.graphics.Paint

enum class FilterBlurMode {
    NORMAL,
    SOLID,
    OUTER,
    INNER;
}

expect fun Paint.applyBlurMaskFilter(radius: Float, mode: FilterBlurMode)
