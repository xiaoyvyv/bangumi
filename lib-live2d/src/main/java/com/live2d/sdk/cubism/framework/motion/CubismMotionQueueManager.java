/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.motion;

import com.live2d.sdk.cubism.framework.model.CubismModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * The manager class for playing motions. This is used to play ACubismMotion's subclasses such as CubismMotion's motion.
 * <p>
 * If another motion is done "StartMotion()" during playback, the motion changes smoothly to the new motion and the old motion is interrupted. When multiple motions are played back simultaneously (For example, separate motions for facial expressions, body motions, etc.), multiple CubismMotionQueueManager instances are used.
 */
public class CubismMotionQueueManager {
    /**
     * Start the specified motion. If there is already a motion of the same type, set the end flag for the existing motion and start fading out.
     *
     * @param motion motion to start
     * @param userTimeSeconds total user time[s]
     * @return Returns the identification number(OptionalInt) of the motion that has started. Used as an argument for isFinished(), which judges whether an individual motion has been completed. When it cannot be started, it returns an empty OptionalInt.
     */
    public int startMotion(ACubismMotion motion, float userTimeSeconds) {
        if (motion == null) {
            return -1;
        }

        // If there is already motion, flag it as finished.
        for (int i = 0; i < motions.size(); i++) {
            CubismMotionQueueEntry entry = motions.get(i);

            if (entry == null) {
                continue;
            }
            entry.setFadeOut(entry.getMotion().getFadeOutTime());
        }

        CubismMotionQueueEntry motionQueueEntry = new CubismMotionQueueEntry();
        motionQueueEntry.setMotion(motion);

        motions.add(motionQueueEntry);

        return System.identityHashCode(motionQueueEntry);
    }

    public boolean isFinished() {
        // ---- Do processing ----
        // If there is already a motion, flag it as finished.

        // At first, remove the null elements from motions list.
        motions.removeAll(nullSet);

        // motionがnullならば要素をnullとする
        // 後でnull要素を全て削除する。
        for (int i = 0; i < motions.size(); i++) {
            CubismMotionQueueEntry motionQueueEntry = motions.get(i);
            ACubismMotion motion = motionQueueEntry.getMotion();

            if (motion == null) {
                motions.set(i, null);
                continue;
            }

            if (!motionQueueEntry.isFinished()) {
                return false;
            }
        }

        motions.removeAll(nullSet);

        return true;
    }

    public boolean isFinished(int motionQueueEntryNumber) {
        // ---- Do processing ----
        // If there is already a motion, flag it as finished.
        for (int i = 0; i < motions.size(); i++) {
            CubismMotionQueueEntry motionQueueEntry = motions.get(i);

            if (motionQueueEntry == null) {
                continue;
            }

            if (System.identityHashCode(motionQueueEntry) == motionQueueEntryNumber && !motionQueueEntry.isFinished()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Stop all motions.
     */
    public void stopAllMotions() {
        motions.clear();
    }

    /**
     * Get the specified CubismMotionQueueEntry instance.
     *
     * @param motionQueueEntryNumber identification number of the motion
     * @return specified CubismMotionQueueEntry object. If not found, empty Optional is returned.
     */
    public CubismMotionQueueEntry getCubismMotionQueueEntry(int motionQueueEntryNumber) {
        // ---- Do processing ----
        // If there is already a motion, flag it as finished.
        for (int i = 0; i < motions.size(); i++) {
            CubismMotionQueueEntry motionQueueEntry = motions.get(i);

            if (motionQueueEntry == null) {
                continue;
            }

            if (System.identityHashCode(motionQueueEntry) == motionQueueEntryNumber) {
                return motionQueueEntry;
            }
        }
        return null;
    }

    /**
     * Register the callback function to receive events.
     *
     * @param callback callback function
     * @param customData data to be given to callback
     */
    public void setEventCallback(ICubismMotionEventFunction callback, Object customData) {
        eventCallback = callback;
        eventCustomData = customData;
    }

    /**
     * Update the motion and reflect the parameter values to the model.
     *
     * @param model target model
     * @param userTimeSeconds total delta time[s]
     * @return If reflecting the parameter value to the model(the motion is changed.) is successed, return true.
     */
    protected boolean doUpdateMotion(CubismModel model, float userTimeSeconds) {
        boolean isUpdated = false;

        // ---- Do processing ----
        // If there is already a motion, flag it as finished.

        // At first, remove the null elements from motions list.
        motions.removeAll(nullSet);

        for (int i = 0; i < motions.size(); i++) {
            CubismMotionQueueEntry motionQueueEntry = motions.get(i);
            ACubismMotion motion = motionQueueEntry.getMotion();

            if (motion == null) {
                motions.set(i, null);
                continue;
            }

            motion.updateParameters(model, motionQueueEntry, userTimeSeconds);
            isUpdated = true;

            // Inspect user-triggered events.
            final List<String> firedList = motion.getFiredEvent(
                motionQueueEntry.getLastCheckEventTime() - motionQueueEntry.getStartTime(),
                userTimeSeconds - motionQueueEntry.getStartTime());

            for (int j = 0; j < firedList.size(); j++) {
                String event = firedList.get(j);
                eventCallback.apply(this, event, eventCustomData);
            }
            motionQueueEntry.setLastCheckEventTime(userTimeSeconds);

            // If any processes have already been finished, delete them.
            if (motionQueueEntry.isFinished()) {
                motions.set(i, null);
            } else {
                if (motionQueueEntry.isTriggeredFadeOut()) {
                    motionQueueEntry.startFadeOut(motionQueueEntry.getFadeOutSeconds(), userTimeSeconds);
                }
            }
        }

        motions.removeAll(nullSet);

        return isUpdated;
    }

    /**
     * total delta time[s]
     */
    protected float userTimeSeconds;

    /**
     * List of motions
     */
    private final List<CubismMotionQueueEntry> motions = new ArrayList<CubismMotionQueueEntry>();
    /**
     * Callback function
     */
    private ICubismMotionEventFunction eventCallback;
    /**
     * Data to be given to the callback
     */
    private Object eventCustomData;

    // nullが格納されたSet. null要素だけListから排除する際に使用される。
    private final Set<Object> nullSet = Collections.singleton(null);
}
