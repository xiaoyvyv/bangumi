/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */


package com.live2d.sdk.cubism.framework.utils.jsonparser;

public class CubismJsonString extends ACubismJsonValue {
    /**
     * stringのインスタンスを得る
     *
     * @return stringのインスタンス
     *
     * @throws IllegalArgumentException If the given value is 'null'.
     */
    public static CubismJsonString valueOf(String value) {
        if (value == null) {
            throw new IllegalArgumentException("The value is null.");
        }
        return new CubismJsonString(value);
    }

    @Override
    public String getString(String defaultValue, String indent) {
        return stringBuffer;
    }

    @Override
    public boolean isString() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CubismJsonString that = (CubismJsonString) o;

        return stringBuffer != null ? stringBuffer.equals(that.stringBuffer) : that.stringBuffer == null;
    }

    @Override
    public int hashCode() {
        return stringBuffer != null ? stringBuffer.hashCode() : 0;
    }

    private CubismJsonString(String value) {
        stringBuffer = value;
    }
}
