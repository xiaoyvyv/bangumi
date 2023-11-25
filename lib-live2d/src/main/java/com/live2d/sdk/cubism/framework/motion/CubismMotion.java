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
import com.live2d.sdk.cubism.framework.motion.CubismMotionInternal.*;
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
     * Create an instance.
     *
     * @param buffer buffer where motion3.json is loaded
     * @param callback callback function called at the end of motion playback, not called if null.
     * @return instance of CubismMotion
     */
    public static CubismMotion create(byte[] buffer, IFinishedMotionCallback callback) {
        CubismMotion motion = new CubismMotion();
        motion.parse(buffer);

        motion.sourceFrameRate = motion.motionData.fps;
        motion.loopDurationSeconds = motion.motionData.duration;
        motion.onFinishedMotion = callback;

        // NOTE: Exporting motion with loop is not supported in Editor.
        return motion;
    }

    /**
     * Create an instance.
     * If callback function is not specified, it becomes 'null'.
     *
     * @param buffer buffer where motion3.json is loaded.
     * @return instance of CubismMotion
     */
    public static CubismMotion create(byte[] buffer) {
        return create(buffer, null);
    }

    /**
     * Set loop information.
     *
     * @param loop loop information
     **/
    public void isLooped(boolean loop) {
        isLooped = loop;
    }

    /**
     * Whether the motion loops.
     *
     * @return If it loops, return true.
     */
    public boolean isLooped() {
        return isLooped;
    }

    /**
     * Set the fade-in information at looping.
     *
     * @param loopFadeIn fade-in information at looping
     */
    public void isLoopFadeIn(boolean loopFadeIn) {
        isLoopFadeIn = loopFadeIn;
    }

    /**
     * Whether the motion fade in at looping.
     *
     * @return If it fades in, return true.
     */
    public boolean isLoopFadeIn() {
        return isLoopFadeIn;
    }

    /**
     * Set the fade-in duration for parameters.
     *
     * @param parameterId parameter ID
     * @param value fade-in duration[s]
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
     * @param value fade-out duration[s]
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
     * @param lipSyncParameterIds parameter ID list to which automatic lip-syncing is applied
     */
    public void setEffectIds(List<CubismId> eyeBlinkParameterIds, List<CubismId> lipSyncParameterIds) {
        this.eyeBlinkParameterIds.clear();
        this.eyeBlinkParameterIds.addAll(eyeBlinkParameterIds);

        this.lipSyncParameterIds.clear();
        this.lipSyncParameterIds.addAll(lipSyncParameterIds);
    }

    @Override
    public float getDuration() {
        return isLooped
               ? -1.0f
               : loopDurationSeconds;
    }

    @Override
    public float getLoopDuration() {
        return loopDurationSeconds;
    }

    @Override
    public List<String> getFiredEvent(float beforeCheckTimeSeconds, float motionTimeSeconds) {
        if (areFiredEventValuesChanged) {
            firedEventValues.clear();

            for (int i = 0; i < motionData.events.size(); i++) {
                CubismMotionEvent event = motionData.events.get(i);

                if ((event.fireTime > beforeCheckTimeSeconds)
                    && (event.fireTime <= motionTimeSeconds)) {
                    firedEventValues.add(event.value);
                }
            }
            cachedImmutableFiredEventValues = Collections.unmodifiableList(firedEventValues);
            areFiredEventValuesChanged = false;
        }
        return cachedImmutableFiredEventValues;
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
     * @param model target model
     * @param userTimeSeconds current time[s]
     * @param fadeWeight weight of motion
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

        if (isLooped) {
            while (time > motionData.duration) {
                time -= motionData.duration;
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
            value = evaluateCurve(curve, time);

            if (curve.id.equals(modelCurveIdEyeBlink)) {
                eyeBlinkValue = value;
                isUpdatedEyeBlink = true;
            } else if (curve.id.equals(modelCurveIdLipSync)) {
                lipSyncValue = value;
                isUpdatedLipSync = true;
            } else if (curve.id.equals(modelCurveIdOpacity)){
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
            value = evaluateCurve(curve, time);

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
            value = evaluateCurve(curve, time);
            model.setParameterValue(parameterIndex, value);
        }

        if (timeOffsetSeconds >= motionData.duration) {
            if (isLooped) {
                // Initialize
                motionQueueEntry.setStartTime(userTimeSeconds);

                if (isLoopFadeIn) {
                    // If fade-in for loop is enabled in a loop, set fade-in again.
                    motionQueueEntry.setFadeInStartTime(userTimeSeconds);
                }
            } else {
                if (onFinishedMotion != null) {
                    onFinishedMotion.execute(this);
                }
                motionQueueEntry.isFinished(true);
            }
        }
        lastWeight = fadeWeight;
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

    private class LinearEvaluator implements CsmMotionSegmentEvaluationFunction {
        @Override
        public float evaluate(float time, int basePointIndex) {
            CubismMotionPoint p0 = motionData.points.get(basePointIndex);
            CubismMotionPoint p1 = motionData.points.get(basePointIndex + 1);

            float t = (time - p0.time) / (p1.time - p0.time);

            if (t < 0.0f) {
                t = 0.0f;
            }
            return p0.value + (p1.value - p0.value) * t;
        }
    }

    private class BezierEvaluator implements CsmMotionSegmentEvaluationFunction {
        @Override
        public float evaluate(final float time, final int basePointIndex) {
            final CubismMotionPoint p0 = motionData.points.get(basePointIndex);
            final CubismMotionPoint p1 = motionData.points.get(basePointIndex + 1);
            final CubismMotionPoint p2 = motionData.points.get(basePointIndex + 2);
            final CubismMotionPoint p3 = motionData.points.get(basePointIndex + 3);

            float t = (time - p0.time) / (p3.time - p0.time);

            if (t < 0.0f) {
                t = 0.0f;
            }

            return getLerpPointsValue(p0, p1, p2, p3, t);
        }
    }

    private class BezierEvaluatorCardanoInterpretation implements CsmMotionSegmentEvaluationFunction {
        @Override
        public float evaluate(final float time, final int basePointIndex) {
            CubismMotionPoint p0 = motionData.points.get(basePointIndex);
            CubismMotionPoint p1 = motionData.points.get(basePointIndex + 1);
            CubismMotionPoint p2 = motionData.points.get(basePointIndex + 2);
            CubismMotionPoint p3 = motionData.points.get(basePointIndex + 3);

            final float x1 = p0.time;
            final float x2 = p3.time;
            final float cx1 = p1.time;
            final float cx2 = p2.time;

            final float a = x2 - 3.0f * cx2 + 3.0f * cx1 - x1;
            final float b = 3.0f * cx2 - 6.0f * cx1 + 3.0f * x1;
            final float c = 3.0f * cx1 - 3.0f * x1;
            final float d = x1 - time;

            float t = CubismMath.cardanoAlgorithmForBezier(a, b, c, d);

            return getLerpPointsValue(p0, p1, p2, p3, t);
        }
    }

    private class SteppedEvaluator implements CsmMotionSegmentEvaluationFunction {
        @Override
        public float evaluate(float time, int basePointIndex) {
            return motionData.points.get(basePointIndex).value;
        }
    }

    private class InverseSteppedEvaluator implements CsmMotionSegmentEvaluationFunction {
        @Override
        public float evaluate(final float time, final int basePointIndex) {
            return motionData.points.get(basePointIndex + 1).value;
        }
    }

    // lerp: Linear Interpolate(線形補間の略)
    private static CubismMotionPoint lerpPoints(
        final CubismMotionPoint a,
        final CubismMotionPoint b,
        final float t,
        CubismMotionPoint motionPoint
    ) {
        motionPoint.time = a.time + ((b.time - a.time) * t);
        motionPoint.value = a.value + ((b.value - a.value) * t);

        return motionPoint;
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
     * @param motionJson buffer where motion3.json is loaded
     */
    private void parse(byte[] motionJson) {
        motionData = new CubismMotionData();
        final CubismMotionJson json;
        json = new CubismMotionJson(motionJson);

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

                final int tmpSegment = (int) (json.getMotionCurveSegment(curveCount, segmentPosition));

                CubismMotionSegmentType segmentType = null;

                if (tmpSegment == 0) {
                    segmentType = CubismMotionSegmentType.LINEAR;
                } else if (tmpSegment == 1) {
                    segmentType = CubismMotionSegmentType.BEZIER;
                } else if (tmpSegment == 2) {
                    segmentType = CubismMotionSegmentType.STEPPED;
                } else if (tmpSegment == 3) {
                    segmentType = CubismMotionSegmentType.INVERSESTEPPED;
                } else {
                    assert (false);
                }

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

        areFiredEventValuesChanged = true;
    }

    private float getLerpPointsValue(
        CubismMotionPoint p0,
        CubismMotionPoint p1,
        CubismMotionPoint p2,
        CubismMotionPoint p3,
        float t
    ) {
        lerpPoints(p0, p1, t, p01);
        lerpPoints(p1, p2, t, p12);
        lerpPoints(p2, p3, t, p23);

        lerpPoints(p01, p12, t, p012);
        lerpPoints(p12, p23, t, p123);

        return lerpPoints(p012, p123, t, result).value;
    }

    // These are only used by 'getLerpPointsValue' method.
    // Avoid creating a new CubismMotionPoint instance in 'lerpPoints' method.
    private final CubismMotionPoint p01 = new CubismMotionPoint();
    private final CubismMotionPoint p12 = new CubismMotionPoint();
    private final CubismMotionPoint p23 = new CubismMotionPoint();
    private final CubismMotionPoint p012 = new CubismMotionPoint();
    private final CubismMotionPoint p123 = new CubismMotionPoint();
    private final CubismMotionPoint result = new CubismMotionPoint();

    private float bezierEvaluateBinarySearch(final float time, final int basePointIndex) {
        final float x_error = 0.01f;

        final CubismMotionPoint p0 = motionData.points.get(basePointIndex);
        final CubismMotionPoint p1 = motionData.points.get(basePointIndex + 1);
        final CubismMotionPoint p2 = motionData.points.get(basePointIndex + 2);
        final CubismMotionPoint p3 = motionData.points.get(basePointIndex + 3);

        float x1 = p0.time;
        float x2 = p3.time;
        float cx1 = p1.time;
        float cx2 = p2.time;

        float ta = 0.0f;
        float tb = 1.0f;
        float t = 0.0f;

        int i;
        for (i = 0; i < 20; i++) {

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
            float ctrlx12 = (cx1 + centerx) * 0.5f;
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

        t = CubismMath.rangeF(t, 0.0f, 1.0f);

        return getLerpPointsValue(p0, p1, p2, p3, t);
    }

    private float evaluateCurve(final CubismMotionCurve curve, final float time) {
        // Find segment to evaluate.
        int target = -1;

        final int totalSegmentCount = curve.baseSegmentIndex + curve.segmentCount;
        int pointPosition = 0;

        for (int i = curve.baseSegmentIndex; i < totalSegmentCount; i++) {
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
            return motionData.points.get(pointPosition).value;
        }

        final CubismMotionSegment segment = motionData.segments.get(target);

        return segment.evaluator.evaluate(time, segment.basePointIndex);
    }

    /**
     * FPS of the loaded file; if not specified, the default value is 30 fps.
     */
    private float sourceFrameRate = 30.0f;
    /**
     * length of the sequence of motions defined in the motion3.json file.
     */
    private float loopDurationSeconds = -1.0f;
    /**
     * enable/Disable loop
     */
    private boolean isLooped;
    /**
     * flag whether fade-in is enabled at looping. Default value is true.
     */
    private boolean isLoopFadeIn = true;
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

