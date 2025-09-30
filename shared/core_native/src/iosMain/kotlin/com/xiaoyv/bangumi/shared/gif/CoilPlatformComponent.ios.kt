package com.xiaoyv.bangumi.shared.gif

import coil3.ComponentRegistry

actual fun ComponentRegistry.Builder.addPlatformGifSupport() {
    add(AnimatedSkiaImageDecoder.Factory(prerenderFrames = true))
}