/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.motion;

import com.live2d.sdk.cubism.framework.CubismFramework;
import com.live2d.sdk.cubism.framework.id.CubismId;
import com.live2d.sdk.cubism.framework.math.CubismMath;
import com.live2d.sdk.cubism.framework.model.CubismModel;
import com.live2d.sdk.cubism.framework.motion.CubismMotionInternal.CsmMotionSegmentEvaluationFunction;
import com.live2d.sdk.cubism.framework.motion.CubismMotionInternal.CubismMotionCurve;
import com.live2d.sdk.cubism.framework.motion.CubismMotionInternal.CubismMotionCurveTarget;
import com.live2d.sdk.cubism.framework.motion.CubismMotionInternal.CubismMotionData;
import com.live2d.sdk.cubism.framework.motion.CubismMotionInternal.CubismMotionEvent;
import com.live2d.sdk.cubism.framework.motion.CubismMotionInternal.CubismMotionPoint;
import com.live2d.sdk.cubism.framework.motion.CubismMotionInternal.CubismMotionSegment;
import com.live2d.sdk.cubism.framework.motion.CubismMotionInternal.CubismMotionSegmentType;
import com.live2d.sdk.cubism.framework.utils.CubismDebug;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

/**
 * Motion class.
 */
public final class CubismMotion extends ACubismMotion {
    /**
     * Enumerator for version control of Motion Behavior.
     * For details, see the SDK Manual.
     */
    public enum MotionBehavior {
        MOTION_BEHAVIOR_V1,
        MOTION_BEHAVIOR_V2,
    }

    /**
     * Create an instance.
     *
     * @param buffer                       buffer where motion3.json is loaded
     * @param finishedMotionCallBack       callback function called at the end of motion playback, not called if null.
     * @param beganMotionCallBack          callback function called at the start of motion playback, not called if null.
     * @param shouldCheckMotionConsistency flag to validate the consistency of motion3.json.
     * @return instance of CubismMotion
     */
    public static CubismMotion create(
            byte[] buffer,
            IFinishedMotionCallback finishedMotionCallBack,
            IBeganMotionCallback beganMotionCallBack,
            boolean shouldCheckMotionConsistency
    ) {
        CubismMotion motion = new CubismMotion();
        motion.parse(buffer, shouldCheckMotionConsistency);

        if (motion.motionData != null) {
            motion.sourceFrameRate = motion.motionData.fps;
            motion.loopDurationSeconds = motion.motionData.duration;
            motion.onFinishedMotion = finishedMotionCallBack;
            motion.onBeganMotion = beganMotionCallBack;
        } else {
            motion = null;
        }

        // NOTE: Exporting motion with loop is not supported in Editor.
        return motion;
    }

    /**
     * Create an instance.
     * This method does not check the consistency of motion3.json.
     * To check the consistency of motion3.json,
     * use {@link #create(byte[], IFinishedMotionCallback, IBeganMotionCallback, boolean)}
     * and set the fourth argument to `true`.
     *
     * @param buffer                 buffer where motion3.json is loaded
     * @param finishedMotionCallBack callback function called at the end of motion playback, not called if null.
     * @param beganMotionCallBack    callback function called at the start of motion playback, not called if null.
     * @return instance of CubismMotion
     */
    public static CubismMotion create(
            byte[] buffer,
            IFinishedMotionCallback finishedMotionCallBack,
            IBeganMotionCallback beganMotionCallBack
    ) {
        return create(buffer, finishedMotionCallBack, beganMotionCallBack, false);
    }

    /**
     * Create an instance.
     * This method does not set any callback functions.
     *
     * @param buffer                       buffer where motion3.json is loaded
     * @param shouldCheckMotionConsistency flag to validate the consistency of motion3.json.
     * @return instance of CubismMotion
     */
    public static CubismMotion create(byte[] buffer, boolean shouldCheckMotionConsistency) {
        return create(buffer, null, null, shouldCheckMotionConsistency);
    }

    /**
     * Create an instance.
     * This method does not check the consistency of motion3.json.
     * To check the consistency of motion3.json,
     * use {@link #create(byte[], boolean)}
     * and set the second argument to `true`.
     * This method does not set any callback functions.
     *
     * @param buffer buffer where motion3.json is loaded.
     * @return instance of CubismMotion
     */
    public static CubismMotion create(byte[] buffer) {
        return create(buffer, null, null, false);
    }

    /**
     * Set loop information.
     *
     * @param loop loop information
     * @deprecated Not recommended due to the relocation of isLoop to the base class.
     * Use ACubismMotion.setLoop(boolean loop) instead.
     **/
    @Deprecated
    public void isLooped(boolean loop) {
        CubismDebug.cubismLogWarning("isLoop(boolean loop) is a deprecated function. Please use setLoop(boolean loop).");
        super.setLoop(loop);
    }

    /**
     * Whether the motion loops.
     *
     * @return If it loops, return true.
     * @deprecated Not recommended due to the relocation of isLoop to the base class.
     * Use ACubismMotion.getLoop() instead.
     */
    @Deprecated
    public boolean isLooped() {
        CubismDebug.cubismLogWarning("isLoop() is a deprecated function. Please use getLoop().");
        return super.getLoop();
    }

    /**
     * Set the fade-in information at looping.
     *
     * @param loopFadeIn fade-in information at looping
     * @deprecated Not recommended due to the relocation of isLoopFadeIn to the base class.
     * Use ACubismMotion.setLoopFadeIn(boolean loopFadeIn) instead.
     */
    @Deprecated
    public void isLoopFadeIn(boolean loopFadeIn) {
        CubismDebug.cubismLogWarning("isLoopFadeIn(boolean loopFadeIn) is a deprecated function. Please use setLoopFadeIn(boolean loopFadeIn)");
        super.setLoopFadeIn(loopFadeIn);
    }

    /**
     * Whether the motion fade in at looping.
     *
     * @return If it fades in, return true.
     * @deprecated Not recommended due to the relocation of isLoopFadeIn to the base class.
     * Use ACubismMotion.getLoopFadeIn() instead.
     */
    @Deprecated
    public boolean isLoopFadeIn() {
        CubismDebug.cubismLogWarning("isLoopFadeIn() is a deprecated function. Please use getLoopFadeIn().");
        return super.getLoopFadeIn();
    }

    /**
     * Sets the version of the Motion Behavior.
     *
     * @param motionBehavior the version of the Motion Behavior.
     */
    public void setMotionBehavior(MotionBehavior motionBehavior) {
        this.motionBehavior = motionBehavior;
    }

    /**
     * Gets the version of the Motion Behavior.
     *
     * @return Returns the version of the Motion Behavior.
     */
    public MotionBehavior getMotionBehavior() {
        return motionBehavior;
    }

    /**
     * Set the fade-in duration for parameters.
     *
     * @param parameterId parameter ID
     * @param value       fade-in duration[s]
     */
    public void setParameterFadeInTime(CubismId parameterId, float value) {
        for (int i = 0; i < motionData.curves.size(); i++) {
            CubismMotionCurve curve = motionData.curves.get(i);

            if (parameterId.equals(curve.id)) {
                curve.fadeInTime = value;
                return;
            }
        }
    }

    /**
     * Get the fade-in duration for parameters.
     *
     * @param parameterId parameter ID
     * @return fade-in duration[s]
     */
    public float getParameterFadeInTime(CubismId parameterId) {
        for (int i = 0; i < motionData.curves.size(); i++) {
            CubismMotionCurve curve = motionData.curves.get(i);

            if (parameterId.equals(curve.id)) {
                return curve.fadeInTime;
            }
        }
        return -1;
    }

    /**
     * Set the fade-out duration for parameters.
     *
     * @param parameterId parameter ID
     * @param value       fade-out duration[s]
     */
    public void setParameterFadeOutTime(CubismId parameterId, float value) {
        for (int i = 0; i < motionData.curves.size(); i++) {
            CubismMotionCurve curve = motionData.curves.get(i);

            if (parameterId.equals(curve.id)) {
                curve.fadeOutTime = value;
                return;
            }
        }
    }

    /**
     * Get the fade-out duration for parameters.
     *
     * @param parameterId parameter ID
     * @return fade-out duration[s]
     */
    public float getParameterFadeOutTime(CubismId parameterId) {
        for (int i = 0; i < motionData.curves.size(); i++) {
            CubismMotionCurve curve = motionData.curves.get(i);

            if (parameterId.equals(curve.id)) {
                return curve.fadeOutTime;
            }
        }
        return -1;
    }

    /**
     * Set the parameter ID list to which automatic effects are applied.
     *
     * @param eyeBlinkParameterIds parameter ID list to which automatic eye blinking is applied
     * @param lipSyncParameterIds  parameter ID list to which automatic lip-syncing is applied
     */
    public void setEffectIds(List<CubismId> eyeBlinkParameterIds, List<CubismId> lipSyncParameterIds) {
        this.eyeBlinkParameterIds.clear();
        this.eyeBlinkParameterIds.addAll(eyeBlinkParameterIds);

        this.lipSyncParameterIds.clear();
        this.lipSyncParameterIds.addAll(lipSyncParameterIds);
    }

    @Override
    public float getDuration() {
        return isLoop
                ? -1.0f
                : loopDurationSeconds;
    }

    @Override
    public float getLoopDuration() {
        return loopDurationSeconds;
    }

    @Override
    public List<String> getFiredEvent(float beforeCheckTimeSeconds, float motionTimeSeconds) {
        firedEventValues.clear();

        for (int i = 0; i < motionData.events.size(); i++) {
            CubismMotionEvent event = motionData.events.get(i);

            if ((event.fireTime > beforeCheckTimeSeconds) && (event.fireTime <= motionTimeSeconds)) {
                firedEventValues.add(event.value);
            }
        }
        return Collections.unmodifiableList(firedEventValues);
    }

    @Override
    public boolean isExistModelOpacity() {
        for (int i = 0; i < motionData.curves.size(); i++) {
            CubismMotionCurve curve = motionData.curves.get(i);

            if (curve.type != CubismMotionCurveTarget.MODEL) {
                continue;
            }
            if (curve.id.getString().equals(ID_NAME_OPACITY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getModelOpacityIndex() {
        if (isExistModelOpacity()) {
            for (int i = 0; i < motionData.curves.size(); i++) {
                CubismMotionCurve curve = motionData.curves.get(i);

                if (curve.type != CubismMotionCurveTarget.MODEL) {
                    continue;
                }
                if (curve.id.getString().equals(ID_NAME_OPACITY)) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public CubismId getModelOpacityId(int index) {
        if (index == -1) {
            return null;
        }

        CubismMotionCurve curve = motionData.curves.get(index);

        if (curve.type == CubismMotionCurveTarget.MODEL) {
            if (curve.id.getString().equals(ID_NAME_OPACITY)) {
                return CubismFramework.getIdManager().getId(curve.id);
            }
        }

        return null;
    }

    @Override
    public float getModelOpacityValue() {
        return modelOpacity;
    }

    /**
     * Update parameters of the model.
     *
     * @param model            target model
     * @param userTimeSeconds  current time[s]
     * @param fadeWeight       weight of motion
     * @param motionQueueEntry motion managed by CubismMotionQueueManager
     */
    @Override
    protected void doUpdateParameters(
            final CubismModel model,
            final float userTimeSeconds,
            final float fadeWeight,
            final CubismMotionQueueEntry motionQueueEntry
    ) {
        if (modelCurveIdEyeBlink == null) {
            modelCurveIdEyeBlink = CubismFramework.getIdManager().getId(EffectName.EYE_BLINK.name);
        }

        if (modelCurveIdLipSync == null) {
            modelCurveIdLipSync = CubismFramework.getIdManager().getId(EffectName.LIP_SYNC.name);
        }

        if (modelCurveIdOpacity == null) {
            modelCurveIdOpacity = CubismFramework.getIdManager().getId(ID_NAME_OPACITY);
        }

        if (motionBehavior == MotionBehavior.MOTION_BEHAVIOR_V2) {
            if (previousLoopState != isLoop) {
                // 終了時間を再計算する
                adjustEndTime(motionQueueEntry);
                previousLoopState = isLoop;
            }
        }

        float timeOffsetSeconds = userTimeSeconds - motionQueueEntry.getStartTime();


        // Error avoidance.
        if (timeOffsetSeconds < 0.0f) {
            timeOffsetSeconds = 0.0f;
        }

        final int MAX_TARGET_SIZE = 64;

        if (eyeBlinkParameterIds.size() > MAX_TARGET_SIZE) {
            String message = "too many eye blink targets: " + eyeBlinkParameterIds.size();
            CubismDebug.cubismLogDebug(message);
        }
        if (lipSyncParameterIds.size() > MAX_TARGET_SIZE) {
            String message = "too many lip sync targets: " + lipSyncParameterIds.size();
            CubismDebug.cubismLogDebug(message);
        }

        // 'Repeat time as necessary'
        float time = timeOffsetSeconds;
        float duration = motionData.duration;
        boolean isCorrection = motionBehavior == MotionBehavior.MOTION_BEHAVIOR_V2 && isLoop;

        if (isLoop) {
            if (motionBehavior == MotionBehavior.MOTION_BEHAVIOR_V2) {
                duration += 1.0f / motionData.fps;
            }
            while (time > duration) {
                time -= duration;
            }
        }

        List<CubismMotionCurve> curves = motionData.curves;

        float eyeBlinkValue = 0;
        float lipSyncValue = 0;

        // A bit flag indicating whether the blink and lip-sync motions have been applied.
        boolean isUpdatedEyeBlink = false;
        boolean isUpdatedLipSync = false;

        float value;

        // Evaluate model curves
        for (int i = 0; i < curves.size(); i++) {
            CubismMotionCurve curve = curves.get(i);

            if (curve.type != CubismMotionCurveTarget.MODEL) {
                continue;
            }

            // Evaluate curve and call handler.
            value = evaluateCurve(motionData, i, time, isCorrection, duration);

            if (curve.id.equals(modelCurveIdEyeBlink)) {
                eyeBlinkValue = value;
                isUpdatedEyeBlink = true;
            } else if (curve.id.equals(modelCurveIdLipSync)) {
                lipSyncValue = value;
                isUpdatedLipSync = true;
            } else if (curve.id.equals(modelCurveIdOpacity)) {
                modelOpacity = value;

                // 不透明度の値が存在すれば反映する。
                model.setModelOpacity(getModelOpacityValue());
            }
        }

        final float tmpFadeIn = (fadeInSeconds <= 0.0f)
                ? 1.0f
                : CubismMath.getEasingSine((userTimeSeconds - motionQueueEntry.getFadeInStartTime()) / fadeInSeconds);
        final float tmpFadeOut = (fadeOutSeconds <= 0.0f || motionQueueEntry.getEndTime() < 0.0f)
                ? 1.0f
                : CubismMath.getEasingSine((motionQueueEntry.getEndTime() - userTimeSeconds) / fadeOutSeconds);

        for (int i = 0; i < curves.size(); i++) {
            CubismMotionCurve curve = curves.get(i);

            if (curve.type != CubismMotionCurveTarget.PARAMETER) {
                continue;
            }

            // Find parameter index.
            final int parameterIndex = model.getParameterIndex(curve.id);

            // Skip curve evaluation if no value.
            if (parameterIndex == -1) {
                continue;
            }

            final float sourceValue = model.getParameterValue(parameterIndex);

            // Evaluate curve and apply value.
            value = evaluateCurve(motionData, i, time, isCorrection, duration);

            if (isUpdatedEyeBlink) {
                for (int j = 0; j < eyeBlinkParameterIds.size(); j++) {
                    CubismId id = eyeBlinkParameterIds.get(j);

                    if (j == MAX_TARGET_SIZE) {
                        break;
                    }

                    if (id.equals(curve.id)) {
                        value *= eyeBlinkValue;
                        eyeBlinkFlags.set(j);
                        break;
                    }
                }
            }

            if (isUpdatedLipSync) {
                for (int j = 0; j < lipSyncParameterIds.size(); j++) {
                    CubismId id = lipSyncParameterIds.get(j);

                    if (j == MAX_TARGET_SIZE) {
                        break;
                    }

                    if (id.equals(curve.id)) {
                        value += lipSyncValue;
                        lipSyncFlags.set(j);
                        break;
                    }
                }
            }

            // Process repeats only for compatibility
            if (model.isRepeat(parameterIndex)) {
                value = model.getParameterRepeatValue(parameterIndex, value);
            }

            float v;
            if (existFade(curve)) {
                // If the parameter has a fade-in or fade-out setting, apply it.
                float fin;
                float fout;

                if (existFadeIn(curve)) {
                    final float easedValue = (userTimeSeconds - motionQueueEntry.getFadeInStartTime()) / curve.fadeInTime;

                    fin = curve.fadeInTime == 0.0f
                            ? 1.0f
                            : CubismMath.getEasingSine(easedValue);
                } else {
                    fin = tmpFadeIn;
                }

                if (existFadeOut(curve)) {
                    final float easedValue = (motionQueueEntry.getEndTime() - userTimeSeconds) / curve.fadeOutTime;

                    fout = (curve.fadeOutTime == 0.0f || motionQueueEntry.getEndTime() < 0.0f)
                            ? 1.0f
                            : CubismMath.getEasingSine(easedValue);
                } else {
                    fout = tmpFadeOut;
                }

                final float paramWeight = weight * fin * fout;

                // Apply each fading.
                v = sourceValue + (value - sourceValue) * paramWeight;

            } else {
                // Apply each fading.
                v = sourceValue + (value - sourceValue) * fadeWeight;
            }
            model.setParameterValue(parameterIndex, v);
        }


        if (isUpdatedEyeBlink) {
            for (int i = 0; i < eyeBlinkParameterIds.size(); i++) {
                CubismId id = eyeBlinkParameterIds.get(i);

                if (i == MAX_TARGET_SIZE) {
                    break;
                }

                // Blink does not apply when there is a motion overriding.
                if (eyeBlinkFlags.get(i)) {
                    continue;
                }

                final float sourceValue = model.getParameterValue(id);
                final float v = sourceValue + (eyeBlinkValue - sourceValue) * fadeWeight;

                model.setParameterValue(id, v);
            }
        }

        if (isUpdatedLipSync) {
            for (int i = 0; i < lipSyncParameterIds.size(); i++) {
                CubismId id = lipSyncParameterIds.get(i);

                if (i == MAX_TARGET_SIZE) {
                    break;
                }

                // Lip-sync does not apply when there is a motion overriding.
                if (lipSyncFlags.get(i)) {
                    continue;
                }

                final float sourceValue = model.getParameterValue(id);

                final float v = sourceValue + (lipSyncValue - sourceValue) * fadeWeight;

                model.setParameterValue(id, v);
            }
        }

        int curveSize = curves.size();
        for (int i = 0; i < curveSize; i++) {
            CubismMotionCurve curve = curves.get(i);

            if (curve.type != CubismMotionCurveTarget.PART_OPACITY) {
                continue;
            }

            // Find parameter index.
            final int parameterIndex = model.getParameterIndex(curve.id);

            // Skip curve evaluation if no value.
            if (parameterIndex == -1) {
                continue;
            }

            // Evaluate curve and apply value.
            value = evaluateCurve(motionData, i, time, isCorrection, duration);
            model.setParameterValue(parameterIndex, value);
        }

        if (timeOffsetSeconds >= duration) {
            if (isLoop) {
                UpdateForNextLoop(motionQueueEntry, userTimeSeconds, time);
            } else {
                if (onFinishedMotion != null) {
                    onFinishedMotion.execute(this);
                }
                motionQueueEntry.isFinished(true);
            }
        }
        lastWeight = fadeWeight;
    }

    private void UpdateForNextLoop(CubismMotionQueueEntry motionQueueEntry, float userTimeSeconds, float time) {
        switch (motionBehavior) {
            case MOTION_BEHAVIOR_V1:
                // 旧ループ処理
                motionQueueEntry.setStartTime(userTimeSeconds); //最初の状態へ
                if (isLoopFadeIn) {
                    //ループ中でループ用フェードインが有効のときは、フェードイン設定し直し
                    motionQueueEntry.setFadeInStartTime(userTimeSeconds);
                }
                break;
            case MOTION_BEHAVIOR_V2:
            default:
                motionQueueEntry.setStartTime(userTimeSeconds - time); //最初の状態へ
                if (isLoopFadeIn) {
                    //ループ中でループ用フェードインが有効のときは、フェードイン設定し直し
                    motionQueueEntry.setFadeInStartTime(userTimeSeconds - time);
                }

                if (this.onFinishedMotion != null) {
                    this.onFinishedMotion.execute(this);
                }
                break;
        }
    }

    // ID
    private static final String ID_NAME_OPACITY = "Opacity";

    /**
     * It is set to "true" to reproduce the motion of Cubism SDK R2 or earlier, or "false" to reproduce the animator's motion correctly.
     */
    private static final boolean USE_OLD_BEZIERS_CURVE_MOTION = false;


    private enum EffectName {
        EYE_BLINK("EyeBlink"),
        LIP_SYNC("LipSync");

        private final String name;

        EffectName(String name) {
            this.name = name;
        }
    }

    private enum TargetName {
        MODEL("Model"),
        PARAMETER("Parameter"),
        PART_OPACITY("PartOpacity");

        private final String name;

        TargetName(String name) {
            this.name = name;
        }
    }

    private static class LinearEvaluator implements CsmMotionSegmentEvaluationFunction {
        @Override
        public float evaluate(final List<CubismMotionPoint> points, final float time) {
            float t = (time - points.get(0).time) / (points.get(1).time - points.get(0).time);

            if (t < 0.0f) {
                t = 0.0f;
            }

            return points.get(0).value + ((points.get(1).value - points.get(0).value) * t);
        }
    }

    private static class BezierEvaluator implements CsmMotionSegmentEvaluationFunction {
        @Override
        public float evaluate(final List<CubismMotionPoint> points, final float time) {
            float t = (time - points.get(0).time) / (points.get(3).time - points.get(0).time);

            if (t < 0.0f) {
                t = 0.0f;
            }

            final CubismMotionPoint p01 = lerpPoints(points.get(0), points.get(1), t);
            final CubismMotionPoint p12 = lerpPoints(points.get(1), points.get(2), t);
            final CubismMotionPoint p23 = lerpPoints(points.get(2), points.get(3), t);

            final CubismMotionPoint p012 = lerpPoints(p01, p12, t);
            final CubismMotionPoint p123 = lerpPoints(p12, p23, t);

            return lerpPoints(p012, p123, t).value;
        }
    }

    private static class BezierEvaluatorCardanoInterpretation implements CsmMotionSegmentEvaluationFunction {
        @Override
        public float evaluate(final List<CubismMotionPoint> points, final float time) {
            final float x1 = points.get(0).time;
            final float x2 = points.get(3).time;
            final float cx1 = points.get(1).time;
            final float cx2 = points.get(2).time;

            final float a = x2 - 3.0f * cx2 + 3.0f * cx1 - x1;
            final float b = 3.0f * cx2 - 6.0f * cx1 + 3.0f * x1;
            final float c = 3.0f * cx1 - 3.0f * x1;
            final float d = x1 - time;

            final float t = CubismMath.cardanoAlgorithmForBezier(a, b, c, d);

            final CubismMotionPoint p01 = lerpPoints(points.get(0), points.get(1), t);
            final CubismMotionPoint p12 = lerpPoints(points.get(1), points.get(2), t);
            final CubismMotionPoint p23 = lerpPoints(points.get(2), points.get(3), t);

            final CubismMotionPoint p012 = lerpPoints(p01, p12, t);
            final CubismMotionPoint p123 = lerpPoints(p12, p23, t);

            return lerpPoints(p012, p123, t).value;
        }
    }

    private static class SteppedEvaluator implements CsmMotionSegmentEvaluationFunction {
        @Override
        public float evaluate(final List<CubismMotionPoint> points, final float time) {
            return points.get(0).value;
        }
    }

    private static class InverseSteppedEvaluator implements CsmMotionSegmentEvaluationFunction {
        @Override
        public float evaluate(final List<CubismMotionPoint> points, final float time) {
            return points.get(1).value;
        }
    }

    // lerp: Linear Interpolate(線形補間の略)
    private static CubismMotionPoint lerpPoints(
            final CubismMotionPoint a,
            final CubismMotionPoint b,
            final float t
    ) {
        CubismMotionPoint result = new CubismMotionPoint();

        result.time = a.time + ((b.time - a.time) * t);
        result.value = a.value + ((b.value - a.value) * t);

        return result;
    }

    /**
     * Check for the presence of fade-in.
     *
     * @param curve motion curve instance
     * @return If fade-in exists, return true.
     */
    private boolean existFadeIn(CubismMotionCurve curve) {
        return curve.fadeInTime >= 0.0f;
    }

    /**
     * Check for the presence of fade-out.
     *
     * @param curve motion curve instance
     * @return If fade-out exists, return true.
     */
    private boolean existFadeOut(CubismMotionCurve curve) {
        return curve.fadeOutTime >= 0.0f;
    }

    /**
     * Check for the presence of fading.
     *
     * @param curve motion curve instance
     * @return If fading exists, return true.
     */
    private boolean existFade(CubismMotionCurve curve) {
        return existFadeIn(curve) || existFadeOut(curve);
    }

    /**
     * Parse motion3.json.
     *
     * @param motionJson                   buffer where motion3.json is loaded
     * @param shouldCheckMotionConsistency flag to validate the consistency of motion3.json.
     */
    private void parse(byte[] motionJson, boolean shouldCheckMotionConsistency) {
        final CubismMotionJson json = new CubismMotionJson(motionJson);

        if (shouldCheckMotionConsistency) {
            boolean consistency = json.hasConsistency();

            if (!consistency) {
                // 整合性が確認できなければ処理しない。
                CubismDebug.cubismLogError("Inconsistent motion3.json.");
                return;
            }
        }

        motionData = new CubismMotionData();

        motionData.duration = json.getMotionDuration();
        motionData.isLooped = json.isMotionLoop();
        motionData.curveCount = json.getMotionCurveCount();
        motionData.fps = json.getMotionFps();
        motionData.eventCount = json.getEventCount();

        boolean areBeziersRestricted = json.getEvaluationOptionFlag(CubismMotionJson.EvaluationOptionFlag.ARE_BEZIERS_RESTRICTED);


        if (json.existsMotionFadeInTime()) {
            fadeInSeconds = (json.getMotionFadeInTime() < 0.0f)
                    ? 1.0f
                    : json.getMotionFadeInTime();
        } else {
            fadeInSeconds = 1.0f;
        }

        if (json.existsMotionFadeOutTime()) {
            fadeOutSeconds = (json.getMotionFadeOutTime() < 0.0f)
                    ? 1.0f
                    : json.getMotionFadeOutTime();
        } else {
            fadeOutSeconds = 1.0f;
        }

        motionData.curves = new ArrayList<CubismMotionCurve>(motionData.curveCount);
        for (int i = 0; i < motionData.curveCount; i++) {
            motionData.curves.add(new CubismMotionCurve());
        }

        motionData.segments = new ArrayList<CubismMotionSegment>(json.getMotionTotalSegmentCount());
        for (int i = 0; i < json.getMotionTotalSegmentCount(); i++) {
            motionData.segments.add(new CubismMotionSegment());
        }

        motionData.points = new ArrayList<CubismMotionPoint>(json.getMotionTotalPointCount());
        for (int i = 0; i < json.getMotionTotalPointCount(); i++) {
            motionData.points.add(new CubismMotionPoint());
        }

        motionData.events = new ArrayList<CubismMotionEvent>(motionData.eventCount);
        for (int i = 0; i < motionData.eventCount; i++) {
            motionData.events.add(new CubismMotionEvent());
        }

        int totalPointCount = 0;
        int totalSegmentCount = 0;

        // Curves
        for (int curveCount = 0; curveCount < motionData.curveCount; curveCount++) {
            final CubismMotionCurve curve = motionData.curves.get(curveCount);

            // Register target type.
            final String targetName = json.getMotionCurveTarget(curveCount);
            if (targetName.equals(TargetName.MODEL.name)) {
                curve.type = CubismMotionCurveTarget.MODEL;
            } else if (targetName.equals(TargetName.PARAMETER.name)) {
                curve.type = CubismMotionCurveTarget.PARAMETER;
            } else if (targetName.equals(TargetName.PART_OPACITY.name)) {
                curve.type = CubismMotionCurveTarget.PART_OPACITY;
            } else {
                CubismDebug.cubismLogWarning("Warning: Unable to get segment type from Curve! The number of \"CurveCount\" may be incorrect!");
            }

            curve.id = json.getMotionCurveId(curveCount);
            curve.baseSegmentIndex = totalSegmentCount;
            curve.fadeInTime =
                    (json.existsMotionCurveFadeInTime(curveCount))
                            ? json.getMotionCurveFadeInTime(curveCount)
                            : -1.0f;
            curve.fadeOutTime =
                    (json.existsMotionCurveFadeOutTime(curveCount))
                            ? json.getMotionCurveFadeOutTime(curveCount)
                            : -1.0f;

            // Segments
            for (int segmentPosition = 0; segmentPosition < json.getMotionCurveSegmentCount(curveCount); ) {
                if (segmentPosition == 0) {
                    motionData.segments.get(totalSegmentCount).basePointIndex = totalPointCount;

                    motionData.points.get(totalPointCount).time = json.getMotionCurveSegment(curveCount, segmentPosition);
                    motionData.points.get(totalPointCount).value = json.getMotionCurveSegment(curveCount, segmentPosition + 1);

                    totalPointCount += 1;
                    segmentPosition += 2;
                } else {
                    motionData.segments.get(totalSegmentCount).basePointIndex = totalPointCount - 1;
                }

                final CubismMotionSegmentType segmentType = json.getMotionCurveSegmentType(curveCount, segmentPosition);

                switch (segmentType) {
                    case LINEAR: {
                        CubismMotionSegment segment = motionData.segments.get(totalSegmentCount);
                        segment.segmentType = CubismMotionSegmentType.LINEAR;
                        segment.evaluator = linearEvaluator;

                        CubismMotionPoint point = motionData.points.get(totalPointCount);
                        point.time = json.getMotionCurveSegment(curveCount, segmentPosition + 1);
                        point.value = json.getMotionCurveSegment(curveCount, segmentPosition + 2);

                        totalPointCount += 1;
                        segmentPosition += 3;

                        break;
                    }
                    case BEZIER: {
                        CubismMotionSegment segment = motionData.segments.get(totalSegmentCount);
                        segment.segmentType = CubismMotionSegmentType.BEZIER;

                        if (areBeziersRestricted || USE_OLD_BEZIERS_CURVE_MOTION) {
                            segment.evaluator = bezierEvaluator;
                        } else {
                            segment.evaluator = bezierCardanoInterpretationEvaluator;
                        }

                        motionData.points.get(totalPointCount).time = json.getMotionCurveSegment(curveCount, (segmentPosition + 1));
                        motionData.points.get(totalPointCount).value = json.getMotionCurveSegment(curveCount, (segmentPosition + 2));

                        motionData.points.get(totalPointCount + 1).time = json.getMotionCurveSegment(curveCount, (segmentPosition + 3));
                        motionData.points.get(totalPointCount + 1).value = json.getMotionCurveSegment(curveCount, (segmentPosition + 4));

                        motionData.points.get(totalPointCount + 2).time = json.getMotionCurveSegment(curveCount, (segmentPosition + 5));
                        motionData.points.get(totalPointCount + 2).value = json.getMotionCurveSegment(curveCount, (segmentPosition + 6));

                        totalPointCount += 3;
                        segmentPosition += 7;

                        break;
                    }
                    case STEPPED: {
                        motionData.segments.get(totalSegmentCount).segmentType = CubismMotionSegmentType.STEPPED;
                        motionData.segments.get(totalSegmentCount).evaluator = steppedEvaluator;

                        motionData.points.get(totalPointCount).time = json.getMotionCurveSegment(curveCount, (segmentPosition + 1));
                        motionData.points.get(totalPointCount).value = json.getMotionCurveSegment(curveCount, (segmentPosition + 2));

                        totalPointCount += 1;
                        segmentPosition += 3;

                        break;
                    }
                    case INVERSESTEPPED: {
                        motionData.segments.get(totalSegmentCount).segmentType = CubismMotionSegmentType.INVERSESTEPPED;
                        motionData.segments.get(totalSegmentCount).evaluator = inverseSteppedEvaluator;

                        motionData.points.get(totalPointCount).time = json.getMotionCurveSegment(curveCount, (segmentPosition + 1));
                        motionData.points.get(totalPointCount).value = json.getMotionCurveSegment(curveCount, (segmentPosition + 2));

                        totalPointCount += 1;
                        segmentPosition += 3;

                        break;
                    }
                    default: {
                        assert (false);
                        break;
                    }
                }

                ++motionData.curves.get(curveCount).segmentCount;
                ++totalSegmentCount;
            }
        }

        for (int userdatacount = 0; userdatacount < json.getEventCount(); ++userdatacount) {
            motionData.events.get(userdatacount).fireTime = json.getEventTime(userdatacount);
            motionData.events.get(userdatacount).value = json.getEventValue(userdatacount);
        }
    }

    private float bezierEvaluateBinarySearch(final CubismMotionPoint[] points, final float time) {
        final float x_error = 0.01f;

        float x1 = points[0].time;
        float x2 = points[3].time;
        float cx1 = points[1].time;
        float cx2 = points[2].time;

        float ta = 0.0f;
        float tb = 1.0f;
        float t = 0.0f;
        int i = 0;

        for (boolean var33 = true; i < 20; ++i) {
            if (time < x1 + x_error) {
                t = ta;
                break;
            }

            if (x2 - x_error < time) {
                t = tb;
                break;
            }

            float centerx = (cx1 + cx2) * 0.5f;
            cx1 = (x1 + cx1) * 0.5f;
            cx2 = (x2 + cx2) * 0.5f;
            final float ctrlx12 = (cx1 + centerx) * 0.5f;
            float ctrlx21 = (cx2 + centerx) * 0.5f;
            centerx = (ctrlx12 + ctrlx21) * 0.5f;
            if (time < centerx) {
                tb = (ta + tb) * 0.5f;
                if (centerx - x_error < time) {
                    t = tb;
                    break;
                }

                x2 = centerx;
                cx2 = ctrlx12;
            } else {
                ta = (ta + tb) * 0.5f;
                if (time < centerx + x_error) {
                    t = ta;
                    break;
                }

                x1 = centerx;
                cx1 = ctrlx21;
            }
        }

        if (i == 20) {
            t = (ta + tb) * 0.5f;
        }

        if (t < 0.0f) {
            t = 0.0f;
        }
        if (t > 1.0f) {
            t = 1.0f;
        }

        final CubismMotionPoint p01 = lerpPoints(points[0], points[1], t);
        final CubismMotionPoint p12 = lerpPoints(points[1], points[2], t);
        final CubismMotionPoint p23 = lerpPoints(points[2], points[3], t);

        final CubismMotionPoint p012 = lerpPoints(p01, p12, t);
        final CubismMotionPoint p123 = lerpPoints(p12, p23, t);

        return lerpPoints(p012, p123, t).value;
    }

    private float correctEndPoint(
            final CubismMotionData motionData,
            final int segmentIndex,
            final int beginIndex,
            final int endIndex,
            final float time,
            final float endTime
    ) {
        ArrayList<CubismMotionPoint> motionPoint = new ArrayList<CubismMotionPoint>(2);
        {
            final CubismMotionPoint src = motionData.points.get(endIndex);
            motionPoint.add(new CubismMotionPoint(src.time, src.value));
        }
        {
            final CubismMotionPoint src = motionData.points.get(beginIndex);
            motionPoint.add(new CubismMotionPoint(src.time, src.value));
        }
        motionPoint.get(1).time = endTime;

        switch (motionData.segments.get(segmentIndex).segmentType) {
            case STEPPED:
                return steppedEvaluator.evaluate(motionPoint, time);
            case INVERSESTEPPED:
                return inverseSteppedEvaluator.evaluate(motionPoint, time);
            case LINEAR:
            case BEZIER:
            default:
                return linearEvaluator.evaluate(motionPoint, time);
        }
    }

    private float evaluateCurve(final CubismMotionData motionData, final int index, float time, final boolean isCorrection, final float endTime) {
        // Find segment to evaluate.
        final CubismMotionCurve curve = motionData.curves.get(index);

        int target = -1;
        final int totalSegmentCount = curve.baseSegmentIndex + curve.segmentCount;
        int pointPosition = 0;
        for (int i = curve.baseSegmentIndex; i < totalSegmentCount; ++i) {
            // Get first point of next segment.
            pointPosition = motionData.segments.get(i).basePointIndex
                    + (motionData.segments.get(i).segmentType == CubismMotionSegmentType.BEZIER
                    ? 3
                    : 1);

            // Break if time lies within current segment.
            if (motionData.points.get(pointPosition).time > time) {
                target = i;
                break;
            }
        }

        if (target == -1) {
            if (isCorrection && time < endTime) {
                // 終点から始点への補正処理
                return correctEndPoint(
                        motionData,
                        totalSegmentCount - 1,
                        motionData.segments.get(curve.baseSegmentIndex).basePointIndex,
                        pointPosition,
                        time,
                        endTime
                );
            }

            return motionData.points.get(pointPosition).value;
        }

        final CubismMotionSegment segment = motionData.segments.get(target);

        final List<CubismMotionPoint> points = motionData.points.subList(segment.basePointIndex, motionData.points.size());
        return segment.evaluator.evaluate(points, time);
    }

    /**
     * FPS of the loaded file; if not specified, the default value is 30 fps.
     */
    private float sourceFrameRate = 30.0f;
    /**
     * length of the sequence of motions defined in the motion3.json file.
     */
    private float loopDurationSeconds = -1.0f;

    private MotionBehavior motionBehavior = MotionBehavior.MOTION_BEHAVIOR_V2;

    /**
     * last set weight
     */
    private float lastWeight;
    /**
     * actual motion data itself
     */
    private CubismMotionData motionData;
    /**
     * list of parameter ID handles to which automatic eye blinking is applied. Corresponds to a model (model setting) and a parameter.
     */
    private final List<CubismId> eyeBlinkParameterIds = new ArrayList<CubismId>();
    /**
     * list of parameter ID handles to which lip-syncing is applied. Corresponds to a model (model setting) and a parameter.
     */
    private final List<CubismId> lipSyncParameterIds = new ArrayList<CubismId>();

    private final BitSet eyeBlinkFlags = new BitSet(eyeBlinkParameterIds.size());
    private final BitSet lipSyncFlags = new BitSet(lipSyncParameterIds.size());

    /**
     * handle to the parameter ID for automatic eye blinking that the model has. Map a model to a motion.
     */
    private CubismId modelCurveIdEyeBlink;
    /**
     * handle to the parameter ID for lip-syncing that the model has. Map a model to a motion.
     */
    private CubismId modelCurveIdLipSync;
    /**
     * handle to the parameter ID for opacity that the moder has. Map a model to a motion.
     */
    private CubismId modelCurveIdOpacity;

    /**
     * モーションから取得した不透明度
     */
    private float modelOpacity;


    private final LinearEvaluator linearEvaluator = new LinearEvaluator();
    private final BezierEvaluator bezierEvaluator = new BezierEvaluator();
    private final BezierEvaluatorCardanoInterpretation bezierCardanoInterpretationEvaluator = new BezierEvaluatorCardanoInterpretation();
    private final SteppedEvaluator steppedEvaluator = new SteppedEvaluator();
    private final InverseSteppedEvaluator inverseSteppedEvaluator = new InverseSteppedEvaluator();
}

