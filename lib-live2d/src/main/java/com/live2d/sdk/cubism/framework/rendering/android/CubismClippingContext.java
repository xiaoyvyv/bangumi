/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.rendering.android;

import com.live2d.sdk.cubism.framework.math.CubismMatrix44;
import com.live2d.sdk.cubism.framework.type.csmRectF;

import java.util.ArrayList;
import java.util.List;

/**
 * Context of Clipping Mask
 */
class CubismClippingContext {
    /**
     * Constructor
     */
    public CubismClippingContext(
        CubismClippingManagerAndroid manager,
        final int[] clippingDrawableIndices,
        int clipCount
    ) {
        if (manager == null || clippingDrawableIndices == null) {
            throw new IllegalArgumentException("manager or clippingDrawableIndices is null.");
        }

        owner = manager;

        // クリップしている（＝マスク用の）Drawableのインデックスリスト
        clippingIdList = clippingDrawableIndices;

        // マスクの数
        clippingIdCount = clipCount;
    }

    /**
     * このマスクにクリップされる描画オブジェクトを追加する
     *
     * @param drawableIndex クリッピング対象に追加する描画オブジェクトのインデックス
     */
    public void addClippedDrawable(int drawableIndex) {
        clippedDrawableIndexList.add(drawableIndex);
    }

    /**
     * このマスクを管理するマネージャのインスタンスを取得する。
     *
     * @return クリッピングマネージャのインスタンス
     */
    public CubismClippingManagerAndroid getClippingManager() {
        return owner;
    }

    /**
     * クリッピングマスクのIDリスト
     */
    public final int[] clippingIdList;
    /**
     * 現在の描画状態でマスクの準備が必要ならtrue
     */
    public boolean isUsing;
    /**
     * クリッピングマスクの数
     */
    public final int clippingIdCount;
    /**
     * RGBAのいずれのチャンネルにこのクリップを配置するか(0:R, 1:G, 2:B, 3:A)
     */
    public int layoutChannelNo;
    /**
     * マスク用チャンネルのどの領域にマスクを入れるか(View座標-1..1, UVは0..1に直す)
     */
    public final csmRectF layoutBounds = csmRectF.create();
    /**
     * このクリッピングで、クリッピングされる全ての描画オブジェクトの囲み矩形（毎回更新）
     */
    public final csmRectF allClippedDrawRect = csmRectF.create();
    /**
     * マスクの位置計算結果を保持する行列
     */
    public final CubismMatrix44 matrixForMask = CubismMatrix44.create();
    /**
     * 描画オブジェクトの位置計算結果を保持する行列
     */
    public final CubismMatrix44 matrixForDraw = CubismMatrix44.create();
    /**
     * このマスクにクリップされる描画オブジェクトのリスト
     */
    public final List<Integer> clippedDrawableIndexList = new ArrayList<Integer>();
    /**
     * このマスクが割り当てられるレンダーテクスチャ（フレームバッファ）やカラーバッファのインデックス
     */
    public int bufferIndex;

    /**
     * このマスクを管理しているマネージャのインスタンス
     */
    private final CubismClippingManagerAndroid owner;
}
