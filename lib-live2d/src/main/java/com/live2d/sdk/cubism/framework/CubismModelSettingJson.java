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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This class deals with model3.json data.
 */
public class CubismModelSettingJson implements ICubismModelSetting {
    public CubismModelSettingJson(byte[] buffer) {
        CubismJson json;
        json = CubismJson.create(buffer);

        this.json = json;

        if (jsonFrequencyValue != null) {
            jsonFrequencyValue.clear();
        } else {
            jsonFrequencyValue = new ArrayList<ACubismJsonValue>();
        }

        // The order should match the enum FrequentNode
        jsonFrequencyValue.add(this.json.getRoot().get(JsonKey.GROUPS.key));
        jsonFrequencyValue.add(this.json.getRoot().get(JsonKey.FILE_REFERENCES.key).get(JsonKey.MOC.key));
        jsonFrequencyValue.add(this.json.getRoot().get(JsonKey.FILE_REFERENCES.key).get(JsonKey.MOTIONS.key));
        jsonFrequencyValue.add(this.json.getRoot().get(JsonKey.FILE_REFERENCES.key).get(JsonKey.DISPLAY_INFO.key));
        jsonFrequencyValue.add(this.json.getRoot().get(JsonKey.FILE_REFERENCES.key).get(JsonKey.EXPRESSIONS.key));
        jsonFrequencyValue.add(this.json.getRoot().get(JsonKey.FILE_REFERENCES.key).get(JsonKey.TEXTURES.key));
        jsonFrequencyValue.add(this.json.getRoot().get(JsonKey.FILE_REFERENCES.key).get(JsonKey.PHYSICS.key));
        jsonFrequencyValue.add(this.json.getRoot().get(JsonKey.FILE_REFERENCES.key).get(JsonKey.POSE.key));
        jsonFrequencyValue.add(this.json.getRoot().get(JsonKey.HIT_AREAS.key));
    }

    @Override
    public CubismJson getJson() {
        return json;
    }

    @Override
    public String getModelFileName() {
        if (!existsModelFile()) {
            return "";
        }

        return jsonFrequencyValue.get(FrequentNode.MOC.id).getString();
    }

    @Override
    public int getTextureCount() {
        if (!existsTextureFiles()) {
            return 0;
        }

        return json.getRoot()
                   .get(JsonKey.FILE_REFERENCES.key)
                   .get(JsonKey.TEXTURES.key)
                   .size();
    }

    @Override
    public String getTextureDirectory() {
        if (!existsTextureFiles()) {
            return "";
        }

        String rowString = jsonFrequencyValue.get(FrequentNode.TEXTURES.id).get(0).getString();
        return rowString.split("/")[0];
    }

    @Override
    public String getTextureFileName(int index) {
        return jsonFrequencyValue.get(FrequentNode.TEXTURES.id).get(index).getString();
    }

    @Override
    public int getHitAreasCount() {
        if (!existsHitAreas()) {
            return 0;
        }
        return jsonFrequencyValue.get(FrequentNode.HIT_AREAS.id).size();
    }

    @Override
    public CubismId getHitAreaId(int index) {
        return CubismFramework.getIdManager().getId(jsonFrequencyValue.get(FrequentNode.HIT_AREAS.id).get(index).get(JsonKey.ID.key).getString());
    }

    @Override
    public String getHitAreaName(int index) {
        return jsonFrequencyValue.get(FrequentNode.HIT_AREAS.id).get(index).get(JsonKey.NAME.key).getString();
    }

    @Override
    public String getPhysicsFileName() {
        if (!existsPhysicsFile()) {
            return "";
        }
        return jsonFrequencyValue.get(FrequentNode.PHYSICS.id).getString();
    }

    @Override
    public String getPoseFileName() {
        if (!existsPoseFile()) {
            return "";
        }

        return jsonFrequencyValue.get(FrequentNode.POSE.id).getString();
    }

    @Override
    public String getDisplayInfoFileName() {
        if (!existsDisplayInfoFile()) {
            return "";
        }
        return jsonFrequencyValue.get(FrequentNode.DISPLAY_INFO.id).getString();
    }

    @Override
    public int getExpressionCount() {
        if (!existsExpressionFile()) {
            return 0;
        }
        return jsonFrequencyValue.get(FrequentNode.EXPRESSIONS.id).size();

    }

    @Override
    public String getExpressionName(int index) {
        return jsonFrequencyValue.get(FrequentNode.EXPRESSIONS.id).get(index).get(JsonKey.NAME.key).getString();
    }

    @Override
    public String getExpressionFileName(int index) {
        return jsonFrequencyValue.get(FrequentNode.EXPRESSIONS.id).get(index).get(JsonKey.FILEPATH.key).getString();
    }

    @Override
    public int getMotionGroupCount() {
        if (!existsMotionGroups()) {
            return 0;
        }
        return jsonFrequencyValue.get(FrequentNode.MOTIONS.id).size();
    }

    @Override
    public String getMotionGroupName(int index) {
        if (!existsMotionGroups()) {
            return null;
        }
        return jsonFrequencyValue.get(FrequentNode.MOTIONS.id).getKeys().get(index).getString();
    }

    @Override
    public int getMotionCount(final String groupName) {
        if (!existsMotionGroupName(groupName)) {
            return 0;
        }
        return jsonFrequencyValue.get(FrequentNode.MOTIONS.id).get(groupName).size();

    }

    @Override
    public String getMotionFileName(final String groupName, int index) {
        if (!existsMotionGroupName(groupName)) {
            return "";
        }

        return jsonFrequencyValue.get(FrequentNode.MOTIONS.id).get(groupName).get(index).get(JsonKey.FILEPATH.key).getString();
    }

    @Override
    public String getMotionSoundFileName(final String groupName, int index) {
        if (!existsMotionSoundFile(groupName, index)) {
            return "";
        }

        return jsonFrequencyValue.get(FrequentNode.MOTIONS.id).get(groupName).get(index).get(JsonKey.SOUND_PATH.key).getString();
    }

    @Override
    public float getMotionFadeInTimeValue(final String groupName, int index) {
        if (!existsMotionFadeIn(groupName, index)) {
            return -1.0f;
        }

        return jsonFrequencyValue.get(FrequentNode.MOTIONS.id).get(groupName).get(index).get(JsonKey.FADE_IN_TIME.key).toFloat();
    }

    @Override
    public float getMotionFadeOutTimeValue(String groupName, int index) {
        if (!existsMotionFadeOut(groupName, index)) {
            return -1.0f;
        }

        return jsonFrequencyValue.get(FrequentNode.MOTIONS.id).get(groupName).get(index).get(JsonKey.FADE_OUT_TIME.key).toFloat();
    }

    @Override
    public String getUserDataFile() {
        if (!existsUserDataFile()) {
            return "";
        }
        return json.getRoot().get(JsonKey.FILE_REFERENCES.key).get(JsonKey.USER_DATA.key).getString();
    }

    @Override
    public boolean getLayoutMap(Map<String, Float> outLayoutMap) {
        Map<CubismJsonString, ACubismJsonValue> map = json.getRoot().get(JsonKey.LAYOUT.key).getMap();

        if (map == null) {
            return false;
        }

        boolean result = false;
        for (Map.Entry<CubismJsonString, ACubismJsonValue> entry : map.entrySet()) {
            outLayoutMap.put(entry.getKey().getString(), entry.getValue().toFloat());
            result = true;
        }

        return result;
    }

    @Override
    public int getEyeBlinkParameterCount() {
        if (!existsEyeBlinkParameters()) {
            return 0;
        }

        int eyeBlinkParameterCount = 0;
        for (int i = 0; i < jsonFrequencyValue.get(FrequentNode.GROUPS.id).size(); i++) {
            ACubismJsonValue refI = jsonFrequencyValue.get(FrequentNode.GROUPS.id).get(i);

            if (refI.isNull() || refI.isError()) {
                continue;
            }

            if (refI.get(JsonKey.NAME.key).getString().equals(JsonKey.EYE_BLINK.key)) {
                eyeBlinkParameterCount = refI.get(JsonKey.IDS.key).getList().size();
                break;
            }
        }

        return eyeBlinkParameterCount;
    }

    @Override
    public CubismId getEyeBlinkParameterId(int index) {
        if (!existsEyeBlinkParameters()) {
            return null;
        }

        for (int i = 0; i < jsonFrequencyValue.get(FrequentNode.GROUPS.id).size(); ++i) {
            ACubismJsonValue refI = jsonFrequencyValue.get(FrequentNode.GROUPS.id).get(i);

            if (refI.isNull() || refI.isError()) {
                continue;
            }

            if (refI.get(JsonKey.NAME.key).getString().equals(JsonKey.EYE_BLINK.key)) {
                return CubismFramework.getIdManager().getId(refI.get(JsonKey.IDS.key).get(index).getString());
            }
        }
        return null;
    }


    @Override
    public int getLipSyncParameterCount() {
        if (!existsLipSyncParameters()) {
            return 0;
        }


        int lipSyncParameterCount = 0;
        for (int i = 0; i < jsonFrequencyValue.get(FrequentNode.GROUPS.id).size(); i++) {
            ACubismJsonValue refI = jsonFrequencyValue.get(FrequentNode.GROUPS.id).get(i);

            if (refI.isNull() || refI.isError()) {
                continue;
            }

            if (refI.get(JsonKey.NAME.key).getString().equals(JsonKey.LIP_SYNC.key)) {
                lipSyncParameterCount = refI.get(JsonKey.IDS.key).getList().size();
                break;
            }
        }
        return lipSyncParameterCount;
    }

    @Override
    public CubismId getLipSyncParameterId(int index) {
        if (!existsLipSyncParameters()) {
            return null;
        }

        for (int i = 0; i < jsonFrequencyValue.get(FrequentNode.GROUPS.id).size(); i++) {
            ACubismJsonValue refI = jsonFrequencyValue.get(FrequentNode.GROUPS.id).get(i);

            if (refI.isNull() || refI.isError()) {
                continue;
            }

            if (refI.get(JsonKey.NAME.key).getString().equals(JsonKey.LIP_SYNC.key)) {
                return CubismFramework.getIdManager().getId(refI.get(JsonKey.IDS.key).get(index).getString());
            }
        }
        return null;
    }

    /**
     * Enum class for frequent nodes.
     */
    private enum FrequentNode {
        GROUPS(0),
        MOC(1),
        MOTIONS(2),
        DISPLAY_INFO(3),
        EXPRESSIONS(4),
        TEXTURES(5),
        PHYSICS(6),
        POSE(7),
        HIT_AREAS(8);

        private final int id;

        FrequentNode(final int id) {
            this.id = id;
        }
    }

    private enum JsonKey {
        VERSION("Version"),
        FILE_REFERENCES("FileReferences"),
        GROUPS("Groups"),
        LAYOUT("Layout"),
        HIT_AREAS("HitAreas"),

        MOC("Moc"),
        TEXTURES("Textures"),
        PHYSICS("Physics"),
        DISPLAY_INFO("DisplayInfo"),
        POSE("Pose"),
        EXPRESSIONS("Expressions"),
        MOTIONS("Motions"),

        USER_DATA("UserData"),
        NAME("Name"),
        FILEPATH("File"),
        ID("Id"),
        IDS("Ids"),
        TARGET("Target"),

        // Motions
        IDLE("Idle"),
        TAP_BODY("TapBody"),
        PINCH_IN("PinchIn"),
        PINCH_OUT("PinchOut"),
        SHAKE("Shake"),
        FLICK_HEAD("FlickHead"),
        PARAMETER("Parameter"),

        SOUND_PATH("Sound"),
        FADE_IN_TIME("FadeInTime"),
        FADE_OUT_TIME("FadeOutTime"),

        // Layout
        CENTER_X("CenterX"),
        CENTER_Y("CenterY"),
        X("X"),
        Y("Y"),
        WIDTH("Width"),
        HEIGHT("Height"),

        LIP_SYNC("LipSync"),
        EYE_BLINK("EyeBlink"),

        INIT_PARAMETER("init_param"),
        INIT_PARTS_VISIBLE("init_parts_visible"),
        VAL("val");

        private final String key;

        JsonKey(String key) {
            this.key = key;
        }
    }


    // キーが存在するかどうかのチェック
    private boolean existsModelFile() {
        ACubismJsonValue node = jsonFrequencyValue.get(FrequentNode.MOC.id);
        return !node.isNull() && !node.isError();
    }

    private boolean existsTextureFiles() {
        ACubismJsonValue node = jsonFrequencyValue.get(FrequentNode.TEXTURES.id);
        return !node.isNull() && !node.isError();
    }

    private boolean existsHitAreas() {
        ACubismJsonValue node = jsonFrequencyValue.get(FrequentNode.HIT_AREAS.id);
        return !node.isNull() && !node.isError();
    }

    private boolean existsPhysicsFile() {
        ACubismJsonValue node = jsonFrequencyValue.get(FrequentNode.PHYSICS.id);
        return !node.isNull() && !node.isError();
    }

    private boolean existsPoseFile() {
        ACubismJsonValue node = jsonFrequencyValue.get(FrequentNode.POSE.id);
        return !node.isNull() && !node.isError();
    }

    private boolean existsDisplayInfoFile() {
        ACubismJsonValue node = jsonFrequencyValue.get(FrequentNode.DISPLAY_INFO.id);
        return !node.isNull() && !node.isError();
    }

    private boolean existsExpressionFile() {
        ACubismJsonValue node = jsonFrequencyValue.get(FrequentNode.EXPRESSIONS.id);
        return !node.isNull() && !node.isError();
    }

    private boolean existsMotionGroups() {
        ACubismJsonValue node = jsonFrequencyValue.get(FrequentNode.MOTIONS.id);
        return !node.isNull() && !node.isError();
    }

    private boolean existsMotionGroupName(String groupName) {
        ACubismJsonValue node = jsonFrequencyValue.get(FrequentNode.MOTIONS.id).get(groupName);
        return !node.isNull() && !node.isError();
    }

    private boolean existsMotionSoundFile(String groupName, int index) {
        ACubismJsonValue node = jsonFrequencyValue.get(FrequentNode.MOTIONS.id).get(groupName).get(index).get(JsonKey.SOUND_PATH.key);
        return !node.isNull() && !node.isError();
    }

    private boolean existsMotionFadeIn(String groupName, int index) {
        ACubismJsonValue node = jsonFrequencyValue.get(FrequentNode.MOTIONS.id).get(groupName).get(index).get(JsonKey.FADE_IN_TIME.key);
        return !node.isNull() && !node.isError();
    }

    private boolean existsMotionFadeOut(String groupName, int index) {
        ACubismJsonValue node = jsonFrequencyValue.get(FrequentNode.MOTIONS.id).get(groupName).get(index).get(JsonKey.FADE_OUT_TIME.key);
        return !node.isNull() && !node.isError();
    }

    private boolean existsUserDataFile() {
        return !json.getRoot().get(JsonKey.FILE_REFERENCES.key).get(JsonKey.USER_DATA.key).isNull();
    }

    private boolean existsEyeBlinkParameters() {
        if (jsonFrequencyValue.get(FrequentNode.GROUPS.id).isNull() || jsonFrequencyValue.get(FrequentNode.GROUPS.id).isError()) {
            return false;
        }

        for (int i = 0; i < jsonFrequencyValue.get(FrequentNode.GROUPS.id).size(); ++i) {
            if (jsonFrequencyValue.get(FrequentNode.GROUPS.id).get(i).get(JsonKey.NAME.key).getString().equals(JsonKey.EYE_BLINK.key)) {
                return true;
            }
        }

        return false;
    }

    private boolean existsLipSyncParameters() {
        if (jsonFrequencyValue.get(FrequentNode.GROUPS.id).isNull() || jsonFrequencyValue.get(FrequentNode.GROUPS.id).isError()) {
            return false;
        }

        for (int i = 0; i < jsonFrequencyValue.get(FrequentNode.GROUPS.id).size(); i++) {
            if (jsonFrequencyValue.get(FrequentNode.GROUPS.id).get(i).get(JsonKey.NAME.key).getString().equals(JsonKey.LIP_SYNC.key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * model3.json data
     */
    private final CubismJson json;
    /**
     * Frequent nodes in the _json data
     */
    private List<ACubismJsonValue> jsonFrequencyValue;
}
