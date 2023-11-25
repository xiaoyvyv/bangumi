/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework;

import com.live2d.sdk.cubism.framework.id.CubismId;
import com.live2d.sdk.cubism.framework.utils.jsonparser.ACubismJsonValue;
import com.live2d.sdk.cubism.framework.utils.jsonparser.CubismJson;
import com.live2d.sdk.cubism.framework.utils.jsonparser.CubismJsonString;

import java.util.Map;

/**
 * This interface deal with model setting information.
 * <p>
 * The class implemented this interface can deal with model setting info.
 */
public interface ICubismModelSetting {
    /**
     * Get model3.json.
     *
     * @return model3.json
     */
    CubismJson getJson();

    /**
     * Get the name of Moc file.
     *
     * @return name of Moc file
     */
    String getModelFileName();

    /**
     * Get a number of textures model uses.
     *
     * @return number of textures
     */
    int getTextureCount();

    /**
     * Get the name of directory is located textures.
     *
     * @return name of directory is located textures.
     */
    String getTextureDirectory();

    /**
     * Get the name of textures used by model.
     *
     * @param index index value of array
     * @return the name of textures
     */
    String getTextureFileName(int index);

    /**
     * Get the number of collision detection set to model.
     *
     * @return the number of collision detection set to model
     */
    int getHitAreasCount();

    /**
     * Get the ID set to collision detection.
     *
     * @param index index value of array
     * @return the ID set to collision detection
     */
    CubismId getHitAreaId(int index);

    /**
     * Get the name set to collision detection
     *
     * @param index index value of array
     * @return the name set to collision detection
     */
    String getHitAreaName(int index);

    /**
     * Get the name of the physics setting file.
     *
     * @return the name of the physics setting file.
     */
    String getPhysicsFileName();

    /**
     * Get the name of parts switching setting file.
     *
     * @return Parts switching setting file
     */
    String getPoseFileName();

    /**
     * Get the name of cdi3.json file.
     *
     * @return
     */
    String getDisplayInfoFileName();

    /**
     * Get the number of expression setting file.
     *
     * @return the number of expression setting file
     */
    int getExpressionCount();

    /**
     * Get the name(Alias) identifying expression setting file.
     *
     * @param index index value of array
     * @return the name of expression
     */
    String getExpressionName(int index);

    /**
     * Get the name of expression setting file.
     *
     * @param index index value of array
     * @return the name of expression setting file
     */
    String getExpressionFileName(int index);

    /**
     * Get the number of motion groups.
     *
     * @return the number of motion groups
     */
    int getMotionGroupCount();

    String getMotionGroupName(int index);

    /**
     * Get the number of motion included in motion group given to this method by an argument.
     *
     * @param groupName the name of motion group
     * @return the number of motion included in the motion group.
     */
    int getMotionCount(final String groupName);

    /**
     * Get the name of the motion file from group name and index value.
     *
     * @param groupName the name of the motion group
     * @param index index value of array
     * @return the name of motion file
     */
    String getMotionFileName(final String groupName, int index);

    /**
     * Get the name of sound file mapped to the motion.
     *
     * @param groupName the name of motion group
     * @param index index value of arrayz
     * @return the name of sound file
     */
    String getMotionSoundFileName(final String groupName, int index);

    /**
     * Get the fade-in processing time at start of motion
     *
     * @param groupName the name of motion group
     * @param index index value  of array
     * @return fade-in processing time[s]
     */
    float getMotionFadeInTimeValue(final String groupName, int index);

    /**
     * Get fade-out processing time at end of motion.
     *
     * @param groupName the name of motion group
     * @param index index value of array
     * @return fade-out processing time[s]
     */
    float getMotionFadeOutTimeValue(final String groupName, int index);

    /**
     * Get the name of userdata file.
     *
     * @return the name of userdata file
     */
    String getUserDataFile();

    /**
     * Get the layout information.
     *
     * @return if layout information exists, return true
     */
    boolean getLayoutMap(Map<String, Float> outLayoutMap);

    /**
     * Get the number of parameters associated to eye blink.
     *
     * @return the number of parameters associated to eye blink
     */
    int getEyeBlinkParameterCount();

    /**
     * Get the parameter ID associated to eye blink.
     *
     * @param index index value of array
     * @return parameter ID
     */
    CubismId getEyeBlinkParameterId(int index);

    /**
     * Get the number of parameters associated to lip sync.
     *
     * @return the number of parameters associated to lip sync
     */
    int getLipSyncParameterCount();

    /**
     * Get the parameter ID associated to lip sync.
     *
     * @param index index value of array
     * @return parameter ID
     */
    CubismId getLipSyncParameterId(int index);
}
