/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.motion;

/**
 * Manager class for each motion being played by CubismMotionQueueManager.
 */
class CubismMotionQueueEntry {
    /**
     * Get the motion.
     *
     * @return motion instance
     */
    public ACubismMotion getMotion() {
        return motion;
    }

    /**
     * Set a motion instance.
     *
     * @param motion motion instance
     */
    public void setMotion(ACubismMotion motion) {
        this.motion = motion;
    }


    /**
     * Set the fade-out to the start state.
     *
     * @param fadeOutSeconds time it takes to fade out[s]
     */
    public void setFadeOut(float fadeOutSeconds) {
        this.fadeOutSeconds = fadeOutSeconds;
        isTriggeredFadeOut = true;
    }

    /**
     * Start fade-out.
     *
     * @param fadeOutSeconds time it takes to fade out[s]
     * @param userTimeSeconds total delta time[s]
     */
    public void startFadeOut(float fadeOutSeconds, float userTimeSeconds) {
        final float newEndTimeSeconds = userTimeSeconds + fadeOutSeconds;
        isTriggeredFadeOut = true;

        if (endTimeSeconds < 0.0f || newEndTimeSeconds < endTimeSeconds) {
            endTimeSeconds = newEndTimeSeconds;
        }
    }

    /**
     * Get the start time of the motion.
     *
     * @return start time of the motion[s]
     */
    public float getStartTime() {
        return startTimeSeconds;
    }

    /**
     * Set the start time of the motion.
     *
     * @param startTime start time of the motion[s]
     */
    public void setStartTime(float startTime) {
        startTimeSeconds = startTime;
    }

    /**
     * Get the start time of the fade-in.
     *
     * @return start time of the fade-in[s]
     */
    public float getFadeInStartTime() {
        return fadeInStartTimeSeconds;
    }

    /**
     * Set the start time of fade-in.
     *
     * @param startTime start time of fade-in[s]
     */
    public void setFadeInStartTime(float startTime) {
        fadeInStartTimeSeconds = startTime;
    }

    /**
     * Get the end time of the fade-in.
     *
     * @return end time of the fade-in
     */
    public float getEndTime() {
        return endTimeSeconds;
    }

    /**
     * Set end time of the motion.
     *
     * @param endTime end time of the motion[s]
     */
    public void setEndTime(float endTime) {
        endTimeSeconds = endTime;
    }

    /**
     * Whether the motion is finished.
     *
     * @return If the motion is finished, return true.
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Set end state of the motion.
     *
     * @param isMotionFinished If true, set the motion to the end state.
     */
    public void isFinished(boolean isMotionFinished) {
        finished = isMotionFinished;
    }

    /**
     * Whether the motion is started.
     *
     * @return If the motion is started, return true.
     */
    public boolean isStarted() {
        return started;
    }

    /**
     * Set start state of the motion.
     *
     * @param isMotionStarted If true, set the motion to the start state.
     */
    public void isStarted(boolean isMotionStarted) {
        started = isMotionStarted;
    }

    /**
     * Get the status of the motion.(Enable/Disable)
     *
     * @return If the motion is valid, return true.
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Set the status of the motion.(Enable/Disable)
     *
     * @param isMotionAvailable If it is true, the motion is enabled.
     */
    public void isAvailable(boolean isMotionAvailable) {
        available = isMotionAvailable;
    }

    /**
     * Set the state of the motion.
     *
     * @param timeSeconds current time[s]
     * @param weight weight of motion
     */
    public void setState(float timeSeconds, float weight) {
        stateTimeSeconds = timeSeconds;
        stateWeight = weight;
    }

    /**
     * Get the current time of the motion.
     *
     * @return current time of the motion.
     */
    public float getStateTime() {
        return stateTimeSeconds;
    }

    /**
     * Get the weight of the motion.
     *
     * @return weight of the motion
     */
    public float getStateWeight() {
        return stateWeight;
    }

    /**
     * Get the time when the last event firing was checked.
     *
     * @return time when the last event firing was checked[s]
     */
    public float getLastCheckEventTime() {
        return lastEventCheckSeconds;
    }

    /**
     * Set the time when the last event firing was checked.
     *
     * @param checkTime time when the last event firing was checked[s]
     */
    public void setLastCheckEventTime(float checkTime) {
        lastEventCheckSeconds = checkTime;
    }

    /**
     * Get the starting status of the fade-out.
     *
     * @return Whether fade out is started
     */
    public boolean isTriggeredFadeOut() {
        return isTriggeredFadeOut;
    }

    /**
     * Get fade-out duration.
     *
     * @return fade-out duration
     */
    public float getFadeOutSeconds() {
        return fadeOutSeconds;
    }

    /**
     * motion
     */
    private ACubismMotion motion;
    /**
     * Enable flag
     */
    private boolean available = true;
    /**
     * finished flag
     */
    private boolean finished;
    /**
     * start flag(0.9.00 or later)
     */
    private boolean started;
    /**
     * Motion playback start time[s]
     */
    private float startTimeSeconds = -1.0f;
    /**
     * Fade-in start time[s] (When in a loop, only the first time.)
     */
    private float fadeInStartTimeSeconds;
    /**
     * Scheduled end time[s]
     */
    private float endTimeSeconds = -1.0f;
    /**
     * state of time[s]
     */
    private float stateTimeSeconds;
    /**
     * state of weight
     */
    private float stateWeight;
    /**
     * last event check time
     */
    private float lastEventCheckSeconds;
    /**
     * fade-out duration of the motion[s]
     */
    private float fadeOutSeconds;
    /**
     * Whether the motion fade-out is started
     */
    private boolean isTriggeredFadeOut;
}
