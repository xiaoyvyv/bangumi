/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.rendering.android;

import android.opengl.GLES11Ext;
import com.live2d.sdk.cubism.framework.math.CubismVector2;
import com.live2d.sdk.cubism.framework.model.CubismModel;
import com.live2d.sdk.cubism.framework.rendering.CubismRenderer;
import com.live2d.sdk.cubism.framework.utils.CubismDebug;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static android.opengl.GLES20.*;
import static com.live2d.sdk.cubism.framework.CubismFrameworkConfig.CSM_DEBUG;

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
     * @param extMode Whether to draw using the extended method
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
                model.getDrawableCount(),
                model.getDrawableMasks(),
                model.getDrawableMaskCounts(),
                maskBufferCount
            );

            offscreenFrameBuffers = new CubismOffscreenSurfaceAndroid[maskBufferCount];

            for (int i = 0; i < maskBufferCount; i++) {
                CubismOffscreenSurfaceAndroid offscreenSurface = new CubismOffscreenSurfaceAndroid();
                offscreenSurface.createOffscreenFrame(clippingManager.getClippingMaskBufferSize(), null);

                offscreenFrameBuffers[i] = offscreenSurface;
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

        if (offscreenFrameBuffers != null) {
            for (int i = 0; i < offscreenFrameBuffers.length; i++) {
                if (offscreenFrameBuffers[i].isValid()) {
                    offscreenFrameBuffers[i].destroyOffscreenFrame();
                }
            }
        }

        drawableInfoCachesHolder = null;
    }

    /**
     * Bind processing of OpenGL textures.
     *
     * @param modelTextureNo number of the model texture to set
     * @param glTextureNo number of the OpenGL texture to bind
     */
    public void bindTexture(int modelTextureNo, int glTextureNo) {
        textures.put(modelTextureNo, glTextureNo);
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
     * This method's processing cost is high because the FrameBuffer for the mask is destroyed and recreated.
     *
     * @param width width of MaskBufferSize
     * @param height height of MaskBufferSize
     */
    public void setClippingMaskBufferSize(final float width, final float height) {
        if (clippingManager == null) {
            return;
        }

        // インスタンス破棄前にレンダーテクスチャの数を保存
        final int renderTextureCount = this.clippingManager.getRenderTextureCount();

        // Destroy and recreate instances to change the size of FrameBuffer
        clippingManager = new CubismClippingManagerAndroid();
        clippingManager.setClippingMaskBufferSize(width, height);

        CubismModel model = getModel();
        clippingManager.initialize(
            model.getDrawableCount(),
            model.getDrawableMasks(),
            model.getDrawableMaskCounts(),
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
     * @param textureNo number of the drawed texture
     * @param indexCount index of the drawing object
     * @param vertexCount number of the polygon mesh vertices
     * @param indexArray index array of the polygon mesh
     * @param vertexArrayBuffer vertex array of the polygon mesh
     * @param uvArrayBuffer uv array
     * @param multiplyColor multiply color
     * @param screenColor screen color
     * @param opacity opacity
     * @param colorBlendMode color blending mode
     * @param invertedMask Inverted use of masks when masks are used
     */
    protected void drawMeshAndroid(
        int textureNo,
        int indexCount,
        int vertexCount,
        ShortBuffer indexArray,
        FloatBuffer vertexArrayBuffer,
        FloatBuffer uvArrayBuffer,
        CubismTextureColor multiplyColor,
        CubismTextureColor screenColor,
        float opacity,
        CubismBlendMode colorBlendMode,
        boolean invertedMask
    ) {
        if (!CSM_DEBUG) {
            // If the texture referenced by the model is not bound, skip drawing.
            if (textures.get(textureNo) == null) {
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

        CubismTextureColor tmp = getModelColor();
        modelColorRGBA.r = tmp.r;
        modelColorRGBA.g = tmp.g;
        modelColorRGBA.b = tmp.b;
        modelColorRGBA.a = tmp.a;


        // Except at generating mask
        if (getClippingContextBufferForMask() == null) {
            modelColorRGBA.a *= opacity;
            if (isPremultipliedAlpha()) {
                modelColorRGBA.r *= modelColorRGBA.a;
                modelColorRGBA.g *= modelColorRGBA.a;
                modelColorRGBA.b *= modelColorRGBA.a;
            }
        }

        // Texture ID given to the shader
        int drawTextureId;

        // Get the bound texture ID from the texture map.
        // If the ID has not bound, set the dummy texture ID.
        if (textures.get(textureNo) != null) {
            drawTextureId = textures.get(textureNo);
        } else {
            drawTextureId = -1;
        }

        CubismShaderAndroid.getInstance().setupShaderProgram(
            this,
            drawTextureId,
            vertexCount,
            vertexArrayBuffer,
            uvArrayBuffer,
            opacity,
            colorBlendMode,
            modelColorRGBA,
            multiplyColor,
            screenColor,
            isPremultipliedAlpha(),
            getMvpMatrix(),
            invertedMask
        );

        // Draw the prygon mesh
        glDrawElements(
            GL_TRIANGLES,
            indexCount,
            GL_UNSIGNED_SHORT,
            indexArray
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
                CubismOffscreenSurfaceAndroid offscreenFrameBuffer = offscreenFrameBuffers[i];

                if (!offscreenFrameBuffer.isSameSize(clippingManager.getClippingMaskBufferSize())) {
                    offscreenFrameBuffer.createOffscreenFrame(clippingManager.getClippingMaskBufferSize(), null);
                }
            }

            clippingManager.setupClippingContext(model, this, rendererProfile.lastFBO, rendererProfile.lastViewport);
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
            CubismClippingContext clipContext = (clippingManager != null)
                ? clippingManager.getClippingContextListForDraw().get(drawableIndex)
                : null;

            // マスクを描く必要がある
            if (clipContext != null && isUsingHighPrecisionMask()) {
                // 描くことになっていた
                if (clipContext.isUsing) {
                    // 生成したFrameBufferと同じサイズでビューポートを設定
                    glViewport(0, 0, (int) clippingManager.getClippingMaskBufferSize().x, (int) clippingManager.getClippingMaskBufferSize().y);

                    // バッファをクリアする
                    preDraw();

                    // マスク描画処理
                    // マスク用RenderTextureをactiveにセット
                    getMaskBuffer(clipContext.bufferIndex).beginDraw(rendererProfile.lastFBO);

                    // マスクをクリアする。
                    // 1が無効（描かれない領域）、0が有効（描かれる）領域。（シェーダーでCd*Csで0に近い値をかけてマスクを作る。1をかけると何も起こらない。）
                    glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
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

                    drawMeshAndroid(
                        model.getDrawableTextureIndex(clipDrawIndex),
                        model.getDrawableVertexIndexCount(clipDrawIndex),
                        model.getDrawableVertexCount(clipDrawIndex),
                        indexArrayBuffer,
                        vertexArrayBuffer,
                        uvArrayBuffer,
                        model.getMultiplyColor(clipDrawIndex),
                        model.getScreenColor(clipDrawIndex),
                        model.getDrawableOpacity(clipDrawIndex),
                        CubismBlendMode.NORMAL, // クリッピングは通常描画を強制
                        false   // マスク生成時はクリッピングの反転使用は全く関係がない
                    );
                }
                // --- 後処理 ---
                for (int j = 0; j < clippingManager.getRenderTextureCount(); j++) {
                    offscreenFrameBuffers[j].endDraw();
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

            // vertex array
            FloatBuffer vertexArrayBuffer = drawableInfoCachesHolder.setUpVertexArray(
                drawableIndex,
                model.getDrawableVertices(drawableIndex)
            );

            // uv array
            FloatBuffer uvArrayBuffer = drawableInfoCachesHolder.setUpUvArray(
                drawableIndex,
                model.getDrawableVertexUvs(drawableIndex)
            );

            // index array
            ShortBuffer indexArrayBuffer = drawableInfoCachesHolder.setUpIndexArray(
                drawableIndex,
                model.getDrawableVertexIndices(drawableIndex)
            );

            drawMeshAndroid(
                model.getDrawableTextureIndex(drawableIndex),
                model.getDrawableVertexIndexCount(drawableIndex),
                model.getDrawableVertexCount(drawableIndex),
                indexArrayBuffer,
                vertexArrayBuffer,
                uvArrayBuffer,
                model.getMultiplyColor(drawableIndex),
                model.getScreenColor(drawableIndex),
                model.getDrawableOpacity(drawableIndex),
                model.getDrawableBlendMode(drawableIndex),
                model.getDrawableInvertedMask(drawableIndex)    // Whether the mask is used inverted
            );
        }
        postDraw();
    }

    @Override
    protected void drawMesh(CubismModel model, int drawableIndex) {
        drawMesh(model,
            drawableIndex,
            model.getDrawableBlendMode(drawableIndex),
            model.getDrawableInvertedMask(drawableIndex));
    }

    @Override
    protected void drawMesh(
        final CubismModel model,
        final int drawableIndex,
        final CubismBlendMode blendMode,
        final boolean isInverted
    ) {
        CubismDebug.cubismLogWarning("Use 'drawMeshAndroid' function");
        assert false;
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
    CubismClippingContext getClippingContextBufferForMask() {
        return clippingContextBufferForMask;
    }

    /**
     * Set the clipping context to draw to the mask texture
     *
     * @param clip clipping context to draw to the mask texture
     */
    void setClippingContextBufferForMask(CubismClippingContext clip) {
        clippingContextBufferForMask = clip;
    }

    /**
     * Get the clipping context to draw on display
     *
     * @return the clipping context to draw on display
     */
    CubismClippingContext getClippingContextBufferForDraw() {
        return clippingContextBufferForDraw;
    }

    /**
     * Set the clipping context to draw on display
     *
     * @param clip the clipping context to draw on display
     */
    void setClippingContextBufferForDraw(CubismClippingContext clip) {
        clippingContextBufferForDraw = clip;
    }

    /**
     * Get the offscreen frame buffer
     *
     * @return the offscreen frame buffer
     */
    CubismOffscreenSurfaceAndroid getMaskBuffer(int index) {
        return offscreenFrameBuffers[index];
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
        if (getAnisotropy() > 0.0f) {
            for (Map.Entry<Integer, Integer> entry : textures.entrySet()) {
                glBindTexture(GL_TEXTURE_2D, entry.getValue());
                glTexParameterf(GL_TEXTURE_2D, GLES11Ext.GL_TEXTURE_MAX_ANISOTROPY_EXT, getAnisotropy());
            }
        }
    }

    /**
     * Additinal processing after drawing is completed.
     */
    void postDraw() {}

    /**
     * Frame buffer for drawing mask
     */
    CubismOffscreenSurfaceAndroid[] offscreenFrameBuffers;

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
    private CubismClippingContext clippingContextBufferForMask;
    /**
     * Clipping context for drawing on the screen
     */
    private CubismClippingContext clippingContextBufferForDraw;

    /**
     * Drawable情報のキャッシュ変数
     */
    private CubismDrawableInfoCachesHolder drawableInfoCachesHolder;
}
