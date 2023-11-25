/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.math;

/**
 * This class offers 2D vector function.
 */
public class CubismVector2 {
    /**
     * Constructor.
     */
    public CubismVector2() {}

    /**
     * Constructor.
     *
     * @param x x-value
     * @param y y-value
     */
    public CubismVector2(float x, float y) {
        set(x, y);
    }

    /**
     * Copy constructor.
     *
     * @param vec the vector which is copied
     */
    public CubismVector2(CubismVector2 vec) {
        set(vec);
    }

    /**
     * 第1引数と第2引数のベクトルを加算して、計算結果を第3引数のベクトルにコピーする。
     *
     * @param augend 被加数のベクトル
     * @param addend 加数のベクトル
     * @param dest 代入先のベクトル
     * @return 代入先のベクトル
     */
    public static CubismVector2 add(CubismVector2 augend, CubismVector2 addend, CubismVector2 dest) {
        dest.x = augend.x + addend.x;
        dest.y = augend.y + addend.y;

        return dest;
    }

    /**
     * 第1引数と第2引数のベクトルを引き算して、計算結果を第3引数のベクトルにコピーする。
     *
     * @param minuend 被減数
     * @param subtrahend 減数
     * @param dest 代入先のベクトル
     * @return 代入先のベクトル
     */
    public static CubismVector2 subtract(CubismVector2 minuend, CubismVector2 subtrahend, CubismVector2 dest) {
        dest.x = minuend.x - subtrahend.x;
        dest.y = minuend.y - subtrahend.y;

        return dest;
    }

    /**
     * 第1引数と第2引数のベクトルを掛け算して、計算結果を第3引数のベクトルにコピーする。
     *
     * @param multiplicand 被乗数
     * @param multiplier 乗数
     * @param dest 代入先のベクトル
     * @return 代入先のベクトル
     */
    public static CubismVector2 multiply(CubismVector2 multiplicand, CubismVector2 multiplier, CubismVector2 dest) {
        dest.x = multiplicand.x * multiplier.x;
        dest.y = multiplicand.y * multiplier.y;

        return dest;
    }

    /**
     * 第1引数のベクトルと第2引数のスカラーを掛け算して、計算結果を第3引数のベクトルにコピーする。
     *
     * @param multiplicand 被乗数のベクトル
     * @param multiplier 乗数のスカラー
     * @param dest 代入先のベクトル
     * @return 代入先のベクトル
     */
    public static CubismVector2 multiply(CubismVector2 multiplicand, float multiplier, CubismVector2 dest) {
        dest.x = multiplicand.x * multiplier;
        dest.y = multiplicand.y * multiplier;

        return dest;
    }

    /**
     * 第1引数と第2引数のベクトルを割り算して、計算結果を第3引数のベクトルにコピーする。
     *
     * @param dividend 被除数
     * @param divisor 除数
     * @param dest 代入先のベクトル
     * @return 代入先のベクトル
     */
    public static CubismVector2 divide(CubismVector2 dividend, CubismVector2 divisor, CubismVector2 dest) {
        dest.x = dividend.x / divisor.x;
        dest.y = dividend.y / divisor.y;

        return dest;
    }

    /**
     * 第1引数のベクトルと第2引数のスカラーを割り算して、計算結果を第3引数のベクトルにコピーする。
     *
     * @param dividend 被除数のベクトル
     * @param divisor 除数のスカラー
     * @param dest 代入先のベクトル
     * @return 代入先のベクトル
     */
    public static CubismVector2 divide(CubismVector2 dividend, float divisor, CubismVector2 dest) {
        dest.x = dividend.x / divisor;
        dest.y = dividend.y / divisor;

        return dest;
    }

    /**
     * Add the given vector to this vector.
     *
     * @param vec the added vector
     * @return calculated result
     */
    public CubismVector2 add(CubismVector2 vec) {
        this.x += vec.x;
        this.y += vec.y;

        return this;
    }

    /**
     * Subtract the given vector from this.
     *
     * @param vec the subtracted vector
     * @return the calculated result
     */
    public CubismVector2 subtract(CubismVector2 vec) {
        this.x -= vec.x;
        this.y -= vec.y;

        return this;
    }

    /**
     * Multiply this vector by the given vector.
     *
     * @param vec multiplier
     * @return the calculated result
     */
    public CubismVector2 multiply(CubismVector2 vec) {
        x *= vec.x;
        y *= vec.y;

        return this;
    }

    /**
     * Multiply this vector by a scalar value.
     *
     * @param scalar a scalar value
     * @return a calculated value
     */
    public CubismVector2 multiply(float scalar) {
        x *= scalar;
        y *= scalar;

        return this;
    }

    /**
     * Divide this vector by the given vector.
     *
     * @param vec divisor
     * @return the calculated result
     */
    public CubismVector2 divide(CubismVector2 vec) {
        x /= vec.x;
        y /= vec.y;

        return this;
    }

    /**
     * Divide this vector by a scalar value.
     *
     * @param scalar a scalar value
     * @return a calculated value
     */
    public CubismVector2 divide(float scalar) {
        this.x /= scalar;
        this.y /= scalar;

        return this;
    }

    /**
     * Normalize this vector.
     */
    public void normalize() {
        float length = (float) (Math.pow((x * x) + (y * y), 0.5f));

        x /= length;
        y /= length;
    }

    /**
     * Get the length of this vector.
     *
     * @return the length of this vector
     */
    public float getLength() {
        return CubismMath.sqrtF(x * x + y * y);
    }

    /**
     * Get a distance between vectors.
     *
     * @param vec a position vector
     * @return a distance between vectors
     */
    public float getDistanceWith(CubismVector2 vec) {
        return CubismMath.sqrtF(((this.x - vec.x) * (this.x - vec.x)) + ((this.y - vec.y) * (this.y - vec.y)));
    }

    /**
     * Calculate dot product
     *
     * @param vec a vector
     * @return a calculated result
     */
    public float dot(CubismVector2 vec) {
        return (this.x * vec.x) + (this.y * vec.y);
    }

    /**
     * 与えられたベクトルのx, yの値をこのベクトルのx, yに設定する。
     *
     * @param vec copied vector
     * @return this vector for chaining
     */
    public CubismVector2 set(CubismVector2 vec) {
        this.x = vec.x;
        this.y = vec.y;

        return this;
    }

    /**
     * 与えられたx, yの値をこのベクトルのx, yに設定する。
     *
     * @param x x value
     * @param y y value
     * @return this vector for chaining
     */
    public CubismVector2 set(float x, float y) {
        this.x = x;
        this.y = y;

        return this;
    }

    /**
     * Sets the components of this vector to 0.
     *
     * @return this vector for chaining
     */
    public CubismVector2 setZero() {
        this.x = 0.0f;
        this.y = 0.0f;

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CubismVector2 that = (CubismVector2) o;

        if (Float.compare(that.x, x) != 0) return false;
        return Float.compare(that.y, y) == 0;
    }

    @Override
    public int hashCode() {
        int result = (x != 0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != 0.0f ? Float.floatToIntBits(y) : 0);
        return result;
    }

    /**
     * X-value
     */
    public float x;
    /**
     * Y-value
     */
    public float y;
}
