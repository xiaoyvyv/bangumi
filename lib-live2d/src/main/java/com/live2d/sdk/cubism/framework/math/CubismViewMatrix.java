/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.math;

/**
 * Class of 4x4 matrices that can be used to reposition the camera.
 */
public class CubismViewMatrix extends CubismMatrix44 {
    public CubismViewMatrix() {}

    /**
     * Move for the given arguments.
     * The amount of movement is adjusted.
     *
     * @param x amount of X-axis movement
     * @param y amount of Y-axis movement
     */
    public void adjustTranslate(float x, float y) {
        if (tr[0] * maxLeft + (tr[12] + x) > screenLeft) {
            x = screenLeft - tr[0] * maxLeft - tr[12];
        }

        if (tr[0] * maxRight + (tr[12] + x) < screenRight) {
            x = screenRight - tr[0] * maxRight - tr[12];
        }

        if (tr[5] * maxTop + (tr[13] + y) < screenTop) {
            y = screenTop - tr[5] * maxTop - tr[13];
        }

        if (tr[5] * maxBottom + (tr[13] + y) > screenBottom) {
            y = screenBottom - tr[5] * maxBottom - tr[13];
        }

        float[] tr = {
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            x, y, 0.0f, 1.0f
        };
        multiply(tr, this.tr, this.tr);
    }

    /**
     * Scale with the given arguments.
     * The amount of scaling is adjusted.
     *
     * @param cx center position of X-axis to be scaled
     * @param cy center position of Y-axis to be scaled
     * @param scale scaling rate
     */
    public void adjustScale(final float cx, final float cy, float scale) {
        float maxScale = getMaxScale();
        float minScale = getMinScale();

        float targetScale = scale * tr[0];

        if (targetScale < minScale) {
            if (tr[0] > 0.0f) {
                scale = minScale / tr[0];
            }
        } else if (targetScale > maxScale) {
            if (tr[0] > 0.0f) {
                scale = maxScale / tr[0];
            }
        }

        float[] tr1 = {
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            cx, cy, 0.0f, 1.0f
        };
        float[] tr2 = {
            scale, 0.0f, 0.0f, 0.0f,
            0.0f, scale, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
        };
        float[] tr3 = {
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            -cx, -cy, 0.0f, 1.0f
        };

        multiply(tr3, tr, tr);
        multiply(tr2, tr, tr);
        multiply(tr1, tr, tr);
    }

    /**
     * Get the X-axis position of the left side of the logical coordinate corresponding to the device.
     *
     * @return X-axis position of the left side of the logical coordinate corresponding to the device
     */
    public float getScreenLeft() {
        return screenLeft;
    }

    /**
     * Get the X-axis position of the right side of the logical coordinate corresponding to the device.
     *
     * @return X-axis position of the right side of the logical coordinate corresponding to the device
     */
    public float getScreenRight() {
        return screenRight;
    }

    /**
     * Get the Y-axis position of the top side of the logical coordinate corresponding to the device.
     *
     * @return Y-axis position of the top side of the logical coordinate corresponding to the device
     */
    public float getScreenTop() {
        return screenTop;
    }

    /**
     * Get the Y-axis position of the bottom side of the logical coordinate corresponding to the device.
     *
     * @return Y-axis position of the bottom side of the logical coordinate corresponding to the device
     */
    public float getScreenBottom() {
        return screenBottom;
    }

    /**
     * Get the maximum X-axis position of the left side.
     *
     * @return maximum X-axis position of the left side
     */
    public float getMaxLeft() {
        return maxLeft;
    }

    /**
     * Get the maximum X-axis position of the right side.
     *
     * @return maximum X-axis position of the right side
     */
    public float getMaxRight() {
        return maxRight;
    }

    /**
     * Get the maximum Y-axis position of the top side.
     *
     * @return maximum Y-axis position of the top side
     */
    public float getMaxTop() {
        return maxTop;
    }

    /**
     * Get the maximum Y-axis position of the bottom side.
     *
     * @return maximum Y-axis position of the bottom side
     */
    public float getMaxBottom() {
        return maxBottom;
    }

    /**
     * Get the maximum scaling.
     *
     * @return maximum scaling
     */
    public float getMaxScale() {
        return maxScale;
    }

    /**
     * Set the maximum scaling.
     *
     * @param maxScale maximum scaling
     */
    public void setMaxScale(float maxScale) {
        this.maxScale = maxScale;
    }

    /**
     * Check to see if the magnification ratio is at its maximum.
     *
     * @return Return true if the scaling factor is at its maximum.
     */
    public boolean isMaxScale() {
        return getScaleX() >= maxScale;
    }

    /**
     * Get the minimum scaling.
     *
     * @return minimum scaling
     */
    public float getMinScale() {
        return minScale;
    }

    /**
     * Set the minimum scaling.
     *
     * @param minScale minimum scaling
     */
    public void setMinScale(float minScale) {
        this.minScale = minScale;
    }

    /**
     * Check to see if the magnification ratio is at its minimum.
     *
     * @return Return true if the scaling factor is at its minimum.
     */
    public boolean isMinScale() {
        return getScaleX() <= minScale;
    }


    /**
     * Set the range on the logical coordinates corresponding to the device.
     *
     * @param left position of X-axis on the left side
     * @param right position of X-axis on the right side
     * @param bottom position of Y-axis on the bottom side
     * @param top position of Y-axis on the top side
     */
    public void setScreenRect(float left, float right, float bottom, float top) {
        screenLeft = left;
        screenRight = right;
        screenBottom = bottom;
        screenTop = top;
    }

    /**
     * Set the movable range on the logical coordinates corresponding to the device.
     *
     * @param left position of X-axis on the left side
     * @param right position of X-axis on the right side
     * @param bottom position of Y-axis on the bottom side
     * @param top position of Y-axis on the top side
     */
    public void setMaxScreenRect(float left, float right, float bottom, float top) {
        maxLeft = left;
        maxRight = right;
        maxBottom = bottom;
        maxTop = top;
    }

    /**
     * range on logical coordinates corresponding to device (left side X-axis position)
     */
    private float screenLeft;
    /**
     * range on logical coordinates corresponding to device (right side X-axis position)
     */
    private float screenRight;
    /**
     * range on logical coordinates corresponding to device (top side Y-axis position)
     */
    private float screenBottom;
    /**
     * range on logical coordinates corresponding to device (bottom side Y-axis position)
     */
    private float screenTop;
    /**
     * Moveable range on logical coordinates (left side X-axis position)
     */
    private float maxLeft;
    /**
     * Moveable range on logical coordinates (right side X-axis position)
     */
    private float maxRight;
    /**
     * Moveable range on logical coordinates (top side Y-axis position)
     */
    private float maxBottom;
    /**
     * Moveable range on logical coordinates (bottom side Y-axis position)
     */
    private float maxTop;
    /**
     * maximum value of scaling rate
     */
    private float maxScale;
    /**
     * minimum value of scaling rate
     */
    private float minScale;
}
