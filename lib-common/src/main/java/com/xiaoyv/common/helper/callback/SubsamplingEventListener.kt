package com.xiaoyv.common.helper.callback

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import java.lang.Exception

/**
 * Class: [SubsamplingEventListener]
 *
 * @author why
 * @since 12/9/23
 */
open class SubsamplingEventListener : SubsamplingScaleImageView.OnImageEventListener {
    override fun onReady() {

    }

    override fun onImageLoaded() {
    }

    override fun onPreviewLoadError(e: Exception?) {
    }

    override fun onImageLoadError(e: Exception?) {
    }

    override fun onTileLoadError(e: Exception?) {
    }

    override fun onPreviewReleased() {
    }
}