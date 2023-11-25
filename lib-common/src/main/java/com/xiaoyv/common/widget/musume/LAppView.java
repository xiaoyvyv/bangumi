/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.xiaoyv.common.widget.musume;

import static com.xiaoyv.common.widget.musume.LAppDefine.DEBUG_TOUCH_LOG_ENABLE;

import com.live2d.sdk.cubism.framework.math.CubismMatrix44;
import com.live2d.sdk.cubism.framework.math.CubismViewMatrix;
import com.live2d.sdk.cubism.framework.rendering.android.CubismOffscreenSurfaceAndroid;

public class LAppView {
    /**
     * LAppModelのレンダリング先
     */
    public enum RenderingTarget {
        NONE,   // デフォルトのフレームバッファにレンダリング
        MODEL_FRAME_BUFFER,     // LAppModelForSmallDemoが各自持つフレームバッファにレンダリング
        VIEW_FRAME_BUFFER  // LAppViewForSmallDemoが持つフレームバッファにレンダリング
    }

    public LAppView() {
        clearColor[0] = 1.0f;
        clearColor[1] = 1.0f;
        clearColor[2] = 1.0f;
        clearColor[3] = 0.0f;
    }

    // シェーダーを初期化する
    public void initializeShader() {
        programId = LAppDelegate.getInstance().createShader();
    }

    // ビューを初期化する
    public void initialize() {
        int width = LAppDelegate.getInstance().getWindowWidth();
        int height = LAppDelegate.getInstance().getWindowHeight();

        float ratio = (float) width / (float) height;
        float left = -ratio;
        float right = ratio;
        float bottom = LAppDefine.LogicalView.LEFT.getValue();
        float top = LAppDefine.LogicalView.RIGHT.getValue();

        // デバイスに対応する画面範囲。Xの左端、Xの右端、Yの下端、Yの上端
        viewMatrix.setScreenRect(left, right, bottom, top);
        viewMatrix.scale(LAppDefine.Scale.DEFAULT.getValue(), LAppDefine.Scale.DEFAULT.getValue());

        // 単位行列に初期化
        deviceToScreen.loadIdentity();

        if (width > height) {
            float screenW = Math.abs(right - left);
            deviceToScreen.scaleRelative(screenW / width, -screenW / width);
        } else {
            float screenH = Math.abs(top - bottom);
            deviceToScreen.scaleRelative(screenH / height, -screenH / height);
        }
        deviceToScreen.translateRelative(-width * 0.5f, -height * 0.5f);

        // 表示範囲の設定
        viewMatrix.setMaxScale(LAppDefine.Scale.MAX.getValue());   // 限界拡大率
        viewMatrix.setMinScale(LAppDefine.Scale.MIN.getValue());   // 限界縮小率

        // 表示できる最大範囲
        viewMatrix.setMaxScreenRect(
                LAppDefine.MaxLogicalView.LEFT.getValue(),
                LAppDefine.MaxLogicalView.RIGHT.getValue(),
                LAppDefine.MaxLogicalView.BOTTOM.getValue(),
                LAppDefine.MaxLogicalView.TOP.getValue()
        );
    }

    // 画像を初期化する
    public void initializeSprite() {
        int windowWidth = LAppDelegate.getInstance().getWindowWidth();
        int windowHeight = LAppDelegate.getInstance().getWindowHeight();

        LAppTextureManager textureManager = LAppDelegate.getInstance().getTextureManager();

        // 加载背景图像
        LAppTextureManager.TextureInfo backgroundTexture = textureManager.createTextureFromPngFile(LAppDefine.ResourcePath.ROOT.getPath() + LAppDefine.ResourcePath.BACK_IMAGE.getPath());

        // x，y 是图像的中心坐标
        float x = windowWidth * 0.5f;
        float y = windowHeight * 0.5f;
        float fWidth = backgroundTexture.width * 2.0f;
        float fHeight = windowHeight * 0.95f;

        if (backSprite == null) {
            backSprite = new LAppSprite(x, y, fWidth, fHeight, backgroundTexture.id, programId);
        } else {
            backSprite.resize(x, y, fWidth, fHeight);
        }

        // 歯車画像の読み込み
        LAppTextureManager.TextureInfo gearTexture = textureManager.createTextureFromPngFile(LAppDefine.ResourcePath.ROOT.getPath() + LAppDefine.ResourcePath.GEAR_IMAGE.getPath());


        x = windowWidth - gearTexture.width * 0.5f - 96.f;
        y = windowHeight - gearTexture.height * 0.5f;
        fWidth = (float) gearTexture.width;
        fHeight = (float) gearTexture.height;

        if (gearSprite == null) {
            gearSprite = new LAppSprite(x, y, fWidth, fHeight, gearTexture.id, programId);
        } else {
            gearSprite.resize(x, y, fWidth, fHeight);
        }

        // 電源画像の読み込み
        LAppTextureManager.TextureInfo powerTexture = textureManager.createTextureFromPngFile(LAppDefine.ResourcePath.ROOT.getPath() + LAppDefine.ResourcePath.POWER_IMAGE.getPath());

        x = windowWidth - powerTexture.width * 0.5f - 96.0f;
        y = powerTexture.height * 0.5f;
        fWidth = (float) powerTexture.width;
        fHeight = (float) powerTexture.height;

        if (powerSprite == null) {
            powerSprite = new LAppSprite(x, y, fWidth, fHeight, powerTexture.id, programId);
        } else {
            powerSprite.resize(x, y, fWidth, fHeight);
        }

        // 画面全体を覆うサイズ
        x = windowWidth * 0.5f;
        y = windowHeight * 0.5f;

        if (renderingSprite == null) {
            renderingSprite = new LAppSprite(x, y, windowWidth, windowHeight, 0, programId);
        } else {
            renderingSprite.resize(x, y, windowWidth, windowHeight);
        }
    }

    // 描画する
    public void render() {
        // UIと背景の描画
        //backSprite.render();
        //gearSprite.render();
       // powerSprite.render();

        if (isChangedModel) {
            isChangedModel = false;
            LAppLive2DManager.getInstance().nextScene();
        }

        // モデルの描画
        LAppLive2DManager live2dManager = LAppLive2DManager.getInstance();
        live2dManager.onUpdate();

        // When the texture is the drawing target of each model
        if (renderingTarget == RenderingTarget.MODEL_FRAME_BUFFER && renderingSprite != null) {
            final float[] uvVertex = {
                    1.0f, 1.0f,
                    0.0f, 1.0f,
                    0.0f, 0.0f,
                    1.0f, 0.0f
            };

            for (int i = 0; i < live2dManager.getModelNum(); i++) {
                LAppModel model = live2dManager.getModel(i);
                float alpha = i < 1 ? 1.0f : model.getOpacity();    // 片方のみ不透明度を取得できるようにする。

                renderingSprite.setColor(1.0f, 1.0f, 1.0f, alpha);

                if (model != null) {
                    renderingSprite.renderImmediate(model.getRenderingBuffer().getColorBuffer()[0], uvVertex);
                }
            }
        }
    }

    /**
     * モデル1体を描画する直前にコールされる
     *
     * @param refModel モデルデータ
     */
    public void preModelDraw(LAppModel refModel) {
        // 別のレンダリングターゲットへ向けて描画する場合の使用するフレームバッファ
        CubismOffscreenSurfaceAndroid useTarget;

        // 別のレンダリングターゲットへ向けて描画する場合
        if (renderingTarget != RenderingTarget.NONE) {

            // 使用するターゲット
            useTarget = (renderingTarget == RenderingTarget.VIEW_FRAME_BUFFER)
                    ? renderingBuffer
                    : refModel.getRenderingBuffer();

            // 描画ターゲット内部未作成の場合はここで作成
            if (!useTarget.isValid()) {
                int width = LAppDelegate.getInstance().getWindowWidth();
                int height = LAppDelegate.getInstance().getWindowHeight();

                // モデル描画キャンバス
                useTarget.createOffscreenFrame((int) width, (int) height, null);
            }
            // レンダリング開始
            useTarget.beginDraw(null);
            useTarget.clear(clearColor[0], clearColor[1], clearColor[2], clearColor[3]);   // 背景クリアカラー
        }
    }

    /**
     * モデル1体を描画した直後にコールされる
     *
     * @param refModel モデルデータ
     */
    public void postModelDraw(LAppModel refModel) {
        CubismOffscreenSurfaceAndroid useTarget = null;

        // 別のレンダリングターゲットへ向けて描画する場合
        if (renderingTarget != RenderingTarget.NONE) {
            // 使用するターゲット
            useTarget = (renderingTarget == RenderingTarget.VIEW_FRAME_BUFFER)
                    ? renderingBuffer
                    : refModel.getRenderingBuffer();

            // レンダリング終了
            useTarget.endDraw();

            // LAppViewの持つフレームバッファを使うなら、スプライトへの描画はこことなる
            if (renderingTarget == RenderingTarget.VIEW_FRAME_BUFFER && renderingSprite != null) {
                final float[] uvVertex = {
                        1.0f, 1.0f,
                        0.0f, 1.0f,
                        0.0f, 0.0f,
                        1.0f, 0.0f
                };
                renderingSprite.setColor(1.0f, 1.0f, 1.0f, getSpriteAlpha(0));
                renderingSprite.renderImmediate(useTarget.getColorBuffer()[0], uvVertex);
            }
        }
    }

    /**
     * レンダリング先を切り替える
     *
     * @param targetType レンダリング先
     */
    public void switchRenderingTarget(RenderingTarget targetType) {
        renderingTarget = targetType;
    }

    /**
     * タッチされたときに呼ばれる
     *
     * @param pointX スクリーンX座標
     * @param pointY スクリーンY座標
     */
    public void onTouchesBegan(float pointX, float pointY) {
        touchManager.touchesBegan(pointX, pointY);
    }

    /**
     * タッチしているときにポインターが動いたら呼ばれる
     *
     * @param pointX スクリーンX座標
     * @param pointY スクリーンY座標
     */
    public void onTouchesMoved(float pointX, float pointY) {
        float viewX = transformViewX(touchManager.getLastX());
        float viewY = transformViewY(touchManager.getLastY());

        touchManager.touchesMoved(pointX, pointY);

        LAppLive2DManager.getInstance().onDrag(viewX, viewY);
    }

    /**
     * タッチが終了したら呼ばれる
     *
     * @param pointX スクリーンX座標
     * @param pointY スクリーンY座標
     */
    public void onTouchesEnded(float pointX, float pointY) {
        // タッチ終了
        LAppLive2DManager live2DManager = LAppLive2DManager.getInstance();
        live2DManager.onDrag(0.0f, 0.0f);

        // シングルタップ
        // 論理座標変換した座標を取得
        float x = deviceToScreen.transformX(touchManager.getLastX());
        // 論理座標変換した座標を取得
        float y = deviceToScreen.transformY(touchManager.getLastY());

        if (DEBUG_TOUCH_LOG_ENABLE) {
            LAppPal.printLog("触摸结束 x: " + x + ", y:" + y);
        }

        live2DManager.onTap(x, y);

        // 歯車ボタンにタップしたか
        if (gearSprite.isHit(pointX, pointY)) {
            isChangedModel = true;
        }

        // 電源ボタンにタップしたか
        if (powerSprite.isHit(pointX, pointY)) {
            // アプリを終了する
            LAppDelegate.getInstance().deactivateApp();
        }

    }

    /**
     * X座標をView座標に変換する
     *
     * @param deviceX デバイスX座標
     * @return ViewX座標
     */
    public float transformViewX(float deviceX) {
        // 論理座標変換した座標を取得
        float screenX = deviceToScreen.transformX(deviceX);
        // 拡大、縮小、移動後の値
        return viewMatrix.invertTransformX(screenX);
    }

    /**
     * Y座標をView座標に変換する
     *
     * @param deviceY デバイスY座標
     * @return ViewY座標
     */
    public float transformViewY(float deviceY) {
        // 論理座標変換した座標を取得
        float screenY = deviceToScreen.transformY(deviceY);
        // 拡大、縮小、移動後の値
        return viewMatrix.invertTransformX(screenY);
    }

    /**
     * X座標をScreen座標に変換する
     *
     * @param deviceX デバイスX座標
     * @return ScreenX座標
     */
    public float transformScreenX(float deviceX) {
        return deviceToScreen.transformX(deviceX);
    }

    /**
     * Y座標をScreen座標に変換する
     *
     * @param deviceY デバイスY座標
     * @return ScreenY座標
     */
    public float transformScreenY(float deviceY) {
        return deviceToScreen.transformX(deviceY);
    }

    /**
     * レンダリング先をデフォルト以外に切り替えた際の背景クリア色設定
     *
     * @param r 赤(0.0~1.0)
     * @param g 緑(0.0~1.0)
     * @param b 青(0.0~1.0)
     */
    public void setRenderingTargetClearColor(float r, float g, float b) {
        clearColor[0] = r;
        clearColor[1] = g;
        clearColor[2] = b;
    }

    /**
     * 別レンダリングターゲットにモデルを描画するサンプルで描画時のαを決定する
     *
     * @param assign
     * @return
     */
    public float getSpriteAlpha(int assign) {
        // assignの数値に応じて適当な差をつける
        float alpha = 0.25f + (float) assign * 0.5f;

        // サンプルとしてαに適当な差をつける
        if (alpha > 1.0f) {
            alpha = 1.0f;
        }
        if (alpha < 0.1f) {
            alpha = 0.1f;
        }
        return alpha;
    }

    /**
     * Return rendering target enum instance.
     *
     * @return rendering target
     */
    public RenderingTarget getRenderingTarget() {
        return renderingTarget;
    }

    private final CubismMatrix44 deviceToScreen = CubismMatrix44.create(); // デバイス座標からスクリーン座標に変換するための行列
    private final CubismViewMatrix viewMatrix = new CubismViewMatrix();   // 画面表示の拡縮や移動の変換を行う行列
    private int programId;
    private int windowWidth;
    private int windowHeight;

    /**
     * レンダリング先の選択肢
     */
    private RenderingTarget renderingTarget = RenderingTarget.NONE;
    /**
     * レンダリングターゲットのクリアカラー
     */
    private final float[] clearColor = new float[4];

    private CubismOffscreenSurfaceAndroid renderingBuffer = new CubismOffscreenSurfaceAndroid();

    private LAppSprite backSprite;
    private LAppSprite gearSprite;
    private LAppSprite powerSprite;
    private LAppSprite renderingSprite;

    /**
     * モデルの切り替えフラグ
     */
    private boolean isChangedModel;

    private final TouchManager touchManager = new TouchManager();
}
