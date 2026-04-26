package com.kyant.backdrop.utils

import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RenderEffect

expect fun createColorFilterEffect(
    renderEffect: RenderEffect?,
    colorFilter: ColorFilter
) : RenderEffect