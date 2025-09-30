/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.model;

import com.live2d.sdk.cubism.framework.CubismFramework;
import com.live2d.sdk.cubism.framework.exception.CubismJsonParseException;
import com.live2d.sdk.cubism.framework.id.CubismId;
import com.live2d.sdk.cubism.framework.utils.jsonparser.CubismJson;

/**
 * This class deals with an userdata3.json data.
 */
class CubismModelUserDataJson {
    /**
     * Constructor
     *
     * @param userdata3Json byte data of userdata3.json
     * @throws CubismJsonParseException If parsing JSON failed, return CubismJsonException is thrown.
     */
    public CubismModelUserDataJson(byte[] userdata3Json) {
        final CubismJson json;
        json = CubismJson.create(userdata3Json);

        this.json = json;
    }


    /**
     * Get the number of user data in userdata3.json.
     *
     * @return the number of user data
     */
    public int getUserDataCount() {
        return json.getRoot().get(JsonKey.META.key).get(JsonKey.USER_DATA_COUNT.key).toInt();
    }

    /**
     * Get the total user data string number.
     *
     * @return the total user data string number
     */
    public int getTotalUserDataSize() {
        return json.getRoot().get(JsonKey.META.key).get(JsonKey.TOTAL_USER_DATA_SIZE.key).toInt();
    }

    /**
     * Get the user data type of specified number.
     *
     * @param index index
     * @return user data type
     */
    public String getUserDataTargetType(int index) {
        return json.getRoot().get(JsonKey.USER_DATA.key).get(index).get(JsonKey.TARGET.key).getString();
    }

    /**
     * Get the user data target ID of the specified number.
     *
     * @param index index
     * @return a user data target ID
     */
    public CubismId getUserDataId(int index) {
        return CubismFramework.getIdManager().getId(json.getRoot().get(JsonKey.USER_DATA.key).get(index).get(JsonKey.ID.key).getString());
    }

    /**
     * Get the user data string of the specified number.
     *
     * @param index index
     * @return user data
     */
    public String getUserDataValue(int index) {
        return json.getRoot().get(JsonKey.USER_DATA.key).get(index).get(JsonKey.VALUE.key).getString();
    }

    private enum JsonKey {
        META("Meta"),
        USER_DATA_COUNT("UserDataCount"),
        TOTAL_USER_DATA_SIZE("TotalUserDataSize"),
        USER_DATA("UserData"),
        TARGET("Target"),
        ID("Id"),
        VALUE("Value");

        private final String key;

        JsonKey(String key) {
            this.key = key;
        }
    }

    /**
     * JSON data
     */
    private final CubismJson json;
}
