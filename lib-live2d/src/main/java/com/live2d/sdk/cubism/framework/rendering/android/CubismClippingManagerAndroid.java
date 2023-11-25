/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.rendering.android;

import com.live2d.sdk.cubism.framework.math.CubismMatrix44;
import com.live2d.sdk.cubism.framework.math.CubismVector2;
import com.live2d.sdk.cubism.framework.model.CubismModel;
import com.live2d.sdk.cubism.framework.rendering.CubismRenderer;
import com.live2d.sdk.cubism.framework.type.csmRectF;

import java.io.Closeable;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.*;
import static com.live2d.sdk.cubism.framework.CubismFramework.VERTEX_OFFSET;
import static com.live2d.sdk.cubism.framework.CubismFramework.VERTEX_STEP;
import static com.live2d.sdk.cubism.framework.utils.CubismDebug.cubismLogError;

/**
 * This class deals with clipping mask processes.
 */
class CubismClippingManagerAndroid implements Closeable {
    /**
     * Constructor
     */
    public CubismClippingManagerAndroid() {
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

    /**
     * Close resources.
     */
    @Override
    public void close() {
        clippingContextListForMask.clear();
        clippingContextListForDraw.clear();

        channelColors.clear();

        vertexArrayCache = null;
        uvArrayCache = null;
        indexArrayCache = null;

        if (clearedFrameBufferFlags != null) {
            clearedFrameBufferFlags = null;
        }
    }

    /**
     * Initialization process of the manager.
     * Register drawing objects that use clipping masks.
     *
     * @param drawableCount number of drawing objects
     * @param drawableMasks list of drawing object indices to mask drawing objects
     * @param drawableMaskCounts number of drawing objects to mask drawing objects
     * @param maskBufferCount number of mask buffers
     */
    public void initialize(
        int drawableCount,
        final int[][] drawableMasks,
        final int[] drawableMaskCounts,
        final int maskBufferCount
    ) {
        renderTextureCount = maskBufferCount;

        // レンダーテクスチャのクラアフラグの配列の初期化
        clearedFrameBufferFlags = new boolean[renderTextureCount];

        // Register all drawing objects that use clipping masks.
        // The use of clipping masks is usually limited to a few objects.
        for (int i = 0; i < drawableCount; i++) {
            if (drawableMaskCounts[i] <= 0) {
                // Art mesh with no clipping mask (often not used)
                clippingContextListForDraw.add(null);
                continue;
            }

            // Check if it is the same as an already existing ClipContext.
            CubismClippingContext cc = findSameClip(drawableMasks[i], drawableMaskCounts[i]);
            if (cc == null) {
                // Generate if no identical mask exists.
                cc = new CubismClippingContext(this, drawableMasks[i], drawableMaskCounts[i]);
                clippingContextListForMask.add(cc);
            }
            cc.addClippedDrawable(i);
            clippingContextListForDraw.add(cc);
        }
    }

    /**
     * Create a clipping context. Run at drawing the model.
     *
     * @param model model instance
     * @param renderer renderer instance
     */
    public void setupClippingContext(CubismModel model, CubismRendererAndroid renderer, int[] lastFBO, int[] lastViewport) {
        // Prepare all clipping.
        // Set only once when using the same clip (or a group of clips if there are multiple clips).
        int usingClipCount = 0;
        for (int i = 0; i < clippingContextListForMask.size(); i++) {
            CubismClippingContext clipContext = clippingContextListForMask.get(i);

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
        if (!renderer.isUsingHighPrecisionMask()) {
            // Set up a viewport with the same size as the generated FrameBuffer.
            glViewport(0, 0, (int) clippingMaskBufferSize.x, (int) clippingMaskBufferSize.y);

            // 後の計算のためにインデックスの最初をセットする。
            currentOffscreenFrame = renderer.getMaskBuffer(0);

            // マスク描画処理
            currentOffscreenFrame.beginDraw(lastFBO);

            // バッファをクリアする
            renderer.preDraw();
        }

        // Determine the layout of each mask.
        setupLayoutBounds(renderer.isUsingHighPrecisionMask() ? 0 : usingClipCount);

        // サイズがレンダーテクスチャの枚数と合わない場合は合わせる。
        if (clearedFrameBufferFlags.length != renderTextureCount) {
            clearedFrameBufferFlags = new boolean[renderTextureCount];
        }
        // マスクのクリアフラグを毎フレーム開始時に初期化する。
        else {
            for (int i = 0; i < renderTextureCount; i++) {
                clearedFrameBufferFlags[i] = false;
            }
        }

        // ---------- Mask Drawing Process -----------
        // Actually generate the masks.
        // Determine how to layout and draw all the masks, and store them in ClipContext and ClippedDrawContext.
        for (int j = 0; j < clippingContextListForMask.size(); j++) {
            CubismClippingContext clipContext = clippingContextListForMask.get(j);

            // The enclosing rectangle in logical coordinates of all drawing objects that use this mask.
            csmRectF allClippedDrawRect = clipContext.allClippedDrawRect;
            // Fit the mask in here.
            csmRectF layoutBoundsOnTex01 = clipContext.layoutBounds;

            float scaleX, scaleY;
            final float margin = 0.05f;

            // clipContextに設定したオフスクリーンフレームをインデックスで取得
            final CubismOffscreenSurfaceAndroid clipContextOffscreenFrame = renderer.getMaskBuffer(clipContext.bufferIndex);

            // 現在のオフスクリーンフレームがclipContextのものと異なる場合
            if (currentOffscreenFrame != clipContextOffscreenFrame &&
                !renderer.isUsingHighPrecisionMask()
            ) {
                currentOffscreenFrame.endDraw();
                currentOffscreenFrame = clipContextOffscreenFrame;

                // マスク用RenderTextureをactiveにセット。
                currentOffscreenFrame.beginDraw(lastFBO);

                // バッファをクリアする。
                renderer.preDraw();
            }

            if (renderer.isUsingHighPrecisionMask()) {
                final float ppu = model.getPixelPerUnit();
                final float maskPixelWidth = clipContext.getClippingManager().clippingMaskBufferSize.x;
                final float maskPixelHeight = clipContext.getClippingManager().clippingMaskBufferSize.y;
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
            } else {
                // Use a rectangle on the model coordinates with margins as appropriate.
                tmpBoundsOnModel.setRect(allClippedDrawRect);

                tmpBoundsOnModel.expand(
                    allClippedDrawRect.getWidth() * margin,
                    allClippedDrawRect.getHeight() * margin
                );

                // ######## It is best to keep the size to a minimum, rather than using the entire allocated space.
                // Find the formula for the shader. If rotation is not taken into account, the formula is as follows.
                // movePeriod' = movePeriod * scaleX + offX     [[ movePeriod' = (movePeriod - tmpBoundsOnModel.movePeriod)*scale + layoutBoundsOnTex01.movePeriod ]]
                scaleX = layoutBoundsOnTex01.getWidth() / tmpBoundsOnModel.getWidth();
                scaleY = layoutBoundsOnTex01.getHeight() / tmpBoundsOnModel.getHeight();

            }

            // Calculate the matrix to be used for mask generation.
            tmpMatrix.loadIdentity();

            // Find the matrix to pass to the shader <<< optimization required (can be simplified by calculating in reverse order)
            // Convert Layout0..1 to -1..1
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
            // tmpMatrixForMaskが計算結果
            tmpMatrixForMask.setMatrix(tmpMatrix);

            // Calculate the mask reference matrix for draw.
            // Find the matrix to pass to the shader <<< optimization required (can be simplified by calculating in reverse order)
            tmpMatrix.loadIdentity();
            tmpMatrix.translateRelative(
                layoutBoundsOnTex01.getX(),
                layoutBoundsOnTex01.getY()
            );
            tmpMatrix.scaleRelative(scaleX, scaleY);
            tmpMatrix.translateRelative(
                -tmpBoundsOnModel.getX(),
                -tmpBoundsOnModel.getY()
            );
            tmpMatrixForDraw.setMatrix(tmpMatrix);

            clipContext.matrixForMask.setMatrix(tmpMatrixForMask);
            clipContext.matrixForDraw.setMatrix(tmpMatrixForDraw);

            if (!renderer.isUsingHighPrecisionMask()) {
                final int clipDrawCount = clipContext.clippingIdCount;

                for (int i = 0; i < clipDrawCount; i++) {
                    final int clipDrawIndex = clipContext.clippingIdList[i];

                    // If vertex information is not updated and reliable, pass drawing.
                    if (!model.getDrawableDynamicFlagVertexPositionsDidChange(clipDrawIndex)) {
                        continue;
                    }

                    renderer.isCulling(model.getDrawableCulling(clipDrawIndex));

                    // マスクがクリアされていないなら処理する。
                    if (!clearedFrameBufferFlags[clipContext.bufferIndex]) {
                        // マスクをクリアする。
                        // (仮仕様) 1が無効（描かれない）領域、0が有効（描かれる）領域。（シェーダーCd*Csで0に近い値をかけてマスクを作る。1をかけると何も起こらない）
                        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
                        glClear(GL_COLOR_BUFFER_BIT);
                        clearedFrameBufferFlags[clipContext.bufferIndex] = true;
                    }

                    // Apply this special transformation to draw it.
                    // Switching channel is also needed.(A,R,G,B)
                    renderer.setClippingContextBufferForMask(clipContext);

                    // キャッシュされたバッファを取得し、実際のデータを格納する。
                    CubismDrawableInfoCachesHolder drawableInfoCachesHolder = renderer.getDrawableInfoCachesHolder();

                    // vertex array
                    FloatBuffer vertexArrayBuffer = drawableInfoCachesHolder.setUpVertexArray(
                        clipDrawIndex,
                        model.getDrawableVertices(clipDrawIndex)
                    );

                    // uv array
                    FloatBuffer uvArrayBuffer = drawableInfoCachesHolder.setUpUvArray(
                        clipDrawIndex,
                        model.getDrawableVertexUvs(clipDrawIndex)
                    );

                    // index array
                    ShortBuffer indexArrayBuffer = drawableInfoCachesHolder.setUpIndexArray(
                        clipDrawIndex,
                        model.getDrawableVertexIndices(clipDrawIndex)
                    );

                    renderer.drawMeshAndroid(
                        model.getDrawableTextureIndex(clipDrawIndex),
                        model.getDrawableVertexIndexCount(clipDrawIndex),
                        model.getDrawableVertexCount(clipDrawIndex),
                        indexArrayBuffer,
                        vertexArrayBuffer,
                        uvArrayBuffer,
                        model.getMultiplyColor(clipDrawIndex),
                        model.getScreenColor(clipDrawIndex),
                        model.getDrawableOpacity(clipDrawIndex),
                        CubismRenderer.CubismBlendMode.NORMAL,  // Clipping is forced normal drawing.
                        false   // The inverted use of clipping is completely irrelevant when generating masks.
                    );
                }
            }
        }

        if (!renderer.isUsingHighPrecisionMask()) {
            // --- Post Processing ---
            // Return the drawing target
            currentOffscreenFrame.endDraw();
            renderer.setClippingContextBufferForMask(null);

            glViewport(lastViewport[0], lastViewport[1], lastViewport[2], lastViewport[3]);
        }
    }

    // setupClippingContextメソッド内でのみ使用されるキャッシュ変数
    private final CubismMatrix44 tmpMatrix = CubismMatrix44.create();
    private final CubismMatrix44 tmpMatrixForMask = CubismMatrix44.create();
    private final CubismMatrix44 tmpMatrixForDraw = CubismMatrix44.create();
    private FloatBuffer[] vertexArrayCache;
    private FloatBuffer[] uvArrayCache;
    private ShortBuffer[] indexArrayCache;

    private final csmRectF tmpBoundsOnModel = csmRectF.create();


    /**
     * Get the flag of color channel(RGBA).
     *
     * @param channelNo number of color channel(RGBA)(0:R, 1:G, 2:B, 3:A)
     */
    public CubismRenderer.CubismTextureColor getChannelFlagAsColor(int channelNo) {
        return channelColors.get(channelNo);
    }

    /**
     * Get a list of clipping masks to be used for screen drawing.
     *
     * @return list of clipping masks to be usef for screen drawing
     */
    public List<CubismClippingContext> getClippingContextListForDraw() {
        return clippingContextListForDraw;
    }

    /**
     * マスクの合計数をカウントする。
     *
     * @return マスクの合計数
     */
    public int getClippingMaskCount() {
        return clippingContextListForMask.size();
    }

    /**
     * Get the size of clipping mask buffer.
     *
     * @return size of clipping mask buffer
     */
    public CubismVector2 getClippingMaskBufferSize() {
        return clippingMaskBufferSize;
    }

    /**
     * Set the size of clipping mask buffer.
     *
     * @param width width of clipping mask buffer
     * @param height height of clipping mask buffer
     */
    public void setClippingMaskBufferSize(float width, float height) {
        clippingMaskBufferSize.set(width, height);
    }

    /**
     * このバッファのレンダーテクスチャの枚数を取得する。
     *
     * @return このバッファのレンダーテクスチャの枚数
     */
    public int getRenderTextureCount() {
        return renderTextureCount;
    }

    /**
     * 1 for one channel at the time of the experiment, 3 for only RGB, and 4 for including alpha.
     */
    private static final int COLOR_CHANNEL_COUNT = 4;
    /**
     * 通常のフレームバッファ1枚あたりのマスク最大数
     */
    private static final int CLIPPING_MASK_MAX_COUNT_ON_DEFAULT = 36;
    /**
     * フレームバッファが2枚以上ある場合のフレームバッファ1枚あたりのマスク最大数
     */
    private static final int CLIPPING_MASK_MAX_COUNT_ON_MULTI_RENDER_TEXTURE = 32;

    /**
     * Check if the mask has already been created.
     * If it has, return an instance of the corresponding clipping mask.
     * If it has not been created, return null.
     *
     * @param drawableMasks list of drawing objects to mask drawing objects
     * @param drawableMaskCounts number of drawing objects to mask drawing objects
     * @return returns an instance of the corresponding clipping mask if it exists, or null if it does not.
     */
    private CubismClippingContext findSameClip(final int[] drawableMasks, int drawableMaskCounts) {
        // Check if the ClippingContext matches the one already created.
        for (int k = 0; k < clippingContextListForMask.size(); k++) {
            CubismClippingContext clipContext = clippingContextListForMask.get(k);

            final int count = clipContext.clippingIdCount;
            if (count != drawableMaskCounts) {
                // If the number of pieces is different, it's different.
                continue;
            }
            int sameCount = 0;

            // Check if they have the same ID. Since the number of arrays is the same, if the number of matches is the same, it is assumed the same thing.
            for (int i = 0; i < count; i++) {
                final int clipId = clipContext.clippingIdList[i];
                for (int j = 0; j < count; ++j) {
                    if (drawableMasks[j] == clipId) {
                        sameCount++;
                        break;
                    }
                }
            }
            if (sameCount == count) {
                return clipContext;
            }
        }
        // Cannot find the same clip.
        return null;
    }


    /**
     * Calculate the rectangle that encloses the entire group of drawing objects to be masked(model coordinate system).
     *
     * @param model model instance
     * @param clippingContext context of clipping mask
     */
    private void calcClippedDrawTotalBounds(CubismModel model, CubismClippingContext clippingContext) {
        // The overall rectangle of the clipping mask (the drawing object to be masked).
        float clippedDrawTotalMinX = Float.MAX_VALUE;
        float clippedDrawTotalMinY = Float.MAX_VALUE;
        float clippedDrawTotalMaxX = -Float.MAX_VALUE;
        float clippedDrawTotalMaxY = -Float.MAX_VALUE;

        // Determine if this mask is actually needed.
        // If there is even one "drawing object" available that uses this clipping, generating a mask is required.
        final int clippedDrawCount = clippingContext.clippedDrawableIndexList.size();

        for (int clippedDrawableIndex = 0; clippedDrawableIndex < clippedDrawCount; clippedDrawableIndex++) {
            // Find the rectangle to be drawn for a drawing object that uses a mask.
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

            // If getting a single valid point is falied, skip it.
            if (minX == Float.MAX_VALUE) {
                continue;
            }

            // Reflect in the overall rectangle.
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
     * Layout to place the clipping context.
     * Layout masks using the specified number of render textures as fully as possible.
     * If the number of mask groups is 4 or less, layout one mask for each RGBA channel; if the number is between 5 and 6, layout RGBA as 2,2,1,1.
     *
     * @param usingClipCount number of clipping contexts to place
     */
    private void setupLayoutBounds(int usingClipCount) {
        final int useClippingMaskMaxCount = renderTextureCount <= 1
                                            ? CLIPPING_MASK_MAX_COUNT_ON_DEFAULT
                                            : CLIPPING_MASK_MAX_COUNT_ON_MULTI_RENDER_TEXTURE * renderTextureCount;
        if (usingClipCount <= 0 || usingClipCount > useClippingMaskMaxCount) {
            if (usingClipCount > useClippingMaskMaxCount) {
                // マスクの制限数の警告を出す
                int count = usingClipCount - useClippingMaskMaxCount;
                cubismLogError("not supported mask count: " + count + "\n[Details] render texture count: " + renderTextureCount + ", mask count: " + usingClipCount);
            }
            // この場合は一つのマスクターゲットを毎回クリアして使用する
            for (int index = 0; index < clippingContextListForMask.size(); index++) {
                CubismClippingContext cc = clippingContextListForMask.get(index);

                cc.layoutChannelNo = 0;   // どうせ毎回消すので固定で良い
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

        // Layout the masks using as much of one RenderTexture as possible.
        // If the number of mask groups is 4 or less, place one mask in each RGBA channel; if the number is between 5 and 6, place RGBA 2,2,1,1
        int countPerSheetDiv = usingClipCount / renderTextureCount;     // レンダーテクスチャ1枚あたり何枚割り当てるか
        int countPerSheetMod = usingClipCount % renderTextureCount;     // この番号のレンダーテクスチャまでに1つずつ配分する。

        // Use the RGBAs in order
        // Basic number of masks to place on one channel
        final int div = countPerSheetDiv / COLOR_CHANNEL_COUNT;
        // Remainder. Allocate one to each channel of this number.
        final int mod = countPerSheetDiv % COLOR_CHANNEL_COUNT;

        // Prepare the channels for each RGBA. (0:R, 1:G, 2:B, 3:A)
        // Set them in order.
        int curClipIndex = 0;

        for (int renderTextureNo = 0; renderTextureNo < renderTextureCount; renderTextureNo++) {
            for (int channelNo = 0; channelNo < COLOR_CHANNEL_COUNT; channelNo++) {
                // Number of layouts for this channel.
                int layoutCount = div + (channelNo < mod ? 1 : 0);

                // このレンダーテクスチャにまだ割り当てられていなければ追加する
                final int checkChannelNo = mod + 1 >= COLOR_CHANNEL_COUNT ? 0 : mod + 1;
                if (layoutCount < layoutCountMaxValue && channelNo == checkChannelNo) {
                    layoutCount += renderTextureNo < countPerSheetMod ? 1 : 0;
                }

                // Determine the division method.
                if (layoutCount == 0) {
                    // Do nothing.
                } else if (layoutCount == 1) {
                    // Use everything as is.
                    CubismClippingContext cc = clippingContextListForMask.get(curClipIndex++);
                    cc.layoutChannelNo = channelNo;
                    csmRectF bounds = cc.layoutBounds;

                    bounds.setX(0.0f);
                    bounds.setY(0.0f);
                    bounds.setWidth(1.0f);
                    bounds.setHeight(1.0f);

                    cc.bufferIndex = renderTextureNo;
                } else if (layoutCount == 2) {
                    for (int i = 0; i < layoutCount; i++) {
                        final int xpos = i % 2;

                        CubismClippingContext cc = clippingContextListForMask.get(curClipIndex++);
                        cc.layoutChannelNo = channelNo;
                        csmRectF bounds = cc.layoutBounds;

                        // UVを2つに分解して使う
                        bounds.setX(xpos * 0.5f);
                        bounds.setY(0.0f);
                        bounds.setWidth(0.5f);
                        bounds.setHeight(1.0f);

                        cc.bufferIndex = renderTextureNo;
                    }
                } else if (layoutCount <= 4) {
                    // 4分割して使う
                    for (int i = 0; i < layoutCount; i++) {
                        final int xpos = i % 2;
                        final int ypos = i / 2;

                        CubismClippingContext cc = clippingContextListForMask.get(curClipIndex++);
                        cc.layoutChannelNo = channelNo;
                        csmRectF bounds = cc.layoutBounds;

                        bounds.setX(xpos * 0.5f);
                        bounds.setY(ypos * 0.5f);
                        bounds.setWidth(0.5f);
                        bounds.setHeight(0.5f);

                        cc.bufferIndex = renderTextureNo;
                    }
                } else if (layoutCount <= layoutCountMaxValue) {
                    // 9分割して使う
                    for (int i = 0; i < layoutCount; i++) {
                        final int xpos = i % 3;
                        final int ypos = i / 3;

                        CubismClippingContext cc = clippingContextListForMask.get(curClipIndex++);
                        cc.layoutChannelNo = channelNo;
                        csmRectF bounds = cc.layoutBounds;

                        bounds.setX(xpos / 3.0f);
                        bounds.setY(ypos / 3.0f);
                        bounds.setWidth(1.0f / 3.0f);
                        bounds.setHeight(1.0f / 3.0f);

                        cc.bufferIndex = renderTextureNo;
                    }
                }
                // マスクの制限枚数を超えた場合の処理
                else {
                    int count = usingClipCount - useClippingMaskMaxCount;
                    cubismLogError("not supported mask count: " + count + "\n[Details] render texture count: " + renderTextureCount + ", mask count: " + usingClipCount);

                    // Stop this program if in development mode.
                    assert false;

                    // If you continue to run, SetupShaderProgram() method will cause over-access, so you have no choice but to put it in properly.
                    // Of course, the result of drawing is so bad.
                    for (int i = 0; i < layoutCount; i++) {
                        CubismClippingContext cc = clippingContextListForMask.get(curClipIndex++);
                        cc.layoutChannelNo = 0;

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


    /**
     * 現在のオフスクリーンフレームのインスタンス
     */
    private CubismOffscreenSurfaceAndroid currentOffscreenFrame;

    /**
     * マスクのクリアフラグの配列
     */
    private boolean[] clearedFrameBufferFlags;

    /**
     * list of flags of color channel(RGBA)(0:R, 1:G, 2:B, 3:A)
     */
    private final List<CubismRenderer.CubismTextureColor> channelColors = new ArrayList<CubismRenderer.CubismTextureColor>();
    /**
     * List of clipping contexts for masks.
     */
    private final List<CubismClippingContext> clippingContextListForMask = new ArrayList<CubismClippingContext>();
    /**
     * List of clipping contexts for drawing.
     */
    private final List<CubismClippingContext> clippingContextListForDraw = new ArrayList<CubismClippingContext>();
    /**
     * Buffer size for clipping mask.
     */
    private final CubismVector2 clippingMaskBufferSize = new CubismVector2(256, 256);
    /**
     * 生成するレンダーテクスチャの枚数
     */
    private int renderTextureCount;
}
