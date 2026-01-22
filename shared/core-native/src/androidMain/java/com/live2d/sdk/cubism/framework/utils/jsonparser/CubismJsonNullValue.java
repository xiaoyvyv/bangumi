/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.utils.jsonparser;

import java.util.Objects;

/**
 * This class expresses a JSON null Value.
 * It has no fields and methods.
 */
class CubismJsonNullValue extends ACubismJsonValue {
    public CubismJsonNullValue() {
        stringBuffer = "NullValue";
    }

    @Override
    public String getString(String defaultValue, String indent) {
        return stringBuffer;
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CubismJsonNullValue that = (CubismJsonNullValue) o;

        return Objects.equals(stringBuffer, that.stringBuffer);
    }

    @Override
    public int hashCode() {
        return stringBuffer != null ? stringBuffer.hashCode() : 0;
    }


}
