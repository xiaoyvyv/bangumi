package com.kyant.backdrop.utils

import androidx.compose.ui.graphics.RenderEffect

expect fun createChainEffect(outer: RenderEffect, inner: RenderEffect): RenderEffect