package com.xiaoyv.common.config.glide

import android.graphics.drawable.Drawable
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition

open class WrappingTarget<Z>(protected val target: Target<in Z>) : Target<Z> {
    override fun getSize(cb: SizeReadyCallback) {
        target.getSize(cb)
    }

    override fun onLoadStarted(placeholder: Drawable?) {
        target.onLoadStarted(placeholder)
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        target.onLoadFailed(errorDrawable)
    }

    override fun removeCallback(cb: SizeReadyCallback) {
        target.removeCallback(cb)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onResourceReady(resource: Z & Any, transition: Transition<in Z>?) {
        target.onResourceReady(resource, transition as Transition<Any?>?)
    }

    override fun onLoadCleared(placeholder: Drawable?) {
        target.onLoadCleared(placeholder)
    }

    override fun getRequest(): Request? {
        return target.request
    }

    override fun setRequest(request: Request?) {
        target.request = request
    }

    override fun onStart() {
        target.onStart()
    }

    override fun onStop() {
        target.onStop()
    }

    override fun onDestroy() {
        target.onDestroy()
    }
}