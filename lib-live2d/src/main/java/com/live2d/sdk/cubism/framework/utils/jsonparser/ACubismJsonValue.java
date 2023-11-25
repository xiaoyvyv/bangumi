package com.live2d.sdk.cubism.framework.utils.jsonparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class ACubismJsonValue {
    /**
     * If the class implemented this interface is {@code CubismJsonObject}, returns the value corresponding to the key.
     * <p>
     * If this is implemented in other concrete class, {@code UnsupportedOperationException} is thrown.
     *
     * @param key the key of the JSON Object
     * @return the value of the JSON Object
     */
    public ACubismJsonValue get(String key) {
        ACubismJsonValue nullValue = new CubismJsonNullValue();
        nullValue.setErrorNotForClientCall(JsonError.TYPE_MISMATCH.message);

        return nullValue;
    }

    /**
     * If the class implemented this interface is {@code CubismJsonArray}, returns the value corresponding to the index of the argument.
     * <p>
     * If this is implemented in other concrete class, {@code UnsupportedOperationException} is thrown.
     *
     * @param index index of the element to return
     * @return the value corresponding to the index
     */
    public ACubismJsonValue get(int index) {
        ACubismJsonValue errorValue = new CubismJsonErrorValue();
        errorValue.setErrorNotForClientCall(JsonError.TYPE_MISMATCH.message);

        return errorValue;
    }

    /**
     * Returns the JSON Value's string expression.
     *
     * @return the JSON Value's string expression
     */
    public abstract String getString(String defaultValue, String indent);

    public String getString(String defaultValue) {
        return getString(defaultValue, "");
    }

    public String getString() {
        return getString("", "");
    }

    /**
     * If the class implemented this interface is {@code CubismJsonObject}, returns the map of {@code CubismJsonString} and {@code ICubismJsonValue}.
     * <p>
     * If this is implemented in other concrete class, {@code UnsupportedOperationException} is thrown.
     *
     * @return the map of strings and values
     */
    public Map<CubismJsonString, ACubismJsonValue> getMap() {
        return null;
    }

    /**
     * もしこのインターフェースが実装されているクラスが{@code CubismJsonObject}ならば、{@code CubismJsonString}と{@code ACubismJsonValue}をペアにしたMapを返す。<br>
     * もし他の具象クラスに実装されていた場合は、{@code null}を返す。
     *
     * @param defaultValue デフォルト値
     * @return デフォルト値
     */
    public Map<CubismJsonString, ACubismJsonValue> getMap(Map<CubismJsonString, ACubismJsonValue> defaultValue) {
        return defaultValue;
    }

    /**
     * If the class implemented this interface is {@code CubismJsonArray}, returns the list of {@code ICubismJsonValue}.
     * <p>
     * If this is implemented in other concrete class, {@code UnsupportedOperationException} is thrown.
     *
     * @return the list of {@code ICubismJsonValue}
     */
    public List<ACubismJsonValue> getList() {
        return new ArrayList<ACubismJsonValue>();
    }

    /**
     * もしこのインターフェースが実装されているクラスが{@code CubismJsonArray}ならば、{@code ACubismJsonValue}のListを返す。<br>
     * もし他の具象クラスに実装されていた場合は、{@code null}を返す。
     *
     * @param defaultValue デフォルト値
     * @return デフォルト値
     */
    public List<ACubismJsonValue> getList(List<ACubismJsonValue> defaultValue) {
        return defaultValue;
    }

    /**
     * If the class implemented this interface is {@code CubismJsonObject}, returns the set of the keys({@code CubismJsonString}).
     * <p>
     * If this is implemented in other concrete class, {@code UnsupportedOperationException} is thrown.
     *
     * @return the set of the keys({@code CubismJsonString})
     */
    public List<CubismJsonString> getKeys() {
        return Collections.emptyList();
    }

    /**
     * If the class implemented this interface is {@code CubismJsonObject} and {@code CubismJsonArray}, returns the number of JSON Value that they hold.
     * <p>
     * If this is implemented in {@code CubismJsonString}, returns the length of the string.
     * <p>
     * If this is implemented in other concrete class, {@code UnsupportedOperationException} is thrown.
     *
     * @return the number of JSON Value that they hold or the length of the string
     */
    public int size() {
        return 0;
    }

    /**
     * If the class implemented this interface is {@code CubismJsonNumber}, returns the value cast to an integer value.
     * <p>
     * If this is implemented in other concrete class, {@code UnsupportedOperationException} is thrown.
     *
     * @return the value cast to an integer value
     */
    public int toInt() {
        return 0;
    }

    public int toInt(int defaultValue) {
        return defaultValue;
    }

    /**
     * If the class implemented this interface is {@code CubismJsonNumber}, returns the value cast to a float value.
     * <p>
     * If this is implemented in other concrete class, {@code UnsupportedOperationException} is thrown.
     *
     * @return the value cast to an float value
     */
    public float toFloat() {
        return 0.0f;
    }

    public float toFloat(float defaultValue) {
        return defaultValue;
    }

    /**
     * If the class implemented this interface is {@code CubismJsonBoolean}, returns the boolean value it holds
     * <p>
     * If this is implemented in other concrete class, {@code UnsupportedOperationException} is thrown.
     *
     * @return the boolean value that the class has
     */
    public boolean toBoolean() {
        return false;
    }

    public boolean toBoolean(boolean defaultValue) {
        return defaultValue;
    }

    public boolean isError() {
        return false;
    }

    /**
     * If the class implemented this interface is {@code CubismJsonNullValue}, returns {@code true}.
     * <p>
     * If this is implemented in other concrete class, returns {@code false}.
     *
     * @return If this JSON Value is Null Value, returns true
     */
    public boolean isNull() {
        return false;
    }

    public boolean isBoolean() {
        return false;
    }

    public boolean isNumber() {
        return false;
    }

    public boolean isString() {
        return false;
    }

    public boolean isArray() {
        return false;
    }

    public boolean isObject() {
        return false;
    }

    /**
     * Valueにエラー値をセットする。
     *
     * @param errorMsg エラーメッセージ
     * @return JSON Null Value
     */
    public ACubismJsonValue setErrorNotForClientCall(String errorMsg) {
        stringBuffer = errorMsg;
        return new CubismJsonNullValue();
    }

    /**
     * エラーメッセージ定義
     */
    protected enum JsonError {
        TYPE_MISMATCH("Error: type mismatch"),
        INDEX_OUT_OF_BOUNDS("Error: index out of bounds");

        public final String message;

        JsonError(String message) {
            this.message = message;
        }
    }

    protected String stringBuffer;
}
