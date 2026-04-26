package com.kyant.backdrop.utils

expect object PlatformCapabilities {
    fun hasShaderCapability(): Boolean
    fun hasAdvancedShaderCapability(): Boolean
}