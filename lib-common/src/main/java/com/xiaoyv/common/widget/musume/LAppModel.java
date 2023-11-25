/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.xiaoyv.common.widget.musume;

import com.live2d.sdk.cubism.framework.CubismDefaultParameterId.ParameterId;
import com.live2d.sdk.cubism.framework.CubismFramework;
import com.live2d.sdk.cubism.framework.CubismModelSettingJson;
import com.live2d.sdk.cubism.framework.ICubismModelSetting;
import com.live2d.sdk.cubism.framework.effect.CubismBreath;
import com.live2d.sdk.cubism.framework.effect.CubismEyeBlink;
import com.live2d.sdk.cubism.framework.id.CubismId;
import com.live2d.sdk.cubism.framework.id.CubismIdManager;
import com.live2d.sdk.cubism.framework.math.CubismMatrix44;
import com.live2d.sdk.cubism.framework.model.CubismMoc;
import com.live2d.sdk.cubism.framework.model.CubismUserModel;
import com.live2d.sdk.cubism.framework.motion.ACubismMotion;
import com.live2d.sdk.cubism.framework.motion.CubismExpressionMotion;
import com.live2d.sdk.cubism.framework.motion.CubismMotion;
import com.live2d.sdk.cubism.framework.motion.IFinishedMotionCallback;
import com.live2d.sdk.cubism.framework.rendering.CubismRenderer;
import com.live2d.sdk.cubism.framework.rendering.android.CubismOffscreenSurfaceAndroid;
import com.live2d.sdk.cubism.framework.rendering.android.CubismRendererAndroid;
import com.live2d.sdk.cubism.framework.utils.CubismDebug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LAppModel extends CubismUserModel {
    public LAppModel() {
        if (LAppDefine.MOC_CONSISTENCY_VALIDATION_ENABLE) {
            mocConsistency = true;
        }

        if (LAppDefine.DEBUG_LOG_ENABLE) {
            debugMode = true;
        }

        CubismIdManager idManager = CubismFramework.getIdManager();

        idParamAngleX = idManager.getId(ParameterId.ANGLE_X.getId());
        idParamAngleY = idManager.getId(ParameterId.ANGLE_Y.getId());
        idParamAngleZ = idManager.getId(ParameterId.ANGLE_Z.getId());
        idParamBodyAngleX = idManager.getId(ParameterId.BODY_ANGLE_X.getId());
        idParamEyeBallX = idManager.getId(ParameterId.EYE_BALL_X.getId());
        idParamEyeBallY = idManager.getId(ParameterId.EYE_BALL_Y.getId());
    }

    public void loadAssets(final String dir, final String fileName) {
        if (LAppDefine.DEBUG_LOG_ENABLE) {
            LAppPal.printLog("load model setting: " + fileName);
        }

        modelHomeDirectory = dir;
        String filePath = modelHomeDirectory + fileName;

        // json読み込み
        byte[] buffer = createBuffer(filePath);

        ICubismModelSetting setting = new CubismModelSettingJson(buffer);

        // Setup model
        setupModel(setting);

        if (model == null) {
            LAppPal.printLog("Failed to loadAssets().");
            return;
        }

        // Setup renderer.
        CubismRenderer renderer = CubismRendererAndroid.create();
        setupRenderer(renderer);

        setupTextures();
    }

    /**
     * Delete the model which LAppModel has.
     */
    public void deleteModel() {
        delete();
    }

    /**
     * モデルの更新処理。モデルのパラメーターから描画状態を決定する
     */
    public void update() {
        final float deltaTimeSeconds = LAppPal.getDeltaTime();
        userTimeSeconds += deltaTimeSeconds;

        dragManager.update(deltaTimeSeconds);
        dragX = dragManager.getX();
        dragY = dragManager.getY();

        // モーションによるパラメーター更新の有無
        boolean isMotionUpdated = false;

//         前回セーブされた状態をロード
        model.loadParameters();

        // モーションの再生がない場合、待機モーションの中からランダムで再生する
        if (motionManager.isFinished()) {
            startRandomMotion(LAppDefine.MotionGroup.IDLE.getId(), LAppDefine.Priority.IDLE.getPriority());
        } else {
            // モーションを更新
            isMotionUpdated = motionManager.updateMotion(model, deltaTimeSeconds);
        }

        // モデルの状態を保存
        model.saveParameters();

        // 不透明度
        opacity = model.getModelOpacity();

        // eye blink
        // メインモーションの更新がないときだけまばたきする
        if (!isMotionUpdated) {
            if (eyeBlink != null) {
                eyeBlink.updateParameters(model, deltaTimeSeconds);
            }
        }

        // expression
        if (expressionManager != null) {
            // 表情でパラメータ更新（相対変化）
            expressionManager.updateMotion(model, deltaTimeSeconds);
        }

        // ドラッグ追従機能
        // ドラッグによる顔の向きの調整
        model.addParameterValue(idParamAngleX, dragX * 30); // -30から30の値を加える
        model.addParameterValue(idParamAngleY, dragY * 30);
        model.addParameterValue(idParamAngleZ, dragX * dragY * (-30));

        // ドラッグによる体の向きの調整
        model.addParameterValue(idParamBodyAngleX, dragX * 10); // -10から10の値を加える

        // ドラッグによる目の向きの調整
        model.addParameterValue(idParamEyeBallX, dragX);  // -1から1の値を加える
        model.addParameterValue(idParamEyeBallY, dragY);

        // Breath Function
        if (breath != null) {
            breath.updateParameters(model, deltaTimeSeconds);
        }

        // Physics Setting
        if (physics != null) {
            physics.evaluate(model, deltaTimeSeconds);
        }

        // Lip Sync Setting
        if (lipSync) {
            // リアルタイムでリップシンクを行う場合、システムから音量を取得して0~1の範囲で値を入力します
            float value = 0.0f;

            for (int i = 0; i < lipSyncIds.size(); i++) {
                CubismId lipSyncId = lipSyncIds.get(i);
                model.addParameterValue(lipSyncId, value, 0.8f);
            }
        }

        // Pose Setting
        if (pose != null) {
            pose.updateParameters(model, deltaTimeSeconds);
        }

        model.update();
    }

    /**
     * 开始播放指定参数的动作。
     * 如果未提供回调函数，则将其视为null并调用此方法。
     *
     * @param group    动作组名称
     * @param number   组内编号
     * @param priority 优先级
     * @return 返回开始的动作的标识号。用于判断个别动作是否已经结束，可用作isFinished()方法的参数。如果无法开始，则返回"-1"。
     */
    public int startMotion(final String group, int number, int priority) {
        return startMotion(group, number, priority, null);
    }

    /**
     * 开始播放指定参数的动作。
     *
     * @param group                   动作组名称
     * @param number                  组内编号
     * @param priority                优先级
     * @param onFinishedMotionHandler 动作播放结束时调用的回调函数。如果为null，则不调用。
     * @return 返回开始的动作的标识号。用于判断个别动作是否已经结束，可用作isFinished()方法的参数。如果无法开始，则返回"-1"。
     */
    public int startMotion(final String group,
                           int number,
                           int priority,
                           IFinishedMotionCallback onFinishedMotionHandler
    ) {
        if (priority == LAppDefine.Priority.FORCE.getPriority()) {
            motionManager.setReservationPriority(priority);
        } else if (!motionManager.reserveMotion(priority)) {
            if (debugMode) {
                LAppPal.printLog("Cannot start motion.");
            }
            return -1;
        }

        // ex) idle_0
        String name = group + "_" + number;

        CubismMotion motion = (CubismMotion) motions.get(name);

        if (motion == null) {
            String fileName = modelSetting.getMotionFileName(group, number);
            if (!fileName.isEmpty()) {
                String path = modelHomeDirectory + fileName;

                byte[] buffer;
                buffer = createBuffer(path);

                motion = loadMotion(buffer, onFinishedMotionHandler);
                if (motion != null) {
                    final float fadeInTime = modelSetting.getMotionFadeInTimeValue(group, number);

                    if (fadeInTime != -1.0f) {
                        motion.setFadeInTime(fadeInTime);
                    }

                    final float fadeOutTime = modelSetting.getMotionFadeOutTimeValue(group, number);
                    if (fadeOutTime != -1.0f) {
                        motion.setFadeOutTime(fadeOutTime);
                    }

                    motion.setEffectIds(eyeBlinkIds, lipSyncIds);
                }
            }
        } else {
            motion.setFinishedMotionHandler(onFinishedMotionHandler);
        }

        // 加载音频文件
        String voice = modelSetting.getMotionSoundFileName(group, number);
        if (debugMode) {
            LAppPal.printLog("加载音频文件: " + group + "_" + number + ", voice: " + voice);
        }

        if (!voice.isEmpty()) {
            String path = modelHomeDirectory + voice;

            // 在单独的线程中播放音频
            LAppWavFileHandler voicePlayer = new LAppWavFileHandler(path);
            voicePlayer.start();
        }

        if (debugMode) {
            LAppPal.printLog("播放: " + group + "_" + number);
        }
        return motionManager.startMotionPriority(motion, priority);
    }

    /**
     * 开始播放随机选择的动作。
     * 如果未提供回调函数，则将其视为null并调用此方法。
     *
     * @param group    动作组名称
     * @param priority 优先级
     * @return 返回开始的动作的标识号。用于判断个别动作是否已经结束，可用作isFinished()方法的参数。如果无法开始，则返回"-1"。
     */
    public int startRandomMotion(final String group, int priority) {
        return startRandomMotion(group, priority, null);
    }

    /**
     * 开始播放随机选择的动作。
     *
     * @param group                   动作组名称
     * @param priority                优先级
     * @param onFinishedMotionHandler 动作播放结束时调用的回调函数。如果为null，则不调用。
     * @return 返回开始的动作的标识号。用于判断个别动作是否已经结束，可用作isFinished()方法的参数。如果无法开始，则返回"-1"。
     */
    public int startRandomMotion(final String group, int priority, IFinishedMotionCallback onFinishedMotionHandler) {
        if (modelSetting.getMotionCount(group) == 0) {
            return -1;
        }

        Random random = new Random();
        int number = random.nextInt(Integer.MAX_VALUE) % modelSetting.getMotionCount(group);

        return startMotion(group, number, priority, onFinishedMotionHandler);
    }

    /**
     * 绘制模型。
     *
     * @param matrix 模型的变换矩阵
     */
    public void draw(CubismMatrix44 matrix) {
        if (model == null) {
            return;
        }

        matrix.multiplyByMatrix(modelMatrix);

        this.<CubismRendererAndroid>getRenderer().setMvpMatrix(matrix);
        this.<CubismRendererAndroid>getRenderer().drawModel();
    }

    /**
     * 碰撞检测测试。
     * 从指定ID的顶点列表计算矩形，并判断坐标是否在矩形范围内。
     *
     * @param hitAreaName 要测试碰撞的区域的ID
     * @param x           进行判定的x坐标
     * @param y           进行判定的y坐标
     * @return 如果碰撞则返回true
     */
    public boolean hitTest(final String hitAreaName, float x, float y) {
        // 在透明状态下无碰撞检测
        if (opacity < 1) {
            return false;
        }

        final int count = modelSetting.getHitAreasCount();
        for (int i = 0; i < count; i++) {
            if (modelSetting.getHitAreaName(i).equals(hitAreaName)) {
                final CubismId drawID = modelSetting.getHitAreaId(i);

                return isHit(drawID, x, y);
            }
        }
        // 如果不存在则返回false
        return false;
    }

    /**
     * 设置指定表情动作。
     *
     * @param expressionID 表情动作的ID
     */
    public void setExpression(final String expressionID) {
        ACubismMotion motion = expressions.get(expressionID);

        if (debugMode) {
            LAppPal.printLog("expression: " + expressionID);
        }

        if (motion != null) {
            expressionManager.startMotionPriority(motion, LAppDefine.Priority.FORCE.getPriority());
        } else {
            if (debugMode) {
                LAppPal.printLog("expression " + expressionID + "is null");
            }
        }
    }

    /**
     * 设置随机选择的表情动作。
     */
    public void setRandomExpression() {
        if (expressions.isEmpty()) {
            return;
        }

        Random random = new Random();
        int number = random.nextInt(Integer.MAX_VALUE) % expressions.size();

        int i = 0;
        for (String key : expressions.keySet()) {
            if (i == number) {
                setExpression(key);
                return;
            }
            i++;
        }
    }

    /**
     * 获取渲染缓冲区。
     *
     * @return CubismOffscreenSurfaceAndroid 对象
     */
    public CubismOffscreenSurfaceAndroid getRenderingBuffer() {
        return renderingBuffer;
    }

    /**
     * 检查.moc3文件的一致性。
     *
     * @param mocFileName MOC3文件名
     * @return MOC3是否一致。如果一致则返回true。
     */
    public boolean hasMocConsistencyFromFile(String mocFileName) {
        assert mocFileName != null && !mocFileName.isEmpty();

        String path = mocFileName;
        path = modelHomeDirectory + path;

        byte[] buffer = createBuffer(path);
        boolean consistency = CubismMoc.hasMocConsistency(buffer);

        if (!consistency) {
            CubismDebug.cubismLogInfo("不一致的 MOC3。");
        } else {
            CubismDebug.cubismLogInfo("一致的 MOC3。");
        }

        return consistency;
    }


    private static byte[] createBuffer(final String path) {
        if (LAppDefine.DEBUG_LOG_ENABLE) {
            LAppPal.printLog("create buffer: " + path);
        }
        return LAppPal.loadFileAsBytes(path);
    }

    // model3.jsonからモデルを生成する
    private void setupModel(ICubismModelSetting setting) {
        modelSetting = setting;

        isUpdated = true;
        isInitialized = false;

        // Load Cubism Model
        {
            String fileName = modelSetting.getModelFileName();
            if (!fileName.equals("")) {
                String path = modelHomeDirectory + fileName;

                if (LAppDefine.DEBUG_LOG_ENABLE) {
                    LAppPal.printLog("create model: " + modelSetting.getModelFileName());
                }

                byte[] buffer = createBuffer(path);
                loadModel(buffer, mocConsistency);
            }
        }

        // load expression files(.exp3.json)
        {
            if (modelSetting.getExpressionCount() > 0) {
                final int count = modelSetting.getExpressionCount();

                for (int i = 0; i < count; i++) {
                    String name = modelSetting.getExpressionName(i);
                    String path = modelSetting.getExpressionFileName(i);
                    path = modelHomeDirectory + path;

                    byte[] buffer = createBuffer(path);
                    CubismExpressionMotion motion = loadExpression(buffer);

                    expressions.put(name, motion);
                }
            }
        }

        // Physics
        {
            String path = modelSetting.getPhysicsFileName();
            if (!path.equals("")) {
                String modelPath = modelHomeDirectory + path;
                byte[] buffer = createBuffer(modelPath);

                loadPhysics(buffer);
            }
        }

        // Pose
        {
            String path = modelSetting.getPoseFileName();
            if (!path.equals("")) {
                String modelPath = modelHomeDirectory + path;
                byte[] buffer = createBuffer(modelPath);
                loadPose(buffer);
            }
        }

        // Load eye blink data
        if (modelSetting.getEyeBlinkParameterCount() > 0) {
            eyeBlink = CubismEyeBlink.create(modelSetting);
        }

        // Load Breath Data
        breath = CubismBreath.create();
        List<CubismBreath.BreathParameterData> breathParameters = new ArrayList<CubismBreath.BreathParameterData>();

        breathParameters.add(new CubismBreath.BreathParameterData(idParamAngleX, 0.0f, 15.0f, 6.5345f, 0.5f));
        breathParameters.add(new CubismBreath.BreathParameterData(idParamAngleY, 0.0f, 8.0f, 3.5345f, 0.5f));
        breathParameters.add(new CubismBreath.BreathParameterData(idParamAngleZ, 0.0f, 10.0f, 5.5345f, 0.5f));
        breathParameters.add(new CubismBreath.BreathParameterData(idParamBodyAngleX, 0.0f, 4.0f, 15.5345f, 0.5f));
        breathParameters.add(new CubismBreath.BreathParameterData(CubismFramework.getIdManager().getId(ParameterId.BREATH.getId()), 0.5f, 0.5f, 3.2345f, 0.5f));

        breath.setParameters(breathParameters);

        // Load UserData
        {
            String path = modelSetting.getUserDataFile();
            if (!path.equals("")) {
                String modelPath = modelHomeDirectory + path;
                byte[] buffer = createBuffer(modelPath);
                loadUserData(buffer);
            }
        }


        // EyeBlinkIds
        int eyeBlinkIdCount = modelSetting.getEyeBlinkParameterCount();
        for (int i = 0; i < eyeBlinkIdCount; i++) {
            eyeBlinkIds.add(modelSetting.getEyeBlinkParameterId(i));
        }

        // LipSyncIds
        int lipSyncIdCount = modelSetting.getLipSyncParameterCount();
        for (int i = 0; i < lipSyncIdCount; i++) {
            lipSyncIds.add(modelSetting.getLipSyncParameterId(i));
        }

        if (modelSetting == null || modelMatrix == null) {
            LAppPal.printLog("Failed to setupModel().");
            return;
        }

        // Set layout
        Map<String, Float> layout = new HashMap<String, Float>();

        // レイアウト情報が存在すればその情報からモデル行列をセットアップする
        if (modelSetting.getLayoutMap(layout)) {
            modelMatrix.setupFromLayout(layout);
        }

        model.saveParameters();

        // Load motions
        for (int i = 0; i < modelSetting.getMotionGroupCount(); i++) {
            String group = modelSetting.getMotionGroupName(i);
            preLoadMotionGroup(group);
        }

        motionManager.stopAllMotions();

        isUpdated = false;
        isInitialized = true;
    }

    /**
     * モーションデータをグループ名から一括でロードする。
     * モーションデータの名前はModelSettingから取得する。
     *
     * @param group モーションデータのグループ名
     **/
    private void preLoadMotionGroup(final String group) {
        final int count = modelSetting.getMotionCount(group);

        for (int i = 0; i < count; i++) {
            // ex) idle_0
            String name = group + "_" + i;

            String path = modelSetting.getMotionFileName(group, i);
            if (!path.equals("")) {
                String modelPath = modelHomeDirectory + path;

                if (debugMode) {
                    LAppPal.printLog("load motion: " + path + "==>[" + group + "_" + i + "]");
                }

                byte[] buffer;
                buffer = createBuffer(modelPath);

                // If a motion cannot be loaded, a process is skipped.
                CubismMotion tmp = loadMotion(buffer);
                if (tmp == null) {
                    continue;
                }

                final float fadeInTime = modelSetting.getMotionFadeInTimeValue(group, i);

                if (fadeInTime != -1.0f) {
                    tmp.setFadeInTime(fadeInTime);
                }

                final float fadeOutTime = modelSetting.getMotionFadeOutTimeValue(group, i);

                if (fadeOutTime != -1.0f) {
                    tmp.setFadeOutTime(fadeOutTime);
                }

                tmp.setEffectIds(eyeBlinkIds, lipSyncIds);
                motions.put(name, tmp);
            }
        }
    }

    /**
     * OpenGLのテクスチャユニットにテクスチャをロードする
     */
    private void setupTextures() {
        for (int modelTextureNumber = 0; modelTextureNumber < modelSetting.getTextureCount(); modelTextureNumber++) {
            // テクスチャ名が空文字だった場合はロード・バインド処理をスキップ
            if (modelSetting.getTextureFileName(modelTextureNumber).equals("")) {
                continue;
            }

            // OpenGL ESのテクスチャユニットにテクスチャをロードする
            String texturePath = modelSetting.getTextureFileName(modelTextureNumber);
            texturePath = modelHomeDirectory + texturePath;

            LAppTextureManager.TextureInfo texture =
                    LAppDelegate.getInstance()
                            .getTextureManager()
                            .createTextureFromPngFile(texturePath);
            final int glTextureNumber = texture.id;

            this.<CubismRendererAndroid>getRenderer().bindTexture(modelTextureNumber, glTextureNumber);

            if (LAppDefine.PREMULTIPLIED_ALPHA_ENABLE) {
                this.<CubismRendererAndroid>getRenderer().isPremultipliedAlpha(true);
            } else {
                this.<CubismRendererAndroid>getRenderer().isPremultipliedAlpha(false);
            }
        }
    }

    private ICubismModelSetting modelSetting;
    /**
     * モデルのホームディレクトリ
     */
    private String modelHomeDirectory;
    /**
     * デルタ時間の積算値[秒]
     */
    private float userTimeSeconds;

    /**
     * モデルに設定されたまばたき機能用パラメーターID
     */
    private final List<CubismId> eyeBlinkIds = new ArrayList<CubismId>();
    /**
     * モデルに設定されたリップシンク機能用パラメーターID
     */
    private final List<CubismId> lipSyncIds = new ArrayList<CubismId>();
    /**
     * 読み込まれているモーションのマップ
     */
    private final Map<String, ACubismMotion> motions = new HashMap<String, ACubismMotion>();
    /**
     * 読み込まれている表情のマップ
     */
    private final Map<String, ACubismMotion> expressions = new HashMap<String, ACubismMotion>();

    /**
     * パラメーターID: ParamAngleX
     */
    private final CubismId idParamAngleX;
    /**
     * パラメーターID: ParamAngleY
     */
    private final CubismId idParamAngleY;
    /**
     * パラメーターID: ParamAngleZ
     */
    private final CubismId idParamAngleZ;
    /**
     * パラメーターID: ParamBodyAngleX
     */
    private final CubismId idParamBodyAngleX;
    /**
     * パラメーターID: ParamEyeBallX
     */
    private final CubismId idParamEyeBallX;
    /**
     * パラメーターID: ParamEyeBallY
     */
    private final CubismId idParamEyeBallY;

    /**
     * フレームバッファ以外の描画先
     */
    private final CubismOffscreenSurfaceAndroid renderingBuffer = new CubismOffscreenSurfaceAndroid();
}
