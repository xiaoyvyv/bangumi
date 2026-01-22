/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.utils.jsonparser;

/**
 * This class expresses a boolean value.
 */
class CubismJsonBoolean extends ACubismJsonValue {
    /**
     * Get a boolean instance
     *
     * @param value a boolean value
     * @return a JSON boolean value
     */
    public static CubismJsonBoolean valueOf(final boolean value) {
        return new CubismJsonBoolean(value);
    }

    @Override
    public String getString(String defaultValue, String indent) {
        return stringBuffer;
    }

    @Override
    public boolean toBoolean() {
        return value;
    }

    @Override
    public boolean toBoolean(boolean defaultValue) {
        return value;
    }

    @Override
    public boolean isBoolean() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CubismJsonBoolean that = (CubismJsonBoolean) o;

        return value == that.value;
    }

    @Override
    public int hashCode() {
        return (value ? 1 : 0);
    }

    /**
     * Private constructor
     *
     * @param value a boolean value
     */
    private CubismJsonBoolean(boolean value) {
        this.value = value;
        stringBuffer = String.valueOf(value);
    }

    /**
     * A boolean value
     */
    private final boolean value;
}
