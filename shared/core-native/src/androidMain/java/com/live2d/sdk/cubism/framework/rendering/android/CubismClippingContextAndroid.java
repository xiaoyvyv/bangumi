/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.rendering.android;

import com.live2d.sdk.cubism.framework.rendering.ACubismClippingContext;
import com.live2d.sdk.cubism.framework.rendering.ICubismClippingManager;

/**
 * Context of Clipping Mask
 */
public class CubismClippingContextAndroid extends ACubismClippingContext {
    /**
     * コンストラクタ
     *
     * @param manager                このクリッピングコンテキストを保持するマネージャーのインスタンス
     * @param clipingDrawableIndices クリッピングマスクのIDの配列
     * @param clipCount              クリッピングマスクの数
     */
    public CubismClippingContextAndroid(
            ICubismClippingManager manager,
            int[] clipingDrawableIndices,
            int clipCount
    ) {
        super(manager, clipingDrawableIndices, clipCount);
    }

    /**
     * このマスクを管理するマネージャーのインスタンスを返す。
     *
     * @return クリッピングマネージャーのインスタンス。
     * @throws ClassCastException 具象クラスへのキャストに失敗しました。
     */
    @Override
    public CubismClippingManagerAndroid getClippingManager() {
        // キャストに失敗する可能性があります。その場合は内部でClassCastExceptionが送出されます。
        return (CubismClippingManagerAndroid) owner;
    }
}
