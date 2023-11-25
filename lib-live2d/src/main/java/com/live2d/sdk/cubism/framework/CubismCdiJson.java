/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */


package com.live2d.sdk.cubism.framework;

import com.live2d.sdk.cubism.framework.utils.jsonparser.ACubismJsonValue;
import com.live2d.sdk.cubism.framework.utils.jsonparser.CubismJson;

/**
 * This class handles cdi.json data.
 */
public class CubismCdiJson {
    public static CubismCdiJson create(byte[] buffer) {
        CubismJson json;
        json = CubismJson.create(buffer);

        return new CubismCdiJson(json);
    }

    // ----Parameters----//

    /**
     * Get the parameters number.
     *
     * @return number of parameters
     */
    public int getParametersCount() {
        if (!existsParameters()) {
            return 0;
        }
        return json.getRoot().get(JsonKey.PARAMETERS.key).size();
    }

    /**
     * Get Parameters ID
     *
     * @param index index
     * @return parameter ID
     */
    public String getParametersId(int index) {
        return json.getRoot().get(JsonKey.PARAMETERS.key).get(index).get(JsonKey.ID.key).getString();
    }

    /**
     * Get parameters group ID.
     *
     * @param index index
     * @return parameters group ID
     */
    public String getParametersGroupId(int index) {
        return json.getRoot().get(JsonKey.PARAMETERS.key).get(index).get(JsonKey.GROUP_ID.key).getString();
    }

    /**
     * Get parameters name
     *
     * @param index index
     * @return parameters name
     */
    public String getParametersName(int index) {
        return json.getRoot().get(JsonKey.PARAMETERS.key).get(index).get(JsonKey.NAME.key).getString();
    }

    // ----ParameterGroups----//

    /**
     * Get the number of parameter groups.
     *
     * @return number of parameter groups
     */
    public int getParameterGroupsCount() {
        if (!existsParameterGroups()) {
            return 0;
        }
        return json.getRoot().get(JsonKey.PARAMETER_GROUPS.key).size();
    }

    /**
     * ZGet parameter groups ID
     *
     * @param index index
     * @return parameter groups ID
     */
    public String getParameterGroupsId(int index) {
        return json.getRoot().get(JsonKey.PARAMETER_GROUPS.key).get(index).get(JsonKey.ID.key).getString();
    }

    /**
     * Get parameter groups' group ID.
     *
     * @param index index
     * @return parameter groups' group ID
     */
    public String getParameterGroupsGroupId(int index) {
        return json.getRoot().get(JsonKey.PARAMETER_GROUPS.key).get(index).get(JsonKey.GROUP_ID.key).getString();
    }

    /**
     * Get parameter groups name
     *
     * @param index index
     * @return parameter groups name
     */
    public String getParameterGroupsName(int index) {
        return json.getRoot().get(JsonKey.PARAMETER_GROUPS.key).get(index).get(JsonKey.NAME.key).getString();
    }

    // ----Parts----

    /**
     * Get the number of parts.
     *
     * @return number of parts
     */
    public int getPartsCount() {
        if (!existsParts()) {
            return 0;
        }
        return json.getRoot().get(JsonKey.PARTS.key).size();
    }

    /**
     * Get parts ID.
     *
     * @param index index
     * @return parts ID
     */
    public String getPartsId(int index) {
        return json.getRoot().get(JsonKey.PARTS.key).get(index).get(JsonKey.ID.key).getString();
    }

    /**
     * Get parts name.
     *
     * @param index index
     * @return parts name
     */
    public String getPartsName(int index) {
        return json.getRoot().get(JsonKey.PARTS.key).get(index).get(JsonKey.NAME.key).getString();
    }

    // JSON keys
    private enum JsonKey {
        VERSION("Version"),
        PARAMETERS("Parameters"),
        PARAMETER_GROUPS("ParameterGroups"),
        PARTS("Parts"),
        ID("Id"),
        GROUP_ID("GroupId"),
        NAME("Name");

        private final String key;

        JsonKey(String key) {
            this.key = key;
        }
    }

    private CubismCdiJson(CubismJson json) {
        this.json = json;
    }

    /**
     * Check if the parameter key exists.
     *
     * @return If true, the key exists
     */
    private boolean existsParameters() {
        ACubismJsonValue node = json.getRoot().get(JsonKey.PARAMETERS.key);
        return !node.isNull() && !node.isError();
    }

    /**
     * Check if the parameter group key exists.
     *
     * @return If true, the key exists
     */
    private boolean existsParameterGroups() {
        ACubismJsonValue node = json.getRoot().get(JsonKey.PARAMETER_GROUPS.key);
        return !node.isNull() && !node.isError();
    }

    /**
     * Check if the part's key exists.
     *
     * @return If true, the key exists
     */
    private boolean existsParts() {
        ACubismJsonValue node = json.getRoot().get(JsonKey.PARTS.key);
        return !node.isNull() && !node.isError();
    }

    /**
     * cdi.json data
     */
    private final CubismJson json;
}
