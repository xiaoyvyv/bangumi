/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.xiaoyv.common.widget.musume;

/**
 * タッチマネージャー
 */
public class TouchManager {
    /**
     * タッチ開始時のイベント
     *
     * @param deviceX タッチした画面のxの値
     * @param deviceY タッチした画面のyの値
     */
    public void touchesBegan(float deviceX, float deviceY) {
        lastX = deviceX;
        lastY = deviceY;

        startX = deviceX;
        startY = deviceY;

        lastTouchDistance = -1.0f;

        isFlipAvailable = true;
        isTouchSingle = true;
    }

    /**
     * ドラッグ時のイベント
     *
     * @param deviceX タッチした画面のxの値
     * @param deviceY タッチした画面のyの値
     */
    public void touchesMoved(float deviceX, float deviceY) {
        lastX = deviceX;
        lastY = deviceY;
        lastTouchDistance = -1.0f;
        isTouchSingle = true;
    }

    /**
     * ドラッグ時のイベント
     *
     * @param deviceX1 1つ目のタッチした画面のxの値
     * @param deviceY1 1つ目のタッチした画面のyの値
     * @param deviceX2 2つ目のタッチした画面のxの値
     * @param deviceY2 2つ目のタッチした画面のyの値
     */
    public void touchesMoved(float deviceX1, float deviceY1, float deviceX2, float deviceY2) {
        float distance = calculateDistance(deviceX1, deviceY1, deviceX2, deviceY2);
        float centerX = (deviceX1 + deviceX2) * 0.5f;
        float centerY = (deviceY1 + deviceY2) * 0.5f;

        if (lastTouchDistance > 0.0f) {
            scale = (float) Math.pow(distance / lastTouchDistance, 0.75f);
            deltaX = calculateMovingAmount(deviceX1 - lastX1, deviceX2 - lastX2);
            deltaX = calculateMovingAmount(deviceY1 - lastY1, deviceY2 - lastY2);
        } else {
            scale = 1.0f;
            deltaX = 0.0f;
            deltaY = 0.0f;
        }

        lastX = centerX;
        lastY = centerY;
        lastX1 = deviceX1;
        lastY1 = deviceY1;
        lastX2 = deviceX2;
        lastY2 = deviceY2;
        lastTouchDistance = distance;
        isTouchSingle = false;
    }

    /**
     * フリックの距離を測定する
     *
     * @return フリック距離
     */
    public float calculateGetFlickDistance() {
        return calculateDistance(startX, startY, lastX, lastY);
    }

    // ----- getter methods -----
    public float getStartX() {
        return startX;
    }

    public float getStartY() {
        return startY;
    }

    public float getLastX() {
        return lastX;
    }

    public float getLastY() {
        return lastY;
    }

    public float getLastX1() {
        return lastX1;
    }

    public float getLastY1() {
        return lastY1;
    }

    public float getLastX2() {
        return lastX2;
    }

    public float getLastY2() {
        return lastY2;
    }

    public float getLastTouchDistance() {
        return lastTouchDistance;
    }

    public float getDeltaX() {
        return deltaX;
    }

    public float getDeltaY() {
        return deltaY;
    }

    public float getScale() {
        return scale;
    }

    public boolean isTouchSingle() {
        return isTouchSingle;
    }

    public boolean isFlipAvailable() {
        return isFlipAvailable;
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
    private float calculateDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    /**
     * 2つの値から、移動量を求める
     * 違う方向の場合は移動量0。同じ方向の場合は、絶対値が小さい方の値を参照する
     *
     * @param v1 1つ目の移動量
     * @param v2 2つ目の移動量
     * @return 小さい方の移動量
     */
    private float calculateMovingAmount(float v1, float v2) {
        if ((v1 > 0.0f) != (v2 > 0.0f)) {
            return 0.0f;
        }

        float sign = v1 > 0.0f ? 1.0f : -1.0f;
        float absoluteValue1 = Math.abs(v1);
        float absoluteValue2 = Math.abs(v2);

        return sign * (Math.min(absoluteValue1, absoluteValue2));
    }

    /**
     * タッチ開始時のxの値
     */
    private float startX;
    /**
     * タッチ開始時のyの値
     */
    private float startY;
    /**
     * シングルタッチ時のxの値
     */
    private float lastX;
    /**
     * シングルタッチ時のyの値
     */
    private float lastY;
    /**
     * ダブルタッチ時の1つ目のxの値
     */
    private float lastX1;
    /**
     * ダブルタッチ時の1つ目のyの値
     */
    private float lastY1;
    /**
     * ダブルタッチ時の2つ目のxの値
     */
    private float lastX2;
    /**
     * ダブルタッチ時の2つ目のyの値
     */
    private float lastY2;
    /**
     * 2本以上でタッチしたときの指の距離
     */
    private float lastTouchDistance;
    /**
     * 前回の値から今回の値へのxの移動距離
     */
    private float deltaX;
    /**
     * 前回の値から今回の値へのyの移動距離
     */
    private float deltaY;
    /**
     * このフレームで掛け合わせる拡大率。拡大操作中以外は1
     */
    private float scale;
    /**
     * シングルタッチ時はtrue
     */
    private boolean isTouchSingle;
    /**
     * フリップが有効かどうか
     */
    private boolean isFlipAvailable;
}
