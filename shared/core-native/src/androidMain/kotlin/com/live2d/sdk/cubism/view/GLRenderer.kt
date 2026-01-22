/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */
@file:Suppress("KotlinConstantConditions")

package com.live2d.sdk.cubism.view

import android.opengl.GLES20
import com.live2d.sdk.cubism.framework.CubismFramework
import com.live2d.sdk.cubism.framework.GLTextureView
import com.live2d.sdk.cubism.framework.math.CubismMatrix44
import com.live2d.sdk.cubism.framework.math.CubismViewMatrix
import com.live2d.sdk.cubism.view.LAppDefine.MaxLogicalView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.abs

class GLRenderer : GLTextureView.Renderer {
    private val touchManager = TouchManager()

    val manager = LAppLive2DManager()

    /**
     * デバイス座標からスクリーン座標に変換するための行列
     */
    private val deviceToScreen: CubismMatrix44 = CubismMatrix44.create()

    /**
     * 画面表示の拡縮や移動の変換を行う行列
     */
    private val viewMatrix = CubismViewMatrix()

    @Volatile
    private var width = 0

    @Volatile
    private var height = 0

    fun loadModel(modelName: String, modelDir: String) = manager.loadModel(modelName, modelDir)

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        LAppPal.printLog("GLRenderer: onSurfaceCreated ${Thread.currentThread()}")

        // テクスチャサンプリング設定
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)

        // 透過設定
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        CubismFramework.initialize()
    }

    // Mainly called when switching between landscape and portrait.
    override fun onSurfaceChanged(unused: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        resizeScreen(width, height)
    }

    // Called repeatedly for drawing.
    override fun onDrawFrame(unused: GL10?): Boolean {
        // 時間更新
        LAppPal.updateTime()

        // 画面初期化
        GLES20.glClearColor(0f, 0f, 0f, 0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glClearDepthf(1.0f)

        manager.onUpdate(width, height)
        return true
    }

    override fun onSurfaceDestroyed() {
        LAppPal.printLog("onSurfaceDestroyed")
        manager.releaseModel()
        CubismFramework.dispose()
    }

    fun resizeScreen(width: Int, height: Int) {
        this.width = width
        this.height = height

        val ratio = width.toFloat() / height.toFloat()
        val left = -ratio
        val right = ratio
        val bottom = LAppDefine.LogicalView.LEFT.value
        val top = LAppDefine.LogicalView.RIGHT.value

        // デバイスに対応する画面範囲。Xの左端、Xの右端、Yの下端、Yの上端
        viewMatrix.setScreenRect(left, right, bottom, top)
        viewMatrix.scale(LAppDefine.Scale.DEFAULT.value, LAppDefine.Scale.DEFAULT.value)

        // 単位行列に初期化
        deviceToScreen.loadIdentity()

        if (width > height) {
            val screenW = abs(right - left)
            deviceToScreen.scaleRelative(screenW / width, -screenW / width)
        } else {
            val screenH = abs(top - bottom)
            deviceToScreen.scaleRelative(screenH / height, -screenH / height)
        }
        deviceToScreen.translateRelative(-width * 0.5f, -height * 0.5f)

        // 表示範囲の設定
        viewMatrix.maxScale = LAppDefine.Scale.MAX.value// 限界拡大率
        viewMatrix.minScale = LAppDefine.Scale.MIN.value // 限界縮小率

        // 表示できる最大範囲
        viewMatrix.setMaxScreenRect(
            MaxLogicalView.LEFT.value,
            MaxLogicalView.RIGHT.value,
            MaxLogicalView.BOTTOM.value,
            MaxLogicalView.TOP.value
        )
    }


    /**
     * タッチされたときに呼ばれる
     *
     * @param pointX スクリーンX座標
     * @param pointY スクリーンY座標
     */
    fun onTouchesBegan(pointX: Float, pointY: Float) {
        touchManager.touchesBegan(pointX, pointY)
    }

    /**
     * タッチしているときにポインターが動いたら呼ばれる
     *
     * @param pointX スクリーンX座標
     * @param pointY スクリーンY座標
     */
    fun onTouchesMoved(pointX: Float, pointY: Float) {
        val viewX = transformViewX(touchManager.lastX)
        val viewY = transformViewY(touchManager.lastY)

        touchManager.touchesMoved(pointX, pointY)

        manager.onDrag(viewX, viewY)
    }

    /**
     * タッチが終了したら呼ばれる
     *
     * @param pointX スクリーンX座標
     * @param pointY スクリーンY座標
     */
    fun onTouchesEnded(pointX: Float, pointY: Float) {
        // タッチ終了
        manager.onDrag(0.0f, 0.0f)

        // シングルタップ
        // 論理座標変換した座標を取得
        val x = deviceToScreen.transformX(touchManager.lastX)
        // 論理座標変換した座標を取得
        val y = deviceToScreen.transformY(touchManager.lastY)

        if (LAppDefine.DEBUG_TOUCH_LOG_ENABLE) {
            LAppPal.printLog("Touches ended x: $x, y:$y")
        }

//        manager.onTap(x, y)
    }

    /**
     * X座標をView座標に変換する
     *
     * @param deviceX デバイスX座標
     * @return ViewX座標
     */
    fun transformViewX(deviceX: Float): Float {
        // 論理座標変換した座標を取得
        val screenX = deviceToScreen.transformX(deviceX)
        // 拡大、縮小、移動後の値
        return viewMatrix.invertTransformX(screenX)
    }

    /**
     * Y座標をView座標に変換する
     *
     * @param deviceY デバイスY座標
     * @return ViewY座標
     */
    fun transformViewY(deviceY: Float): Float {
        // 論理座標変換した座標を取得
        val screenY = deviceToScreen.transformY(deviceY)
        // 拡大、縮小、移動後の値
        return viewMatrix.invertTransformX(screenY)
    }

    /**
     * X座標をScreen座標に変換する
     *
     * @param deviceX デバイスX座標
     * @return ScreenX座標
     */
    fun transformScreenX(deviceX: Float): Float {
        return deviceToScreen.transformX(deviceX)
    }

    /**
     * Y座標をScreen座標に変換する
     *
     * @param deviceY デバイスY座標
     * @return ScreenY座標
     */
    fun transformScreenY(deviceY: Float): Float {
        return deviceToScreen.transformY(deviceY)
    }
}
