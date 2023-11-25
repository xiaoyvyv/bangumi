/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.utils.jsonparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class expresses JSON Object.
 * If duplicate key is put, an error is not returned. However, the value corresponding duplicate key is overridden with the value corresponding the key which is defined later.
 */
class CubismJsonObject extends ACubismJsonValue {
    /**
     * Put a pair of string and value
     *
     * @param string key(string)
     * @param value value
     */
    public void putPair(CubismJsonString string, ACubismJsonValue value) {
        this.value.put(string, value);

        // Add to key list
        keys.add(string);
    }

    @Override
    public ACubismJsonValue get(String key) {
        CubismJsonString str = CubismJsonString.valueOf(key);
        ACubismJsonValue value = this.value.get(str);

        if (value == null) {
            return new CubismJsonNullValue();
        }

        return value;
    }

    @Override
    public ACubismJsonValue get(int index) {
        return new CubismJsonErrorValue().setErrorNotForClientCall(JsonError.TYPE_MISMATCH.message);
    }

    @Override
    public String getString(String defaultValue, String indent) {
        // バッファをクリアする
        bufferForGetString.delete(0, bufferForGetString.length());

        bufferForGetString.append(indent);
        bufferForGetString.append("{\n");

        for (int i = 0; i < keys.size(); i++) {
            CubismJsonString key = keys.get(i);
            ACubismJsonValue value = this.value.get(key);

            assert value != null;

            bufferForGetString.append(indent);
            bufferForGetString.append(" ");
            bufferForGetString.append(key);
            bufferForGetString.append(" : ");
            bufferForGetString.append(value.getString(indent + " "));
            bufferForGetString.append("\n");
        }

        bufferForGetString.append(indent);
        bufferForGetString.append("}\n");

        stringBuffer = bufferForGetString.toString();
        return stringBuffer;
    }


    @Override
    public Map<CubismJsonString, ACubismJsonValue> getMap() {
        return value;
    }

    @Override
    public List<CubismJsonString> getKeys() {
        return keys;
    }

    @Override
    public int size() {
        return value.size();
    }

    @Override
    public boolean isObject() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CubismJsonObject)) return false;

        CubismJsonObject that = (CubismJsonObject) o;

        if (!value.equals(that.value)) return false;
        return keys.equals(that.keys);
    }

    @Override
    public int hashCode() {
        int result = value.hashCode();
        result = 31 * result + keys.hashCode();
        return result;
    }

    /**
     * {@code getString}メソッドで使われる一時的な文字列バッファの最小容量。
     */
    private static final int MINIMUM_CAPACITY = 128;

    /**
     * JSON Object value(map of JSON String and JSON Value)
     */
    private final Map<CubismJsonString, ACubismJsonValue> value = new HashMap<CubismJsonString, ACubismJsonValue>();
    /**
     * List of keys to speed up access to the specified index
     */
    private final List<CubismJsonString> keys = new ArrayList<CubismJsonString>();

    /**
     * {@code getString}で使用される文字列バッファ
     */
    private final StringBuffer bufferForGetString = new StringBuffer(MINIMUM_CAPACITY);
}
