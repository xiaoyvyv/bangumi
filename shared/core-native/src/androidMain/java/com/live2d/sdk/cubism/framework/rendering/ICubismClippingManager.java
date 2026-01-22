/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.rendering;

import com.live2d.sdk.cubism.framework.math.CubismVector2;
import com.live2d.sdk.cubism.framework.model.CubismModel;
import com.live2d.sdk.cubism.framework.type.csmRectF;

public interface ICubismClippingManager {
    /**
     * 実験時に1チャンネルの場合は1、RGBだけの場合は3、アルファも含める場合は4
     */
    int COLOR_CHANNEL_COUNT = 4;
    /**
     * 通常のフレームバッファ1枚あたりのマスク最大数
     */
    int CLIPPING_MASK_MAX_COUNT_ON_DEFAULT = 36;
    /**
     * フレームバッファが2枚以上ある場合のフレームバッファ1枚あたりのマスク最大数
     */
    int CLIPPING_MASK_MAX_COUNT_ON_MULTI_RENDER_TEXTURE = 32;

    /**
     * マネージャーの初期化処理
     * クリッピングマスクを使う描画オブジェクトの登録を行う。
     *
     * @param type            レンダラーの種類
     * @param model           モデルのインスタンス
     * @param maskBufferCount バッファの生成数
     */
    void initialize(
            CubismRenderer.RendererType type,
            CubismModel model,
            int maskBufferCount
    );

    /**
     * 高精細マスク処理用の行列を計算する。
     *
     * @param model         モデルのインスタンス
     * @param isRightHanded 処理が右手系かどうか。右手系ならtrue
     */
    void setupMatrixForHighPrecision(CubismModel model, boolean isRightHanded);

    /**
     * マスク作成・描画用の行列を作成する。
     *
     * @param isRightHanded       座標を右手系として扱うかどうか。右手系として扱うならtrue
     * @param layoutBoundsOnTex01 マスクを収める領域
     * @param scaleX              描画オブジェクトのX方向への伸縮率
     * @param scaleY              描画オブジェクトのY方向への伸縮率
     */
    void createMatrixForMask(
            boolean isRightHanded,
            csmRectF layoutBoundsOnTex01,
            float scaleX,
            float scaleY
    );

    /**
     * クリッピングコンテキストを配置するレイアウト。
     * 1つのレンダーテクスチャを極力いっぱいに使ってマスクをレイアウトする。
     * マスクグループの数が4以下ならRGBA各チャンネルに1つずつマスクを配置し、5以上6以下ならRGBAを2, 2, 1, 1と配置する。
     *
     * @param usingClipCount 配置するクリッピングコンテキストの数
     */
    void setupLayoutBounds(int usingClipCount);

    /**
     * クリッピングマスクバッファのサイズを取得する。
     *
     * @return クリッピングマスクバッファのサイズ
     */
    CubismVector2 getClippingMaskBufferSize();

    /**
     * クリッピングマスクバッファのサイズを設定する。
     *
     * @param width  クリッピングマスクバッファの幅
     * @param height クリッピングマスクバッファの高さ
     */
    void setClippingMaskBufferSize(float width, float height);

    /**
     * このバッファのレンダーテクスチャの枚数を取得する。
     *
     * @return このバッファのレンダーテクスチャの枚数
     */
    int getRenderTextureCount();

    /**
     * カラーチャンネル（RGBA）のフラグを取得する。
     *
     * @param channelIndex カラーチャンネル（RGBA）の番号（0:R, 1:G, 2:B, 3:A）
     * @return カラーチャンネルのフラグ
     */
    CubismRenderer.CubismTextureColor getChannelFlagAsColor(int channelIndex);
}
