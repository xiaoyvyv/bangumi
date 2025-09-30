@file:Suppress("KotlinConstantConditions")

package com.live2d.sdk.cubism.view

import android.opengl.GLES20
import com.live2d.sdk.cubism.framework.math.CubismMatrix44
import com.live2d.sdk.cubism.framework.motion.IBeganMotionCallback
import com.live2d.sdk.cubism.framework.motion.IFinishedMotionCallback
import com.live2d.sdk.cubism.view.LAppPal.printLog

/**
 * サンプルアプリケーションにおいてCubismModelを管理するクラス。
 * モデル生成と破棄、タップイベントの処理、モデル切り替えを行う。
 */
class LAppLive2DManager {
    private val beganMotion = IBeganMotionCallback { motion -> printLog("Motion Began: $motion") }
    private val finishedMotion = IFinishedMotionCallback { motion -> printLog("Motion Finish: $motion") }

    private var currentModel: LAppModel? = null

    // onUpdateメソッドで使用されるキャッシュ変数
    private val viewMatrix: CubismMatrix44 = CubismMatrix44.create()
    private val projection: CubismMatrix44 = CubismMatrix44.create()

    private val textureManager = LAppTextureManager()

    fun loadModel(modelName: String, modelDir: String) {
        releaseModel()
        val modelJsonName = "$modelName.model3.json"
        this.currentModel = LAppModel(textureManager)
        currentModel?.loadAssets(modelDir, modelJsonName)
    }

    /**
     * 現在のシーンで保持している全てのモデルを解放する
     */
    fun releaseModel() {
        currentModel?.deleteModel()
        currentModel = null
    }

    // モデル更新処理及び描画処理を行う
    fun onUpdate(width: Int, height: Int) {
        val model = this.currentModel
        if (model == null || model.getModel() == null || !model.isInitialized) {
            return
        }

        projection.loadIdentity()

        if (model.getModel().getCanvasWidth() > 1.0f && width < height) {
            // 横に長いモデルを縦長ウィンドウに表示する際モデルの横サイズでscaleを算出する
            model.getModelMatrix().setWidth(2.0f)
            projection.scale(1.0f, width.toFloat() / height.toFloat())
        } else {
            projection.scale(height.toFloat() / width.toFloat(), 1.0f)
        }

        // 必要があればここで乗算する
        viewMatrix.multiplyByMatrix(projection)

        // モデル1体描画前コール
        // 透過設定
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        model.update()
        model.draw(projection) // 参照渡しなのでprojectionは変質する
    }

    /**
     * 画面をドラッグした時の処理
     *
     * @param x 画面のx座標
     * @param y 画面のy座標
     */
    fun onDrag(x: Float, y: Float) = currentModel?.setDragging(x, y)

    /**
     * 画面をタップした時の処理
     *
     * @param x 画面のx座標
     * @param y 画面のy座標
     */
    fun isTapArea(hitAreaName: String, x: Float, y: Float): Boolean {
        val model = this.currentModel
        if (model != null) {
            return model.hitTest(hitAreaName, x, y)
        }
        return false
    }

    fun getModelName(): String {
        return currentModel?.getModelName().orEmpty()
    }

    fun setExpression(expressionId: String) {
        currentModel?.setExpression(expressionId)
    }

    fun setRandomExpression() {
        currentModel?.setRandomExpression()
    }

    fun setMotion(group: String, no: Int) {
        currentModel?.startMotion(group, no, LAppDefine.Priority.NORMAL.priority, finishedMotion, beganMotion)
    }

    fun setRandomMotion(group: String) {
        currentModel?.startRandomMotion(group, LAppDefine.Priority.NORMAL.priority, finishedMotion, beganMotion)
    }

    fun getAllExpressionIds(): List<String> {
        return currentModel?.getAllExpressionIds().orEmpty()
    }

    fun getAllMotionIds(): List<String> {
        return currentModel?.getAllMotionIds().orEmpty()
    }
}
