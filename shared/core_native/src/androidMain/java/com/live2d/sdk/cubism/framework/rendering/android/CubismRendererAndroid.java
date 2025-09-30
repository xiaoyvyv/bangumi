/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.rendering.android;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_CCW;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_SCISSOR_TEST;
import static android.opengl.GLES20.GL_STENCIL_TEST;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glColorMask;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glFrontFace;
import static android.opengl.GLES20.glTexParameterf;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glViewport;
import static com.live2d.sdk.cubism.framework.CubismFrameworkConfig.CSM_DEBUG;

import android.opengl.GLES11Ext;

import com.live2d.sdk.cubism.framework.math.CubismVector2;
import com.live2d.sdk.cubism.framework.model.CubismModel;
import com.live2d.sdk.cubism.framework.rendering.CubismRenderer;
import com.live2d.sdk.cubism.framework.utils.CubismDebug;

import java.nio.ShortBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The class that implements drawing instructions for Android.
 */
public class CubismRendererAndroid extends CubismRenderer {
    /**
     * Create the renderer instance for Android platform.
     *
     * @return renderer instance
     */
    public static CubismRenderer create() {
        return new CubismRendererAndroid();
    }

    /**
     * Release static resources that this renderer keeps.
     */
    public static void staticRelease() {
        CubismRendererAndroid.doStaticRelease();
    }

    /**
     * Tegra processor support.
     * Enable/Disable drawing by extension method.
     *
     * @param extMode   Whether to draw using the extended method
     * @param extPAMode Whether to enable the PA setting for the extended method
     */
    public static void setExtShaderMode(boolean extMode, boolean extPAMode) {
        CubismShaderAndroid.setExtShaderMode(extMode, extPAMode);
        CubismShaderAndroid.deleteInstance();
    }

    /**
     * Android-Tegra support. Reload shader programs.
     */
    public static void reloadShader() {
        CubismShaderAndroid.deleteInstance();
    }

    @Override
    public void initialize(CubismModel model) {
        initialize(model, 1);
    }

    @Override
    public void initialize(CubismModel model, int maskBufferCount) {
        // 頂点情報をキャッシュする。
        drawableInfoCachesHolder = new CubismDrawableInfoCachesHolder(model);

        if (model.isUsingMasking()) {
            // マスクバッファの枚数として、0または負の値が指定されている場合は強制的に1枚と設定し、警告ログを出力する。
            // Webと違いCubismOffscreenSurfaceの配列を作成するため、こちらで不正値を検知し修正する。
            if (maskBufferCount < 1) {
                maskBufferCount = 1;
                CubismDebug.cubismLogWarning("The number of render textures must be an integer greater than or equal to 1. Set the number of render textures to 1.");
            }

            // Initialize clipping mask and buffer preprocessing method
            clippingManager = new CubismClippingManagerAndroid();
            clippingManager.initialize(
                    RendererType.ANDROID,
                    model,
                    maskBufferCount
            );

            offscreenSurfaces = new CubismOffscreenSurfaceAndroid[maskBufferCount];

            for (int i = 0; i < maskBufferCount; i++) {
                CubismOffscreenSurfaceAndroid offscreenSurface = new CubismOffscreenSurfaceAndroid();
                offscreenSurface.createOffscreenSurface(clippingManager.getClippingMaskBufferSize(), null);

                offscreenSurfaces[i] = offscreenSurface;
            }
        }

        sortedDrawableIndexList = new int[model.getDrawableCount()];
        super.initialize(model);
    }

    @Override
    public void close() {
        super.close();

        if (clippingManager != null) {
            clippingManager.close();
        }

        if (offscreenSurfaces != null) {
            for (int i = 0; i < offscreenSurfaces.length; i++) {
                if (offscreenSurfaces[i].isValid()) {
                    offscreenSurfaces[i].destroyOffscreenSurface();
                }
            }
        }

        drawableInfoCachesHolder = null;
    }

    /**
     * Bind processing of OpenGL textures.
     *
     * @param modelTextureIndex number of the model texture to set
     * @param glTextureIndex    number of the OpenGL texture to bind
     */
    public void bindTexture(int modelTextureIndex, int glTextureIndex) {
        textures.put(modelTextureIndex, glTextureIndex);
        areTexturesChanged = true;
    }

    /**
     * Get textures list bound to OpenGL.
     *
     * @return textures list
     */
    public Map<Integer, Integer> getBoundTextures() {
        if (areTexturesChanged) {
            cachedImmutableTextures = Collections.unmodifiableMap(textures);
            areTexturesChanged = false;
        }
        return cachedImmutableTextures;
    }

    /**
     * Get the size of clipping mask buffer.
     *
     * @return size of clipping mask buffer
     */
    private CubismVector2 getClippingMaskBufferSize() {
        return clippingManager.getClippingMaskBufferSize();
    }

    /**
     * Set the size of clipping mask buffer.
     * This method's processing cost is high because the MaskBuffer for the mask is destroyed and recreated.
     *
     * @param width  width of MaskBufferSize
     * @param height height of MaskBufferSize
     */
    public void setClippingMaskBufferSize(final float width, final float height) {
        if (clippingManager == null) {
            return;
        }

        // インスタンス破棄前にレンダーテクスチャの数を保存
        final int renderTextureCount = this.clippingManager.getRenderTextureCount();

        // Destroy and recreate instances to change the size of MaskBuffer
        clippingManager = new CubismClippingManagerAndroid();
        clippingManager.setClippingMaskBufferSize(width, height);

        CubismModel model = getModel();
        clippingManager.initialize(
                RendererType.ANDROID,
                model,
                renderTextureCount
        );
    }

    /**
     * レンダーテクスチャの枚数を取得する。
     *
     * @return レンダーテクスチャの枚数
     */
    public int getRenderTextureCount() {
        return clippingManager.getRenderTextureCount();
    }

    /**
     * Draw the drawing objects (ArtMesh). <br>
     * Both polygon mesh and the texture number is given to this method.
     *
     * @param model number of the drawed texture
     * @param index index of the drawing object
     */
    protected void drawMeshAndroid(
            final CubismModel model,
            final int index
    ) {
        if (!CSM_DEBUG) {
            // If the texture referenced by the model is not bound, skip drawing.
            if (textures.get(model.getDrawableTextureIndex(index)) == null) {
                return;
            }
        }

        // Enabling/disabling culling
        if (isCulling()) {
            glEnable(GL_CULL_FACE);
        } else {
            glDisable(GL_CULL_FACE);
        }

        // In Cubism3 OpenGL, CCW becomes surface for both masks and art meshes.
        glFrontFace(GL_CCW);

        // マスク生成時
        if (isGeneratingMask()) {
            CubismShaderAndroid.getInstance().setupShaderProgramForMask(this, model, index);
        } else {
            CubismShaderAndroid.getInstance().setupShaderProgramForDraw(this, model, index);
        }

        // Draw the prygon mesh
        final int indexCount = model.getDrawableVertexIndexCount(index);
        final ShortBuffer indexArrayBuffer = drawableInfoCachesHolder.setUpIndexArray(
                index,
                model.getDrawableVertexIndices(index)
        );
        glDrawElements(
                GL_TRIANGLES,
                indexCount,
                GL_UNSIGNED_SHORT,
                indexArrayBuffer
        );

        // post-processing
        glUseProgram(0);
        setClippingContextBufferForDraw(null);
        setClippingContextBufferForMask(null);
    }

    // This is only used by 'drawMeshAndroid' method.
    // Avoid creating a new CubismTextureColor instance.
    private final CubismTextureColor modelColorRGBA = new CubismTextureColor();

    @Override
    protected void doDrawModel() {
        final CubismModel model = getModel();

        // In the case of clipping mask and buffer preprocessing method
        if (clippingManager != null) {
            preDraw();

            // If offscreen frame buffer size is different from clipping mask buffer size, recreate it.
            for (int i = 0; i < clippingManager.getRenderTextureCount(); i++) {
                CubismOffscreenSurfaceAndroid offscreenSurface = offscreenSurfaces[i];

                if (!offscreenSurface.isSameSize(clippingManager.getClippingMaskBufferSize())) {
                    offscreenSurface.createOffscreenSurface(clippingManager.getClippingMaskBufferSize(), null);
                }
            }

            if (isUsingHighPrecisionMask()) {
                clippingManager.setupMatrixForHighPrecision(getModel(), false);
            } else {
                clippingManager.setupClippingContext(
                        getModel(),
                        this,
                        rendererProfile.lastFBO,
                        rendererProfile.lastViewport
                );
            }
        }

        // preDraw() method is called twice.
        preDraw();

        final int drawableCount = model.getDrawableCount();
        final int[] renderOrder = model.getDrawableRenderOrders();

        // Sort the index by drawing order
        for (int i = 0; i < drawableCount; i++) {
            final int order = renderOrder[i];
            sortedDrawableIndexList[order] = i;
        }

        // Draw process
        for (int i = 0; i < drawableCount; i++) {
            final int drawableIndex = sortedDrawableIndexList[i];

            // If Drawable is not in the display state, the process is passed.
            if (!model.getDrawableDynamicFlagIsVisible(drawableIndex)) {
                continue;
            }

            // Set clipping mask
            CubismClippingContextAndroid clipContext = (clippingManager != null)
                    ? clippingManager.getClippingContextListForDraw().get(drawableIndex)
                    : null;

            // マスクを描く必要がある
            if (clipContext != null && isUsingHighPrecisionMask()) {
                // 描くことになっていた
                if (clipContext.isUsing) {
                    // 生成したOffscreenSurfaceと同じサイズでビューポートを設定
                    glViewport(0, 0, (int) clippingManager.getClippingMaskBufferSize().x, (int) clippingManager.getClippingMaskBufferSize().y);

                    // バッファをクリアする
                    preDraw();

                    // マスク描画処理
                    // マスク用RenderTextureをactiveにセット
                    getMaskBuffer(clipContext.bufferIndex).beginDraw(rendererProfile.lastFBO);

                    // マスクをクリアする。
                    // 1が無効（描かれない領域）、0が有効（描かれる）領域。（シェーダーでCd*Csで0に近い値をかけてマスクを作る。1をかけると何も起こらない。）
                    glClearColor(1f, 1f, 1f, 1f);
                    glClear(GL_COLOR_BUFFER_BIT);
                }

                final int clipDrawCount = clipContext.clippingIdCount;
                for (int index = 0; index < clipDrawCount; index++) {
                    final int clipDrawIndex = clipContext.clippingIdList[index];

                    // 頂点情報が更新されておらず、信頼性がない場合は描画をパスする
                    if (!getModel().getDrawableDynamicFlagVertexPositionsDidChange(clipDrawIndex)) {
                        continue;
                    }

                    isCulling(getModel().getDrawableCulling(clipDrawIndex));

                    // 今回専用の変換を適用して描く
                    // チャンネルも切り替える必要がある（A,R,G,B）
                    setClippingContextBufferForMask(clipContext);

                    drawMeshAndroid(model, clipDrawIndex);
                }
                // --- 後処理 ---
                for (int j = 0; j < clippingManager.getRenderTextureCount(); j++) {
                    offscreenSurfaces[j].endDraw();
                    setClippingContextBufferForMask(null);
                    glViewport(
                            rendererProfile.lastViewport[0],
                            rendererProfile.lastViewport[1],
                            rendererProfile.lastViewport[2],
                            rendererProfile.lastViewport[3]
                    );
                }
            }
            // クリッピングマスクをセットする
            setClippingContextBufferForDraw(clipContext);

            isCulling(model.getDrawableCulling(drawableIndex));

            drawMeshAndroid(model, drawableIndex);
        }
        postDraw();
    }

    @Override
    protected void saveProfile() {
        rendererProfile.save();
    }

    @Override
    protected void restoreProfile() {
        rendererProfile.restore();
    }

    /**
     * Release OpenGLES2 static shader programs.
     */
    static void doStaticRelease() {
        CubismShaderAndroid.deleteInstance();
    }

    /**
     * Get the clipping context to draw to the mask texture
     *
     * @return the clipping context to draw to the mask texture
     */
    CubismClippingContextAndroid getClippingContextBufferForMask() {
        return clippingContextBufferForMask;
    }

    /**
     * Set the clipping context to draw to the mask texture
     *
     * @param clip clipping context to draw to the mask texture
     */
    void setClippingContextBufferForMask(CubismClippingContextAndroid clip) {
        clippingContextBufferForMask = clip;
    }

    /**
     * Get the clipping context to draw on display
     *
     * @return the clipping context to draw on display
     */
    CubismClippingContextAndroid getClippingContextBufferForDraw() {
        return clippingContextBufferForDraw;
    }

    /**
     * Set the clipping context to draw on display
     *
     * @param clip the clipping context to draw on display
     */
    void setClippingContextBufferForDraw(CubismClippingContextAndroid clip) {
        clippingContextBufferForDraw = clip;
    }

    /**
     * Get the offscreen frame buffer
     *
     * @return the offscreen frame buffer
     */
    CubismOffscreenSurfaceAndroid getMaskBuffer(int index) {
        return offscreenSurfaces[index];
    }

    CubismDrawableInfoCachesHolder getDrawableInfoCachesHolder() {
        return drawableInfoCachesHolder;
    }

    /**
     * Additional proccesing at the start of drawing
     * This method implements the necessary processing for the clipping mask before drawing the model
     */
    void preDraw() {
        glDisable(GL_SCISSOR_TEST);
        glDisable(GL_STENCIL_TEST);
        glDisable(GL_DEPTH_TEST);

        glEnable(GL_BLEND);
        glColorMask(true, true, true, true);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        // If the buffer has been bound before, it needs to be destroyed
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        // Anisotropic filtering. If it is not supported, do not set it
        if (getAnisotropy() >= 1.0f) {
            for (Map.Entry<Integer, Integer> entry : textures.entrySet()) {
                glBindTexture(GL_TEXTURE_2D, entry.getValue());
                glTexParameterf(GL_TEXTURE_2D, GLES11Ext.GL_TEXTURE_MAX_ANISOTROPY_EXT, getAnisotropy());
            }
        }
    }

    /**
     * Additinal processing after drawing is completed.
     */
    void postDraw() {
    }

    /**
     * テクスチャマップにバインドされたテクスチャIDを取得する。
     * バインドされていなければダミーとして-1を返します。
     *
     * @param textureId テクスチャID
     * @return バインドされたテクスチャID
     */
    int getBoundTextureId(int textureId) {
        Integer boundTextureId = textures.get(textureId);
        return boundTextureId == null ? -1 : boundTextureId;
    }

    /**
     * Frame buffer for drawing mask
     */
    CubismOffscreenSurfaceAndroid[] offscreenSurfaces;

    /**
     * マスク生成時かどうかを判定する。
     *
     * @return マスク生成時かどうか。生成時ならtrue。
     */
    private boolean isGeneratingMask() {
        return getClippingContextBufferForMask() != null;
    }

    /**
     * Map between the textures referenced by the model and the textures bound by the renderer
     */
    private final Map<Integer, Integer> textures = new HashMap<Integer, Integer>(32);

    private boolean areTexturesChanged = true;

    private Map<Integer, Integer> cachedImmutableTextures;
    /**
     * A list of drawing object indices arranged in drawing order
     */
    private int[] sortedDrawableIndexList;
    /**
     * the object which keeps the OpenGL state
     */
    private final CubismRendererProfileAndroid rendererProfile = new CubismRendererProfileAndroid();
    /**
     * Clipping mask management object
     */
    private CubismClippingManagerAndroid clippingManager;
    /**
     * Clippping context for drawing on mask texture
     */
    private CubismClippingContextAndroid clippingContextBufferForMask;
    /**
     * Clipping context for drawing on the screen
     */
    private CubismClippingContextAndroid clippingContextBufferForDraw;

    /**
     * Drawable情報のキャッシュ変数
     */
    private CubismDrawableInfoCachesHolder drawableInfoCachesHolder;
}
