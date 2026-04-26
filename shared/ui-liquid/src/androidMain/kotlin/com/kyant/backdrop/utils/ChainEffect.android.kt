package com.kyant.backdrop.utils

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect

@RequiresApi(Build.VERSION_CODES.S)
actual fun createChainEffect(outer: RenderEffect, inner: RenderEffect): RenderEffect {
    return android.graphics.RenderEffect.createChainEffect(outer.asAndroidRenderEffect(), inner.asAndroidRenderEffect()).asComposeRenderEffect()
}