/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.rendering;

import com.live2d.sdk.cubism.framework.math.CubismMatrix44;
import com.live2d.sdk.cubism.framework.model.CubismModel;

/**
 * A renderer which processes drawing models.
 * <p>
 * Environment-dependent drawing instructions are written in subclasses that inherit from this class.
 */
public abstract class CubismRenderer {
    /**
     * Color blending mode
     */
    public enum CubismBlendMode {
        NORMAL,
        ADDITIVE,
        MULTIPLICATIVE
    }

    /**
     * Data class for handling texture colors in RGBA.
     */
    public static class CubismTextureColor {
        public CubismTextureColor() {}

        public CubismTextureColor(
            float r,
            float g,
            float b,
            float a
        ) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CubismTextureColor that = (CubismTextureColor) o;

            if (Float.compare(that.r, r) != 0) return false;
            if (Float.compare(that.g, g) != 0) return false;
            if (Float.compare(that.b, b) != 0) return false;
            return Float.compare(that.a, a) == 0;
        }

        @Override
        public int hashCode() {
            int result = (r != 0.0f ? Float.floatToIntBits(r) : 0);
            result = 31 * result + (g != 0.0f ? Float.floatToIntBits(g) : 0);
            result = 31 * result + (b != 0.0f ? Float.floatToIntBits(b) : 0);
            result = 31 * result + (a != 0.0f ? Float.floatToIntBits(a) : 0);
            return result;
        }

        /**
         * Red channel
         */
        public float r = 1.0f;
        /**
         * Green channel
         */
        public float g = 1.0f;
        /**
         * Blue channel
         */
        public float b = 1.0f;
        /**
         * Alpha channel
         */
        public float a = 1.0f;
    }

    public static void delete() {

    }

    public static void staticRelease() {

    }

    /**
     * Initialize this renderer.
     * A model instance has the information which is required to initialize the renderer.
     *
     * @param model model instance
     */
    public void initialize(CubismModel model) {
        this.model = model;
    }

    /**
     * レンダラーの初期化処理を実行する。<br>
     * マスクバッファを2つ以上作成する場合はこのメソッドを使用する。第2引数に何も入れない場合のデフォルト値は1となる。
     *
     * @param model モデルのインスタンス
     * @param maskBufferCount バッファの生成数
     */
    abstract public void initialize(CubismModel model, int maskBufferCount);

    public void close() {
        model.close();
    }

    /**
     * Draw the model.
     * <p>
     * Calling following methods before and after DoDrawModel() method is required.
     * ・saveProfile()
     * ・restoreProfile()
     * These methods return the state of the renderer to the state before drawing the model.
     */
    public void drawModel() {
        if (getModel() == null) {
            return;
        }

        /*
         * Call the following methods before and after DoDrawModel() method.
         * ・saveProfile()
         * ・restoreProfile()
         * These methods save and restore the renderer's drawing settings to the state immediately before the model is drawn.
         */
        saveProfile();
        doDrawModel();
        restoreProfile();
    }


    /**
     * Get the Model-View-Projection Matrix.
     *
     * @return Model-View-Projection Matrix
     */
    public CubismMatrix44 getMvpMatrix() {
        return mvpMatrix44;
    }

    /**
     * Set the Model-View-Projection Matrix
     *
     * @param matrix4x4 Model-View-Projection Matrix
     */
    public void setMvpMatrix(final CubismMatrix44 matrix4x4) {
        mvpMatrix44.setMatrix(matrix4x4);
    }

    /**
     * Set colors of a model.
     * Each color is specified in the range 0.0~1.0. (1.0 is standard value)
     *
     * @param red value of red channel
     * @param green value of green channel
     * @param blue value of blue channel
     * @param alpha value of alpha channel
     */
    public void setModelColor(float red, float green, float blue, float alpha) {
        modelColor.r = constrain(red);
        modelColor.g = constrain(green);
        modelColor.b = constrain(blue);
        modelColor.a = constrain(alpha);
    }

    /**
     * Set colors of a model.
     *
     * @param color CubismTextureColor instance
     */
    public void setModelColor(CubismTextureColor color) {
        modelColor.r = color.r;
        modelColor.g = color.g;
        modelColor.b = color.b;
        modelColor.a = color.a;
    }

    /**
     * Get the model's colors.
     * Each color is specified in the range 0.0~1.0. (1.0 is standard value)
     *
     * @return the color information of RGBA.
     */
    public CubismTextureColor getModelColor() {
        return modelColor;
    }

    /**
     * Get the validity or invalidity of multiplied alpha.
     *
     * @return If multiplied alpha is valid, return true.
     */
    public boolean isPremultipliedAlpha() {
        return isPremultipliedAlpha;
    }

    /**
     * Set the validity or invalidity of multiplied alpha.
     *
     * @param enable a state of premultiplied alpha.
     */
    public void isPremultipliedAlpha(boolean enable) {
        isPremultipliedAlpha = enable;
    }

    /**
     * Get the validity or invalid of culling.
     *
     * @return If culling is valid, return true.
     */
    public boolean isCulling() {
        return isCulling;
    }

    /**
     * Set the validity or invalidity of culling.
     *
     * @param enable a state of culling.
     */
    public void isCulling(boolean enable) {
        isCulling = enable;
    }

    /**
     * Get the parameters for anisotropic filtering of textures.
     *
     * @return the parameters for anisotropic filtering of textures
     */
    public float getAnisotropy() {
        return anisotropy;
    }

    /**
     * Set the parameters for anisotropic filtering of textures.
     * The influence of parameter values depends on the implementation of the renderer.
     *
     * @param value parameter value
     */
    public void setAnisotropy(float value) {
        anisotropy = value;
    }

    /**
     * Get the rendered model.
     *
     * @return the rendered model
     */
    public CubismModel getModel() {
        return model;
    }

    /**
     * Change the mask rendering method.
     * In the case of false, the mask is divided into a single texture and rendered(default is this).
     * This method is high speed, but the upper limit of the number of masks is limited to 36 and the quality is also rough.
     * In the case of true, the necessary mask is redrawn before drawing parts.
     * The rendering quality is high, but the drawing processing load increase.
     *
     * @param isHigh rendering quality
     */
    public void isUsingHighPrecisionMask(boolean isHigh) {
        useHighPrecisionMask = isHigh;
    }

    /**
     * Get the mask rendering method.
     *
     * @return the mask rendering method
     */
    public boolean isUsingHighPrecisionMask() {
        return useHighPrecisionMask;
    }

    /**
     * Constructor
     */
    protected CubismRenderer() {
        mvpMatrix44 = CubismMatrix44.create();
        mvpMatrix44.loadIdentity();
    }

    /**
     * the model drawing inplementation.
     */
    protected abstract void doDrawModel();

    /**
     * Draw the drawing objects(Art Mesh).
     * Both polygon mesh and the texture number is given to this method.
     *
     * @param model model instance
     * @param drawableIndex drawable index
     * @param blendMode blend type
     * @param isInverted whether it is inverted
     */
    protected abstract void drawMesh(
        CubismModel model,
        int drawableIndex,
        CubismBlendMode blendMode,
        boolean isInverted
    );

    /**
     * Draw the drawing objects(Art Mesh).
     * Both polygon mesh and the texture number is given to this method.
     *
     * @param model model instance
     * @param drawableIndex drawable index
     */
    protected abstract void drawMesh(CubismModel model, int drawableIndex);

    /**
     * Save the state of the renderer just before drawing the model.
     */
    protected abstract void saveProfile();

    /**
     * Restore the state of the renderer just before drawing the model.
     */
    protected abstract void restoreProfile();

    /**
     * Limit a value to a range of 0.0 to 1.0.
     *
     * @param target value to limit
     * @return value after limit processing
     */
    private float constrain(float target) {
        if (target < 0.0f) {
            return 0.0f;
        } else return Math.min(target, 1.0f);
    }


    /**
     * Model-View-Projection Matrix
     */
    private final CubismMatrix44 mvpMatrix44;
    /**
     * the color which model has.(RGBA)
     */
    private final CubismTextureColor modelColor = new CubismTextureColor();
    /**
     * whether culling is valid
     */
    private boolean isCulling;
    /**
     * whether premultiplied alpha is valid
     */
    private boolean isPremultipliedAlpha;
    /**
     * parameters for anisotropic filtering of textures
     */
    private float anisotropy;
    /**
     * the model to be rendered
     */
    private CubismModel model;
    /**
     * If this is false, the masks are drawn together. If this is true, the masks are redrawn for each part drawing.
     */
    private boolean useHighPrecisionMask;
}
