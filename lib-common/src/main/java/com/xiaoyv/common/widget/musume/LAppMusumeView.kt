@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.common.widget.musume

import android.annotation.SuppressLint
import android.content.Context
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val pointX = event.x
        val pointY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> LAppDelegate.getInstance().onTouchBegan(pointX, pointY)
            MotionEvent.ACTION_UP -> LAppDelegate.getInstance().onTouchEnd(pointX, pointY)
            MotionEvent.ACTION_MOVE -> LAppDelegate.getInstance().onTouchMoved(pointX, pointY)
        }
        return super.onTouchEvent(event)
    }

    fun init() {
        setEGLContextClientVersion(2)
        setRenderer(LAppDelegate.getInstance())

        renderMode = RENDERMODE_CONTINUOUSLY
    }

    fun onStart(context: Context) {
        LAppDelegate.getInstance().onStart(context)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        LAppDelegate.getInstance().onPause()
    }

    fun onStop() {
        LAppDelegate.getInstance().onStop()
    }

    fun onDestroy() {
        LAppDelegate.getInstance().onDestroy()
    }
}