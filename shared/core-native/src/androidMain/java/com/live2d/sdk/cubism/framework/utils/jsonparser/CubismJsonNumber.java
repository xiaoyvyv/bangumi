/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.utils.jsonparser;

/**
 * This class expresses a number value.
 * It has double type number.
 */
class CubismJsonNumber extends ACubismJsonValue {
    /**
     * Get an instance of number type.
     *
     * @param value number value
     * @return an instance of number type
     */
    public static CubismJsonNumber valueOf(double value) {
        return new CubismJsonNumber(value);
    }

    @Override
    public String getString(String defaultValue, String indent) {
        return stringBuffer;
    }


    @Override
    public int toInt() {
        return (int) value;
    }

    @Override
    public int toInt(int defaultValue) {
        return (int) value;
    }

    @Override
    public float toFloat() {
        return (float) value;
    }

    @Override
    public float toFloat(float defaultValue) {
        return (float) value;
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CubismJsonNumber that = (CubismJsonNumber) o;

        return Float.compare((float) that.value, (float) value) == 0;
    }

    @Override
    public int hashCode() {
        long temp = Float.floatToIntBits((float) value);
        return (int) (temp ^ (temp >>> 32));
    }

    /**
     * private constructor
     *
     * @param value number value
     */
    private CubismJsonNumber(double value) {
        this.value = value;
        stringBuffer = String.valueOf(value);
    }

    /**
     * number value
     */
    private final double value;
}
