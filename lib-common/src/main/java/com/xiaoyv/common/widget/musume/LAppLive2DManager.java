/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.xiaoyv.common.widget.musume;

import static com.xiaoyv.common.widget.musume.LAppDefine.DEBUG_LOG_ENABLE;
import static com.xiaoyv.common.widget.musume.LAppDefine.USE_MODEL_RENDER_TARGET;
import static com.xiaoyv.common.widget.musume.LAppDefine.USE_RENDER_TARGET;

import com.live2d.sdk.cubism.framework.math.CubismMatrix44;
import com.live2d.sdk.cubism.framework.motion.ACubismMotion;
import com.live2d.sdk.cubism.framework.motion.IFinishedMotionCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * サンプルアプリケーションにおいてCubismModelを管理するクラス。
 * モデル生成と破棄、タップイベントの処理、モデル切り替えを行う。
 */
public class LAppLive2DManager {
    public static LAppLive2DManager getInstance() {
        if (s_instance == null) {
            s_instance = new LAppLive2DManager();
        }
        return s_instance;
    }

    public static void releaseInstance() {
        s_instance = null;
    }

    /**
     * 現在のシーンで保持している全てのモデルを解放する
     */
    public void releaseAllModel() {
        for (LAppModel model : models) {
            model.deleteModel();
        }
        models.clear();
    }

    // モデル更新処理及び描画処理を行う
    public void onUpdate() {
        int width = LAppDelegate.getInstance().getWindowWidth();
        int height = LAppDelegate.getInstance().getWindowHeight();

        for (int i = 0; i < models.size(); i++) {
            LAppModel model = models.get(i);

            if (model.getModel() == null) {
                LAppPal.printLog("Failed to model.getModel().");
                continue;
            }

            projection.loadIdentity();

            if (model.getModel().getCanvasWidth() > 1.0f && width < height) {
                // 横に長いモデルを縦長ウィンドウに表示する際モデルの横サイズでscaleを算出する
                model.getModelMatrix().setWidth(2.0f);
                projection.scale(1.0f, (float) width / (float) height);
            } else {
                projection.scale((float) height / (float) width, 1.0f);
            }

            // 必要があればここで乗算する
            if (viewMatrix != null) {
                projection.multiplyByMatrix(viewMatrix);
            }

            // モデル1体描画前コール
            LAppDelegate.getInstance().getView().preModelDraw(model);

            model.update();

            model.draw(projection);     // 参照渡しなのでprojectionは変質する

            // モデル1体描画後コール
            LAppDelegate.getInstance().getView().postModelDraw(model);
        }
    }

    /**
     * 画面をドラッグした時の処理
     *
     * @param x 画面のx座標
     * @param y 画面のy座標
     */
    public void onDrag(float x, float y) {
        for (int i = 0; i < models.size(); i++) {
            LAppModel model = getModel(i);
            model.setDragging(x, y);
        }
    }

    /**
     * 画面をタップした時の処理
     *
     * @param x 画面のx座標
     * @param y 画面のy座標
     */
    public void onTap(float x, float y) {
        if (DEBUG_LOG_ENABLE) {
            LAppPal.printLog("tap point: {" + x + ", y: " + y);
        }

        for (int i = 0; i < models.size(); i++) {
            LAppModel model = models.get(i);

            // 如果你点击你的头，你的面部表情会随机播放
            if (model.hitTest(LAppDefine.HitAreaName.HAIR.getId(), x, y)) {
                if (DEBUG_LOG_ENABLE) {
                    LAppPal.printLog("hit area: " + LAppDefine.HitAreaName.HAIR.getId());
                }
                model.setRandomExpression();
            }
            // 如果你轻拍你的脸
            else if (model.hitTest(LAppDefine.HitAreaName.FACE.getId(), x, y)) {
                if (DEBUG_LOG_ENABLE) {
                    LAppPal.printLog("hit area: " + LAppDefine.HitAreaName.FACE.getId());
                }
                model.startRandomMotion(LAppDefine.MotionGroup.Tap.getId(), LAppDefine.Priority.NORMAL.getPriority(), finishedMotion);
            }
            // 如果你轻拍你的身体，它会开始一个随机的运动
            else if (model.hitTest(LAppDefine.HitAreaName.BODY.getId(), x, y)) {
                if (DEBUG_LOG_ENABLE) {
                    LAppPal.printLog("hit area: " + LAppDefine.HitAreaName.BODY.getId());
                }
                model.startRandomMotion(LAppDefine.MotionGroup.TAP_BODY.getId(), LAppDefine.Priority.NORMAL.getPriority(), finishedMotion);
            }
        }
    }

    /**
     * 切换到下一个场景
     * 在示例应用程序中，模型集是切换的。
     */
    public void nextScene() {
        final int number = (currentModel.getOrder() + 1) % LAppDefine.ModelDir.values().length;

        changeScene(number);
    }

    /**
     * シーンを切り替える
     *
     * @param index 切り替えるシーンインデックス
     */
    public void changeScene(int index) {
        currentModel = LAppDefine.ModelDir.values()[index];
        if (DEBUG_LOG_ENABLE) {
            LAppPal.printLog("model index: " + currentModel.getOrder());
        }

        String modelDirName = currentModel.getDirName();
        String modelPath = LAppDefine.ResourcePath.ROOT.getPath() + modelDirName + "/";
        String modelJsonName = currentModel.getDirName() + ".model3.json";

        releaseAllModel();

        models.add(new LAppModel());
        models.get(0).loadAssets(modelPath, modelJsonName);

        /*
         * モデル半透明表示を行うサンプルを提示する。
         * ここでUSE_RENDER_TARGET、USE_MODEL_RENDER_TARGETが定義されている場合
         * 別のレンダリングターゲットにモデルを描画し、描画結果をテクスチャとして別のスプライトに張り付ける。
         */
        LAppView.RenderingTarget useRenderingTarget;
        if (USE_RENDER_TARGET) {
            // LAppViewの持つターゲットに描画を行う場合こちらを選択
            useRenderingTarget = LAppView.RenderingTarget.VIEW_FRAME_BUFFER;
        } else if (USE_MODEL_RENDER_TARGET) {
            // 各LAppModelの持つターゲットに描画を行う場合こちらを選択
            useRenderingTarget = LAppView.RenderingTarget.MODEL_FRAME_BUFFER;
        } else {
            // デフォルトのメインフレームバッファへレンダリングする(通常)
            useRenderingTarget = LAppView.RenderingTarget.NONE;
        }

        if (USE_RENDER_TARGET || USE_MODEL_RENDER_TARGET) {
            // モデル個別にαを付けるサンプルとして、もう1体モデルを作成し少し位置をずらす。
            models.add(new LAppModel());
            models.get(1).loadAssets(modelPath, modelJsonName);
            models.get(1).getModelMatrix().translateX(0.2f);
        }

        // レンダリングターゲットを切り替える
        LAppDelegate.getInstance().getView().switchRenderingTarget(useRenderingTarget);

        // 別レンダリング先を選択した際の背景クリア色
        float[] clearColor = {1.0f, 1.0f, 1.0f};
        LAppDelegate.getInstance().getView().setRenderingTargetClearColor(clearColor[0], clearColor[1], clearColor[2]);
    }

    /**
     * 現在のシーンで保持しているモデルを返す
     *
     * @param number モデルリストのインデックス値
     * @return モデルのインスタンスを返す。インデックス値が範囲外の場合はnullを返す
     */
    public LAppModel getModel(int number) {
        if (number < models.size()) {
            return models.get(number);
        }
        return null;
    }

    /**
     * シーンインデックスを返す
     *
     * @return シーンインデックス
     */
    public LAppDefine.ModelDir getCurrentModel() {
        return currentModel;
    }

    /**
     * Return the number of models in this LAppLive2DManager instance has.
     *
     * @return number fo models in this LAppLive2DManager instance has. If models list is null, return 0.
     */
    public int getModelNum() {
        if (models == null) {
            return 0;
        }
        return models.size();
    }

    /**
     * モーション終了時に実行されるコールバック関数
     */
    private static class FinishedMotion implements IFinishedMotionCallback {
        @Override
        public void execute(ACubismMotion motion) {
            LAppPal.printLog("Motion Finished: " + motion);
        }
    }

    private static final FinishedMotion finishedMotion = new FinishedMotion();

    /**
     * シングルトンインスタンス
     */
    private static LAppLive2DManager s_instance;

    private LAppLive2DManager() {
        currentModel = LAppDefine.ModelDir.values()[0];
        changeScene(currentModel.getOrder());
    }

    private final List<LAppModel> models = new ArrayList<LAppModel>();

    /**
     * 表示するシーンのインデックス値
     */
    private LAppDefine.ModelDir currentModel;

    // onUpdateメソッドで使用されるキャッシュ変数
    private final CubismMatrix44 viewMatrix = CubismMatrix44.create();
    private final CubismMatrix44 projection = CubismMatrix44.create();
}
