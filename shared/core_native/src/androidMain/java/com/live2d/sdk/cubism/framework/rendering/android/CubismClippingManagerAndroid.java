/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.rendering.android;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

import com.live2d.sdk.cubism.framework.model.CubismModel;
import com.live2d.sdk.cubism.framework.rendering.ACubismClippingManager;
import com.live2d.sdk.cubism.framework.type.csmRectF;

import java.io.Closeable;

/**
 * クリッピングマスクの処理を実行するクラス
 */
class CubismClippingManagerAndroid extends ACubismClippingManager<
        CubismClippingContextAndroid,
        CubismOffscreenSurfaceAndroid
        > implements Closeable {
    /**
     * コンストラクタ
     */
    public CubismClippingManagerAndroid() {
        super();
    }

    /**
     * クリッピングコンテキストを作成する。モデル描画時に実行する。
     *
     * @param model        モデルのインスタンス
     * @param renderer     レンダラーのインスタンス
     * @param lastFBO      フレームバッファ
     * @param lastViewport ビューポート
     */
    public void setupClippingContext(CubismModel model, CubismRendererAndroid renderer, int[] lastFBO, int[] lastViewport) {
        // Prepare all clipping.
        // Set only once when using the same clip (or a group of clips if there are multiple clips).
        int usingClipCount = 0;
        for (int i = 0; i < clippingContextListForMask.size(); i++) {
            CubismClippingContextAndroid clipContext = clippingContextListForMask.get(i);

            // Calculate the rectangle that encloses the entire group of drawing objects that use this clip.
            calcClippedDrawTotalBounds(model, clipContext);

            if (clipContext.isUsing) {
                // Count as in use.
                usingClipCount++;
            }
        }

        if (!(usingClipCount > 0)) {
            return;
        }

        // Process of creating mask.
        // Set up a viewport with the same size as the generated MaskBuffer.
        glViewport(0, 0, (int) clippingMaskBufferSize.x, (int) clippingMaskBufferSize.y);

        // 後の計算のためにインデックスの最初をセットする。
        currentMaskBuffer = renderer.getMaskBuffer(0);

        // マスク描画処理
        currentMaskBuffer.beginDraw(lastFBO);

        // バッファをクリアする
        renderer.preDraw();

        // Determine the layout of each mask.
        setupLayoutBounds(usingClipCount);

        // サイズがレンダーテクスチャの枚数と合わない場合は合わせる。
        if (clearedMaskBufferFlags.length != renderTextureCount) {
            clearedMaskBufferFlags = new boolean[renderTextureCount];
        }
        // マスクのクリアフラグを毎フレーム開始時に初期化する。
        else {
            for (int i = 0; i < renderTextureCount; i++) {
                clearedMaskBufferFlags[i] = false;
            }
        }

        // ---------- Mask Drawing Process -----------
        // Actually generate the masks.
        // Determine how to layout and draw all the masks, and store them in ClipContext and ClippedDrawContext.
        for (int j = 0; j < clippingContextListForMask.size(); j++) {
            CubismClippingContextAndroid clipContext = clippingContextListForMask.get(j);

            // The enclosing rectangle in logical coordinates of all drawing objects that use this mask.
            csmRectF allClippedDrawRect = clipContext.allClippedDrawRect;
            // Fit the mask in here.
            csmRectF layoutBoundsOnTex01 = clipContext.layoutBounds;

            final float margin = 0.05f;

            // clipContextに設定したオフスクリーンサーフェスをインデックスで取得
            final CubismOffscreenSurfaceAndroid clipContextOffscreenSurface = renderer.getMaskBuffer(clipContext.bufferIndex);

            // 現在のオフスクリーンサーフェスがclipContextのものと異なる場合
            if (currentMaskBuffer != clipContextOffscreenSurface) {
                currentMaskBuffer.endDraw();
                currentMaskBuffer = clipContextOffscreenSurface;

                // マスク用RenderTextureをactiveにセット。
                currentMaskBuffer.beginDraw(lastFBO);

                // バッファをクリアする。
                renderer.preDraw();
            }


            // Use a rectangle on the model coordinates with margins as appropriate.
            tmpBoundsOnModel.setRect(allClippedDrawRect);

            tmpBoundsOnModel.expand(
                    allClippedDrawRect.getWidth() * margin,
                    allClippedDrawRect.getHeight() * margin
            );

            // ######## It is best to keep the size to a minimum, rather than using the entire allocated space.
            // Find the formula for the shader. If rotation is not taken into account, the formula is as follows.
            // movePeriod' = movePeriod * scaleX + offX     [[ movePeriod' = (movePeriod - tmpBoundsOnModel.movePeriod)*scale + layoutBoundsOnTex01.movePeriod ]]
            float scaleX = layoutBoundsOnTex01.getWidth() / tmpBoundsOnModel.getWidth();
            float scaleY = layoutBoundsOnTex01.getHeight() / tmpBoundsOnModel.getHeight();

            // Calculate the matrix to be used for mask generation.
            createMatrixForMask(false, layoutBoundsOnTex01, scaleX, scaleY);

            clipContext.matrixForMask.setMatrix(tmpMatrixForMask);
            clipContext.matrixForDraw.setMatrix(tmpMatrixForDraw);

            // 実際の描画を行う。
            final int clipDrawCount = clipContext.clippingIdCount;
            for (int i = 0; i < clipDrawCount; i++) {
                final int clipDrawIndex = clipContext.clippingIdList[i];

                // If vertex information is not updated and reliable, pass drawing.
                if (!model.getDrawableDynamicFlagVertexPositionsDidChange(clipDrawIndex)) {
                    continue;
                }

                renderer.isCulling(model.getDrawableCulling(clipDrawIndex));

                // マスクがクリアされていないなら処理する。
                if (!clearedMaskBufferFlags[clipContext.bufferIndex]) {
                    // マスクをクリアする。
                    // (仮仕様) 1が無効（描かれない）領域、0が有効（描かれる）領域。（シェーダーCd*Csで0に近い値をかけてマスクを作る。1をかけると何も起こらない）
                    glClearColor(1f, 1f, 1f, 1f);
                    glClear(GL_COLOR_BUFFER_BIT);
                    clearedMaskBufferFlags[clipContext.bufferIndex] = true;
                }

                // Apply this special transformation to draw it.
                // Switching channel is also needed.(A,R,G,B)
                renderer.setClippingContextBufferForMask(clipContext);

                renderer.drawMeshAndroid(
                        model,
                        clipDrawIndex
                );
            }
        }

        // --- Post Processing ---
        // Return the drawing target
        currentMaskBuffer.endDraw();
        renderer.setClippingContextBufferForMask(null);

        glViewport(lastViewport[0], lastViewport[1], lastViewport[2], lastViewport[3]);
    }
}
