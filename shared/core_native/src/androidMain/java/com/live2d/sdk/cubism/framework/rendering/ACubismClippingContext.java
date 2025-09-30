/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.rendering;

import com.live2d.sdk.cubism.framework.math.CubismMatrix44;
import com.live2d.sdk.cubism.framework.rendering.android.CubismClippingContextAndroid;
import com.live2d.sdk.cubism.framework.type.csmRectF;

import java.util.ArrayList;
import java.util.List;

/**
 * クリッピングについての設定を保持するクラス
 * サブクラスに環境依存のフィールドを保持する。
 */
public abstract class ACubismClippingContext {
    /**
     * 渡されたレンダラーの種類に基づき、適切なクリッピングコンテキストを生成する。
     *
     * @param type                    生成するレンダラーの種類
     * @param manager                 このクリッピングコンテキストを保持するマネージャーのインスタンス
     * @param clippingDrawableIndices クリッピングマスクのIDの配列
     * @param clipCount               クリッピングマスクの数
     * @return 生成したクリッピングコンテキストのインスタンス
     */
    public static ACubismClippingContext createClippingContext(
            CubismRenderer.RendererType type,
            ICubismClippingManager manager,
            int[] clippingDrawableIndices,
            int clipCount
    ) {
        switch (type) {
            case ANDROID:
                return new CubismClippingContextAndroid(manager, clippingDrawableIndices, clipCount);
            case UNKNOWN:
            default:
                throw new IllegalArgumentException("Failed to create a clipping context. The specified renderer type may be incorrect.");
        }
    }

    /**
     * コンストラクタ
     *
     * @param manager                 このクリッピングコンテキストを保持するマネージャーのインスタンス
     * @param clippingDrawableIndices クリッピングマスクのIDの配列
     * @param clipCount               クリッピングマスクの数
     */
    public ACubismClippingContext(
            ICubismClippingManager manager,
            int[] clippingDrawableIndices,
            int clipCount
    ) {
        if (clippingDrawableIndices == null || manager == null) {
            throw new IllegalArgumentException("The argument is null.");
        }

        // クリップしている（=マスク用の）Drawableのインデックスリスト
        clippingIdList = clippingDrawableIndices;
        // マスクの数
        clippingIdCount = clipCount;
        // このコンテキストを保持するマネージャー
        owner = manager;
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
    public abstract ICubismClippingManager getClippingManager();

    /**
     * 現在の描画状態でマスクの準備が必要ならtrue
     */
    public boolean isUsing;

    /**
     * クリッピングマスクのIDの配列
     */
    public final int[] clippingIdList;

    /**
     * クリッピングマスクの数
     */
    public final int clippingIdCount;

    /**
     * RGBAのいずれのチャンネルにこのクリップを配置するか（0:R, 1:G, 2:B, 3:A）
     */
    public int layoutChannelIndex;

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
    public final List<Integer> clippedDrawableIndexList = new ArrayList<>();
    /**
     * このマスクが割り当てられるレンダーテクスチャ（フレームバッファ）やカラーバッファのインデックス
     */
    public int bufferIndex;

    /**
     * このマスクを管理しているマネージャーのインスタンス
     */
    protected ICubismClippingManager owner;
}
