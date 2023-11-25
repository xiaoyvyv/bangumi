/*
 *
 *  * Copyright(c) Live2D Inc. All rights reserved.
 *  *
 *  * Use of this source code is governed by the Live2D Open Software license
 *  * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 *
 */

package com.live2d.sdk.cubism.framework.utils.jsonparser;

class CubismJsonErrorValue extends ACubismJsonValue {
    @Override
    public String getString(String defaultValue, String indent) {
        return stringBuffer;
    }

    @Override
    public boolean isError() {
        return true;
    }

    @Override
    public ACubismJsonValue setErrorNotForClientCall(String s) {
        this.stringBuffer = s;
        return this;
    }
}
