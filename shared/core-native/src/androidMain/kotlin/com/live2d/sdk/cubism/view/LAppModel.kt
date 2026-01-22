/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */
@file:Suppress("KotlinConstantConditions")

package com.live2d.sdk.cubism.view

import com.live2d.sdk.cubism.framework.CubismDefaultParameterId.ParameterId
import com.live2d.sdk.cubism.framework.CubismFramework
import com.live2d.sdk.cubism.framework.CubismModelSettingJson
import com.live2d.sdk.cubism.framework.ICubismModelSetting
import com.live2d.sdk.cubism.framework.effect.CubismBreath
import com.live2d.sdk.cubism.framework.effect.CubismBreath.BreathParameterData
import com.live2d.sdk.cubism.framework.effect.CubismEyeBlink
import com.live2d.sdk.cubism.framework.id.CubismId
import com.live2d.sdk.cubism.framework.math.CubismMatrix44
import com.live2d.sdk.cubism.framework.model.CubismMoc
import com.live2d.sdk.cubism.framework.model.CubismUserModel
import com.live2d.sdk.cubism.framework.motion.ACubismMotion
import com.live2d.sdk.cubism.framework.motion.CubismMotion
import com.live2d.sdk.cubism.framework.motion.IBeganMotionCallback
import com.live2d.sdk.cubism.framework.motion.IFinishedMotionCallback
import com.live2d.sdk.cubism.framework.rendering.android.CubismRendererAndroid
import com.live2d.sdk.cubism.framework.utils.CubismDebug
import com.live2d.sdk.cubism.view.LAppPal.deltaTime
import com.live2d.sdk.cubism.view.LAppPal.loadFileAsBytes
import com.live2d.sdk.cubism.view.LAppPal.printLog
import java.util.Random

class LAppModel(private val textureManager: LAppTextureManager) : CubismUserModel() {

    private var _modelSetting: ICubismModelSetting? = null
    private val modelSetting get() = requireNotNull(_modelSetting)

    /**
     * モデルのホームディレクトリ
     */
    private var modelHomeDirectory: String? = null

    /**
     * デルタ時間の積算値 秒
     */
    private var userTimeSeconds = 0f

    /**
     * モデルに設定されたまばたき機能用パラメーターID
     */
    private val eyeBlinkIds: MutableList<CubismId> = arrayListOf()

    /**
     * モデルに設定されたリップシンク機能用パラメーターID
     */
    private val lipSyncIds: MutableList<CubismId> = arrayListOf()

    /**
     * 読み込まれているモーションのマップ
     */
    private val motions: MutableMap<String, ACubismMotion?> = hashMapOf()

    /**
     * 読み込まれている表情のマップ
     */
    private val expressions: MutableMap<String, ACubismMotion?> = hashMapOf()

    /**
     * パラメーターID: ParamAngleX
     */
    private val idParamAngleX: CubismId

    /**
     * パラメーターID: ParamAngleY
     */
    private val idParamAngleY: CubismId

    /**
     * パラメーターID: ParamAngleZ
     */
    private val idParamAngleZ: CubismId

    /**
     * パラメーターID: ParamBodyAngleX
     */
    private val idParamBodyAngleX: CubismId

    /**
     * パラメーターID: ParamEyeBallX
     */
    private val idParamEyeBallX: CubismId

    /**
     * パラメーターID: ParamEyeBallY
     */
    private val idParamEyeBallY: CubismId

    init {
        if (LAppDefine.MOC_CONSISTENCY_VALIDATION_ENABLE) {
            mocConsistency = true
        }

        if (LAppDefine.MOTION_CONSISTENCY_VALIDATION_ENABLE) {
            motionConsistency = true
        }

        if (LAppDefine.DEBUG_LOG_ENABLE) {
            debugMode = true
        }

        val idManager = CubismFramework.getIdManager()

        idParamAngleX = idManager.getId(ParameterId.ANGLE_X.id)
        idParamAngleY = idManager.getId(ParameterId.ANGLE_Y.id)
        idParamAngleZ = idManager.getId(ParameterId.ANGLE_Z.id)
        idParamBodyAngleX = idManager.getId(ParameterId.BODY_ANGLE_X.id)
        idParamEyeBallX = idManager.getId(ParameterId.EYE_BALL_X.id)
        idParamEyeBallY = idManager.getId(ParameterId.EYE_BALL_Y.id)
    }


    fun loadAssets(dir: String, fileName: String) {
        if (LAppDefine.DEBUG_LOG_ENABLE) {
            printLog("load model setting: $fileName")
        }

        modelHomeDirectory = dir.trimEnd('/') + '/'
        val filePath = modelHomeDirectory + fileName

        // json読み込み
        val buffer = createBuffer(filePath)

        val setting = CubismModelSettingJson(buffer)

        // Setup model
        setupModel(setting)

        if (model == null) {
            printLog("Failed to loadAssets().")
            return
        }

        // Setup renderer.
        val renderer = CubismRendererAndroid.create()
        setupRenderer(renderer)
        setupTextures()
    }

    /**
     * Delete the model which LAppModel has.
     */
    fun deleteModel() {
        delete()
    }

    /**
     * モデルの更新処理。モデルのパラメーターから描画状態を決定する
     */
    fun update() {
        val deltaTimeSeconds = deltaTime
        userTimeSeconds += deltaTimeSeconds

        dragManager.update(deltaTimeSeconds)
        dragX = dragManager.x
        dragY = dragManager.y

        // モーションによるパラメーター更新の有無
        var isMotionUpdated = false

        //         前回セーブされた状態をロード
        model.loadParameters()

        // モーションの再生がない場合、待機モーションの中からランダムで再生する
        if (motionManager.isFinished()) {
            startRandomMotion(LAppDefine.MotionGroup.IDLE.id, LAppDefine.Priority.IDLE.priority)
        } else {
            // モーションを更新
            isMotionUpdated = motionManager.updateMotion(model, deltaTimeSeconds)
        }

        // モデルの状態を保存
        model.saveParameters()

        // 不透明度
        opacity = model.modelOpacity

        // eye blink
        // メインモーションの更新がないときだけまばたきする
        if (!isMotionUpdated) {
            if (eyeBlink != null) {
                eyeBlink.updateParameters(model, deltaTimeSeconds)
            }
        }

        // expression
        if (expressionManager != null) {
            // 表情でパラメータ更新（相対変化）
            expressionManager.updateMotion(model, deltaTimeSeconds)
        }

        // ドラッグ追従機能
        // ドラッグによる顔の向きの調整
        model.addParameterValue(idParamAngleX, dragX * 30) // -30から30の値を加える
        model.addParameterValue(idParamAngleY, dragY * 30)
        model.addParameterValue(idParamAngleZ, dragX * dragY * (-30))

        // ドラッグによる体の向きの調整
        model.addParameterValue(idParamBodyAngleX, dragX * 10) // -10から10の値を加える

        // ドラッグによる目の向きの調整
        model.addParameterValue(idParamEyeBallX, dragX) // -1から1の値を加える
        model.addParameterValue(idParamEyeBallY, dragY)

        // Breath Function
        if (breath != null) {
            breath.updateParameters(model, deltaTimeSeconds)
        }

        // Physics Setting
        if (physics != null) {
            physics.evaluate(model, deltaTimeSeconds)
        }

        // Lip Sync Setting
        if (lipSync) {
            // リアルタイムでリップシンクを行う場合、システムから音量を取得して0~1の範囲で値を入力します
            val value = 0.0f

            for (i in lipSyncIds.indices) {
                val lipSyncId = lipSyncIds.get(i)
                model.addParameterValue(lipSyncId, value, 0.8f)
            }
        }

        // Pose Setting
        if (pose != null) {
            pose.updateParameters(model, deltaTimeSeconds)
        }

        model.update()
    }

    /**
     * 引数で指定したモーションの再生を開始する。
     * コールバック関数が渡されなかった場合にそれをnullとして同メソッドを呼び出す。
     *
     * @param group    モーショングループ名
     * @param number   グループ内の番号
     * @param priority 優先度
     * @return 開始したモーションの識別番号を返す。個別のモーションが終了したか否かを判別するisFinished()の引数で使用する。開始できない時は「-1」
     */
    @JvmOverloads
    fun startMotion(
        group: String?,
        number: Int,
        priority: Int,
        onFinishedMotionHandler: IFinishedMotionCallback? = null,
        onBeganMotionHandler: IBeganMotionCallback? = null,
    ): Int {
        if (priority == LAppDefine.Priority.FORCE.priority) {
            motionManager.reservationPriority = priority
        } else if (!motionManager.reserveMotion(priority)) {
            if (debugMode) {
                printLog("Cannot start motion.")
            }
            return -1
        }

        // ex) idle_0
        val name = group + "_" + number

        var motion = motions.get(name) as CubismMotion?

        if (motion == null) {
            val fileName = modelSetting.getMotionFileName(group, number)
            if (!fileName.isEmpty()) {
                val path = modelHomeDirectory + fileName
                val buffer = createBuffer(path)
                motion = loadMotion(buffer, onFinishedMotionHandler, onBeganMotionHandler, motionConsistency)
                if (motion != null) {
                    val fadeInTime = modelSetting.getMotionFadeInTimeValue(group, number)

                    if (fadeInTime != -1.0f) {
                        motion.fadeInTime = fadeInTime
                    }

                    val fadeOutTime = modelSetting.getMotionFadeOutTimeValue(group, number)
                    if (fadeOutTime != -1.0f) {
                        motion.fadeOutTime = fadeOutTime
                    }

                    motion.setEffectIds(eyeBlinkIds, lipSyncIds)
                } else {
                    CubismDebug.cubismLogError("Can't start motion %s", path)

                    // ロードできなかったモーションのReservePriorityをリセットする。
                    motionManager.reservationPriority = LAppDefine.Priority.NONE.priority
                    return -1
                }
            }
        } else {
            motion.setBeganMotionHandler(onBeganMotionHandler)
            motion.setFinishedMotionHandler(onFinishedMotionHandler)
        }

        // load sound files
        val voice = modelSetting.getMotionSoundFileName(group, number)
        if (!voice.isEmpty()) {
            modelHomeDirectory + voice

            // 別スレッドで音声再生
//            LAppWavFileHandler voicePlayer = new LAppWavFileHandler(path);
//            voicePlayer.start();
        }

        if (debugMode) {
            printLog("start motion: " + group + "_" + number)
        }
        return motionManager.startMotionPriority(motion, priority)
    }

    /**
     * ランダムに選ばれたモーションの再生を開始する。
     * コールバック関数が渡されなかった場合にそれをnullとして同メソッドを呼び出す。
     *
     * @param group    モーショングループ名
     * @param priority 優先度
     * @return 開始したモーションの識別番号。個別のモーションが終了したか否かを判定するisFinished()の引数で使用する。開始できない時は「-1」
     */
    @JvmOverloads
    fun startRandomMotion(
        group: String?,
        priority: Int,
        onFinishedMotionHandler: IFinishedMotionCallback? = null,
        onBeganMotionHandler: IBeganMotionCallback? = null,
    ): Int {
        if (modelSetting.getMotionCount(group) == 0) {
            return -1
        }

        val random = Random()
        val number = random.nextInt(Int.MAX_VALUE) % modelSetting.getMotionCount(group)

        return startMotion(group, number, priority, onFinishedMotionHandler, onBeganMotionHandler)
    }

    fun draw(matrix: CubismMatrix44) {
        if (model == null) {
            return
        }

        // キャッシュ変数の定義を避けるために、multiplyByMatrix()ではなく、multiply()を使用する。
        CubismMatrix44.multiply(
            modelMatrix.array,
            matrix.array,
            matrix.array
        )

        this.getRenderer<CubismRendererAndroid>().mvpMatrix = matrix
        this.getRenderer<CubismRendererAndroid>().drawModel()
    }

    /**
     * 当たり判定テスト
     * 指定IDの頂点リストから矩形を計算し、座標が矩形範囲内か判定する
     *
     * @param hitAreaName 当たり判定をテストする対象のID
     * @param x           判定を行うx座標
     * @param y           判定を行うy座標
     * @return 当たっているならtrue
     */
    fun hitTest(hitAreaName: String?, x: Float, y: Float): Boolean {
        // 透明時は当たり判定なし
        if (opacity < 1) {
            return false
        }

        val count = modelSetting.getHitAreasCount()
        for (i in 0..<count) {
            if (modelSetting.getHitAreaName(i) == hitAreaName) {
                val drawID = modelSetting.getHitAreaId(i)

                return isHit(drawID, x, y)
            }
        }
        // 存在しない場合はfalse
        return false
    }

    /**
     * 引数で指定した表情モーションを設定する
     *
     * @param expressionID 表情モーションのID
     */
    fun setExpression(expressionID: String?) {
        val motion = expressions.get(expressionID)

        if (debugMode) {
            printLog("expression: " + expressionID)
        }

        if (motion != null) {
            expressionManager.startMotionPriority(motion, LAppDefine.Priority.FORCE.priority)
        } else {
            if (debugMode) {
                printLog("expression " + expressionID + "is null")
            }
        }
    }

    /**
     * ランダムに選ばれた表情モーションを設定する
     */
    fun setRandomExpression() {
        if (expressions.isEmpty()) {
            return
        }

        val random = Random()
        val number = random.nextInt(Int.MAX_VALUE) % expressions.size

        var i = 0
        for (key in expressions.keys) {
            if (i == number) {
                setExpression(key)
                return
            }
            i++
        }
    }

    /**
     * .moc3ファイルの整合性をチェックする。
     *
     * @param mocFileName MOC3ファイル名
     * @return MOC3に整合性があるかどうか。整合性があればtrue。
     */
    fun hasMocConsistencyFromFile(mocFileName: String): Boolean {
        if (mocFileName.isEmpty()) return false

        var path = mocFileName
        path = modelHomeDirectory + path

        val buffer: ByteArray = createBuffer(path)
        val consistency = CubismMoc.hasMocConsistency(buffer)

        if (!consistency) {
            CubismDebug.cubismLogInfo("Inconsistent MOC3.")
        } else {
            CubismDebug.cubismLogInfo("Consistent MOC3.")
        }

        return consistency
    }

    // model3.jsonからモデルを生成する
    private fun setupModel(setting: ICubismModelSetting) {
        _modelSetting = setting

        isUpdated = true
        isInitialized = false

        // Load Cubism Model
        run {
            val fileName = modelSetting.getModelFileName()
            if (!fileName.isEmpty()) {
                val path = modelHomeDirectory + fileName

                if (LAppDefine.DEBUG_LOG_ENABLE) {
                    printLog("create model: " + modelSetting.getModelFileName())
                }

                val buffer: ByteArray = createBuffer(path)
                loadModel(buffer, mocConsistency)
            }
        }

        // load expression files(.exp3.json)
        run {
            if (modelSetting.getExpressionCount() > 0) {
                val count = modelSetting.getExpressionCount()

                for (i in 0..<count) {
                    val name = modelSetting.getExpressionName(i)
                    var path = modelSetting.getExpressionFileName(i)
                    path = modelHomeDirectory + path

                    val buffer: ByteArray = createBuffer(path)
                    val motion = loadExpression(buffer)

                    if (motion != null) {
                        expressions.put(name, motion)
                    }
                }
            }
        }

        // Physics
        run {
            val path = modelSetting.getPhysicsFileName()
            if (!path.isEmpty()) {
                val modelPath = modelHomeDirectory + path
                val buffer: ByteArray = createBuffer(modelPath)

                loadPhysics(buffer)
            }
        }

        // Pose
        run {
            val path = modelSetting.getPoseFileName()
            if (!path.isEmpty()) {
                val modelPath = modelHomeDirectory + path
                val buffer: ByteArray = createBuffer(modelPath)
                loadPose(buffer)
            }
        }

        // Load eye blink data
        if (modelSetting.getEyeBlinkParameterCount() > 0) {
            eyeBlink = CubismEyeBlink.create(modelSetting)
        }

        // Load Breath Data
        breath = CubismBreath.create()
        val breathParameters: MutableList<BreathParameterData> = arrayListOf()

        breathParameters.add(BreathParameterData(idParamAngleX, 0.0f, 15.0f, 6.5345f, 0.5f))
        breathParameters.add(BreathParameterData(idParamAngleY, 0.0f, 8.0f, 3.5345f, 0.5f))
        breathParameters.add(BreathParameterData(idParamAngleZ, 0.0f, 10.0f, 5.5345f, 0.5f))
        breathParameters.add(BreathParameterData(idParamBodyAngleX, 0.0f, 4.0f, 15.5345f, 0.5f))
        breathParameters.add(
            BreathParameterData(
                CubismFramework.getIdManager().getId(ParameterId.BREATH.id),
                0.5f,
                0.5f,
                3.2345f,
                0.5f
            )
        )

        breath.setParameters(breathParameters)

        // Load UserData
        run {
            val path = modelSetting.getUserDataFile()
            if (!path.isEmpty()) {
                val modelPath = modelHomeDirectory + path
                val buffer: ByteArray = createBuffer(modelPath)
                loadUserData(buffer)
            }
        }


        // EyeBlinkIds
        val eyeBlinkIdCount = modelSetting.getEyeBlinkParameterCount()
        for (i in 0..<eyeBlinkIdCount) {
            eyeBlinkIds.add(modelSetting.getEyeBlinkParameterId(i))
        }

        // LipSyncIds
        val lipSyncIdCount = modelSetting.getLipSyncParameterCount()
        for (i in 0..<lipSyncIdCount) {
            lipSyncIds.add(modelSetting.getLipSyncParameterId(i))
        }

        if (modelMatrix == null) {
            printLog("Failed to setupModel().")
            return
        }

        // Set layout
        val layout: MutableMap<String, Float> = hashMapOf()

        // レイアウト情報が存在すればその情報からモデル行列をセットアップする
        if (modelSetting.getLayoutMap(layout)) {
            modelMatrix.setupFromLayout(layout)
        }

        model.saveParameters()

        // Load motions
        for (i in 0..<modelSetting.getMotionGroupCount()) {
            val group = modelSetting.getMotionGroupName(i)
            preLoadMotionGroup(group)
        }

        motionManager.stopAllMotions()

        isUpdated = false
        isInitialized = true
    }

    /**
     * モーションデータをグループ名から一括でロードする。
     * モーションデータの名前はModelSettingから取得する。
     *
     * @param group モーションデータのグループ名
     */
    private fun preLoadMotionGroup(group: String?) {
        val count = modelSetting.getMotionCount(group)

        for (i in 0..<count) {
            // ex) idle_0
            val name = group + "_" + i

            val path = modelSetting.getMotionFileName(group, i)
            if (!path.isEmpty()) {
                val modelPath = modelHomeDirectory + path

                if (debugMode) {
                    printLog("load motion: " + path + "==>[" + group + "_" + i + "]")
                }
                val buffer = createBuffer(modelPath)

                // If a motion cannot be loaded, a process is skipped.
                val tmp = loadMotion(buffer, motionConsistency)
                if (tmp == null) {
                    continue
                }

                val fadeInTime = modelSetting.getMotionFadeInTimeValue(group, i)

                if (fadeInTime != -1.0f) {
                    tmp.fadeInTime = fadeInTime
                }

                val fadeOutTime = modelSetting.getMotionFadeOutTimeValue(group, i)

                if (fadeOutTime != -1.0f) {
                    tmp.fadeOutTime = fadeOutTime
                }

                tmp.setEffectIds(eyeBlinkIds, lipSyncIds)
                motions.put(name, tmp)
            }
        }
    }

    /**
     * OpenGLのテクスチャユニットにテクスチャをロードする
     */
    private fun setupTextures() {
        for (modelTextureNumber in 0..<modelSetting.getTextureCount()) {
            // テクスチャ名が空文字だった場合はロード・バインド処理をスキップ
            if (modelSetting.getTextureFileName(modelTextureNumber).isEmpty()) {
                continue
            }

            // OpenGL ESのテクスチャユニットにテクスチャをロードする
            var texturePath = modelSetting.getTextureFileName(modelTextureNumber)
            texturePath = modelHomeDirectory + texturePath

            val texture = textureManager.createTextureFromPngFile(texturePath)
            val glTextureNumber = texture.id

            this.getRenderer<CubismRendererAndroid>().bindTexture(modelTextureNumber, glTextureNumber)
            this.getRenderer<CubismRendererAndroid>().isPremultipliedAlpha(LAppDefine.PREMULTIPLIED_ALPHA_ENABLE)
        }
    }

    fun getModelName(): String {
        return modelSetting.getModelFileName().orEmpty()
    }

    fun getAllExpressionIds(): List<String> {
        return expressions.keys.toList()
    }

    fun getAllMotionIds(): List<String> {
        return motions.keys.toList()
    }

    companion object {
        private fun createBuffer(path: String): ByteArray {
            if (LAppDefine.DEBUG_LOG_ENABLE) {
                printLog("create buffer: $path")
            }
            return loadFileAsBytes(path)
        }
    }
}
