/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.motion;

import com.live2d.sdk.cubism.framework.id.CubismId;

import java.util.ArrayList;
import java.util.List;

/**
 * Internal data used by the CubismMotion class.
 */
class CubismMotionInternal {
    /**
     * Type of motion curve.
     */
    public enum CubismMotionCurveTarget {
        /**
         * motion curve for the model
         */
        MODEL,
        /**
         * motion curve for parameters
         */
        PARAMETER,
        /**
         * motion curve for part opacity
         */
        PART_OPACITY
    }

    /**
     * Type of motion curve segment.
     */
    public enum CubismMotionSegmentType {
        /**
         * linear
         */
        LINEAR,
        /**
         * bezier curve
         */
        BEZIER,
        /**
         * step
         */
        STEPPED,
        /**
         * inverse step
         */
        INVERSESTEPPED
    }

    /**
     * Motion curve control points.
     */
    public static class CubismMotionPoint {
        /**
         * time[s]
         */
        public float time;
        /**
         * value
         */
        public float value;
    }

    /**
     * Segment of motion curve.
     */
    public static class CubismMotionSegment {
        /**
         * used evaluation function
         */
        public CsmMotionSegmentEvaluationFunction evaluator;
        /**
         * index to the first segment
         */
        public int basePointIndex;
        /**
         * type of segment
         */
        public CubismMotionSegmentType segmentType = CubismMotionSegmentType.LINEAR;
    }

    /**
     * Motion curve
     */
    public static class CubismMotionCurve {
        /**
         * type of curve
         */
        public CubismMotionCurveTarget type = CubismMotionCurveTarget.MODEL;
        /**
         * curve ID
         */
        public CubismId id;
        /**
         * number of segments
         */
        public int segmentCount;
        /**
         * index to the first segment
         */
        public int baseSegmentIndex;
        /**
         * time for fade-in[s]
         */
        public float fadeInTime;
        /**
         * time for fade-out[s]
         */
        public float fadeOutTime;
    }

    /**
     * Motion event
     */
    public static class CubismMotionEvent {
        /**
         * duration of event
         */
        public float fireTime;
        /**
         * value
         */
        public String value;
    }

    /**
     * Motion data
     */
    public static class CubismMotionData {
        /**
         * motion duration
         */
        public float duration;
        /**
         * Whether the motion loops
         */
        public boolean isLooped;
        /**
         * number of curves
         */
        public int curveCount;
        /**
         * number of UserData
         */
        public int eventCount;
        /**
         * framerate per second
         */
        public float fps;
        /**
         * list of curves
         */
        public List<CubismMotionCurve> curves = new ArrayList<CubismMotionCurve>();
        /**
         * list of segments
         */
        public List<CubismMotionSegment> segments = new ArrayList<CubismMotionSegment>();
        /**
         * list of points
         */
        public List<CubismMotionPoint> points = new ArrayList<CubismMotionPoint>();
        /**
         * list of events
         */
        public List<CubismMotionEvent> events = new ArrayList<CubismMotionEvent>();
    }

    /**
     * For strategy pattern.
     */
    public interface CsmMotionSegmentEvaluationFunction {
        float evaluate(float time, int basePointIndex);
    }
}
