/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.motion;

import com.live2d.sdk.cubism.framework.CubismFramework;
import com.live2d.sdk.cubism.framework.id.CubismId;
import com.live2d.sdk.cubism.framework.utils.jsonparser.CubismJson;

/**
 * Container for motion3.json.
 */
class CubismMotionJson {
    /**
     * Flag type of Bezier curve interpretation method
     */
    public enum EvaluationOptionFlag {
        /**
         * Regulatory status of Bezier handle.
         */
        ARE_BEZIERS_RESTRICTED
    }

    public CubismMotionJson(byte[] buffer) {
        CubismJson json;
        json = CubismJson.create(buffer);

        this.json = json;
    }

    /**
     * Get the duration of the motion.
     *
     * @return motion duration[s]
     */
    public float getMotionDuration() {
        return json.getRoot().get(JsonKey.META.key).get(JsonKey.DURATION.key).toFloat();
    }

    /**
     * Whether the motion loops.
     *
     * @return If the motion loops, return true.
     */
    public boolean isMotionLoop() {
        return json.getRoot().get(JsonKey.META.key).get(JsonKey.LOOP.key).toBoolean();
    }

    /**
     * Get the state of the interpretation flag of Bezier curve handles in motion.
     *
     * @param flagType the flag type specified by EvaluationOptionFlag.
     * @return If the flag is present, return true.
     */
    public boolean getEvaluationOptionFlag(EvaluationOptionFlag flagType) {
        if (EvaluationOptionFlag.ARE_BEZIERS_RESTRICTED == flagType) {
            return json.getRoot().get(JsonKey.META.key).get(JsonKey.ARE_BEZIERS_RESTRICTED.key).toBoolean();
        }
        return false;
    }

    /**
     * Get the number of the motion curves.
     *
     * @return number of motion curve
     */
    public int getMotionCurveCount() {
        return json.getRoot().get(JsonKey.META.key).get(JsonKey.CURVE_COUNT.key).toInt();
    }

    /**
     * Get the framerate of the motion.
     *
     * @return framerate[FPS]
     */
    public float getMotionFps() {
        return json.getRoot().get(JsonKey.META.key).get(JsonKey.FPS.key).toFloat();
    }

    /**
     * Get the total number of motion segments.
     *
     * @return total number of motion segments
     */
    public int getMotionTotalSegmentCount() {
        return json.getRoot().get(JsonKey.META.key).get(JsonKey.TOTAL_SEGMENT_COUNT.key).toInt();
    }

    /**
     * Get the total number of control points for the curve of the motion.
     *
     * @return total number of control points for the curve of the motion
     */
    public int getMotionTotalPointCount() {
        return json.getRoot().get(JsonKey.META.key).get(JsonKey.TOTAL_POINT_COUNT.key).toInt();
    }

    /**
     * Whether a fade-in time is set for the motion.
     *
     * @return If motion fade-in time is set, return true.
     */
    public boolean existsMotionFadeInTime() {
        return !json.getRoot().get(JsonKey.META.key).get(JsonKey.FADE_IN_TIME.key).isNull();
    }

    /**
     * Whether a fade-out time is set for the motion.
     *
     * @return If motion fade-out time is set, return true.
     */
    public boolean existsMotionFadeOutTime() {
        return !json.getRoot().get(JsonKey.META.key).get(JsonKey.FADE_OUT_TIME.key).isNull();
    }

    /**
     * Get the motion fade-in duration.
     *
     * @return fade-in duration[s]
     */
    public float getMotionFadeInTime() {
        return json.getRoot().get(JsonKey.META.key).get(JsonKey.FADE_IN_TIME.key).toFloat();
    }

    /**
     * Get the motion fade-out duration.
     *
     * @return fade-out duration[s]
     */
    public float getMotionFadeOutTime() {
        return json.getRoot().get(JsonKey.META.key).get(JsonKey.FADE_OUT_TIME.key).toFloat();
    }

    /**
     * Get the type of motion curve.
     *
     * @param curveIndex index of curve
     * @return type of motion curve
     */
    public String getMotionCurveTarget(int curveIndex) {
        return json.getRoot().get(JsonKey.CURVES.key).get(curveIndex).get(JsonKey.TARGET.key).getString();
    }

    /**
     * Get ID of motion curve.
     *
     * @param curveIndex index of curve
     * @return curve ID
     */
    public CubismId getMotionCurveId(int curveIndex) {
        return CubismFramework.getIdManager().getId(json.getRoot().get(JsonKey.CURVES.key).get(curveIndex).get(JsonKey.ID.key).getString());
    }

    /**
     * Whether the fade-in duration is set for the motion's curve.
     *
     * @param curveIndex index of curve
     * @return If fade-in duration is set, return true.
     */
    public boolean existsMotionCurveFadeInTime(int curveIndex) {
        return !json.getRoot().get(JsonKey.CURVES.key).get(curveIndex).get(JsonKey.FADE_IN_TIME.key).isNull();
    }

    /**
     * Whether the fade-out duration is set for the motion's curve.
     *
     * @param curveIndex index of curve
     * @return If fade-out duration is set, return true.
     */
    public boolean existsMotionCurveFadeOutTime(int curveIndex) {
        return !json.getRoot().get(JsonKey.CURVES.key).get(curveIndex).get(JsonKey.FADE_OUT_TIME.key).isNull();
    }

    /**
     * Get the fade-in duration of the motion curve.
     *
     * @param curveIndex index of curve
     * @return fade-in duration[s]
     */
    public float getMotionCurveFadeInTime(int curveIndex) {
        return json.getRoot().get(JsonKey.CURVES.key).get(curveIndex).get(JsonKey.FADE_IN_TIME.key).toFloat();
    }

    /**
     * Get the fade-out duration of the motion curve.
     *
     * @param curveIndex index of curve
     * @return fade-out duration[s]
     */
    public float getMotionCurveFadeOutTime(int curveIndex) {
        return json.getRoot().get(JsonKey.CURVES.key).get(curveIndex).get(JsonKey.FADE_OUT_TIME.key).toFloat();
    }

    /**
     * Get the number of segments in the curve of the motion.
     *
     * @param curveIndex index of curve
     * @return number of segments in the curve of the motion
     */
    public int getMotionCurveSegmentCount(int curveIndex) {
        return json.getRoot().get(JsonKey.CURVES.key).get(curveIndex).get(JsonKey.SEGMENTS.key).getList().size();
    }

    /**
     * Get the value of a segment of a motion curve.
     *
     * @param curveIndex index of curve
     * @param segmentIndex index of segment
     * @return value of segment
     */
    public float getMotionCurveSegment(int curveIndex, int segmentIndex) {
        return json.getRoot().get(JsonKey.CURVES.key).get(curveIndex).get(JsonKey.SEGMENTS.key).get(segmentIndex).toFloat();
    }

    /**
     * Get the number of events.
     *
     * @return number of events
     */
    public int getEventCount() {
        return json.getRoot().get(JsonKey.META.key).get(JsonKey.USER_DATA_COUNT.key).toInt();
    }

    /**
     * Get the total number of characters in the event.
     *
     * @return total number of characters in the event
     */
    public int getTotalEventValueSize() {
        return json.getRoot().get(JsonKey.META.key).get(JsonKey.TOTAL_USER_DATA_SIZE.key).toInt();
    }

    /**
     * Get the event duration.
     *
     * @param userDataIndex index of events
     * @return event duration[s]
     */
    public float getEventTime(int userDataIndex) {
        return json.getRoot().get(JsonKey.USER_DATA.key).get(userDataIndex).get(JsonKey.TIME.key).toFloat();
    }

    /**
     * Get the event string.
     *
     * @param userDataIndex index of event
     * @return event strings
     */
    public String getEventValue(int userDataIndex) {
        return json.getRoot().get(JsonKey.USER_DATA.key).get(userDataIndex).get(JsonKey.VALUE.key).getString();
    }

    private enum JsonKey {
        META("Meta"),
        DURATION("Duration"),
        LOOP("Loop"),
        ARE_BEZIERS_RESTRICTED("AreBeziersRestricted"),
        CURVE_COUNT("CurveCount"),
        FPS("Fps"),
        TOTAL_SEGMENT_COUNT("TotalSegmentCount"),
        TOTAL_POINT_COUNT("TotalPointCount"),
        CURVES("Curves"),
        TARGET("Target"),
        ID("Id"),
        FADE_IN_TIME("FadeInTime"),
        FADE_OUT_TIME("FadeOutTime"),
        SEGMENTS("Segments"),
        USER_DATA("UserData"),
        USER_DATA_COUNT("UserDataCount"),
        TOTAL_USER_DATA_SIZE("TotalUserDataSize"),
        TIME("Time"),
        VALUE("Value");

        private final String key;

        JsonKey(String key) {
            this.key = key;
        }
    }

    /**
     * motion3.json data
     */
    private final CubismJson json;
}
