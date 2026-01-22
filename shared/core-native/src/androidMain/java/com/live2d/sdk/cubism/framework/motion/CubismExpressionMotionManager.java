/**
 * Copyright(c) Live2D Inc. All rights reserved.
 * <p>
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.motion;

import com.live2d.sdk.cubism.framework.id.CubismId;
import com.live2d.sdk.cubism.framework.math.CubismMath;
import com.live2d.sdk.cubism.framework.model.CubismModel;
import com.live2d.sdk.cubism.framework.utils.CubismDebug;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CubismExpressionMotionManager extends CubismMotionQueueManager {
    public static class ExpressionParameterValue {
        /**
         * パラメータID
         */
        public CubismId parameterId;
        /**
         * 加算値
         */
        public float additiveValue;
        /**
         * 乗算値
         */
        public float multiplyValue;
        /**
         * 上書き値
         */
        public float overwriteValue;
    }

    /**
     * 再生中の表情モーションの優先度を取得する。
     *
     * @return 表情モーションの優先度
     */
    public int getCurrentPriority() {
        return currentPriority;
    }

    /**
     * 予約中の表情モーションの優先度を取得する。
     *
     * @return 表情モーションの優先度
     */
    public int getReservePriority() {
        return reservePriority;
    }

    /**
     * 現在の表情のフェードのウェイト値を取得する。
     *
     * @param index 取得する表情モーションのインデックス
     * @return 表情のフェードのウェイト値
     * @throws IllegalArgumentException if an argument is an invalid value.
     */
    public float getFadeWeight(int index) {
        if (fadeWeights.isEmpty()) {
            throw new IllegalArgumentException("No motion during playback.");
        }

        if (fadeWeights.size() <= index || index < 0) {
            throw new IllegalArgumentException("The index is an invalid value.");
        }

        return fadeWeights.get(index);
    }

    /**
     * 予約中の表情モーションの優先度を設定する。
     *
     * @param priority 設定する表情モーションの優先度
     */
    public void setReservePriority(int priority) {
        this.reservePriority = priority;
    }

    /**
     * 優先度を設定して表情モーションを開始する。
     *
     * @param motion   開始する表情モーション
     * @param priority 優先度
     * @return 開始した表情モーションの識別番号。個別のモーションが終了したか否かを判断するisFinished()の引数で使用する。開始できないときは「-1」を返します。
     */
    public int startMotionPriority(ACubismMotion motion, int priority) {
        if (priority == reservePriority) {
            reservePriority = 0;    // 予約を解除
        }
        currentPriority = priority;     // 再生中モーションの優先度を設定

        return startMotion(motion);
    }

    /**
     * 表情モーションを更新して、モデルにパラメータ値を反映する。
     *
     * @param model            対象のモデル
     * @param deltaTimeSeconds デルタ時間[秒]
     * @return 表情モーションが更新されたかどうか。更新されたならtrue。
     */
    public boolean updateMotion(CubismModel model, float deltaTimeSeconds) {
        userTimeSeconds += deltaTimeSeconds;
        boolean isUpdated = false;
        List<CubismMotionQueueEntry> motions = getCubismMotionQueueEntries();

        float expressionWeight = 0.0f;
        int expressionIndex = 0;

        // motionQueueEntryの中にあるmotionインスタンスがnullの場合、motionQueueEntryインスタンス自体をnullにする
        // for文でnullを順次削除する方式だと例外を出してしまうため。
        for (int i = 0; i < motions.size(); i++) {
            CubismMotionQueueEntry entry = motions.get(i);
            CubismExpressionMotion expressionMotion = (CubismExpressionMotion) entry.getCubismMotion();

            if (expressionMotion == null) {
                motions.set(i, null);
            }
        }

        // 予めnull要素を全て削除
        motions.removeAll(nullSet);

        while (fadeWeights.size() < motions.size()) {
            fadeWeights.add(0.0f);
        }

        // ------ 処理を行う ------
        // 既に表情モーションがあれば終了フラグを立てる
        for (int i = 0; i < motions.size(); i++) {
            CubismMotionQueueEntry motionQueueEntry = motions.get(i);
            CubismExpressionMotion expressionMotion = (CubismExpressionMotion) motionQueueEntry.getCubismMotion();
            List<CubismExpressionMotion.ExpressionParameter> expressionParameters = expressionMotion.getExpressionParameters();

            if (motionQueueEntry.isAvailable()) {
                // 再生中のExpressionが参照しているパラメータをすべてリストアップ
                for (int paramIndex = 0; paramIndex < expressionParameters.size(); paramIndex++) {
                    if (expressionParameters.get(paramIndex).parameterId == null) {
                        continue;
                    }

                    int index = -1;
                    // リストにパラメータIDが存在するか検索
                    for (int j = 0; j < expressionParameterValues.size(); j++) {
                        if (expressionParameterValues.get(j).parameterId != expressionParameters.get(paramIndex).parameterId) {
                            continue;
                        }

                        index = j;
                        break;
                    }

                    if (index >= 0) {
                        continue;
                    }

                    // パラメータがリストに存在しないなら新規追加
                    ExpressionParameterValue item = new ExpressionParameterValue();
                    item.parameterId = expressionParameters.get(paramIndex).parameterId;
                    item.additiveValue = CubismExpressionMotion.DEFAULT_ADDITIVE_VALUE;
                    item.multiplyValue = CubismExpressionMotion.DEFAULT_MULTIPLY_VALUE;
                    item.overwriteValue = model.getParameterValue(item.parameterId);
                    expressionParameterValues.add(item);
                }
            }

            // ------ 値を計算する ------
            expressionMotion.setupMotionQueueEntry(motionQueueEntry, userTimeSeconds);
            setFadeWeight(expressionIndex, expressionMotion.updateFadeWeight(motionQueueEntry, userTimeSeconds));
            expressionMotion.calculateExpressionParameters(
                    model,
                    userTimeSeconds,
                    motionQueueEntry,
                    expressionParameterValues,
                    expressionIndex,
                    getFadeWeight(expressionIndex)
            );

            final float easingSine = expressionMotion.getFadeInTime() == 0.0f
                    ? 1.0f
                    : CubismMath.getEasingSine((userTimeSeconds - motionQueueEntry.getFadeInStartTime()) / expressionMotion.getFadeInTime());
            expressionWeight += easingSine;

            isUpdated = true;

            if (motionQueueEntry.isTriggeredFadeOut()) {
                // フェードアウト開始
                motionQueueEntry.startFadeOut(motionQueueEntry.getFadeOutSeconds(), userTimeSeconds);
            }

            expressionIndex++;
        }

        // ------ 最新のExpressionのフェードが完了していればそれ以前を削除する ------
        if (motions.size() > 1) {
            float latestFadeWeight = getFadeWeight(fadeWeights.size() - 1);

            if (latestFadeWeight >= 1.0f) {
                // 配列の最後の要素は削除しない
                for (int i = motions.size() - 2; i >= 0; i--) {
                    // forでremoveすることはできない。nullをセットしておいて後で削除する。
                    motions.set(i, null);

                    fadeWeights.remove(i);
                }
                motions.removeAll(nullSet);
            }
        }

        if (expressionWeight > 1.0f) {
            expressionWeight = 1.0f;
        }

        // モデルに各値を適用
        for (int i = 0; i < expressionParameterValues.size(); i++) {
            ExpressionParameterValue v = expressionParameterValues.get(i);

            model.setParameterValue(
                    v.parameterId,
                    (v.overwriteValue + v.additiveValue) * v.multiplyValue,
                    expressionWeight);
            v.additiveValue = CubismExpressionMotion.DEFAULT_ADDITIVE_VALUE;
            v.multiplyValue = CubismExpressionMotion.DEFAULT_MULTIPLY_VALUE;
        }

        return isUpdated;
    }

    /**
     * Set the weight of expression fade.
     *
     * @param index                index of the expression motion to be set
     * @param expressionFadeWeight weight value of expression fade
     */
    private void setFadeWeight(int index, float expressionFadeWeight) {
        if (index < 0 || fadeWeights.isEmpty() || fadeWeights.size() <= index) {
            CubismDebug.cubismLogWarning("Failed to set the fade weight value. The element at that index does not exist.");
            return;
        }
        fadeWeights.set(index, expressionFadeWeight);
    }

    // nullが格納されたSet。null要素だけListから排除する際に使用される。
    private static final Set<Object> nullSet = Collections.singleton(null);

    /**
     * モデルに適用する各パラメータの値
     */
    private final List<ExpressionParameterValue> expressionParameterValues = new ArrayList<>();

    /**
     * 現在再生中の表情モーションの優先度
     */
    private int currentPriority;

    /**
     * 再生予定の表情モーションの優先度。再生中は0になる。
     * 表情モーションファイルを別スレッドで読み込むときの機能。
     */
    private int reservePriority;

    /**
     * 再生中の表情モーションのウェイトのリスト
     */
    private final List<Float> fadeWeights = new ArrayList<>();
}
