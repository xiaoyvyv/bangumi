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
import com.live2d.sdk.cubism.framework.utils.CubismDebug;

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
     * @param model            target model
     * @param motionQueueEntry motion managed by CubismMotionQueueManager
     * @param userTimeSeconds  total delta time[s]
     */
    public void updateParameters(
            CubismModel model,
            CubismMotionQueueEntry motionQueueEntry,
            float userTimeSeconds
    ) {
        if (!motionQueueEntry.isAvailable() || motionQueueEntry.isFinished()) {
            return;
        }

        setupMotionQueueEntry(motionQueueEntry, userTimeSeconds);

        float fadeWeight = updateFadeWeight(motionQueueEntry, userTimeSeconds);

        //---- 全てのパラメータIDをループする ----
        doUpdateParameters(model, userTimeSeconds, fadeWeight, motionQueueEntry);

        // 後処理
        // 終了時刻を過ぎたら終了フラグを立てる（CubismMotionQueueManager）
        if (motionQueueEntry.getEndTime() > 0.0f && motionQueueEntry.getEndTime() < userTimeSeconds) {
            motionQueueEntry.isFinished(true);      // 終了
        }
    }

    /**
     * モーションの再生を開始するためのセットアップを行う。
     *
     * @param motionQueueEntry CubismMotionQueueManagerによって管理されるモーション
     * @param userTimeSeconds  総再生時間（秒）
     */
    public void setupMotionQueueEntry(
            CubismMotionQueueEntry motionQueueEntry,
            final float userTimeSeconds
    ) {
        if (!motionQueueEntry.isAvailable() || motionQueueEntry.isFinished()) {
            return;
        }

        if (motionQueueEntry.isStarted()) {
            return;
        }

        motionQueueEntry.isStarted(true);

        // Record the start time of the motion.
        motionQueueEntry.setStartTime(userTimeSeconds - offsetSeconds);
        // Record the start time of fade-in
        motionQueueEntry.setFadeInStartTime(userTimeSeconds);

        // Deal with the case where the status is set "end" before it has started.
        if (motionQueueEntry.getEndTime() < 0) {
            adjustEndTime(motionQueueEntry);
        }
    }

    /**
     * モーションフェードのウェイト値を更新する。
     *
     * @param motionQueueEntry CubismMotionQueueManagerで管理されているモーション
     * @param userTimeSeconds  デルタ時間の積算値[秒]
     * @return 更新されたウェイト値
     */
    public float updateFadeWeight(CubismMotionQueueEntry motionQueueEntry, float userTimeSeconds) {
        if (motionQueueEntry == null) {
            CubismDebug.cubismLogError("motionQueueEntry is null.");
        }

        float fadeWeight = weight;      // 現在の値と掛け合わせる割合

        // ---- フェードイン・アウトの処理 ----
        // 単純なサイン関数でイージングする。
        final float fadeIn = fadeInSeconds == 0.0f
                ? 1.0f
                : CubismMath.getEasingSine((userTimeSeconds - motionQueueEntry.getFadeInStartTime()) / fadeInSeconds);
        final float fadeOut = (fadeOutSeconds == 0.0f || motionQueueEntry.getEndTime() < 0.0f)
                ? 1.0f
                : CubismMath.getEasingSine((motionQueueEntry.getEndTime() - userTimeSeconds) / fadeOutSeconds);
        fadeWeight = fadeWeight * fadeIn * fadeOut;
        motionQueueEntry.setState(userTimeSeconds, fadeWeight);

        assert (0.0f <= fadeWeight && fadeWeight <= 1.0f);

        return fadeWeight;
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
     * Sets whether the motion should loop.
     *
     * @param loop true to set the motion to loop
     */
    public void setLoop(boolean loop) {
        isLoop = loop;
    }

    /**
     * Checks whether the motion is set to loop.
     *
     * @return true if the motion is set to loop; otherwise false.
     */
    public boolean getLoop() {
        return isLoop;
    }

    /**
     * Sets whether to perform fade-in for looping motion.
     *
     * @param loopFadeIn true to perform fade-in for looping motion
     */
    public void setLoopFadeIn(boolean loopFadeIn) {
        isLoopFadeIn = loopFadeIn;
    }

    /**
     * Checks the setting for fade-in of looping motion.
     *
     * @return true if fade-in for looping motion is set; otherwise false.
     */
    public boolean getLoopFadeIn() {
        return isLoopFadeIn;
    }

    /**
     * Check for event firing.
     * The input time reference is set to zero at the called motion timing.
     *
     * @param beforeCheckTimeSeconds last event check time [s]
     * @param motionTimeSeconds      playback time this time [s]
     * @return list of events that have fired
     */
    public List<String> getFiredEvent(float beforeCheckTimeSeconds, float motionTimeSeconds) {
        return Collections.unmodifiableList(firedEventValues);
    }

    /**
     * Registers a motion playback start callback.
     * It is not called in the following states:
     * 1. when the currently playing motion is set as "loop"
     * 2. when null is registered in the callback
     *
     * @param onBeganMotionHandler start-of-motion playback callback function
     */
    public void setBeganMotionHandler(IBeganMotionCallback onBeganMotionHandler) {
        onBeganMotion = onBeganMotionHandler;
    }

    /**
     * Get the start-of-motion playback callback function.
     *
     * @return registered start-of-motion playback callback function; if null, no function is registered
     */
    public IBeganMotionCallback getBeganMotionCallback() {
        return onBeganMotion;
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
     * @param model            target model
     * @param userTimeSeconds  total delta time[s]
     * @param weight           weight of motion
     * @param motionQueueEntry motion managed by CubismMotionQueueManager
     */
    protected abstract void doUpdateParameters(
            CubismModel model,
            float userTimeSeconds,
            float weight,
            CubismMotionQueueEntry motionQueueEntry
    );

    protected void adjustEndTime(CubismMotionQueueEntry motionQueueEntry) {
        final float duration = getDuration();

        // duration == -1 の場合はループする
        final float endTime = (duration <= 0)
                ? -1
                : motionQueueEntry.getStartTime() + duration;

        motionQueueEntry.setEndTime(endTime);
    }

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
     * Enable/Disable loop
     */
    protected boolean isLoop;
    /**
     * flag whether fade-in is enabled at looping. Default value is true.
     */
    protected boolean isLoopFadeIn = true;

    /**
     * The previous state of `_isLoop`.
     */
    protected boolean previousLoopState = isLoop;
    /**
     * List of events that have fired
     */
    protected List<String> firedEventValues = new ArrayList<String>();

    /**
     * Start-of-motion playback callback function
     */
    protected IBeganMotionCallback onBeganMotion;

    /**
     * End-of-motion playback callback function
     */
    protected IFinishedMotionCallback onFinishedMotion;
}
