/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */


package com.live2d.sdk.cubism.framework.type;

/**
 * This class defines a rectangle form(a coordinate and a length is float value)
 */
public class csmRectF {
    /**
     * Create the new CubismRectangle instance.
     *
     * @return CubismRectangle instance
     */
    public static csmRectF create() {
        return new csmRectF();
    }

    /**
     * Create the new CubismRectangle instance with each parameter.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @param w width
     * @param h height
     * @return CubismRectangle instance
     */
    public static csmRectF create(
        float x,
        float y,
        float w,
        float h
    ) {
        return new csmRectF(x, y, w, h);
    }

    /**
     * Create the new CubismRectangle instance.
     * This method works the same way as a copy constructor.
     *
     * @param r CubismRectangle instance to be copied
     * @return CubismRectangle instance
     */
    public static csmRectF create(csmRectF r) {
        return new csmRectF(r);
    }


    /**
     * Get the x-coordinate at center of this rectangle.
     *
     * @return the x-coord at center of this rect.
     */
    public float getCenterX() {
        return x + 0.5f * width;
    }

    /**
     * Get the y-coordinate at center of this rectangle.
     *
     * @return the y-coord at center of this rect
     */
    public float getCenterY() {
        return y + 0.5f * height;
    }

    /**
     * Get the x-coordinate at right end of this rectangle.
     *
     * @return x-coord at right end
     */
    public float getRight() {
        return x + width;
    }

    /**
     * Get the y-coordinate at bottom of this rectangle.
     *
     * @return y-coord at bottom
     */
    public float getBottom() {
        return y + height;
    }

    /**
     * Set a value to this rectangle.
     */
    public void setRect(csmRectF r) {
        x = r.getX();
        y = r.getY();
        width = r.getWidth();
        height = r.getHeight();
    }

    /**
     * Scale width and height with the center of this rectangle as axis.
     *
     * @param w the amount of scaling into width
     * @param h the amount of scaling into height
     */
    public void expand(final float w, final float h) {
        x -= w;
        y -= h;
        width += w * 2.0f;
        height += h * 2.0f;
    }

    /**
     * Get this x-coordinate
     *
     * @return x-coord
     */
    public float getX() {
        return x;
    }

    /**
     * Set x-coordinate to this one.
     *
     * @param x x-coord
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Get this y-coordinate
     *
     * @return y-coord
     */
    public float getY() {
        return y;
    }

    /**
     * Set y-coordinate to this one.
     *
     * @param y y-coord
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Get the width of this.
     *
     * @return width
     */
    public float getWidth() {
        return width;
    }

    /**
     * Set the width to this one.
     *
     * @param width width set to this
     */
    public void setWidth(float width) {
        this.width = width;
    }

    /**
     * Get the height of this.
     *
     * @return height
     */
    public float getHeight() {
        return height;
    }

    /**
     * Set the height to this one.
     *
     * @param height height set to this
     */
    public void setHeight(float height) {
        this.height = height;
    }

    /**
     * Constructor
     */
    private csmRectF() {}

    /**
     * Constructor with each value.
     *
     * @param x left end x-coord
     * @param y top end y-coord
     * @param w width
     * @param h height
     */
    private csmRectF(
        final float x,
        final float y,
        final float w,
        final float h
    ) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    private csmRectF(csmRectF r) {
        setRect(r);
    }

    /**
     * Left end x-coordinate
     */
    private float x;
    /**
     * Top end y-coordinate
     */
    private float y;
    /**
     * Width
     */
    private float width;
    /**
     * Height
     */
    private float height;
}
