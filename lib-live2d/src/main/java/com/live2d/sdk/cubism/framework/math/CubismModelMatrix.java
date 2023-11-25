/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.math;

import java.util.Map;

/**
 * 4x4 matrix class for setting model coordinates.
 */
public class CubismModelMatrix extends CubismMatrix44 {
    /**
     * Create the new CubismModelMatrix instance with the width and height passed as arguments.
     *
     * @param w width
     * @param h height
     * @return CubismModelMatrix instance with the width and height
     *
     * @throws IllegalArgumentException if arguments equals 0 or are less than 0
     */
    public static CubismModelMatrix create(float w, float h) {
        if (w <= 0 || h <= 0) {
            throw new IllegalArgumentException("width or height equals 0 or is less than 0.");
        }
        return new CubismModelMatrix(w, h);
    }


    /**
     * Create the new CubismModelMatrix instance from the CubismModelMatrix instance.
     * It works the same way as a copy constructor.
     *
     * @param modelMatrix CubismModelMatrix instance to be copied
     * @return Copied CubismModelMatrix instance
     */
    public static CubismModelMatrix create(CubismModelMatrix modelMatrix) {
        return new CubismModelMatrix(modelMatrix);
    }

    /**
     * Set the width.
     *
     * @param w width
     */
    public void setWidth(float w) {
        final float scaleX = w / width;
        scale(scaleX, scaleX);
    }

    /**
     * Set the height.
     *
     * @param h height
     */
    public void setHeight(float h) {
        final float scaleX = h / height;
        scale(scaleX, scaleX);
    }

    /**
     * Set the position.
     *
     * @param x X-axis position
     * @param y Y-axis position
     */
    public void setPosition(float x, float y) {
        translate(x, y);
    }

    /**
     * Set the center position.
     * Be sure to set the width or height before using this method.
     *
     * @param x center position of X-axis
     * @param y center position of Y-axis
     */
    public void setCenterPosition(float x, float y) {
        centerX(x);
        centerY(y);
    }

    /**
     * Set the position of the upper edge.
     *
     * @param y the position of the upper edge
     */
    public void top(float y) {
        setY(y);
    }

    /**
     * Set the position of the bottom edge.
     *
     * @param y the position of the bottom edge
     */
    public void bottom(float y) {
        final float h = height * getScaleY();
        translateY(y - h);
    }

    /**
     * Set the position of the left edge.
     *
     * @param x the position of the left edge
     */
    public void left(float x) {
        setX(x);
    }

    /**
     * Set the position of the right edge.
     *
     * @param x the position of the right edge
     */
    public void right(float x) {
        final float w = width * getScaleX();
        translateX(x - w);
    }

    /**
     * Set the center position of X-axis.
     *
     * @param x center position of X-axis
     */
    public void centerX(float x) {
        final float w = width * getScaleX();
        translateX(x - (w / 2.0f));
    }

    /**
     * Set the position of X-axis.
     *
     * @param x position of X-axis
     */
    public void setX(float x) {
        translateX(x);
    }

    /**
     * Set the center position of Y-axis.
     *
     * @param y center position of Y-axis
     */
    public void centerY(float y) {
        final float h = height * getScaleY();
        translateY(y - (h / 2.0f));
    }

    /**
     * Set the position of Y-axis.
     *
     * @param y position of Y-axis
     */
    public void setY(float y) {
        translateY(y);
    }

    /**
     * Set position from layout information.
     *
     * @param layout layout information
     */
    public void setupFromLayout(Map<String, Float> layout) {
        final String keyWidth = "width";
        final String keyHeight = "height";
        final String keyX = "x";
        final String keyY = "y";
        final String keyCenterX = "center_x";
        final String keyCenterY = "center_y";
        final String keyTop = "top";
        final String keyBottom = "bottom";
        final String keyLeft = "left";
        final String keyRight = "right";

        for (Map.Entry<String, Float> entry : layout.entrySet()) {
            String key = entry.getKey();
            if (key.equals(keyWidth)) {
                setWidth(entry.getValue());
            } else if (key.equals(keyHeight)) {
                setHeight(entry.getValue());
            }
        }

        for (Map.Entry<String, Float> entry : layout.entrySet()) {
            String key = entry.getKey();
            float value = entry.getValue();

            if (key.equals(keyX)) {
                setX(value);
            } else if (key.equals(keyY)) {
                setY(value);
            } else if (key.equals(keyCenterX)) {
                centerX(value);
            } else if (key.equals(keyCenterY)) {
                centerY(value);
            } else if (key.equals(keyTop)) {
                top(value);
            } else if (key.equals(keyBottom)) {
                bottom(value);
            } else if (key.equals(keyLeft)) {
                left(value);
            } else if (key.equals(keyRight)) {
                right(value);
            }
        }
    }

    /**
     * Constructor
     */
    private CubismModelMatrix(float w, float h) {
        super();
        width = w;
        height = h;

        setHeight(2.0f);
    }

    /**
     * Copy constructor
     *
     * @param modelMatrix model matrix to be copied
     */
    private CubismModelMatrix(CubismModelMatrix modelMatrix) {
        super();
        System.arraycopy(modelMatrix.tr, 0, this.tr, 0, 16);
        width = modelMatrix.width;
        height = modelMatrix.height;
    }

    /**
     * width
     */
    private final float width;
    /**
     * height
     */
    private final float height;
}
