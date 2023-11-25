/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.motion;

public interface ICubismMotionEventFunction {
    void apply(
        CubismMotionQueueManager caller,
        String eventValue,
        Object customData
    );
}
