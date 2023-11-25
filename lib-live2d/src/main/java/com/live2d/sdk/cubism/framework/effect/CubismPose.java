/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.effect;

import com.live2d.sdk.cubism.framework.CubismFramework;
import com.live2d.sdk.cubism.framework.id.CubismId;
import com.live2d.sdk.cubism.framework.model.CubismModel;
import com.live2d.sdk.cubism.framework.utils.jsonparser.ACubismJsonValue;
import com.live2d.sdk.cubism.framework.utils.jsonparser.CubismJson;
import com.live2d.sdk.cubism.framework.utils.jsonparser.CubismJsonString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class deals with parts opacity value and settings.
 */
public class CubismPose {
    /**
     * Manage data related parts.
     */
    public static class PartData {
        /**
         * Default constructor
         */
        public PartData() {}

        /**
         * Copy constructor
         *
         * @param partData original part data
         */
        public PartData(PartData partData) {
            partId = partData.partId;
            parameterIndex = partData.parameterIndex;
            partIndex = partData.partIndex;

            linkedParameter.addAll(partData.linkedParameter);
        }

        public void initialize(CubismModel model) {
            parameterIndex = model.getParameterIndex(partId);
            partIndex = model.getPartIndex(partId);

            model.setParameterValue(parameterIndex, 1);
        }

        /**
         * Part ID
         */
        public CubismId partId;
        /**
         * Parameter index
         */
        public int parameterIndex;
        /**
         * Part index
         */
        public int partIndex;
        /**
         * Linked parameters list
         */
        public List<PartData> linkedParameter = new ArrayList<PartData>();
    }

    /**
     * Create a CubismPose instance
     *
     * @param pose3json the byte data of pose3.json
     * @return the created instance
     */
    public static CubismPose create(byte[] pose3json) {
        CubismPose pose = new CubismPose();
        final CubismJson json;
        json = CubismJson.create(pose3json);

        ACubismJsonValue root = json.getRoot();
        Map<CubismJsonString, ACubismJsonValue> rootMap = root.getMap();

        // Set the fade time.
        if (!root.get(JsonTag.FADE_IN.tag).isNull()) {
            pose.fadeTimeSeconds = root.get(JsonTag.FADE_IN.tag).toFloat(DEFAULT_FADE_IN_SECONDS);

            if (pose.fadeTimeSeconds < 0.0f) {
                pose.fadeTimeSeconds = DEFAULT_FADE_IN_SECONDS;
            }
        }

        // Parts group
        ACubismJsonValue poseListInfo = root.get(JsonTag.GROUPS.tag);
        int poseCount = poseListInfo.size();

        for (int poseIndex = 0; poseIndex < poseCount; poseIndex++) {
            ACubismJsonValue idListInfo = poseListInfo.get(poseIndex);
            int idCount = idListInfo.size();
            int groupCount = 0;

            for (int groupIndex = 0; groupIndex < idCount; groupIndex++) {
                ACubismJsonValue partInfo = idListInfo.get(groupIndex);
                PartData partData = setupPartGroup(partInfo);
                pose.partGroups.add(partData);
                groupCount++;
            }
            pose.partGroupCounts.add(groupCount);
        }

        return pose;
    }

    /**
     * Update model's parameters.
     *
     * @param model the target model
     * @param deltaTimeSeconds delta time[s]
     */
    public void updateParameters(final CubismModel model, float deltaTimeSeconds) {
        // If given model is different from previous model, it is required to initialize some parameters.
        if (model != lastModel) {
            reset(model);
        }

        lastModel = model;

        // If a negative time is given, 0 value is set.
        if (deltaTimeSeconds < 0.0f) {
            deltaTimeSeconds = 0.0f;
        }

        int beginIndex = 0;

        for (int i = 0; i < partGroupCounts.size(); i++) {
            int partGroupCount = partGroupCounts.get(i);

            doFade(model, deltaTimeSeconds, beginIndex, partGroupCount);
            beginIndex += partGroupCount;
        }
        copyPartOpacities(model);
    }

    private static PartData setupPartGroup(ACubismJsonValue partInfo) {
        final CubismId parameterId = CubismFramework.getIdManager().getId(partInfo.get(JsonTag.ID.tag).getString());

        final PartData partData = new PartData();
        partData.partId = new CubismId(parameterId);

        ACubismJsonValue link = partInfo.get(JsonTag.LINK.tag);
        if (link != null) {
            setupLinkedPart(partData, link);
        }
        return partData;
    }

    /**
     * Setup linked parts.
     *
     * @param partData part data to be done setting
     * @param linkedListInfo linked parts list information
     */
    private static void setupLinkedPart(PartData partData, ACubismJsonValue linkedListInfo) {
        final int linkCount = linkedListInfo.size();

        for (int index = 0; index < linkCount; index++) {
            final CubismId linkedPartId = CubismFramework.getIdManager().getId(linkedListInfo.get(index).getString());

            PartData linkedPart = new PartData();
            linkedPart.partId = linkedPartId;

            partData.linkedParameter.add(linkedPart);
        }
    }

    /**
     * Initialize display.
     *
     * @param model the target model(Parameters that initial opacity is not 0, the opacity is set to 1.)
     */
    private void reset(final CubismModel model) {
        int beginIndex = 0;

        for (int j = 0; j < partGroupCounts.size(); j++) {
            int groupCount = partGroupCounts.get(j);

            for (int i = beginIndex; i < beginIndex + groupCount; i++) {
                partGroups.get(i).initialize(model);

                final int partsIndex = partGroups.get(i).partIndex;
                final int paramIndex = partGroups.get(i).parameterIndex;

                if (partsIndex < 0) {
                    continue;
                }

                if (i == beginIndex) {
                    model.setPartOpacity(partsIndex, 1.0f);
                    model.setParameterValue(paramIndex, 1.0f);
                }

                final float value =
                    i == beginIndex
                    ? 1.0f
                    : 0.0f;
                model.setPartOpacity(partsIndex, value);
                model.setParameterValue(paramIndex, value);

                List<PartData> link = partGroups.get(i).linkedParameter;
                if (link != null) {
                    for (PartData data : link) {
                        data.initialize(model);
                    }
                }
            }
            beginIndex += groupCount;
        }
    }

    /**
     * Parts opacity is copied and set to linked parts.
     *
     * @param model the target model
     */
    private void copyPartOpacities(CubismModel model) {
        for (int i = 0; i < partGroups.size(); i++) {
            PartData partData = partGroups.get(i);
            if (partData.linkedParameter == null) {
                continue;
            }

            final int partIndex = partData.partIndex;
            final float opacity = model.getPartOpacity(partIndex);

            for (int j = 0; j < partData.linkedParameter.size(); j++) {
                PartData linkedPart = partData.linkedParameter.get(j);

                final int linkedPartIndex = linkedPart.partIndex;

                if (linkedPartIndex < 0) {
                    continue;
                }

                model.setPartOpacity(linkedPartIndex, opacity);
            }
        }
    }

    /**
     * Fade parts
     *
     * @param model the target model
     * @param deltaTimeSeconds delta time[s]
     * @param beginIndex the head index of parts groups done fading
     * @param partGroupCount the number of parts groups done fading
     */
    private void doFade(CubismModel model,
                        float deltaTimeSeconds,
                        int beginIndex,
                        int partGroupCount) {
        int visiblePartIndex = -1;
        float newOpacity = 1.0f;

        // Get parts displayed now.
        for (int i = beginIndex; i < beginIndex + partGroupCount; i++) {
            final int paramIndex = partGroups.get(i).parameterIndex;

            if (model.getParameterValue(paramIndex) > EPSILON) {
                if (visiblePartIndex >= 0) {
                    break;
                }

                newOpacity = calculateOpacity(model, i, deltaTimeSeconds);
                visiblePartIndex = i;
            }
        }

        if (visiblePartIndex < 0) {
            visiblePartIndex = 0;
            newOpacity = 1.0f;
        }

        // Set opacity to displayed, and non-displayed parts
        for (int i = beginIndex; i < beginIndex + partGroupCount; i++) {
            final int partsIndex = partGroups.get(i).partIndex;

            // Setting of displayed parts
            if (visiblePartIndex == i) {
                model.setPartOpacity(partsIndex, newOpacity);
            }
            // Setting of non-displayed parts
            else {
                final float opacity = model.getPartOpacity(partsIndex);
                final float result = calcNonDisplayedPartsOpacity(opacity, newOpacity);
                model.setPartOpacity(partsIndex, result);
            }
        }
    }

    /**
     * Calculate the new part opacity. This method is used at fading.
     *
     * @param model target model
     * @param index part index
     * @param deltaTime delta time[s]
     * @return new calculated opacity
     */
    private float calculateOpacity(CubismModel model, int index, float deltaTime) {
        final int partIndex = partGroups.get(index).partIndex;
        float opacity = model.getPartOpacity(partIndex);

        opacity += deltaTime / fadeTimeSeconds;

        if (opacity > 1.0f) {
            opacity = 1.0f;
        }

        return opacity;
    }

    /**
     * Calculate opacity of non-displayed parts.
     *
     * @param currentOpacity current part opacity
     * @param newOpacity calculated opacity
     * @return opacity for non-displayed part
     */
    private float calcNonDisplayedPartsOpacity(float currentOpacity, final float newOpacity) {
        final float PHI = 0.5f;
        final float BACK_OPACITY_THRESHOLD = 0.15f;

        float a1; // opacity to be calculated
        if (newOpacity < PHI) {
            // Linear equation passing through (0,1),(PHI,PHI)
            a1 = newOpacity * (PHI - 1.0f) / (PHI + 1.0f);
        } else {
            a1 = (1 - newOpacity) * PHI / (1.0f - PHI);
        }

        final float backOpacity = (1.0f - a1) * (1.0f - newOpacity);

        if (backOpacity > BACK_OPACITY_THRESHOLD) {
            a1 = 1.0f - BACK_OPACITY_THRESHOLD / (1.0f - newOpacity);
        }

        // The opacity is raised if the opacity is larger(more thicken) than the calculated opacity.
        if (currentOpacity > a1) {
            currentOpacity = a1;
        }

        return currentOpacity;
    }

    /**
     * Epsilon value
     */
    private static final float EPSILON = 0.001f;
    /**
     * Default fade-in duration[s]
     */
    private static final float DEFAULT_FADE_IN_SECONDS = 0.5f;

    // Tags of Pose3.json
    private enum JsonTag {
        FADE_IN("FadeInTime"),
        LINK("Link"),
        GROUPS("Groups"),
        ID("Id");

        private final String tag;

        JsonTag(String tag) {
            this.tag = tag;
        }
    }

    /**
     * Parts group
     */
    private final List<PartData> partGroups = new ArrayList<PartData>();
    /**
     * Each parts group number
     */
    private final List<Integer> partGroupCounts = new ArrayList<Integer>();
    /**
     * Fade time[s]
     */
    private float fadeTimeSeconds = DEFAULT_FADE_IN_SECONDS;
    /**
     * Previous operated model
     */
    private CubismModel lastModel;
}
