package com.xiaoyv.common.kts

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.xiaoyv.common.helper.blur.BlurTransformation
import com.xiaoyv.widget.kts.listener

/**
 * ImageViewKt
 *
 * @author why
 * @since 11/19/23
 */
inline fun ImageView.loadImageAnimate(
    model: Any?, centerCrop: Boolean = true,
    crossinline onReady: (Drawable) -> Unit = {},
    crossinline onFail: (Any?) -> Unit = {},
) {
    Glide.with(this)
        .load(model ?: return)
        .let { if (centerCrop) it.centerCrop() else it }
        .listener(onLoadFailed = onFail, onResourceReady = onReady)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

inline fun ImageView.loadImageBlur(
    model: Any?, centerCrop: Boolean = true,
    crossinline onReady: (Drawable) -> Unit = {},
    crossinline onFail: (Any?) -> Unit = {},
) {
    Glide.with(this)
        .load(model ?: return)
        .let { if (centerCrop) it.centerCrop() else it }
        .listener(onLoadFailed = onFail, onResourceReady = onReady)
        .transform(BlurTransformation(25, 5))
        .into(this)
}