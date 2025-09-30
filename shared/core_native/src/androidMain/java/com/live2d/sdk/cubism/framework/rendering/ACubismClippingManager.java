/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.rendering;

import static com.live2d.sdk.cubism.framework.CubismFramework.VERTEX_OFFSET;
import static com.live2d.sdk.cubism.framework.CubismFramework.VERTEX_STEP;
import static com.live2d.sdk.cubism.framework.utils.CubismDebug.cubismLogError;

import com.live2d.sdk.cubism.framework.math.CubismMatrix44;
import com.live2d.sdk.cubism.framework.math.CubismVector2;
import com.live2d.sdk.cubism.framework.model.CubismModel;
import com.live2d.sdk.cubism.framework.type.csmRectF;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

/**
 * クリッピングマネージャーの抽象骨格クラス
 *
 * @param <T_ClippingContext>  ACubismClippingContextを継承した型
 * @param <T_OffscreenSurface> CubismOffscreenSurface型
 */
public abstract class ACubismClippingManager<
        T_ClippingContext extends ACubismClippingContext,
        T_OffscreenSurface
        > implements Closeable, ICubismClippingManager {
    /**
     * コンストラクタ
     */
    public ACubismClippingManager() {
        CubismRenderer.CubismTextureColor tmp = new CubismRenderer.CubismTextureColor();
        tmp.r = 1.0f;
        tmp.g = 0.0f;
        tmp.b = 0.0f;
        tmp.a = 0.0f;
        channelColors.add(tmp);

        tmp = new CubismRenderer.CubismTextureColor();
        tmp.r = 0.0f;
        tmp.g = 1.0f;
        tmp.b = 0.0f;
        tmp.a = 0.0f;
        channelColors.add(tmp);

        tmp = new CubismRenderer.CubismTextureColor();
        tmp.r = 0.0f;
        tmp.g = 0.0f;
        tmp.b = 1.0f;
        tmp.a = 0.0f;
        channelColors.add(tmp);

        tmp = new CubismRenderer.CubismTextureColor();
        tmp.r = 0.0f;
        tmp.g = 0.0f;
        tmp.b = 0.0f;
        tmp.a = 1.0f;
        channelColors.add(tmp);
    }

    @Override
    public void close() {
        clippingContextListForMask.clear();
        clippingContextListForDraw.clear();

        channelColors.clear();

        if (clearedMaskBufferFlags != null) {
            clearedMaskBufferFlags = null;
        }
    }

    @Override
    public void initialize(
            CubismRenderer.RendererType type,
            CubismModel model,
            int maskBufferCount
    ) {
        renderTextureCount = maskBufferCount;

        // レンダーテクスチャのクリアフラグの配列の初期化
        clearedMaskBufferFlags = new boolean[renderTextureCount];

        final int drawableCount = model.getDrawableCount();     // 描画オブジェクトの数
        final int[][] drawableMasks = model.getDrawableMasks();     // 描画オブジェクトをマスクする描画オブジェクトのインデックスのリスト
        final int[] drawableMaskCounts = model.getDrawableMaskCounts();     // 描画オブジェクトをマスクする描画オブジェクトの数

        // クリッピングマスクを使う描画オブジェクトを全て登録する。
        // クリッピングマスクは、通常数個程度に限定して使うものとする。
        for (int i = 0; i < drawableCount; i++) {
            if (drawableMaskCounts[i] <= 0) {
                // クリッピングマスクが使用されていないアートメッシュ（多くの場合使用しない）
                clippingContextListForDraw.add(null);
                continue;
            }

            // 既にあるClipContextと同じかチェックする。
            T_ClippingContext cc = findSameClip(drawableMasks[i], drawableMaskCounts[i]);
            if (cc == null) {
                // 同一のマスクが存在していない場合は生成する。
                cc = (T_ClippingContext) ACubismClippingContext.createClippingContext(
                        type,
                        this,
                        drawableMasks[i],
                        drawableMaskCounts[i]
                );

                clippingContextListForMask.add(cc);
            }

            cc.addClippedDrawable(i);
            clippingContextListForDraw.add(cc);
        }
    }

    @Override
    public void setupMatrixForHighPrecision(CubismModel model, boolean isRightHanded) {
        // 全てのクリッピングを用意する。
        // 同じクリップ（複数の場合はまとめて1つのクリップ）を使う場合は1度だけ設定する。
        int usingClipCount = 0;
        for (int clipIndex = 0; clipIndex < clippingContextListForMask.size(); clipIndex++) {
            // 1つのクリッピングマスクに関して
            T_ClippingContext cc = clippingContextListForMask.get(clipIndex);

            // このクリップを利用する描画オブジェクト群全体を囲む矩形を計算
            calcClippedDrawTotalBounds(model, cc);

            if (cc.isUsing) {
                usingClipCount++;   // 使用中としてカウント
            }
        }

        // マスク行列作成処理
        if (usingClipCount <= 0) {
            return;     // クリッピングマスクが存在しない場合何もしない。
        }

        setupLayoutBounds(0);

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

        // 実際にマスクを生成する。
        // 全てのマスクをどのようにレイアウトして描くかを決定し、ClipContext, ClippedDrawContextに記憶する。
        for (int clipIndex = 0; clipIndex < clippingContextListForMask.size(); clipIndex++) {
            // ---- 実際に1つのマスクを描く ----
            T_ClippingContext clipContext = clippingContextListForMask.get(clipIndex);
            csmRectF allClippedDrawRect = clipContext.allClippedDrawRect;   // このマスクを使う、全ての描画オブジェクトの論理座標上の囲み矩形
            csmRectF layoutBoundsOnTex01 = clipContext.layoutBounds;    // このマスクを収める

            final float margin = 0.05f;
            float scaleX, scaleY;
            final float ppu = model.getPixelPerUnit();
            final float maskPixelWidth = clipContext.getClippingManager().getClippingMaskBufferSize().x;
            final float maskPixelHeight = clipContext.getClippingManager().getClippingMaskBufferSize().y;
            final float physicalMaskWidth = layoutBoundsOnTex01.getWidth() * maskPixelWidth;
            final float physicalMaskHeight = layoutBoundsOnTex01.getHeight() * maskPixelHeight;

            tmpBoundsOnModel.setRect(allClippedDrawRect);

            if (tmpBoundsOnModel.getWidth() * ppu > physicalMaskWidth) {
                tmpBoundsOnModel.expand(allClippedDrawRect.getWidth() * margin, 0.0f);
                scaleX = layoutBoundsOnTex01.getWidth() / tmpBoundsOnModel.getWidth();
            } else {
                scaleX = ppu / physicalMaskWidth;
            }

            if (tmpBoundsOnModel.getHeight() * ppu > physicalMaskHeight) {
                tmpBoundsOnModel.expand(0.0f, allClippedDrawRect.getHeight() * margin);
                scaleY = layoutBoundsOnTex01.getHeight() / tmpBoundsOnModel.getHeight();
            } else {
                scaleY = ppu / physicalMaskHeight;
            }

            // マスク生成時に使う行列を求める。
            createMatrixForMask(isRightHanded, layoutBoundsOnTex01, scaleX, scaleY);

            clipContext.matrixForMask.setMatrix(tmpMatrixForMask.getArray());
            clipContext.matrixForDraw.setMatrix(tmpMatrixForDraw.getArray());
        }
    }

    @Override
    public void createMatrixForMask(
            boolean isRightHanded,
            csmRectF layoutBoundsOnTex01,
            float scaleX,
            float scaleY
    ) {
        // マスク作成用の行列の計算
        tmpMatrix.loadIdentity();
        {
            // Layout0..1を、-1..1に変換
            tmpMatrix.translateRelative(-1.0f, -1.0f);
            tmpMatrix.scaleRelative(2.0f, 2.0f);

            // view to Layout0..1
            tmpMatrix.translateRelative(
                    layoutBoundsOnTex01.getX(),
                    layoutBoundsOnTex01.getY()
            );
            tmpMatrix.scaleRelative(scaleX, scaleY);
            tmpMatrix.translateRelative(
                    -tmpBoundsOnModel.getX(),
                    -tmpBoundsOnModel.getY()
            );
        }
        tmpMatrixForMask.setMatrix(tmpMatrix);

        // 描画用の行列の計算
        tmpMatrix.loadIdentity();
        {
            tmpMatrix.translateRelative(
                    layoutBoundsOnTex01.getX(),
                    layoutBoundsOnTex01.getY() * ((isRightHanded) ? -1.0f : 1.0f)
            );
            tmpMatrix.scaleRelative(scaleX, scaleY * ((isRightHanded) ? -1.0f : 1.0f));
            tmpMatrix.translateRelative(
                    -tmpBoundsOnModel.getX(),
                    -tmpBoundsOnModel.getY()
            );
        }
        tmpMatrixForDraw.setMatrix(tmpMatrix);
    }

    @Override
    public void setupLayoutBounds(int usingClipCount) {
        final int useClippingMaskMaxCount = renderTextureCount <= 1
                ? CLIPPING_MASK_MAX_COUNT_ON_DEFAULT
                : CLIPPING_MASK_MAX_COUNT_ON_MULTI_RENDER_TEXTURE * renderTextureCount;

        if (usingClipCount <= 0 || usingClipCount > useClippingMaskMaxCount) {
            if (usingClipCount > useClippingMaskMaxCount) {
                // マスクの制限数の警告を出す
                int count = usingClipCount - useClippingMaskMaxCount;
                cubismLogError(
                        "not supported mask count : %d\n[Details] render texture count: %d\n, mask count : %d",
                        count,
                        renderTextureCount,
                        usingClipCount
                );
            }
            // この場合は一つのマスクターゲットを毎回クリアして使用する
            for (int index = 0; index < clippingContextListForMask.size(); index++) {
                T_ClippingContext cc = clippingContextListForMask.get(index);

                cc.layoutChannelIndex = 0;   // どうせ毎回消すので固定で良い
                cc.layoutBounds.setX(0.0f);
                cc.layoutBounds.setY(0.0f);
                cc.layoutBounds.setWidth(1.0f);
                cc.layoutBounds.setHeight(1.0f);
                cc.bufferIndex = 0;
            }
            return;
        }

        // レンダーテクスチャが1枚なら9分割する（最大36枚）
        final int layoutCountMaxValue = renderTextureCount <= 1 ? 9 : 8;

        // ひとつのRenderTextureを極力いっぱいに使ってマスクをレイアウトする。
        // マスクグループの数が4以下ならRGBA各チャンネルに１つずつマスクを配置し、5以上6以下ならRGBAを2,2,1,1と配置する。
        // NOTE: 1枚に割り当てるマスクの分割数を取りたいため、小数点は切り上げる。
        final int countPerSheetDiv = (usingClipCount + renderTextureCount - 1) / renderTextureCount;     // レンダーテクスチャ1枚あたり何枚割り当てるか
        final int reduceLayoutTextureCount = usingClipCount % renderTextureCount;     // レイアウトの数を1枚減らすレンダーテクスチャの数（この数だけのレンダーテクスチャが対象）。

        // RGBAを順番に使っていく。
        final int divCount = countPerSheetDiv / COLOR_CHANNEL_COUNT;     // 1チャンネルに配置する基本のマスク個数
        final int modCount = countPerSheetDiv % COLOR_CHANNEL_COUNT;     // 余り、この番号のチャンネルまでに1つずつ配分する（インデックスではない）

        // RGBAそれぞれのチャンネルを用意していく(0:R , 1:G , 2:B, 3:A, )
        int curClipIndex = 0;   // 順番に設定していく

        for (int renderTextureIndex = 0; renderTextureIndex < renderTextureCount; renderTextureIndex++) {
            for (int channelIndex = 0; channelIndex < COLOR_CHANNEL_COUNT; channelIndex++) {
                // このチャンネルにレイアウトする数
                // NOTE: レイアウト数 = 1チャンネルに配置する基本のマスク + 余りのマスクを置くチャンネルなら1つ追加
                int layoutCount = divCount + (channelIndex < modCount ? 1 : 0);

                // レイアウトの数を1枚減らす場合にそれを行うチャンネルを決定
                // divが0の時は正常なインデックスの範囲になるように調整
                final int checkChannelIndex = modCount + (divCount < 1 ? -1 : 0);

                // 今回が対象のチャンネルかつ、レイアウトの数を1枚減らすレンダーテクスチャが存在する場合
                if (channelIndex == checkChannelIndex && reduceLayoutTextureCount > 0) {
                    // 現在のレンダーテクスチャが、対象のレンダーテクスチャであればレイアウトの数を1枚減らす。
                    layoutCount -= !(renderTextureIndex < reduceLayoutTextureCount) ? 1 : 0;
                }

                // 分割方法を決定する。
                if (layoutCount == 0) {
                    // 何もしない。
                } else if (layoutCount == 1) {
                    // 全てをそのまま使う。
                    T_ClippingContext cc = clippingContextListForMask.get(curClipIndex++);
                    cc.layoutChannelIndex = channelIndex;
                    csmRectF bounds = cc.layoutBounds;

                    bounds.setX(0.0f);
                    bounds.setY(0.0f);
                    bounds.setWidth(1.0f);
                    bounds.setHeight(1.0f);

                    cc.bufferIndex = renderTextureIndex;
                } else if (layoutCount == 2) {
                    for (int i = 0; i < layoutCount; i++) {
                        final int xpos = i % 2;

                        T_ClippingContext cc = clippingContextListForMask.get(curClipIndex++);
                        cc.layoutChannelIndex = channelIndex;
                        csmRectF bounds = cc.layoutBounds;

                        // UVを2つに分解して使う
                        bounds.setX(xpos * 0.5f);
                        bounds.setY(0.0f);
                        bounds.setWidth(0.5f);
                        bounds.setHeight(1.0f);

                        cc.bufferIndex = renderTextureIndex;
                    }
                } else if (layoutCount <= 4) {
                    // 4分割して使う
                    for (int i = 0; i < layoutCount; i++) {
                        final int xpos = i % 2;
                        final int ypos = i / 2;

                        T_ClippingContext cc = clippingContextListForMask.get(curClipIndex++);
                        cc.layoutChannelIndex = channelIndex;
                        csmRectF bounds = cc.layoutBounds;

                        bounds.setX(xpos * 0.5f);
                        bounds.setY(ypos * 0.5f);
                        bounds.setWidth(0.5f);
                        bounds.setHeight(0.5f);

                        cc.bufferIndex = renderTextureIndex;
                    }
                } else if (layoutCount <= layoutCountMaxValue) {
                    // 9分割して使う
                    for (int i = 0; i < layoutCount; i++) {
                        final int xpos = i % 3;
                        final int ypos = i / 3;

                        T_ClippingContext cc = clippingContextListForMask.get(curClipIndex++);
                        cc.layoutChannelIndex = channelIndex;
                        csmRectF bounds = cc.layoutBounds;

                        bounds.setX(xpos / 3.0f);
                        bounds.setY(ypos / 3.0f);
                        bounds.setWidth(1.0f / 3.0f);
                        bounds.setHeight(1.0f / 3.0f);

                        cc.bufferIndex = renderTextureIndex;
                    }
                }
                // マスクの制限枚数を超えた場合の処理
                else {
                    int count = usingClipCount - useClippingMaskMaxCount;
                    cubismLogError(
                            "not supported mask count : %d\n[Details] render texture count: %d\n, mask count : %d",
                            count,
                            renderTextureCount,
                            usingClipCount
                    );

                    // 開発モードの場合は停止させる。
                    assert false;

                    // 引き続き実行する場合、 SetupShaderProgramでオーバーアクセスが発生するので仕方なく適当に入れておく。
                    // もちろん描画結果はろくなことにならない。
                    for (int i = 0; i < layoutCount; i++) {
                        T_ClippingContext cc = clippingContextListForMask.get(curClipIndex++);
                        cc.layoutChannelIndex = 0;

                        csmRectF bounds = cc.layoutBounds;
                        bounds.setX(0.0f);
                        bounds.setY(0.0f);
                        bounds.setWidth(1.0f);
                        bounds.setHeight(1.0f);

                        cc.bufferIndex = 0;
                    }
                }
            }
        }
    }

    @Override
    public CubismVector2 getClippingMaskBufferSize() {
        return clippingMaskBufferSize;
    }

    @Override
    public void setClippingMaskBufferSize(float width, float height) {
        clippingMaskBufferSize.set(width, height);
    }

    @Override
    public int getRenderTextureCount() {
        return renderTextureCount;
    }

    @Override
    public CubismRenderer.CubismTextureColor getChannelFlagAsColor(int channelIndex) {
        return channelColors.get(channelIndex);
    }

    /**
     * 既にマスクを作っているかを確認する。
     * 作っているようであれば該当するクリッピングマスクのインスタンスを返す。
     * 作っていなければnullを返す。
     *
     * @param drawableMasks      描画オブジェクトをマスクする描画オブジェクトのリスト
     * @param drawableMaskCounts 描画オブジェクトをマスクする描画オブジェクトの数
     * @return 該当するクリッピングマスクが存在すればインスタンスを返し、なければnullを返す。
     */
    public T_ClippingContext findSameClip(int[] drawableMasks, int drawableMaskCounts) {
        // 作成済みClippingContextと一致するか確認
        for (int i = 0; i < clippingContextListForMask.size(); i++) {
            T_ClippingContext clipContext = clippingContextListForMask.get(i);

            final int count = clipContext.clippingIdCount;
            if (count != drawableMaskCounts) {
                // 個数が違う場合は別物
                continue;
            }
            int sameCount = 0;

            // 同じIDを持つか確認。配列の数が同じなので、一致した個数が同じなら同じ物を持つとする。
            for (int j = 0; j < count; j++) {
                final int clipId = clipContext.clippingIdList[j];
                for (int k = 0; k < count; k++) {
                    if (drawableMasks[k] == clipId) {
                        sameCount++;
                        break;
                    }
                }
            }
            if (sameCount == count) {
                return clipContext;
            }
        }

        return null;    // 見つからなかった。
    }

    /**
     * マスクされる描画オブジェクト群全体を囲む矩形（モデル座標系）を計算する。
     *
     * @param model           モデルのインスタンス
     * @param clippingContext クリッピングマスクのコンテキスト
     */
    public void calcClippedDrawTotalBounds(CubismModel model, T_ClippingContext clippingContext) {
        // 被クリッピングマスク（マスクされる描画オブジェクト）の全体の矩形
        float clippedDrawTotalMinX = Float.MAX_VALUE;
        float clippedDrawTotalMinY = Float.MAX_VALUE;
        float clippedDrawTotalMaxX = -Float.MAX_VALUE;
        float clippedDrawTotalMaxY = -Float.MAX_VALUE;

        // このマスクが実際に必要か判定する。
        // このクリッピングを利用する「描画オブジェクト」がひとつでも使用可能であればマスクを生成する必要がある。
        final int clippedDrawCount = clippingContext.clippedDrawableIndexList.size();
        for (int clippedDrawableIndex = 0; clippedDrawableIndex < clippedDrawCount; clippedDrawableIndex++) {
            // マスクを使用する描画オブジェクトの描画される矩形を求める。
            final int drawableIndex = clippingContext.clippedDrawableIndexList.get(clippedDrawableIndex);

            final int drawableVertexCount = model.getDrawableVertexCount(drawableIndex);
            float[] drawableVertices = model.getDrawableVertices(drawableIndex);

            float minX = Float.MAX_VALUE;
            float minY = Float.MAX_VALUE;
            float maxX = -Float.MAX_VALUE;
            float maxY = -Float.MAX_VALUE;

            int loop = drawableVertexCount * VERTEX_STEP;
            for (int pi = VERTEX_OFFSET; pi < loop; pi += VERTEX_STEP) {
                float x = drawableVertices[pi];
                float y = drawableVertices[pi + 1];
                if (x < minX) minX = x;
                if (x > maxX) maxX = x;
                if (y < minY) minY = y;
                if (y > maxY) maxY = y;
            }

            if (minX == Float.MAX_VALUE) {
                continue;   // 有効な点が1つも取れなかったのでスキップする
            }

            // 全体の矩形に反映
            if (minX < clippedDrawTotalMinX) clippedDrawTotalMinX = minX;
            if (maxX > clippedDrawTotalMaxX) clippedDrawTotalMaxX = maxX;
            if (minY < clippedDrawTotalMinY) clippedDrawTotalMinY = minY;
            if (maxY > clippedDrawTotalMaxY) clippedDrawTotalMaxY = maxY;
        }

        if (clippedDrawTotalMinX == Float.MAX_VALUE) {
            clippingContext.isUsing = false;

            csmRectF clippedDrawRect = clippingContext.allClippedDrawRect;
            clippedDrawRect.setX(0.0f);
            clippedDrawRect.setY(0.0f);
            clippedDrawRect.setWidth(0.0f);
            clippedDrawRect.setHeight(0.0f);
        } else {
            clippingContext.isUsing = true;
            float w = clippedDrawTotalMaxX - clippedDrawTotalMinX;
            float h = clippedDrawTotalMaxY - clippedDrawTotalMinY;

            csmRectF clippedDrawRect = clippingContext.allClippedDrawRect;
            clippedDrawRect.setX(clippedDrawTotalMinX);
            clippedDrawRect.setY(clippedDrawTotalMinY);
            clippedDrawRect.setWidth(w);
            clippedDrawRect.setHeight(h);
        }
    }

    /**
     * 画面描画に使用するクリッピングマスクのリストを取得する。
     *
     * @return 画面描画に使用するクリッピングマスクのリスト
     */
    public List<T_ClippingContext> getClippingContextListForDraw() {
        return clippingContextListForDraw;
    }

    /**
     * オフスクリーンフレームのインスタンス
     */
    protected T_OffscreenSurface currentMaskBuffer;
    /**
     * マスクのクリアフラグの配列
     */
    protected boolean[] clearedMaskBufferFlags;

    /**
     * カラーチャンネル(RGBA)のフラグのリスト(0:R, 1:G, 2:B, 3:A)
     */
    protected final List<CubismRenderer.CubismTextureColor> channelColors = new ArrayList<>();
    /**
     * マスク用クリッピングコンテキストのリスト
     */
    protected final List<T_ClippingContext> clippingContextListForMask = new ArrayList<>();
    /**
     * 描画用クリッピングコンテキストのリスト
     */
    protected final List<T_ClippingContext> clippingContextListForDraw = new ArrayList<>();
    /**
     * クリッピングマスクのバッファサイズ（初期値：256）
     */
    protected final CubismVector2 clippingMaskBufferSize = new CubismVector2(256, 256);
    /**
     * 生成するレンダーテクスチャの枚数
     */
    protected int renderTextureCount;

    /**
     * 一時計算用行列
     */
    protected CubismMatrix44 tmpMatrix = CubismMatrix44.create();
    /**
     * マスク計算のための一時計算用行列
     */
    protected CubismMatrix44 tmpMatrixForMask = CubismMatrix44.create();
    /**
     * 描画用の一時計算用行列
     */
    protected CubismMatrix44 tmpMatrixForDraw = CubismMatrix44.create();
    /**
     * マスク配置計算用の一時計算用矩形
     */
    protected csmRectF tmpBoundsOnModel = csmRectF.create();
}
