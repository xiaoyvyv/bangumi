/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.effect;

import com.live2d.sdk.cubism.framework.ICubismModelSetting;
import com.live2d.sdk.cubism.framework.id.CubismId;
import com.live2d.sdk.cubism.framework.model.CubismModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class offers auto eyeblink function.
 */
public class CubismEyeBlink {
    /**
     * Eyeblink states
     */
    public enum EyeState {
        /**
         * Initial state
         */
        FIRST,
        /**
         * Non-eyeblink state
         */
        INTERVAL,
        /**
         * Closing-eye state
         */
        CLOSING,
        /**
         * Closed-eye state
         */
        CLOSED,
        /**
         * Opening-eye state
         */
        OPENING
    }

    /**
     * Create a CubismEyeBlink instance.
     * (If an argument is not passed, this function creates empty instance.)
     *
     * @return modelSetting model setting information
     */
    public static CubismEyeBlink create() {
        return new CubismEyeBlink(null);
    }

    /**
     * Create instance.
     *
     * @param modelSetting model setting information
     * @return instance
     */
    public static CubismEyeBlink create(ICubismModelSetting modelSetting) {
        return new CubismEyeBlink(modelSetting);
    }

    /**
     * Update model's parameters.
     *
     * @param model the target model
     * @param deltaTimeSeconds delta time[s]
     */
    public void updateParameters(final CubismModel model, final float deltaTimeSeconds) {
        userTimeSeconds += deltaTimeSeconds;

        switch (blinkingState) {
            case CLOSING:
                updateParametersClosing(model);
                break;
            case CLOSED:
                updateParametersClosed(model);
                break;
            case OPENING:
                updateParametersOpening(model);
                break;
            case INTERVAL:
                updateParametersInterval(model);
                break;
            case FIRST:
            default:
                updateParametersFirst(model);
                break;
        }
    }

    /**
     * Get the blink interval.
     *
     * @param blinkingInterval eye-blinking interval[s]
     */
    public void setBlinkingInterval(float blinkingInterval) {
        blinkingIntervalSeconds = blinkingInterval;
    }

    /**
     * Set eye-blink motion parameters.
     *
     * @param closing a duration of the motion at closing eye[s]
     * @param closed a duration at closing eye[s]
     * @param opening a duration of the motion at opening eye[s]
     */
    public void setBlinkingSettings(float closing, float closed, float opening) {
        closingSeconds = closing;
        closedSeconds = closed;
        openingSeconds = opening;
    }

    /**
     * Set a parameter IDs list to blink.
     *
     * @param parameterIds A parameter IDs list
     * @throws IllegalArgumentException if an argument is null
     */
    public void setParameterIds(List<CubismId> parameterIds) {
        if (parameterIds == null) {
            throw new IllegalArgumentException("parameterIds is null.");
        }
        this.parameterIds = parameterIds;
    }

    /**
     * Get the parameter IDs list to eye-blink.
     * <p>This method creates all new elements with the parameter ID and adds them to the list and returns them.
     * Therefore, it is recommended that this method not be used where it is executed every frame, as it may affect performance.
     *
     * @return A parameter IDs list
     */
    public List<CubismId> getParameterIds() {
        // If there is a change in the ids list, the read-only list is made.
        if (areIdsListChanged) {
            cachedImmutableIdsList = Collections.unmodifiableList(parameterIds);
            areIdsListChanged = false;
        }
        return cachedImmutableIdsList;
    }

    /**
     * This constant is becomes "true" if the eye blinking parameter specified by ID is specified to close when the value is 0, and "false" if the parameter is specified to close when the value is 1.
     */
    private static final boolean CLOSE_IF_ZERO = true;

    /**
     * Update model parameters when _blinkingState is CLOSING.
     *
     * @param model target model
     */
    private void updateParametersClosing(CubismModel model) {
        float time = (userTimeSeconds - stateStartTimeSeconds) / closingSeconds;

        if (time >= 1.0f) {
            time = 1.0f;
            blinkingState = EyeState.CLOSED;
            stateStartTimeSeconds = userTimeSeconds;
        }

        float parameterValue = 1.0f - time;
        setParameterValueToAllIds(model, parameterValue);
    }

    /**
     * Update model parameters when _blinkingState is CLOSED.
     *
     * @param model target model
     */
    private void updateParametersClosed(CubismModel model) {
        float time = (userTimeSeconds - stateStartTimeSeconds) / closedSeconds;

        if (time >= 1.0f) {
            blinkingState = EyeState.OPENING;
            stateStartTimeSeconds = userTimeSeconds;
        }

        float parameterValue = 0.0f;
        setParameterValueToAllIds(model, parameterValue);
    }

    /**
     * Update model parameters when _blinkingState is OPENING.
     *
     * @param model target model
     */
    private void updateParametersOpening(CubismModel model) {
        float time = (userTimeSeconds - stateStartTimeSeconds) / openingSeconds;

        if (time >= 1.0f) {
            time = 1.0f;
            blinkingState = EyeState.INTERVAL;
            nextBlinkingTime = determineNextBlinkingTiming();
        }

        float parameterValue = time;
        setParameterValueToAllIds(model, parameterValue);
    }

    /**
     * Update model parameters when _blinkingState is INTERVAL.
     *
     * @param model target model
     */
    private void updateParametersInterval(CubismModel model) {
        if (nextBlinkingTime < userTimeSeconds) {
            blinkingState = EyeState.CLOSING;
            stateStartTimeSeconds = userTimeSeconds;
        }

        float parameterValue = 1.0f;
        setParameterValueToAllIds(model, parameterValue);
    }

    /**
     * Update model parameters when _blinkingState is FIRST.
     *
     * @param model target model
     */
    private void updateParametersFirst(CubismModel model) {
        blinkingState = EyeState.INTERVAL;
        nextBlinkingTime = determineNextBlinkingTiming();

        float parameterValue = 1.0f;
        setParameterValueToAllIds(model, parameterValue);
    }

    /**
     * Set the given value for all IDs.
     * CLOSE_IF_ZERO constant is "false", given argument is reversed(multiplied -1).
     *
     * @param model target model
     * @param value value to be set
     */
    private void setParameterValueToAllIds(CubismModel model, float value) {
        if (!CubismEyeBlink.CLOSE_IF_ZERO) {
            value *= -1;
        }
        for (int i = 0; i < parameterIds.size(); i++) {
            CubismId id = parameterIds.get(i);
            model.setParameterValue(id, value);
        }
    }

    /**
     * Decide next eye blinking timing.
     *
     * @return the time of next eye blink
     */
    private float determineNextBlinkingTiming() {
        final float r = (float) Math.random();
        return userTimeSeconds + r * (2.0f * blinkingIntervalSeconds - 1.0f);
    }

    /**
     * private constructor
     *
     * @param modelSetting model setting information
     */
    private CubismEyeBlink(ICubismModelSetting modelSetting) {
        if (modelSetting == null) {
            return;
        }

        for (int i = 0; i < modelSetting.getEyeBlinkParameterCount(); i++) {
            CubismId eyeBlinkId = modelSetting.getEyeBlinkParameterId(i);

            if (eyeBlinkId != null) {
                parameterIds.add(eyeBlinkId);
            }
        }
    }

    /**
     * current blinking state
     */
    private EyeState blinkingState = EyeState.FIRST;
    /**
     * target parameter IDs list
     */
    private List<CubismId> parameterIds = new ArrayList<CubismId>();
    /**
     * If the elemtns of _parameterIds are changed, this flag becomes true.
     */
    private boolean areIdsListChanged = true;
    /**
     * List of _parameterIds made read-only by the Collections.unmodifiableList method. This class's getter returns this immutable list.
     */
    private List<CubismId> cachedImmutableIdsList;
    /**
     * next blinking time[s]
     */
    private float nextBlinkingTime;
    /**
     * time at starting current state[s]
     */
    private float stateStartTimeSeconds;
    /**
     * interval of eye blink
     */
    private float blinkingIntervalSeconds = 4.0f;
    /**
     * duration of the motion at closing eye[s]
     */
    private float closingSeconds = 0.1f;
    /**
     * duration at the closing eye[s]
     */
    private float closedSeconds = 0.05f;
    /**
     * duration of the motion at opening eye[s]
     */
    private float openingSeconds = 0.15f;
    /**
     * total elapsed time[s]
     */
    private float userTimeSeconds;
}
