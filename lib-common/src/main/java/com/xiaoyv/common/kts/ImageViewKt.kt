package com.xiaoyv.common.kts

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.helper.PositionCropTransformation
import com.xiaoyv.common.helper.blur.BlurTransformation
import com.xiaoyv.widget.kts.listener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val IMAGE_HOLDER_NONE = 0
const val IMAGE_HOLDER_1X1 = 11
const val IMAGE_HOLDER_3X4 = 43

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
    holderType: Int = IMAGE_HOLDER_NONE,
    crossinline onReady: (Drawable) -> Unit = {},
    crossinline onFail: (Any?) -> Unit = {},
) {
    if (model == null) {
        Glide.with(this).clear(this)
        return
    }

    Glide.with(this)
        .load(model)
        .let {
            when (cropType) {
                ScaleType.CENTER_CROP -> it.centerCrop()
                ScaleType.FIT_START, ScaleType.FIT_END -> it.transform(topCropTransformation)
                else -> it
            }
        }
        .listener(onLoadFailed = onFail, onResourceReady = onReady)
        .let {
            when (holderType) {
                IMAGE_HOLDER_1X1 -> it
                    .placeholder(CommonDrawable.layer_error_1x1)
                    .error(CommonDrawable.layer_error_1x1)

                IMAGE_HOLDER_3X4 -> it
                    .placeholder(CommonDrawable.layer_error_3x4)
                    .error(CommonDrawable.layer_error_3x4)

                else -> it
            }
        }
        .let {
            if (ConfigHelper.isImageAnimation) it.transition(DrawableTransitionOptions.withCrossFade())
            else it
        }
        .into(this)
}

fun ImageView.clear() {
    Glide.with(this).clear(this)
    setImageResource(0)
}

/**
 * 加载模糊图
 */
fun ImageView.loadImageBlur(model: Any?) {
    scaleType = ScaleType.CENTER_CROP

    Glide.with(this)
        .load(model ?: return)
        .transform(BlurTransformation(25, 10))
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