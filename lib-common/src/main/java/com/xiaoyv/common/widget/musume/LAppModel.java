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

    private ICubismModelSetting modelSetting;

    /**
     * 模型的主目录
     */
    private String modelHomeDirectory;

    /**
     * Delta时间的累积值[秒]
     */
    private float userTimeSeconds;

    /**
     * 模型上设置的眨眼功能用参数ID
     */
    private final List<CubismId> eyeBlinkIds = new ArrayList<>();

    /**
     * 模型上设置的LipSync功能用参数ID
     */
    private final List<CubismId> lipSyncIds = new ArrayList<>();

    /**
     * 已加载的动作映射
     */
    private final Map<String, ACubismMotion> motions = new HashMap<>();

    /**
     * 已加载的表情映射
     */
    private final Map<String, ACubismMotion> expressions = new HashMap<>();

    /**
     * 参数ID: ParamAngleX
     */
    private final CubismId idParamAngleX;

    /**
     * 参数ID: ParamAngleY
     */
    private final CubismId idParamAngleY;

    /**
     * 参数ID: ParamAngleZ
     */
    private final CubismId idParamAngleZ;

    /**
     * 参数ID: ParamBodyAngleX
     */
    private final CubismId idParamBodyAngleX;

    /**
     * 参数ID: ParamEyeBallX
     */
    private final CubismId idParamEyeBallX;

    /**
     * 参数ID: ParamEyeBallY
     */
    private final CubismId idParamEyeBallY;

    /**
     * 非帧缓冲区的渲染目标
     */
    private final CubismOffscreenSurfaceAndroid renderingBuffer = new CubismOffscreenSurfaceAndroid();


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
            LAppPal.printLog("加载模型设置：" + fileName);
        }

        modelHomeDirectory = dir;
        String filePath = modelHomeDirectory + fileName;

        // 读取JSON文件
        byte[] buffer = createBuffer(filePath);

        ICubismModelSetting setting = new CubismModelSettingJson(buffer);

        // 配置模型
        setupModel(setting);

        if (model == null) {
            LAppPal.printLog("加载资源失败。");
            return;
        }

        // 配置渲染器
        CubismRenderer renderer = CubismRendererAndroid.create();
        setupRenderer(renderer);

        setupTextures();
    }

    /**
     * 删除LAppModel所拥有的模型。
     */
    public void deleteModel() {
        delete();
    }

    /**
     * 模型更新处理。根据模型参数确定绘制状态。
     */
    public void update() {
        final float deltaTimeSeconds = LAppPal.getDeltaTime();
        userTimeSeconds += deltaTimeSeconds;

        dragManager.update(deltaTimeSeconds);
        dragX = dragManager.getX();
        dragY = dragManager.getY();

        // 是否通过动作更新参数
        boolean isMotionUpdated = false;

        // 加载上次保存的状态
        model.loadParameters();

        // 如果没有正在播放的动作，从待机动作中随机选择播放
        if (motionManager.isFinished()) {
            startRandomMotion(LAppDefine.MotionGroup.IDLE.getId(), LAppDefine.Priority.IDLE.getPriority());
        } else {
            // 更新动作
            isMotionUpdated = motionManager.updateMotion(model, deltaTimeSeconds);
        }

        // 保存模型状态
        model.saveParameters();

        // 不透明度
        opacity = model.getModelOpacity();

        // 眨眼
        // 仅当主要动作没有更新时才进行眨眼
        if (!isMotionUpdated) {
            if (eyeBlink != null) {
                eyeBlink.updateParameters(model, deltaTimeSeconds);
            }
        }

        // 表情
        if (expressionManager != null) {
            // 通过表情更新参数（相对变化）
            expressionManager.updateMotion(model, deltaTimeSeconds);
        }

        // 拖拽追踪功能
        // 通过拖拽调整脸部方向
        model.addParameterValue(idParamAngleX, dragX * 30); // 在-30到30的范围内添加值
        model.addParameterValue(idParamAngleY, dragY * 30);
        model.addParameterValue(idParamAngleZ, dragX * dragY * (-30));

        // 通过拖拽调整身体方向
        model.addParameterValue(idParamBodyAngleX, dragX * 10); // 在-10到10的范围内添加值

        // 通过拖拽调整眼睛方向
        model.addParameterValue(idParamEyeBallX, dragX);  // 在-1到1的范围内添加值
        model.addParameterValue(idParamEyeBallY, dragY);

        // 呼吸功能
        if (breath != null) {
            breath.updateParameters(model, deltaTimeSeconds);
        }

        // 物理设置
        if (physics != null) {
            physics.evaluate(model, deltaTimeSeconds);
        }

        // 唇同步设置
        if (lipSync) {
            // 如果实时进行唇同步，从系统获取音量并在0~1范围内输入值
            float value = 0.0f;

            for (int i = 0; i < lipSyncIds.size(); i++) {
                CubismId lipSyncId = lipSyncIds.get(i);
                model.addParameterValue(lipSyncId, value, 0.8f);
            }
        }

        // 姿势设置
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


        if (!voice.isEmpty()) {
            String path = modelHomeDirectory + voice;

            // 在单独的线程中播放音频
            LAppWavFileHandler voicePlayer = new LAppWavFileHandler(path);
            voicePlayer.start();

            if (debugMode) {
                LAppPal.printLog("播放音频文件: " + group + "_" + number + ", voice: " + voice);
            }
        } else {
            if (debugMode) {
                LAppPal.printLog("音频文件未配置: " + group + "_" + number);
            }
        }

        if (debugMode) {
            LAppPal.printLog("播放: " + group + "_" + number);
        }
        return motionManager.startMotionPriority(motion, priority);
    }

    public void startRandomSound(LAppDefine.HitAreaName hitAreaName) {
        int[] group = new int[35];
        switch (hitAreaName) {
            case HAIR: {
                group = new int[]{29, 30, 31, 39, 53};
                break;
            }
            case FACE: {
                group = new int[]{40, 43, 44, 45, 46, 48, 49, 50, 51, 52, 59};
                break;
            }
            case BODY: {
                group = new int[]{40, 41};
                break;
            }
        }

        Random random = new Random();
        int number = random.nextInt(Integer.MAX_VALUE) % group.length;
        String path = modelHomeDirectory + "sounds/wave" + group[number] + ".wav";

        LAppWavFileHandler voicePlayer = new LAppWavFileHandler(path);

        voicePlayer.start();
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
            LAppPal.printLog("创建缓冲区: " + path);
        }
        return LAppPal.loadFileAsBytes(path);
    }

    // 从model3.json文件中生成模型
    private void setupModel(ICubismModelSetting setting) {
        modelSetting = setting;

        isUpdated = true;
        isInitialized = false;

        // 加载Cubism模型
        {
            String fileName = modelSetting.getModelFileName();
            if (!fileName.isEmpty()) {
                String path = modelHomeDirectory + fileName;

                if (LAppDefine.DEBUG_LOG_ENABLE) {
                    LAppPal.printLog("创建模型: " + modelSetting.getModelFileName());
                }

                byte[] buffer = createBuffer(path);
                loadModel(buffer, mocConsistency);
            }
        }

        // 加载表情文件(.exp3.json)
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

        // 物理效果
        {
            String path = modelSetting.getPhysicsFileName();
            if (!path.equals("")) {
                String modelPath = modelHomeDirectory + path;
                byte[] buffer = createBuffer(modelPath);

                loadPhysics(buffer);
            }
        }

        // 姿势
        {
            String path = modelSetting.getPoseFileName();
            if (!path.equals("")) {
                String modelPath = modelHomeDirectory + path;
                byte[] buffer = createBuffer(modelPath);
                loadPose(buffer);
            }
        }

        // 加载眨眼数据
        if (modelSetting.getEyeBlinkParameterCount() > 0) {
            eyeBlink = CubismEyeBlink.create(modelSetting);
        }

        // 加载呼吸数据
        breath = CubismBreath.create();
        List<CubismBreath.BreathParameterData> breathParameters = new ArrayList<CubismBreath.BreathParameterData>();

        breathParameters.add(new CubismBreath.BreathParameterData(idParamAngleX, 0.0f, 15.0f, 6.5345f, 0.5f));
        breathParameters.add(new CubismBreath.BreathParameterData(idParamAngleY, 0.0f, 8.0f, 3.5345f, 0.5f));
        breathParameters.add(new CubismBreath.BreathParameterData(idParamAngleZ, 0.0f, 10.0f, 5.5345f, 0.5f));
        breathParameters.add(new CubismBreath.BreathParameterData(idParamBodyAngleX, 0.0f, 4.0f, 15.5345f, 0.5f));
        breathParameters.add(new CubismBreath.BreathParameterData(CubismFramework.getIdManager().getId(ParameterId.BREATH.getId()), 0.5f, 0.5f, 3.2345f, 0.5f));

        breath.setParameters(breathParameters);

        // 加载用户数据
        {
            String path = modelSetting.getUserDataFile();
            if (!path.isEmpty()) {
                String modelPath = modelHomeDirectory + path;
                byte[] buffer = createBuffer(modelPath);
                loadUserData(buffer);
            }
        }

        // 眨眼参数
        int eyeBlinkIdCount = modelSetting.getEyeBlinkParameterCount();
        for (int i = 0; i < eyeBlinkIdCount; i++) {
            eyeBlinkIds.add(modelSetting.getEyeBlinkParameterId(i));
        }

        // LipSync参数
        int lipSyncIdCount = modelSetting.getLipSyncParameterCount();
        for (int i = 0; i < lipSyncIdCount; i++) {
            lipSyncIds.add(modelSetting.getLipSyncParameterId(i));
        }

        if (modelSetting == null || modelMatrix == null) {
            LAppPal.printLog("setupModel() 失败。");
            return;
        }

        // 设置布局
        Map<String, Float> layout = new HashMap<String, Float>();

        // 如果存在布局信息，则使用该信息设置模型矩阵
        if (modelSetting.getLayoutMap(layout)) {
            modelMatrix.setupFromLayout(layout);
        }

        model.saveParameters();

        // 加载动作
        for (int i = 0; i < modelSetting.getMotionGroupCount(); i++) {
            String group = modelSetting.getMotionGroupName(i);
            preLoadMotionGroup(group);
        }

        motionManager.stopAllMotions();

        isUpdated = false;
        isInitialized = true;
    }

    /**
     * 根据组名批量加载动作数据。
     * 动作数据的名称从ModelSetting获取。
     *
     * @param group 动作数据的组名
     **/
    private void preLoadMotionGroup(final String group) {
        final int count = modelSetting.getMotionCount(group);

        for (int i = 0; i < count; i++) {
            // 例如：idle_0
            String name = group + "_" + i;

            String path = modelSetting.getMotionFileName(group, i);
            if (!path.equals("")) {
                String modelPath = modelHomeDirectory + path;

                if (debugMode) {
                    LAppPal.printLog("加载动作: " + path + "==>[" + group + "_" + i + "]");
                }

                byte[] buffer;
                buffer = createBuffer(modelPath);

                // 如果无法加载动作，则跳过该过程。
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
     * 将纹理加载到OpenGL的纹理单元中
     */
    private void setupTextures() {
        for (int modelTextureNumber = 0; modelTextureNumber < modelSetting.getTextureCount(); modelTextureNumber++) {
            // 如果纹理名为空字符串，则跳过加载和绑定过程
            if (modelSetting.getTextureFileName(modelTextureNumber).equals("")) {
                continue;
            }

            // 将纹理加载到OpenGL ES的纹理单元中
            String texturePath = modelSetting.getTextureFileName(modelTextureNumber);
            texturePath = modelHomeDirectory + texturePath;

            LAppTextureManager.TextureInfo texture =
                    LAppDelegate.getInstance()
                            .getTextureManager()
                            .createTextureFromPngFile(texturePath);
            final int glTextureNumber = texture.id;

            this.<CubismRendererAndroid>getRenderer().bindTexture(modelTextureNumber, glTextureNumber);

            this.<CubismRendererAndroid>getRenderer().isPremultipliedAlpha(LAppDefine.PREMULTIPLIED_ALPHA_ENABLE);
        }
    }

}
