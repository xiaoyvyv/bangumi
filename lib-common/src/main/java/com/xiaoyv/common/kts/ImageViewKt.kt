package com.xiaoyv.common.kts

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.helper.PositionCropTransformation
import com.xiaoyv.common.helper.blur.BlurTransformation
import com.xiaoyv.widget.kts.listener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 展示顶部
 */
val topCropTransformation by lazy {
    PositionCropTransformation()
}

/**
 * ImageViewKt
 *
 * @author why
 * @since 11/19/23
 */
inline fun ImageView.loadImageAnimate(
    model: Any?,
    cropType: ScaleType = ScaleType.CENTER_CROP,
    holder: Boolean = false,
    crossinline onReady: (Drawable) -> Unit = {},
    crossinline onFail: (Any?) -> Unit = {},
) {
    Glide.with(this)
        .load(model ?: return)
        .let {
            when (cropType) {
                ScaleType.CENTER_CROP -> it.centerCrop()
                ScaleType.FIT_START, ScaleType.FIT_END -> it.transform(topCropTransformation)
                else -> it
            }
        }
        .listener(onLoadFailed = onFail, onResourceReady = onReady)
        .let {
            if (holder) it.placeholder(CommonDrawable.layer_error).error(CommonDrawable.layer_error)
            else it
        }
        .let {
            if (ConfigHelper.isImageAnimation()) it.transition(DrawableTransitionOptions.withCrossFade())
            else it
        }
        .into(this)
}

fun ImageView.clear() {
    Glide.with(this).clear(this)
    setImageResource(0)
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
        .transform(BlurTransformation(25, 10))
        .error(CommonDrawable.layer_error)
        .into(this)
}

fun View.loadImageBlurBackground(model: Any?) {
    findViewTreeLifecycleOwner()?.launchUI {
        background = withContext(Dispatchers.IO) {
            Glide.with(this@loadImageBlurBackground)
                .asDrawable()
                .load(model)
                .error(CommonDrawable.layer_error)
                .centerCrop()
                .transform(BlurTransformation(25, 10))
                .submit()
                .get()
        }
    }
}