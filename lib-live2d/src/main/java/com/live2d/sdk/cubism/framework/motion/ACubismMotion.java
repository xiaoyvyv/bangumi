/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.motion;

import com.live2d.sdk.cubism.framework.id.CubismId;
import com.live2d.sdk.cubism.framework.math.CubismMath;
import com.live2d.sdk.cubism.framework.model.CubismModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstract base class for motion.
 * This class manages motion playback by MotionQueueManager.
 */
public abstract class ACubismMotion {
    /**
     * Update model's parameters.
     *
     * @param model target model
     * @param motionQueueEntry motion managed by CubismMotionQueueManager
     * @param userTimeSeconds total delta time[s]
     */
    public void updateParameters(
        CubismModel model,
        CubismMotionQueueEntry motionQueueEntry,
        float userTimeSeconds
    ) {
        if (!motionQueueEntry.isAvailable() || motionQueueEntry.isFinished()) {
            return;
        }

        if (!motionQueueEntry.isStarted()) {
            motionQueueEntry.isStarted(true);

            // Record the start time of the motion.
            motionQueueEntry.setStartTime(userTimeSeconds - offsetSeconds);
            // Record the start time of fade-in
            motionQueueEntry.setFadeInStartTime(userTimeSeconds);

            final float duration = getDuration();

            // Deal with the case where the status is set "end" before it has started.
            if (motionQueueEntry.getEndTime() < 0) {
                // If duration == -1, loop motion.
                float endTime = (duration <= 0)
                                ? -1
                                : motionQueueEntry.getStartTime() + duration;
                motionQueueEntry.setEndTime(endTime);
            }
        }

        //---- Fade in/out processing. ----
        // Easing with a simple sin function.
        final float fadeIn = fadeInSeconds == 0.0f
                             ? 1.0f
                             : CubismMath.getEasingSine((userTimeSeconds - motionQueueEntry.getFadeInStartTime()) / fadeInSeconds);

        final float fadeOut = (fadeOutSeconds == 0.0f || motionQueueEntry.getEndTime() < 0.0f)
                              ? 1.0f
                              : CubismMath.getEasingSine((motionQueueEntry.getEndTime() - userTimeSeconds) / fadeOutSeconds);

        // Percentage to be multiplied by the current value.
        float fadeWeight = weight * fadeIn * fadeOut;
        motionQueueEntry.setState(userTimeSeconds, fadeWeight);

        assert (0.0f <= fadeWeight && fadeWeight <= 1.0f);

        //---- Loop through all parameter IDs. ----
        doUpdateParameters(model, userTimeSeconds, fadeWeight, motionQueueEntry);

        // Post-processing.
        // Set the end flag when the end time has passed (CubismMotionQueueManager).
        final float endTime = motionQueueEntry.getEndTime();
        if ((endTime > 0) && (endTime < userTimeSeconds)) {
            motionQueueEntry.isFinished(true);      // Termination
        }
    }


    /**
     * Set the time it takes to fade in.
     *
     * @param fadeInSeconds time for fade in [s]
     */
    public void setFadeInTime(float fadeInSeconds) {
        this.fadeInSeconds = fadeInSeconds;
    }

    /**
     * Get the time it takes to fade in.
     *
     * @return time for fade in[s]
     */
    public float getFadeInTime() {
        return fadeInSeconds;
    }

    /**
     * Set a time it takes to fade out.
     *
     * @param fadeOutSeconds time for fade out[s]
     */
    public void setFadeOutTime(float fadeOutSeconds) {
        this.fadeOutSeconds = fadeOutSeconds;
    }

    /**
     * Get the time it takes to fade out.
     *
     * @return time for fade out[s]
     */
    public float getFadeOutTime() {
        return fadeOutSeconds;
    }

    /**
     * Set the weight to be applied to the motion.
     *
     * @param weight weight(0.0 - 1.0)
     */
    public void setWeight(float weight) {
        this.weight = weight;
    }

    /**
     * Get the weight to be applied to the motion.
     *
     * @return weight(0.0 - 1.0)
     */
    public float getWeight() {
        return weight;
    }

    /**
     * Get the duration of the motion.
     *
     * @return duration of motion[s]
     * (If it is a loop, "-1".
     * Override if it is not a loop.
     * If the value is positive, the process ends at the time it is retrieved.
     * When the value is "-1", the process will not end unless there is a stop command from outside)
     */
    public float getDuration() {
        return -1.0f;
    }

    /**
     * Get the duration of one motion loop.
     *
     * @return duration of one motion loop[s]
     * <p>
     * (If it does not loop, it returns the same value as GetDuration().
     * Return "-1" in case the duration of one loop cannot be defined (e.g., a subclass that keeps moving programmatically)).
     */
    public float getLoopDuration() {
        return -1.0f;
    }

    /**
     * Set the start time for motion playback.
     *
     * @param offsetSeconds start time for motion playback[s]
     */
    public void setOffsetTime(float offsetSeconds) {
        this.offsetSeconds = offsetSeconds;
    }

    /**
     * Check for event firing.
     * The input time reference is set to zero at the called motion timing.
     *
     * @param beforeCheckTimeSeconds last event check time [s]
     * @param motionTimeSeconds playback time this time [s]
     * @return list of events that have fired
     */
    public List<String> getFiredEvent(float beforeCheckTimeSeconds, float motionTimeSeconds) {
        if (areFiredEventValuesChanged) {
            cachedImmutableFiredEventValues = Collections.unmodifiableList(firedEventValues);
            areFiredEventValuesChanged = false;
        }
        return cachedImmutableFiredEventValues;
    }

    /**
     * Registers a motion playback end callback.
     * It is called when the isFinished flag is set.
     * It is not called in the following states:
     * 1. when the currently playing motion is set as "loop"
     * 2. when null is registered in the callback
     *
     * @param onFinishedMotionHandler end-of-motion playback callback function
     */
    public void setFinishedMotionHandler(IFinishedMotionCallback onFinishedMotionHandler) {
        onFinishedMotion = onFinishedMotionHandler;
    }

    /**
     * Get the end-of-motion playback callback function.
     *
     * @return registered end-of-motion playback callback function; if null, no function is registered
     */
    public IFinishedMotionCallback getFinishedMotionCallback() {
        return onFinishedMotion;
    }

    /**
     * Check to see if a transparency curve exists.
     *
     * @return return true if the key exists
     */
    public boolean isExistModelOpacity() {
        return false;
    }


    /**
     * Return the index of the transparency curve.
     *
     * @return success: index of the transparency curve
     */
    public int getModelOpacityIndex() {
        return -1;
    }

    /**
     * Return the ID of the transparency curve.
     *
     * @return success: atransparency curve
     */
    public CubismId getModelOpacityId(int index) {
        return null;
    }

    /**
     * Perform parameter updates for the model.
     *
     * @param model target model
     * @param userTimeSeconds total delta time[s]
     * @param weight weight of motion
     * @param motionQueueEntry motion managed by CubismMotionQueueManager
     */
    protected abstract void doUpdateParameters(
        CubismModel model,
        float userTimeSeconds,
        float weight,
        CubismMotionQueueEntry motionQueueEntry
    );

    /**
     * 指定時間の透明度の値を返す。
     * NOTE: 更新後の値を取るには`updateParameters()` の後に呼び出す。
     *
     * @return success : モーションの当該時間におけるOpacityの値
     */
    protected float getModelOpacityValue() {
        return 1.0f;
    }

    /**
     * Time for fade-in [s]
     */
    protected float fadeInSeconds = -1.0f;
    /**
     * Time for fade-out[s]
     */
    protected float fadeOutSeconds = -1.0f;
    /**
     * Weight of motion
     */
    protected float weight = 1.0f;
    /**
     * Start time for motion playback[s]
     */
    protected float offsetSeconds;
    /**
     * List of events that have fired
     */
    protected List<String> firedEventValues = new ArrayList<String>();

    protected boolean areFiredEventValuesChanged = true;

    protected List<String> cachedImmutableFiredEventValues;

    /**
     * End-of-motion playback callback function
     */
    protected IFinishedMotionCallback onFinishedMotion;
}
