/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.physics;

import com.live2d.sdk.cubism.framework.math.CubismMath;
import com.live2d.sdk.cubism.framework.math.CubismVector2;
import com.live2d.sdk.cubism.framework.model.CubismModel;
import com.live2d.sdk.cubism.framework.physics.CubismPhysicsFunctions.*;
import com.live2d.sdk.cubism.framework.physics.CubismPhysicsInternal.*;

import java.util.ArrayList;
import java.util.List;


/**
 * Physics operation class.
 */
public class CubismPhysics {
    /**
     * Options of physics operation
     */
    public static class Options {
        /**
         * Constructor
         */
        public Options() {
            gravity = new CubismVector2();
            wind = new CubismVector2();
        }

        /**
         * Copy constructor.
         *
         * @param options Options instace
         */
        public Options(Options options) {
            this.gravity = new CubismVector2(options.gravity);
            this.wind = new CubismVector2(options.wind);
        }

        /**
         * Gravity
         */
        public final CubismVector2 gravity;
        /**
         * Wind
         */
        public final CubismVector2 wind;
    }

    /**
     * Output result of physics operations before applying to parameters
     */
    public static class PhysicsOutput {
        float[] outputs;
    }

    /**
     * Create an CubismPhysics instance.
     *
     * @param buffer the buffer where physics3.json is loaded.
     * @return the created instance
     */
    public static CubismPhysics create(byte[] buffer) {
        final CubismPhysics physics = new CubismPhysics();
        physics.parse(buffer);
        physics.physicsRig.gravity.y = 0;

        return physics;
    }

    /**
     * Reset parameters.
     */
    public void reset() {
        options.gravity.set(0.0f, -1.0f);
        options.wind.setZero();

        physicsRig.gravity.setZero();
        physicsRig.wind.setZero();

        initialize();
    }

    /**
     * 現在のパラメータ値で物理演算が安定化する状態を演算する。
     *
     * @param model 物理演算の結果を適用するモデル
     */
    public void stabilization(CubismModel model) {
        float[] totalAngle = new float[1];
        float weight;
        float radAngle;
        float outputValue;
        CubismVector2 totalTranslation = new CubismVector2();
        int i, settingIndex, particleIndex;
        CubismPhysicsSubRig currentSetting;

        float[] parameterValues = model.getModel().getParameters().getValues();
        float[] parameterMaximumValues = model.getModel().getParameters().getMaximumValues();
        float[] parameterMinimumValues = model.getModel().getParameters().getMinimumValues();
        float[] parameterDefaultValues = model.getModel().getParameters().getDefaultValues();

        if (parameterCaches.length < model.getParameterCount()) {
            parameterCaches = new float[model.getParameterCount()];
        }
        if (parameterInputCaches.length < model.getParameterCount()) {
            parameterInputCaches = new float[model.getParameterCount()];
        }

        for (int j = 0; j < model.getParameterCount(); j++) {
            parameterCaches[j] = parameterValues[j];
            parameterInputCaches[j] = parameterValues[j];
        }

        for (settingIndex = 0; settingIndex < physicsRig.subRigCount; settingIndex++) {
            totalAngle[0] = 0.0f;
            totalTranslation.setZero();

            currentSetting = physicsRig.settings.get(settingIndex);
            int baseInputIndex = currentSetting.baseInputIndex;
            int baseOutputIndex = currentSetting.baseOutputIndex;
            int baseParticleIndex = currentSetting.baseParticleIndex;

            // Load input parameters.
            for (i = 0; i < currentSetting.inputCount; i++) {
                CubismPhysicsInput currentInput = physicsRig.inputs.get(baseInputIndex + i);

                weight = currentInput.weight / MAXIMUM_WEIGHT;

                if (currentInput.sourceParameterIndex == -1) {
                    currentInput.sourceParameterIndex = model.getParameterIndex(currentInput.source.Id);
                }

                currentInput.getNormalizedParameterValue.getNormalizedParameterValue(
                    totalTranslation,
                    totalAngle,
                    parameterValues[currentInput.sourceParameterIndex],
                    parameterMinimumValues[currentInput.sourceParameterIndex],
                    parameterMaximumValues[currentInput.sourceParameterIndex],
                    parameterDefaultValues[currentInput.sourceParameterIndex],
                    currentSetting.normalizationPosition,
                    currentSetting.normalizationAngle,
                    currentInput.reflect,
                    weight
                );

                parameterCaches[currentInput.sourceParameterIndex] = parameterValues[currentInput.sourceParameterIndex];
            }

            radAngle = CubismMath.degreesToRadian(-totalAngle[0]);

            totalTranslation.x = (totalTranslation.x * CubismMath.cosF(radAngle) - totalTranslation.y * CubismMath.sinF(radAngle));
            totalTranslation.y = (totalTranslation.x * CubismMath.sinF(radAngle) + totalTranslation.y * CubismMath.cosF(radAngle));

            // Calculate particles position.
            updateParticlesForStabilization(
                physicsRig.particles,
                baseParticleIndex,
                currentSetting.particleCount,
                totalTranslation,
                totalAngle[0],
                options.wind,
                MOVEMENT_THRESHOLD * currentSetting.normalizationPosition.maximumValue
            );

            // Update output parameters.
            for (i = 0; i < currentSetting.outputCount; i++) {
                CubismPhysicsOutput currentOutput = physicsRig.outputs.get(baseOutputIndex + i);

                particleIndex = currentOutput.vertexIndex;

                if (currentOutput.destinationParameterIndex == -1) {
                    currentOutput.destinationParameterIndex = model.getParameterIndex(currentOutput.destination.Id);
                }

                if (particleIndex < 1 || particleIndex >= currentSetting.particleCount) {
                    continue;
                }

                CubismVector2 translation = new CubismVector2();
                CubismVector2.subtract(physicsRig.particles.get(baseParticleIndex + particleIndex).position, physicsRig.particles.get(baseParticleIndex + particleIndex - 1).position, translation);

                outputValue = currentOutput.getValue.getValue(
                    translation,
                    physicsRig.particles,
                    physicsRig.settings.get(i).baseParticleIndex,
                    particleIndex,
                    currentOutput.reflect,
                    options.gravity
                );

                currentRigOutputs.get(settingIndex).outputs[i] = outputValue;
                previousRigOutputs.get(settingIndex).outputs[i] = outputValue;

                updateOutputParameterValue(
                    parameterValues,
                    currentOutput.destinationParameterIndex,
                    parameterMinimumValues[currentOutput.destinationParameterIndex],
                    parameterMaximumValues[currentOutput.destinationParameterIndex],
                    outputValue,
                    currentOutput
                );

                parameterCaches[currentOutput.destinationParameterIndex] = parameterValues[currentOutput.destinationParameterIndex];
            }
        }
    }

    /**
     * Evaluate a physics operation.
     * <p>
     * Pendulum interpolation weights
     * <p>
     * 振り子の計算結果は保存され、パラメータへの出力は保存された前回の結果で補間されます。
     * The result of the pendulum calculation is saved and
     * the output to the parameters is interpolated with the saved previous result of the pendulum calculation.
     * <p>
     * 図で示すと[1]と[2]で補間されます。
     * The figure shows the interpolation between [1] and [2].
     * <p>
     * 補間の重みは最新の振り子計算タイミングと次回のタイミングの間で見た現在時間で決定する。
     * The weight of the interpolation are determined by the current time seen between
     * the latest pendulum calculation timing and the next timing.
     * <p>
     * 図で示すと[2]と[4]の間でみた(3)の位置の重みになる。
     * Figure shows the weight of position (3) as seen between [2] and [4].
     * <p>
     * 解釈として振り子計算のタイミングと重み計算のタイミングがズレる。
     * As an interpretation, the pendulum calculation and weights are misaligned.
     * <p>
     * physics3.jsonにFPS情報が存在しない場合は常に前の振り子状態で設定される。
     * If there is no FPS information in physics3.json, it is always set in the previous pendulum state.
     * <p>
     * この仕様は補間範囲を逸脱したことが原因の震えたような見た目を回避を目的にしている。
     * The purpose of this specification is to avoid the quivering appearance caused by deviations from the interpolation range.
     * <p>
     * {@literal ------------ time -------------->}
     * <p>
     * {@literal 　　　　　　　　|+++++|------| <- weight}
     * ==[1]====#=====[2]---(3)----(4)
     * ^ output contents
     * <p>
     * 1:_previousRigOutputs
     * 2:_currentRigOutputs
     * 3:_currentRemainTime (now rendering)
     * 4:next particles timing
     *
     * @param model Model to which the results of physics operation are applied
     * @param deltaTimeSeconds rendering delta time[s]
     */
    public void evaluate(CubismModel model, float deltaTimeSeconds) {
        float weight;
        float radAngle;
        float outputValue;
        int i, settingIndex, particleIndex;
        List<CubismPhysicsInput> inputs = physicsRig.inputs;
        List<CubismPhysicsOutput> outputs = physicsRig.outputs;

        if (0.0f >= deltaTimeSeconds) {
            return;
        }

        float physicsDeltaTime;
        currentRemainTime += deltaTimeSeconds;
        if (currentRemainTime > MAX_DELTA_TIME) {
            currentRemainTime = 0.0f;
        }

        float[] parameterValues = model.getModel().getParameters().getValues();
        final float[] parameterMaximumValues = model.getModel().getParameters().getMaximumValues();
        final float[] parameterMinimumValues = model.getModel().getParameters().getMinimumValues();
        final float[] parameterDefaultValues = model.getModel().getParameters().getDefaultValues();

        if (parameterCaches.length < model.getParameterCount()) {
            parameterCaches = new float[model.getParameterCount()];
        }

        if (parameterInputCaches.length < model.getParameterCount()) {
            parameterInputCaches = new float[model.getParameterCount()];
            if (model.getParameterCount() >= 0)
                System.arraycopy(parameterValues, 0, parameterInputCaches, 0, model.getParameterCount());
        }

        if (physicsRig.fps > 0.0f) {
            physicsDeltaTime = 1.0f / physicsRig.fps;
        } else {
            physicsDeltaTime = deltaTimeSeconds;
        }

        CubismPhysicsSubRig currentSetting;
        CubismPhysicsInput currentInput;
        CubismPhysicsOutput currentOutput;
        CubismPhysicsParticle currentParticle;

        while (currentRemainTime >= physicsDeltaTime) {
            // copy RigOutputs: _currentRigOutputs to _previousRigOutputs
            for (settingIndex = 0; settingIndex < physicsRig.subRigCount; settingIndex++) {
                currentSetting = physicsRig.settings.get(settingIndex);

                for (i = 0; i < currentSetting.outputCount; i++) {
                    previousRigOutputs.get(settingIndex).outputs[i] = currentRigOutputs.get(settingIndex).outputs[i];
                }
            }

            // 入力キャッシュとパラメータで線形補間してUpdateParticlesするタイミングでの入力を計算する。
            // Calculate the input at the timing to UpdateParticles by linear interpolation with the _parameterInputCaches and parameterValues.
            // _parameterCachesはグループ間での値の伝搬の役割があるので_parameterInputCachesとの分離が必要。
            // _parameterCaches needs to be separated from _parameterInputCaches because of its role in propagating values between groups.
            float inputWeight = physicsDeltaTime / currentRemainTime;
            for (int j = 0; j < model.getParameterCount(); j++) {
                parameterCaches[j] = parameterInputCaches[j] * (1.0f - inputWeight) + parameterValues[j] * inputWeight;
                parameterInputCaches[j] = parameterCaches[j];
            }

            for (settingIndex = 0; settingIndex < physicsRig.subRigCount; settingIndex++) {
                totalAngle[0] = 0.0f;
                totalTranslation.setZero();

                currentSetting = physicsRig.settings.get(settingIndex);
                List<CubismPhysicsParticle> particles = physicsRig.particles;

                int baseInputIndex = currentSetting.baseInputIndex;
                int baseOutputIndex = currentSetting.baseOutputIndex;
                int baseParticleIndex = currentSetting.baseParticleIndex;

                // Load input parameters.
                for (i = 0; i < currentSetting.inputCount; i++) {
                    currentInput = inputs.get(baseInputIndex + i);
                    weight = currentInput.weight / MAXIMUM_WEIGHT;

                    if (currentInput.sourceParameterIndex == -1) {
                        currentInput.sourceParameterIndex = model.getParameterIndex(currentInput.source.Id);
                    }

                    currentInput.getNormalizedParameterValue.getNormalizedParameterValue(
                        totalTranslation,
                        totalAngle,
                        parameterCaches[currentInput.sourceParameterIndex],
                        parameterMinimumValues[currentInput.sourceParameterIndex],
                        parameterMaximumValues[currentInput.sourceParameterIndex],
                        parameterDefaultValues[currentInput.sourceParameterIndex],
                        currentSetting.normalizationPosition,
                        currentSetting.normalizationAngle,
                        currentInput.reflect,
                        weight
                    );

                }

                radAngle = CubismMath.degreesToRadian(-totalAngle[0]);

                totalTranslation.x = (totalTranslation.x * CubismMath.cosF(radAngle) - totalTranslation.y * CubismMath.sinF(radAngle));
                totalTranslation.y = (totalTranslation.x * CubismMath.sinF(radAngle) + totalTranslation.y * CubismMath.cosF(radAngle));


                // Calculate particles position.
                updateParticles(
                    particles,
                    baseParticleIndex,
                    currentSetting.particleCount,
                    totalTranslation,
                    totalAngle[0],
                    options.wind,
                    MOVEMENT_THRESHOLD * currentSetting.normalizationPosition.maximumValue,
                    physicsDeltaTime,
                    AIR_RESISTANCE
                );

                // Update output parameters.
                for (i = 0; i < currentSetting.outputCount; i++) {
                    currentOutput = outputs.get(baseOutputIndex + i);
                    particleIndex = currentOutput.vertexIndex;


                    if (currentOutput.destinationParameterIndex == -1) {
                        currentOutput.destinationParameterIndex = model.getParameterIndex(currentOutput.destination.Id);
                    }

                    if (particleIndex < 1 || particleIndex >= currentSetting.particleCount) {
                        continue;
                    }

                    currentParticle = particles.get(baseParticleIndex + particleIndex);
                    CubismPhysicsParticle previousParticle = particles.get(baseParticleIndex + particleIndex - 1);
                    CubismVector2.subtract(currentParticle.position, previousParticle.position, translation);

                    outputValue = currentOutput.getValue.getValue(
                        translation,
                        particles,
                        baseParticleIndex,
                        particleIndex,
                        currentOutput.reflect,
                        options.gravity
                    );

                    currentRigOutputs.get(settingIndex).outputs[i] = outputValue;

                    cache[0] = parameterCaches[currentOutput.destinationParameterIndex];

                    updateOutputParameterValue(
                        cache,
                        0,
                        parameterMinimumValues[currentOutput.destinationParameterIndex],
                        parameterMaximumValues[currentOutput.destinationParameterIndex],
                        outputValue,
                        currentOutput
                    );
                    parameterCaches[currentOutput.destinationParameterIndex] = cache[0];

                }
            }
            currentRemainTime -= physicsDeltaTime;
        }

        final float alpha = currentRemainTime / physicsDeltaTime;
        interpolate(model, alpha);
    }

    // There are only used by 'evaluate' method.
    // Avoid creating a new float array and CubismVector2 instance.
    private final float[] totalAngle = new float[1];
    private final float[] cache = new float[1];
    private final CubismVector2 totalTranslation = new CubismVector2();
    private final CubismVector2 translation = new CubismVector2();

    /**
     * Set an option.
     *
     * @param options a physics operation of option
     */
    public void setOptions(Options options) {
        if (options == null) {
            return;
        }
        this.options = options;
    }

    /**
     * Get the physics operation of option.
     *
     * @return the physics operation of option
     */
    public Options getOptions() {
        return options;
    }

    /**
     * Updates particles
     *
     * @param strand Target array of particle
     * @param strandCount Count of particle
     * @param totalTranslation Total translation value
     * @param totalAngle Total angle
     * @param windDirection Direction of wind
     * @param thresholdValue Threshold of movement
     * @param deltaTimeSeconds Delta time
     * @param airResistance Air resistance
     */
    private static void updateParticles(
        List<CubismPhysicsParticle> strand,
        int baseParticleIndex,
        int strandCount,
        CubismVector2 totalTranslation,
        float totalAngle,
        CubismVector2 windDirection,
        float thresholdValue,
        float deltaTimeSeconds,
        float airResistance
    ) {
        float totalRadian;
        float delay;
        float radian;

        strand.get(baseParticleIndex).position.set(totalTranslation.x, totalTranslation.y);

        totalRadian = CubismMath.degreesToRadian(totalAngle);
        CubismMath.radianToDirection(totalRadian, currentGravity).normalize();

        for (int i = 1; i < strandCount; i++) {
            final CubismPhysicsParticle currentParticle = strand.get(baseParticleIndex + i);
            final CubismPhysicsParticle previousParticle = strand.get(baseParticleIndex + i - 1);

            currentParticle.lastPosition.set(currentParticle.position.x, currentParticle.position.y);

            {
                CubismVector2.multiply(currentGravity, currentParticle.acceleration, currentParticle.force).add(windDirection);

                delay = currentParticle.delay * deltaTimeSeconds * 30.0f;
                CubismVector2.subtract(currentParticle.position, previousParticle.position, direction);
            }
            {
                radian = CubismMath.directionToRadian(currentParticle.lastGravity, currentGravity) / airResistance;

                direction.x = ((CubismMath.cosF(radian) * direction.x) - (CubismMath.sinF(radian) * direction.y));
                direction.y = ((CubismMath.sinF(radian) * direction.x) + (direction.y * CubismMath.cosF(radian)));
            }
            {
                CubismVector2.add(previousParticle.position, direction, currentParticle.position);
                CubismVector2.multiply(currentParticle.velocity, delay, velocity);
                CubismVector2.multiply(currentParticle.force, delay, force).multiply(delay);

                currentParticle.position.add(velocity).add(force);
            }
            {
                float newDirectionX = currentParticle.position.x - previousParticle.position.x;
                float newDirectionY = currentParticle.position.y - previousParticle.position.y;
                float length = (float) (Math.pow((newDirectionX * newDirectionX) + (newDirectionY * newDirectionY), 0.5f));
                newDirectionX /= length;
                newDirectionY /= length;

                currentParticle.position.x = previousParticle.position.x + (newDirectionX * currentParticle.radius);
                currentParticle.position.y = previousParticle.position.y + (newDirectionY * currentParticle.radius);
            }

            if (CubismMath.absF(currentParticle.position.x) < thresholdValue) {
                currentParticle.position.x = 0.0f;
            }

            if (delay != 0.0f) {
                CubismVector2.subtract(currentParticle.position, currentParticle.lastPosition, currentParticle.velocity);

                currentParticle.velocity.divide(delay);
                currentParticle.velocity.multiply(currentParticle.mobility);

            }
            currentParticle.force.setZero();
            currentParticle.lastGravity.set(currentGravity.x, currentGravity.y);
        }
    }

    // There are only used by 'updateParticles' method.
    // Avoid creating a new CubismVector2 instance.
    private static final CubismVector2 direction = new CubismVector2();
    private static final CubismVector2 velocity = new CubismVector2();
    private static final CubismVector2 force = new CubismVector2();
    private static final CubismVector2 currentGravity = new CubismVector2();

    private static void updateParticlesForStabilization(
        List<CubismPhysicsParticle> strand,
        int baseParticleIndex,
        int strandCount,
        CubismVector2 totalTranslation,
        float totalAngle,
        CubismVector2 windDirection,
        float thresholdValue
    ) {
        int i;
        float totalRadian;

        strand.get(baseParticleIndex).position.set(totalTranslation.x, totalTranslation.y);

        totalRadian = CubismMath.degreesToRadian(totalAngle);
        CubismMath.radianToDirection(totalRadian, currentGravityForStablization).normalize();

        for (i = 1; i < strandCount; i++) {
            CubismPhysicsParticle particle = strand.get(baseParticleIndex + i);
            CubismVector2.multiply(currentGravityForStablization, particle.acceleration, particle.force).add(windDirection);

            particle.lastPosition.set(particle.position.x, particle.position.y);
            particle.velocity.setZero();

            forceForStabilization.set(particle.force.x, particle.force.y);
            forceForStabilization.normalize();

            forceForStabilization.multiply(particle.radius);
            CubismVector2.add(strand.get(baseParticleIndex + i - 1).position, forceForStabilization, particle.position);

            if (CubismMath.absF(particle.position.x) < thresholdValue) {
                particle.position.x = 0.0f;
            }

            particle.force.setZero();
            particle.lastGravity.set(currentGravityForStablization.x, currentGravityForStablization.y);
        }
    }

    // updateParticlesForStabilization関数内でのみ使われるキャッシュ変数
    private static final CubismVector2 currentGravityForStablization = new CubismVector2();
    private static final CubismVector2 forceForStabilization = new CubismVector2();

    private static void updateOutputParameterValue(
        float[] parameterValue,
        int destinationParameterIndex,
        float parameterValueMinimum,
        float parameterValueMaximum,
        float translation,
        CubismPhysicsOutput output
    ) {
        float outputScale;
        float value;
        float weight;

        outputScale = output.getScale.getScale(
            output.transitionScale,
            output.angleScale
        );

        value = translation * outputScale;

        if (value < parameterValueMinimum) {
            if (value < output.valueBelowMinimum) {
                output.valueBelowMinimum = value;
            }

            value = parameterValueMinimum;
        } else if (value > parameterValueMaximum) {
            if (value > output.valueExceededMaximum) {
                output.valueExceededMaximum = value;
            }

            value = parameterValueMaximum;
        }

        weight = output.weight / MAXIMUM_WEIGHT;

        if (!(weight >= 1.0f)) {
            value = (parameterValue[destinationParameterIndex] * (1.0f - weight)) + (value * weight);
        }
        parameterValue[destinationParameterIndex] = value;
    }

    /**
     * Constant of air resistance
     */
    private static final float AIR_RESISTANCE = 5.0f;

    /**
     * Constant of maximum weight of input and output ratio
     */
    private static final float MAXIMUM_WEIGHT = 100.0f;

    /**
     * Constant of threshold of movement
     */
    private static final float MOVEMENT_THRESHOLD = 0.001f;
    /**
     * Constant of maximum allowed delta time
     */
    private static final float MAX_DELTA_TIME = 5.0f;

    // -----private constants-----
// Physics types tags
    private enum PhysicsTypeTag {
        X("X"),
        Y("Y"),
        ANGLE("Angle");

        private final String tag;

        PhysicsTypeTag(String tag) {
            this.tag = tag;
        }
    }

    /**
     * Private constructor
     */
    private CubismPhysics() {
        options.gravity.set(0.0f, -1.0f);
        options.wind.setZero();
    }

    /**
     * Parse a physics3.json data.
     *
     * @param physicsJson a buffer where physics3.json is loaded.
     */
    private void parse(final byte[] physicsJson) {
        physicsRig = new CubismPhysicsRig();

        CubismPhysicsJson json;
        json = new CubismPhysicsJson(physicsJson);

        physicsRig.gravity = json.getGravity();
        physicsRig.wind = json.getWind();
        physicsRig.subRigCount = json.getSubRigCount();

        physicsRig.fps = json.getFps();

        physicsRig.settings = new ArrayList<CubismPhysicsSubRig>(physicsRig.subRigCount);

        physicsRig.inputs = new ArrayList<CubismPhysicsInput>(json.getTotalInputCount());

        physicsRig.outputs = new ArrayList<CubismPhysicsOutput>(json.getTotalOutputCount());

        physicsRig.particles = new ArrayList<CubismPhysicsParticle>(json.getVertexCount());

        currentRigOutputs.clear();
        previousRigOutputs.clear();

        int inputIndex = 0;
        int outputIndex = 0;
        int particleIndex = 0;

        for (int i = 0; i < physicsRig.subRigCount; i++) {
            final CubismPhysicsSubRig setting = new CubismPhysicsSubRig();

            // Setting
            setting.baseInputIndex = inputIndex;
            setting.baseOutputIndex = outputIndex;
            setting.baseParticleIndex = particleIndex;
            parseSetting(json, setting, i);

            // Input
            parseInputs(json, i, setting.inputCount);
            inputIndex += setting.inputCount;

            // Output
            parseOutputs(json, i, setting.outputCount);
            outputIndex += setting.outputCount;

            // Particle
            parseParticles(json, i, setting.particleCount);
            particleIndex += setting.particleCount;
        }
        initialize();
    }

    /**
     * Parse setting parameters.
     *
     * @param json physics3.json data
     * @param setting current physics setting
     * @param settingIndex current setting index
     */
    private void parseSetting(final CubismPhysicsJson json, final CubismPhysicsSubRig setting, final int settingIndex) {
        setting.normalizationPosition.minimumValue = json.getNormalizationPositionMinimumValue(settingIndex);
        setting.normalizationPosition.maximumValue = json.getNormalizationPositionMaximumValue(settingIndex);
        setting.normalizationPosition.defaultValue = json.getNormalizationPositionDefaultValue(settingIndex);

        setting.normalizationAngle.minimumValue = json.getNormalizationAngleMinimumValue(settingIndex);
        setting.normalizationAngle.maximumValue = json.getNormalizationAngleMaximumValue(settingIndex);
        setting.normalizationAngle.defaultValue = json.getNormalizationAngleDefaultValue(settingIndex);

        setting.inputCount = json.getInputCount(settingIndex);
        setting.outputCount = json.getOutputCount(settingIndex);
        setting.particleCount = json.getParticleCount(settingIndex);

        physicsRig.settings.add(setting);
    }

    /**
     * Parse inputs parameters.
     * (Used for only the parse() method.)
     *
     * @param json physics3.json data.
     * @param settingIndex current setting index
     * @param inputCount number of the current input
     */
    private void parseInputs(final CubismPhysicsJson json, final int settingIndex, final int inputCount) {
        for (int inputIndex = 0; inputIndex < inputCount; inputIndex++) {
            final CubismPhysicsInput input = new CubismPhysicsInput();

            input.sourceParameterIndex = -1;
            input.weight = json.getInputWeight(settingIndex, inputIndex);
            input.reflect = json.getInputReflect(settingIndex, inputIndex);

            final String tag = json.getInputType(settingIndex, inputIndex);

            if (tag.equals(PhysicsTypeTag.X.tag)) {
                input.type = CubismPhysicsSource.X;
                input.getNormalizedParameterValue = new GetInputTranslationXFromNormalizedParameterValue();
            } else if (tag.equals(PhysicsTypeTag.Y.tag)) {
                input.type = CubismPhysicsSource.Y;
                input.getNormalizedParameterValue = new GetInputTranslationYFromNormalizedParameterValue();
            } else if (tag.equals(PhysicsTypeTag.ANGLE.tag)) {
                input.type = CubismPhysicsSource.ANGLE;
                input.getNormalizedParameterValue = new GetInputAngleFromNormalizedParameterValue();
            }

            input.source.targetType = CubismPhysicsTargetType.PARAMETER;
            input.source.Id = json.getInputSourceId(settingIndex, inputIndex);

            physicsRig.inputs.add(input);
        }
    }

    /**
     * Parse outputs parameters.
     * (Used for only the parse() method.)
     *
     * @param json physics3.json data
     * @param settingIndex current setting index
     * @param outputCount number of the current input
     */
    private void parseOutputs(final CubismPhysicsJson json, final int settingIndex, final int outputCount) {
        final int count = physicsRig.settings.get(settingIndex).outputCount;

        PhysicsOutput currentRigOutput = new PhysicsOutput();
        currentRigOutput.outputs = new float[count];
        currentRigOutputs.add(currentRigOutput);

        PhysicsOutput previousRigOutput = new PhysicsOutput();
        previousRigOutput.outputs = new float[count];
        previousRigOutputs.add(previousRigOutput);

        for (int outputIndex = 0; outputIndex < outputCount; outputIndex++) {
            final CubismPhysicsOutput output = new CubismPhysicsOutput();

            output.destinationParameterIndex = -1;
            output.vertexIndex = json.getOutputVertexIndex(settingIndex, outputIndex);
            output.angleScale = json.getOutputAngleScale(settingIndex, outputIndex);
            output.weight = json.getOutputWeight(settingIndex, outputIndex);
            output.destination.targetType = CubismPhysicsTargetType.PARAMETER;

            output.destination.Id = json.getOutputsDestinationId(settingIndex, outputIndex);

            final String tag = json.getOutputType(settingIndex, outputIndex);
            if (tag.equals(PhysicsTypeTag.X.tag)) {
                output.type = CubismPhysicsSource.X;
                output.getValue = new GetOutputTranslationX();
                output.getScale = new GetOutputScaleTranslationX();
            } else if (tag.equals(PhysicsTypeTag.Y.tag)) {
                output.type = CubismPhysicsSource.Y;
                output.getValue = new GetOutputTranslationY();
                output.getScale = new GetOutputScaleTranslationY();
            } else if (tag.equals(PhysicsTypeTag.ANGLE.tag)) {
                output.type = CubismPhysicsSource.ANGLE;
                output.getValue = new GetOutputAngle();
                output.getScale = new GetOutputScaleAngle();
            }

            output.reflect = json.getOutputReflect(settingIndex, outputIndex);

            physicsRig.outputs.add(output);
        }
    }

    /**
     * Parse particles parameters.
     * (Used for only the parse() method)
     *
     * @param json physics3.json data
     * @param settingIndex current setting index
     * @param particleCount number of the current particle
     */
    private void parseParticles(final CubismPhysicsJson json, final int settingIndex, final int particleCount) {
        for (int particleIndex = 0; particleIndex < particleCount; particleIndex++) {
            final CubismPhysicsParticle particle = new CubismPhysicsParticle();

            particle.mobility = json.getParticleMobility(settingIndex, particleIndex);
            particle.delay = json.getParticleDelay(settingIndex, particleIndex);
            particle.acceleration = json.getParticleAcceleration(settingIndex, particleIndex);
            particle.radius = json.getParticleRadius(settingIndex, particleIndex);
            particle.position = json.getParticlePosition(settingIndex, particleIndex);

            physicsRig.particles.add(particle);
        }
    }

    /**
     * Initializes physics
     */
    private void initialize() {
        for (int settingIndex = 0; settingIndex < physicsRig.subRigCount; settingIndex++) {
            final CubismPhysicsSubRig currentSetting = physicsRig.settings.get(settingIndex);

            final int baseIndex = currentSetting.baseParticleIndex;
            final CubismPhysicsParticle baseParticle = physicsRig.particles.get(baseIndex);

            // Initialize the top of particle
            baseParticle.initialPosition = new CubismVector2();
            baseParticle.lastPosition = new CubismVector2(baseParticle.initialPosition);
            baseParticle.lastGravity = new CubismVector2(0.0f, 1.0f);
            baseParticle.velocity = new CubismVector2();
            baseParticle.force = new CubismVector2();

            // Initialize particles
            for (int i = 1; i < currentSetting.particleCount; i++) {
                final CubismPhysicsParticle currentParticle = physicsRig.particles.get(baseIndex + i);

                final CubismVector2 radius = new CubismVector2(0.0f, currentParticle.radius);

                final CubismVector2 previousPosition = new CubismVector2(physicsRig.particles.get(baseIndex + i - 1).initialPosition);
                currentParticle.initialPosition = previousPosition.add(radius);

                currentParticle.position = new CubismVector2(currentParticle.initialPosition);
                currentParticle.lastPosition = new CubismVector2(currentParticle.initialPosition);
                currentParticle.lastGravity = new CubismVector2(0.0f, 1.0f);
                currentParticle.velocity = new CubismVector2();
                currentParticle.force = new CubismVector2();
            }
        }
    }

    /**
     * 引数で与えられたモデルに、振り子演算の最新の結果とその1つ前の結果、及び与えられた重みから算出された物理演算の結果を適用する。
     * Apply the result of the physics operation calculated from the latest result of the pendulum operation, the previous one, and the given weights, to the model given in the argument.
     *
     * @param model model applied the result of physics operation
     * @param weight weight of the latest result
     */
    private void interpolate(CubismModel model, float weight) {
        for (int settingIndex = 0; settingIndex < physicsRig.subRigCount; settingIndex++) {
            CubismPhysicsSubRig currentSetting = physicsRig.settings.get(settingIndex);
            List<CubismPhysicsOutput> outputs = physicsRig.outputs;
            int baseOutputIndex = currentSetting.baseOutputIndex;

            float[] parameterValues = model.getModel().getParameters().getValues();
            float[] parameterMaximumValues = model.getModel().getParameters().getMaximumValues();
            float[] parameterMinimumValues = model.getModel().getParameters().getMinimumValues();

            tmpValue[0] = 0.0f;

            for (int i = 0; i < currentSetting.outputCount; i++) {
                CubismPhysicsOutput currentOutput = outputs.get(baseOutputIndex + i);

                if (currentOutput.destinationParameterIndex == -1) {
                    continue;
                }

                tmpValue[0] = parameterValues[currentOutput.destinationParameterIndex];

                updateOutputParameterValue(
                    tmpValue,
                    0,
                    parameterMinimumValues[currentOutput.destinationParameterIndex],
                    parameterMaximumValues[currentOutput.destinationParameterIndex],
                    previousRigOutputs.get(settingIndex).outputs[i] * (1 - weight) + currentRigOutputs.get(settingIndex).outputs[i] * weight,
                    currentOutput
                );
                parameterValues[currentOutput.destinationParameterIndex] = tmpValue[0];
            }
        }
    }

    // This is only used by 'interpolate' method.
    // Avoid creating a new float array instance.
    private final float[] tmpValue = new float[1];

    /**
     * Load input parameters.
     * (Used for only evaluate() method.)
     *
     * @param model model instance
     * @param transition total amount of model's transition
     * @param settingIndex index of setting
     * @return total amount of model's angle
     */
    private float loadInputParameters(final CubismModel model, final CubismVector2 transition, final int settingIndex) {
        CubismPhysicsSubRig currentSetting = physicsRig.settings.get(settingIndex);

        final float[] totalAngle = new float[1];

        for (int i = 0; i < currentSetting.inputCount; i++) {
            final CubismPhysicsInput currentInput = physicsRig.inputs.get(currentSetting.baseInputIndex + i);
            final float weight = currentInput.weight / MAXIMUM_WEIGHT;

            if (currentInput.sourceParameterIndex == -1) {
                currentInput.sourceParameterIndex = model.getParameterIndex(currentInput.source.Id);
            }

            final float parameterValue = model.getParameterValue(currentInput.sourceParameterIndex);
            final float parameterMaximumValue = model.getParameterMaximumValue(currentInput.sourceParameterIndex);
            final float parameterMinimumValue = model.getParameterMinimumValue(currentInput.sourceParameterIndex);
            final float parameterDefaultValue = model.getParameterDefaultValue(currentInput.sourceParameterIndex);

            currentInput.getNormalizedParameterValue.getNormalizedParameterValue(
                transition,
                totalAngle,
                parameterValue,
                parameterMinimumValue,
                parameterMaximumValue,
                parameterDefaultValue,
                currentSetting.normalizationPosition,
                currentSetting.normalizationAngle,
                currentInput.reflect,
                weight
            );
        }
        return totalAngle[0];
    }


    /**
     * Physics operation data
     */
    private CubismPhysicsRig physicsRig;
    /**
     * Options of physics operation
     */
    private Options options = new Options();

    /**
     * Result of the latest pendulum calculation
     */
    private final List<PhysicsOutput> currentRigOutputs = new ArrayList<PhysicsOutput>();
    /**
     * Result of one previous pendulum calculation
     */
    private final List<PhysicsOutput> previousRigOutputs = new ArrayList<PhysicsOutput>();

    /**
     * Time not processed by physics
     */
    private float currentRemainTime;

    /**
     * Cache of parameter used in 'Evaluate' method
     */
    private float[] parameterCaches = new float[1];
    /**
     * Cache of parameter input in 'UpdateParticles' method
     */
    private float[] parameterInputCaches = new float[1];
}
