package com.xiaoyv.common.helper.callback

import android.graphics.drawable.Drawable
import android.net.Uri
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.xiaoyv.common.kts.debugLog
import java.io.File

/**
 * Class: [SubsamplingTarget]
 *
 * @author why
 * @since 12/9/23
 */
class SubsamplingTarget(ivImage: SubsamplingScaleImageView) :
    CustomViewTarget<SubsamplingScaleImageView, File>(ivImage) {
    override fun onLoadFailed(errorDrawable: Drawable?) {

    }

    override fun onResourceCleared(placeholder: Drawable?) {

    }

    override fun onResourceReady(resource: File, transition: Transition<in File>?) {
        view.setImage(ImageSource.uri(Uri.fromFile(resource)))
    }
}
