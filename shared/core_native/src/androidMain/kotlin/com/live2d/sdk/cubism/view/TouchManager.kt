package com.live2d.sdk.cubism.view

import kotlin.math.abs
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * タッチマネージャー
 */
class TouchManager {

    // ----- getter methods -----
    /**
     * タッチ開始時のxの値
     */
    var startX: Float = 0f
        private set

    /**
     * タッチ開始時のyの値
     */
    var startY: Float = 0f
        private set

    /**
     * シングルタッチ時のxの値
     */
    var lastX: Float = 0f
        private set

    /**
     * シングルタッチ時のyの値
     */
    var lastY: Float = 0f
        private set

    /**
     * ダブルタッチ時の1つ目のxの値
     */
    var lastX1: Float = 0f
        private set

    /**
     * ダブルタッチ時の1つ目のyの値
     */
    var lastY1: Float = 0f
        private set

    /**
     * ダブルタッチ時の2つ目のxの値
     */
    var lastX2: Float = 0f
        private set

    /**
     * ダブルタッチ時の2つ目のyの値
     */
    var lastY2: Float = 0f
        private set

    /**
     * 2本以上でタッチしたときの指の距離
     */
    var lastTouchDistance: Float = 0f
        private set

    /**
     * 前回の値から今回の値へのxの移動距離
     */
    var deltaX: Float = 0f
        private set

    /**
     * 前回の値から今回の値へのyの移動距離
     */
    var deltaY: Float = 0f
        private set

    /**
     * このフレームで掛け合わせる拡大率。拡大操作中以外は1
     */
    var scale: Float = 0f
        private set

    /**
     * シングルタッチ時はtrue
     */
    var isTouchSingle: Boolean = false
        private set

    /**
     * フリップが有効かどうか
     */
    var isFlipAvailable: Boolean = false
        private set

    /**
     * タッチ開始時のイベント
     *
     * @param deviceX タッチした画面のxの値
     * @param deviceY タッチした画面のyの値
     */
    fun touchesBegan(deviceX: Float, deviceY: Float) {
        lastX = deviceX
        lastY = deviceY

        startX = deviceX
        startY = deviceY

        lastTouchDistance = -1.0f

        isFlipAvailable = true
        isTouchSingle = true
    }

    /**
     * ドラッグ時のイベント
     *
     * @param deviceX タッチした画面のxの値
     * @param deviceY タッチした画面のyの値
     */
    fun touchesMoved(deviceX: Float, deviceY: Float) {
        lastX = deviceX
        lastY = deviceY
        lastTouchDistance = -1.0f
        isTouchSingle = true
    }

    /**
     * ドラッグ時のイベント
     *
     * @param deviceX1 1つ目のタッチした画面のxの値
     * @param deviceY1 1つ目のタッチした画面のyの値
     * @param deviceX2 2つ目のタッチした画面のxの値
     * @param deviceY2 2つ目のタッチした画面のyの値
     */
    fun touchesMoved(deviceX1: Float, deviceY1: Float, deviceX2: Float, deviceY2: Float) {
        val distance = calculateDistance(deviceX1, deviceY1, deviceX2, deviceY2)
        val centerX = (deviceX1 + deviceX2) * 0.5f
        val centerY = (deviceY1 + deviceY2) * 0.5f

        if (lastTouchDistance > 0.0f) {
            scale = (distance / lastTouchDistance).toDouble().pow(0.75).toFloat()
            deltaX = calculateMovingAmount(deviceX1 - lastX1, deviceX2 - lastX2)
            deltaX = calculateMovingAmount(deviceY1 - lastY1, deviceY2 - lastY2)
        } else {
            scale = 1.0f
            deltaX = 0.0f
            deltaY = 0.0f
        }

        lastX = centerX
        lastY = centerY
        lastX1 = deviceX1
        lastY1 = deviceY1
        lastX2 = deviceX2
        lastY2 = deviceY2
        lastTouchDistance = distance
        isTouchSingle = false
    }

    /**
     * フリックの距離を測定する
     *
     * @return フリック距離
     */
    fun calculateGetFlickDistance(): Float {
        return calculateDistance(startX, startY, lastX, lastY)
    }

    /**
     * 点1から点2への距離を求める
     *
     * @param x1 1つ目のタッチした画面のxの値
     * @param y1 1つ目のタッチした画面のyの値
     * @param x2 1つ目のタッチした画面のxの値
     * @param y2 1つ目のタッチした画面のyの値
     * @return 2点の距離
     */
    private fun calculateDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        return sqrt(((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)).toDouble()).toFloat()
    }

    /**
     * 2つの値から、移動量を求める
     * 違う方向の場合は移動量0。同じ方向の場合は、絶対値が小さい方の値を参照する
     *
     * @param v1 1つ目の移動量
     * @param v2 2つ目の移動量
     * @return 小さい方の移動量
     */
    private fun calculateMovingAmount(v1: Float, v2: Float): Float {
        if ((v1 > 0.0f) != (v2 > 0.0f)) {
            return 0.0f
        }

        val sign = if (v1 > 0.0f) 1.0f else -1.0f
        val absoluteValue1 = abs(v1)
        val absoluteValue2 = abs(v2)

        return sign * (min(absoluteValue1, absoluteValue2))
    }
}
