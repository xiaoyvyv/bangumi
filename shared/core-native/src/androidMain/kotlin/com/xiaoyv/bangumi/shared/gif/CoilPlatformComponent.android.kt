package com.xiaoyv.bangumi.shared.gif

import android.os.Build
import coil3.ComponentRegistry
import coil3.gif.GifDecoder
import coil3.svg.SvgDecoder
import coil3.video.VideoFrameDecoder

actual fun ComponentRegistry.Builder.addPlatformGifSupport() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        add(TwinklyAnimatedImageDecoder.Factory())
    } else {
        add(GifDecoder.Factory())
    }
    add(SvgDecoder.Factory())
    add(VideoFrameDecoder.Factory())
}