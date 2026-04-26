package com.kyant.backdrop.effects

import androidx.compose.ui.graphics.RenderEffect
import com.kyant.backdrop.BackdropEffectScope
import com.kyant.backdrop.utils.PlatformCapabilities
import com.kyant.backdrop.utils.createChainEffect

fun BackdropEffectScope.effect(effect: RenderEffect) {
    if (!PlatformCapabilities.hasShaderCapability()) return

    val currentEffect = renderEffect
    renderEffect =
        if (currentEffect != null) {
            createChainEffect(effect, currentEffect)
        } else {
            effect
        }
}
