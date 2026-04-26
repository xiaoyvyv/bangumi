package com.kyant.backdrop.utils

actual object PlatformCapabilities {
    actual fun hasShaderCapability(): Boolean = true
    actual fun hasAdvancedShaderCapability(): Boolean = true
}