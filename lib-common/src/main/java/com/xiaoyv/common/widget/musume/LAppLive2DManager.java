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
 * 在示例应用中管理CubismModel的类。
 * 负责模型的创建、销毁、处理触摸事件以及模型切换。
 */
public class LAppLive2DManager {
    private static final FinishedMotion finishedMotion = new FinishedMotion();

    /**
     * 单例实例
     */
    private static LAppLive2DManager s_instance;

    private final List<LAppModel> models = new ArrayList<LAppModel>();

    /**
     * 当前要显示的场景的索引值
     */
    private LAppDefine.ModelDir currentModel;

    // 在 onUpdate 方法中使用的缓存变量
    private final CubismMatrix44 viewMatrix = CubismMatrix44.create();
    private final CubismMatrix44 projection = CubismMatrix44.create();

    private LAppLive2DManager() {
        currentModel = LAppDefine.ModelDir.values()[0];
        changeScene(currentModel.getOrder());
    }

    public static void releaseInstance() {
        s_instance = null;
    }

    /**
     * 释放当前场景中所有持有的模型
     */
    public void releaseAllModel() {
        for (LAppModel model : models) {
            model.deleteModel();
        }
        models.clear();
    }

    // 模型更新和绘制的处理
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
                // 在纵向窗口中显示横向较长的模型时，通过模型的宽度计算比例
                model.getModelMatrix().setWidth(2.0f);
                projection.scale(1.0f, (float) width / (float) height);
            } else {
                projection.scale((float) height / (float) width, 1.0f);
            }

            // 在这里进行必要的矩阵乘法
            projection.multiplyByMatrix(viewMatrix);

            // 在单个模型绘制前调用
            LAppDelegate.getInstance().getView().preModelDraw(model);

            model.update();

            model.draw(projection);     // projection是引用传递，会被修改

            // 在单个模型绘制后调用
            LAppDelegate.getInstance().getView().postModelDraw(model);
        }
    }

    /**
     * 当屏幕被拖动时的处理
     *
     * @param x 屏幕的x坐标
     * @param y 屏幕的y坐标
     */
    public void onDrag(float x, float y) {
        for (int i = 0; i < models.size(); i++) {
            LAppModel model = getModel(i);
            model.setDragging(x, y);
        }
    }

    /**
     * 当屏幕被轻拍时的处理
     *
     * @param x 屏幕的x坐标
     * @param y 屏幕的y坐标
     */
    public void onTap(float x, float y) {
        if (DEBUG_LOG_ENABLE) {
            LAppPal.printLog("点击坐标: {x: " + x + ", y: " + y);
        }

        for (int i = 0; i < models.size(); i++) {
            LAppModel model = models.get(i);

            // 如果点击头部区域，随机播放面部表情
            if (model.hitTest(LAppDefine.HitAreaName.HAIR.getId(), x, y)) {
                if (DEBUG_LOG_ENABLE) {
                    LAppPal.printLog("点击区域: " + LAppDefine.HitAreaName.HAIR.getId());
                }
                model.startRandomMotion(LAppDefine.MotionGroup.TAP_HAIR.getId(), LAppDefine.Priority.NORMAL.getPriority(), finishedMotion);
                model.startRandomSound(LAppDefine.HitAreaName.HAIR);
            }
            // 如果轻拍脸部区域
            else if (model.hitTest(LAppDefine.HitAreaName.FACE.getId(), x, y)) {
                if (DEBUG_LOG_ENABLE) {
                    LAppPal.printLog("点击区域: " + LAppDefine.HitAreaName.FACE.getId());
                }
                model.startRandomMotion(LAppDefine.MotionGroup.TAP_FACE.getId(), LAppDefine.Priority.NORMAL.getPriority(), finishedMotion);
                model.startRandomSound(LAppDefine.HitAreaName.FACE);
            }
            // 如果轻拍身体区域，开始一个随机的动作
            else if (model.hitTest(LAppDefine.HitAreaName.BODY.getId(), x, y)) {
                if (DEBUG_LOG_ENABLE) {
                    LAppPal.printLog("点击区域: " + LAppDefine.HitAreaName.BODY.getId());
                }
                model.startRandomMotion(LAppDefine.MotionGroup.TAP_BODY.getId(), LAppDefine.Priority.NORMAL.getPriority(), finishedMotion);
                model.startRandomSound(LAppDefine.HitAreaName.BODY);
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
     * 切换到指定场景
     *
     * @param index 要切换的场景的索引
     */
    public void changeScene(int index) {
        currentModel = LAppDefine.ModelDir.values()[index];
        if (DEBUG_LOG_ENABLE) {
            LAppPal.printLog("模型索引: " + currentModel.getOrder());
        }

        String modelDirName = currentModel.getDirName();
        String modelPath = LAppDefine.ResourcePath.ROOT.getPath() + modelDirName + "/";
        String modelJsonName = currentModel.getDirName() + ".model3.json";

        releaseAllModel();

        models.add(new LAppModel());
        models.get(0).loadAssets(modelPath, modelJsonName);

        /*
         * 作为对模型使用半透明的示例。
         * 如果定义了 USE_MODEL_RENDER_TARGET，则在此处使用 RENDER_TARGET
         * 将模型绘制到不同的渲染目标，并将结果作为纹理贴到另一个精灵上。
         */
        LAppView.RenderingTarget useRenderingTarget;
        if (USE_RENDER_TARGET) {
            // 在LAppView拥有的目标上绘制时选择这里
            useRenderingTarget = LAppView.RenderingTarget.VIEW_FRAME_BUFFER;
        } else if (USE_MODEL_RENDER_TARGET) {
            // 在每个LAppModel拥有的目标上绘制时选择这里
            useRenderingTarget = LAppView.RenderingTarget.MODEL_FRAME_BUFFER;
        } else {
            // 默认绘制到主帧缓冲区（通常）
            useRenderingTarget = LAppView.RenderingTarget.NONE;
        }

        if (USE_RENDER_TARGET || USE_MODEL_RENDER_TARGET) {
            // 作为将α添加到单个模型的示例，再创建一个模型并稍微移动其位置。
            models.add(new LAppModel());
            models.get(1).loadAssets(modelPath, modelJsonName);
            models.get(1).getModelMatrix().translateX(0.2f);
        }

        // 切换渲染目标
        LAppDelegate.getInstance().getView().switchRenderingTarget(useRenderingTarget);

        // 在选择不同的渲染目标时的背景清除颜色
        float[] clearColor = {1.0f, 1.0f, 1.0f};
        LAppDelegate.getInstance().getView().setRenderingTargetClearColor(clearColor[0], clearColor[1], clearColor[2]);
    }

    /**
     * 获取当前场景持有的模型
     *
     * @param number 模型列表的索引值
     * @return 返回模型的实例。如果索引值超出范围，则返回null
     */
    public LAppModel getModel(int number) {
        if (number < models.size()) {
            return models.get(number);
        }
        return null;
    }

    /**
     * 获取当前场景的索引
     *
     * @return 当前场景的索引
     */
    public LAppDefine.ModelDir getCurrentModel() {
        return currentModel;
    }

    /**
     * 返回此LAppLive2DManager实例中的模型数量。
     *
     * @return 此LAppLive2DManager实例中的模型数量。如果模型列表为null，则返回0。
     */
    public int getModelNum() {
        return models.size();
    }

    /**
     * 在动作完成时执行的回调函数
     */
    private static class FinishedMotion implements IFinishedMotionCallback {
        @Override
        public void execute(ACubismMotion motion) {
            LAppPal.printLog("动作完成: " + motion);
        }
    }

    public static LAppLive2DManager getInstance() {
        if (s_instance == null) {
            s_instance = new LAppLive2DManager();
        }
        return s_instance;
    }
}
