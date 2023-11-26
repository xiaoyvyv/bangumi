@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.common.widget.musume

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * Class: [LAppMusumeView]
 *
 * @author why
 * @since 11/25/23
 */
class LAppMusumeView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) :
    GLSurfaceView(context, attrs) {

    init {
        setBackgroundColor(Color.TRANSPARENT)

        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        setEGLContextClientVersion(2)

        holder.setFormat(PixelFormat.TRANSLUCENT)
    }


    fun init() {
        setRenderer(LAppDelegate.getInstance())
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    fun onDelegateTouchEvent(event: MotionEvent) {
        val pointX = event.x
        val pointY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> LAppDelegate.getInstance().onTouchBegan(pointX, pointY)
            MotionEvent.ACTION_UP -> LAppDelegate.getInstance().onTouchEnd(pointX, pointY)
            MotionEvent.ACTION_MOVE -> LAppDelegate.getInstance().onTouchMoved(pointX, pointY)
        }
    }

    override fun onResume() {
        super.onResume()
        LAppDelegate.getInstance().onResume(context)
    }

    override fun onPause() {
        super.onPause()
        LAppDelegate.getInstance().onPause()
    }
}