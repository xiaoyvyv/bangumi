/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.motion;

import com.live2d.sdk.cubism.framework.CubismFramework;
import com.live2d.sdk.cubism.framework.id.CubismId;
import com.live2d.sdk.cubism.framework.model.CubismModel;
import com.live2d.sdk.cubism.framework.utils.jsonparser.ACubismJsonValue;
import com.live2d.sdk.cubism.framework.utils.jsonparser.CubismJson;

import java.util.ArrayList;
import java.util.List;

/**
 * A motion class for facial expressions.
 */
public class CubismExpressionMotion extends ACubismMotion {
    /**
     * Calculation method of facial expression parameter values.
     */
    public enum ExpressionBlendType {
        /**
         * Addition
         */
        ADD("Add"),
        /**
         * Multiplication
         */
        MULTIPLY("Multiply"),
        /**
         * Overwriting
         */
        OVERWRITE("Overwrite");

        private final String type;

        ExpressionBlendType(String type) {
            this.type = type;
        }
    }

    /**
     * Internal class for expression parameter information.
     */
    public static class ExpressionParameter {
        public ExpressionParameter(CubismId id, ExpressionBlendType method, float value) {
            if (id == null || method == null) {
                throw new IllegalArgumentException("id or method is null.");
            }
            this.parameterId = id;
            this.blendType = method;
            this.value = value;
        }

        /**
         * Parameter ID
         */
        public final CubismId parameterId;
        /**
         * Type of parameter calculation
         */
        public final ExpressionBlendType blendType;
        /**
         * Value
         */
        public final float value;
    }

    /**
     * Default fade duration.
     */
    public static final float DEFAULT_FADE_TIME = 1.0f;

    /**
     * 加算適用の初期値
     */
    public static final float DEFAULT_ADDITIVE_VALUE = 0.0f;

    /**
     * 乗算適用の初期値
     */
    public static final float DEFAULT_MULTIPLY_VALUE = 1.0f;

    /**
     * Create an ACubismMotion instance.
     *
     * @param buffer buffer where exp3.json file is loaded
     * @return created instance
     */
    public static CubismExpressionMotion create(byte[] buffer) {
        CubismExpressionMotion expression = new CubismExpressionMotion();
        expression.parse(buffer);

        return expression;
    }

    /**
     * モデルの表情に関するパラメータを計算する。
     *
     * @param model                     対象のモデル
     * @param userTimeSeconds           デルタ時間の積算値[秒]
     * @param motionQueueEntry          CubismMotionQueueManagerで管理されているモーション
     * @param expressionParameterValues モデルに適用する各パラメータの値
     * @param expressionIndex           表情のインデックス
     * @param fadeWeight                表情のウェイト
     */
    public void calculateExpressionParameters(
            CubismModel model,
            float userTimeSeconds,
            CubismMotionQueueEntry motionQueueEntry,
            List<CubismExpressionMotionManager.ExpressionParameterValue> expressionParameterValues,
            int expressionIndex,
            float fadeWeight
    ) {
        if (motionQueueEntry == null || expressionParameterValues == null) {
            return;
        }

        if (!motionQueueEntry.isAvailable()) {
            return;
        }

        // CubismExpressionMotion.fadeWeight は廃止予定です。
        // 互換性のために処理は残りますが、実際には使用しておりません。
        this.fadeWeight = updateFadeWeight(motionQueueEntry, userTimeSeconds);

        // モデルに適用する値を計算
        for (int i = 0; i < expressionParameterValues.size(); i++) {
            CubismExpressionMotionManager.ExpressionParameterValue expParamValue = expressionParameterValues.get(i);

            if (expParamValue.parameterId == null) {
                continue;
            }

            final float currentParameterValue = expParamValue.overwriteValue = model.getParameterValue(expParamValue.parameterId);

            List<ExpressionParameter> expressionParameters = getExpressionParameters();
            int parameterIndex = -1;
            for (int j = 0; j < expressionParameters.size(); j++) {
                if (expParamValue.parameterId != expressionParameters.get(j).parameterId) {
                    continue;
                }

                parameterIndex = j;
                break;
            }

            // 再生中のExpressionが参照していないパラメータは初期値を適用
            if (parameterIndex < 0) {
                if (expressionIndex == 0) {
                    expParamValue.additiveValue = DEFAULT_ADDITIVE_VALUE;
                    expParamValue.multiplyValue = DEFAULT_MULTIPLY_VALUE;
                    expParamValue.overwriteValue = currentParameterValue;
                } else {
                    expParamValue.additiveValue = calculateValue(expParamValue.additiveValue, DEFAULT_ADDITIVE_VALUE, fadeWeight);
                    expParamValue.multiplyValue = calculateValue(expParamValue.multiplyValue, DEFAULT_MULTIPLY_VALUE, fadeWeight);
                    expParamValue.overwriteValue = calculateValue(expParamValue.overwriteValue, currentParameterValue, fadeWeight);
                }
                continue;
            }

            // 値を計算
            float value = expressionParameters.get(parameterIndex).value;
            float newAdditiveValue, newMultiplyValue, newOverwriteValue;

            switch (expressionParameters.get(parameterIndex).blendType) {
                case ADD:
                    newAdditiveValue = value;
                    newMultiplyValue = DEFAULT_MULTIPLY_VALUE;
                    newOverwriteValue = currentParameterValue;
                    break;
                case MULTIPLY:
                    newAdditiveValue = DEFAULT_ADDITIVE_VALUE;
                    newMultiplyValue = value;
                    newOverwriteValue = currentParameterValue;
                    break;
                case OVERWRITE:
                    newAdditiveValue = DEFAULT_ADDITIVE_VALUE;
                    newMultiplyValue = DEFAULT_MULTIPLY_VALUE;
                    newOverwriteValue = value;
                    break;
                default:
                    return;
            }

            if (expressionIndex == 0) {
                expParamValue.additiveValue = newAdditiveValue;
                expParamValue.multiplyValue = newMultiplyValue;
                expParamValue.overwriteValue = newOverwriteValue;
            } else {
                expParamValue.additiveValue = (expParamValue.additiveValue * (1.0f - fadeWeight)) + newAdditiveValue * fadeWeight;
                expParamValue.multiplyValue = (expParamValue.multiplyValue * (1.0f - fadeWeight)) + newMultiplyValue * fadeWeight;
                expParamValue.overwriteValue = (expParamValue.overwriteValue * (1.0f - fadeWeight)) + newOverwriteValue * fadeWeight;
            }
        }
    }

    /**
     * 表情が参照しているパラメータを取得する。
     *
     * @return 表情が参照しているパラメータ
     */
    public List<ExpressionParameter> getExpressionParameters() {
        return parameters;
    }


    /**
     * 現在の表情のフェードのウェイト値を取得する。
     *
     * @return 表情のフェードのウェイト値
     * @see CubismExpressionMotionManager#getFadeWeight(int index)
     * @deprecated CubismExpressionMotion.fadeWeightが削除予定のため非推奨。
     * CubismExpressionMotionManager.getFadeWeight(int index) を使用してください。
     */
    @Deprecated
    public float getFadeWeight() {
        return fadeWeight;
    }

    /**
     * デフォルトコンストラクタ
     */
    protected CubismExpressionMotion() {
    }

    @Override
    protected void doUpdateParameters(
            final CubismModel model,
            final float userTimeSeconds,
            final float weight,
            final CubismMotionQueueEntry motionQueueEntry
    ) {
        for (int i = 0; i < parameters.size(); i++) {
            ExpressionParameter parameter = parameters.get(i);
            switch (parameter.blendType) {
                // Relative change: Addition
                case ADD:
                    model.addParameterValue(parameter.parameterId, parameter.value, weight);
                    break;
                // Relative change: Multiplication
                case MULTIPLY:
                    model.multiplyParameterValue(parameter.parameterId, parameter.value, weight);
                    break;
                // Relatice change: Overwriting
                case OVERWRITE:
                    model.setParameterValue(parameter.parameterId, parameter.value, weight);
                    break;
                default:
                    // When you set a value that is not in the specification, it is already in the addition mode.
                    break;
            }
        }
    }

    /**
     * exp3.jsonをパースする。
     *
     * @param exp3Json exp3.jsonが読み込まれているbyte配列
     */
    protected void parse(byte[] exp3Json) {
        CubismJson json = CubismJson.create(exp3Json);

        setFadeInTime(json.getRoot().get(ExpressionKey.FADE_IN.key).toFloat(DEFAULT_FADE_TIME));
        setFadeOutTime(json.getRoot().get(ExpressionKey.FADE_OUT.key).toFloat(DEFAULT_FADE_TIME));

        ACubismJsonValue jsonParameters = json.getRoot().get(ExpressionKey.PARAMETERS.key);
        // Each parameter setting
        for (int i = 0; i < jsonParameters.size(); i++) {
            final ACubismJsonValue param = jsonParameters.get(i);

            // Parameter ID
            final CubismId parameterId = CubismFramework.getIdManager().getId(param.get(ExpressionKey.ID.key).getString());
            // Setting of calculation method.
            final ExpressionBlendType blendType = getBlendMethod(param);
            // Value
            final float value = param.get(ExpressionKey.VALUE.key).toFloat();

            // Create a configuration object and add it to the list.
            ExpressionParameter item = new ExpressionParameter(parameterId, blendType, value);
            this.parameters.add(item);
        }
    }

    /**
     * Get the calculation method for the parameter values of expressions set in JSON.
     *
     * @param parameter JSON parameter value
     * @return calculation method set in JSON
     */
    private static ExpressionBlendType getBlendMethod(ACubismJsonValue parameter) {
        final String method = parameter.get(ExpressionKey.BLEND.key).getString();

        if (method.equals(ExpressionBlendType.ADD.type)) {
            return ExpressionBlendType.ADD;
        } else if (method.equals(ExpressionBlendType.MULTIPLY.type)) {
            return ExpressionBlendType.MULTIPLY;
        } else if (method.equals(ExpressionBlendType.OVERWRITE.type)) {
            return ExpressionBlendType.OVERWRITE;
        }
        // If the value that is not in the specifications is set, it can be recovered by setting addition mode.
        else {
            return ExpressionBlendType.ADD;
        }
    }

    /**
     * Key of exp3.json.
     */
    private enum ExpressionKey {
        FADE_IN("FadeInTime"),
        FADE_OUT("FadeOutTime"),
        PARAMETERS("Parameters"),
        ID("Id"),
        VALUE("Value"),
        BLEND("Blend");

        private final String key;

        ExpressionKey(String key) {
            this.key = key;
        }
    }

    /**
     * 入力された値でブレンド計算をする。
     *
     * @param source      現在の値
     * @param destination 適用する値
     * @return 計算されたブレンド値
     */
    private float calculateValue(float source, float destination, float fadeWeight) {
        return (source * (1.0f - fadeWeight)) + (destination * fadeWeight);
    }

    /**
     * Parameter information list for facial expressions
     */
    private final List<ExpressionParameter> parameters = new ArrayList<>();

    /**
     * 表情の現在のウェイト
     *
     * @deprecated 不具合を引き起こす要因となるため非推奨。
     */
    @Deprecated
    private float fadeWeight;
}
