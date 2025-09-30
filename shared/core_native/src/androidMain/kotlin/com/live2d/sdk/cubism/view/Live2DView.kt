@file:Suppress("KotlinConstantConditions")

package com.live2d.sdk.cubism.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.live2d.sdk.cubism.framework.GLTextureView


class Live2DView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : GLTextureView(context, attrs) {
    private val glRenderer = GLRenderer()
    private val glSurfaceView get() = this

    var onModelTapEvent: ((modelName: String, x: Float, y: Float) -> Unit)? = null

    init {
        glSurfaceView.isOpaque = false
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        glSurfaceView.setEGLContextClientVersion(2)
        glSurfaceView.setRenderer(glRenderer)
        glSurfaceView.renderMode = RENDERMODE_CONTINUOUSLY
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val pointX = event.x
        val pointY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                glSurfaceView.queueEvent { glRenderer.onTouchesBegan(pointX, pointY) }
                return true
            }

            MotionEvent.ACTION_UP -> {
                glSurfaceView.queueEvent { glRenderer.onTouchesEnded(pointX, pointY) }

                val x = glRenderer.transformScreenX(pointX)
                val y = glRenderer.transformScreenY(pointY)

                onModelTapEvent?.invoke(glRenderer.manager.getModelName(), x, y)
            }

            MotionEvent.ACTION_MOVE -> {
                glSurfaceView.queueEvent { glRenderer.onTouchesMoved(pointX, pointY) }
            }
        }
        return super.onTouchEvent(event)
    }

    fun loadModel(modelName: String, modelDir: String) {
        glSurfaceView.queueEvent {
            glRenderer.loadModel(modelName, modelDir)
        }
    }

    fun isTapArea(hitAreaName: String, x: Float, y: Float): Boolean = glRenderer.manager.isTapArea(hitAreaName, x, y)

    fun getModelName(): String = glRenderer.manager.getModelName()

    fun setExpression(expressionId: String) = glRenderer.manager.setExpression(expressionId)

    fun setRandomExpression() = glRenderer.manager.setRandomExpression()

    fun setMotion(group: String, no: Int) = glRenderer.manager.setMotion(group, no)

    fun setRandomMotion(group: String) = glRenderer.manager.setRandomMotion(group)

    fun getAllExpressionIds(): List<String> = glRenderer.manager.getAllExpressionIds()

    fun getAllMotionIds(): List<String> = glRenderer.manager.getAllMotionIds()

    fun releaseView() {
        onModelTapEvent = null
    }
}