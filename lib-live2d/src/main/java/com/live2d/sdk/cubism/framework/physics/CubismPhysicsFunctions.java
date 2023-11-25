/*
 *
 *  * Copyright(c) Live2D Inc. All rights reserved.
 *  *
 *  * Use of this source code is governed by the Live2D Open Software license
 *  * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 *
 */

package com.live2d.sdk.cubism.framework.physics;

import com.live2d.sdk.cubism.framework.math.CubismMath;
import com.live2d.sdk.cubism.framework.math.CubismVector2;
import com.live2d.sdk.cubism.framework.physics.CubismPhysicsInternal.NormalizedPhysicsParameterValueGetter;
import com.live2d.sdk.cubism.framework.physics.CubismPhysicsInternal.PhysicsScaleGetter;
import com.live2d.sdk.cubism.framework.physics.CubismPhysicsInternal.PhysicsValueGetter;

import java.util.List;

/**
 * This is the set of algorithms used in CubismPhysics class.
 * <p>
 * In Cubism SDK for Java Framework's CubismPhysics, employs a design pattern called the "Strategy Pattern". This class is a collection of those 'strategies'.
 */
class CubismPhysicsFunctions {
    public static class GetInputTranslationXFromNormalizedParameterValue implements NormalizedPhysicsParameterValueGetter {
        @Override
        public void getNormalizedParameterValue(
            CubismVector2 targetTranslation,
            float[] targetAngle,
            float value,
            float parameterMinimumValue,
            float parameterMaximumValue,
            float parameterDefaultValue,
            CubismPhysicsInternal.CubismPhysicsNormalization normalizationPosition,
            CubismPhysicsInternal.CubismPhysicsNormalization normalizationAngle,
            boolean isInverted,
            float weight
        ) {
            targetTranslation.x += normalizeParameterValue(
                value,
                parameterMinimumValue,
                parameterMaximumValue,
                parameterDefaultValue,
                normalizationPosition.minimumValue,
                normalizationPosition.maximumValue,
                normalizationPosition.defaultValue,
                isInverted
            ) * weight;
        }
    }

    public static class GetInputTranslationYFromNormalizedParameterValue implements NormalizedPhysicsParameterValueGetter {
        @Override
        public void getNormalizedParameterValue(
            CubismVector2 targetTranslation,
            float[] targetAngle,
            float value,
            float parameterMinimumValue,
            float parameterMaximumValue,
            float parameterDefaultValue,
            CubismPhysicsInternal.CubismPhysicsNormalization normalizationPosition,
            CubismPhysicsInternal.CubismPhysicsNormalization normalizationAngle,
            boolean isInverted,
            float weight
        ) {
            targetTranslation.y += normalizeParameterValue(
                value,
                parameterMinimumValue,
                parameterMaximumValue,
                parameterDefaultValue,
                normalizationPosition.minimumValue,
                normalizationPosition.maximumValue,
                normalizationPosition.defaultValue,
                isInverted
            ) * weight;
        }
    }

    public static class GetInputAngleFromNormalizedParameterValue implements NormalizedPhysicsParameterValueGetter {
        @Override
        public void getNormalizedParameterValue(
            CubismVector2 targetTranslation,
            float[] targetAngle,
            float value,
            float parameterMinimumValue,
            float parameterMaximumValue,
            float parameterDefaultValue,
            CubismPhysicsInternal.CubismPhysicsNormalization normalizationPosition,
            CubismPhysicsInternal.CubismPhysicsNormalization normalizationAngle,
            boolean isInverted,
            float weight
        ) {
            targetAngle[0] += normalizeParameterValue(
                value,
                parameterMinimumValue,
                parameterMaximumValue,
                parameterDefaultValue,
                normalizationAngle.minimumValue,
                normalizationAngle.maximumValue,
                normalizationAngle.defaultValue,
                isInverted
            ) * weight;
        }
    }

    public static class GetOutputTranslationX implements PhysicsValueGetter {
        @Override
        public float getValue(
            CubismVector2 translation,
            List<CubismPhysicsInternal.CubismPhysicsParticle> particles,
            int baseParticleIndex,
            int particleIndex,
            boolean isInverted,
            CubismVector2 parentGravity
        ) {
            float outputValue = translation.x;

            if (isInverted) {
                outputValue *= -1.0f;
            }

            return outputValue;
        }
    }

    public static class GetOutputTranslationY implements PhysicsValueGetter {
        @Override
        public float getValue(
            CubismVector2 translation,
            List<CubismPhysicsInternal.CubismPhysicsParticle> particles,
            int baseParticleIndex,
            int particleIndex,
            boolean isInverted,
            CubismVector2 parentGravity
        ) {
            float outputValue = translation.y;

            if (isInverted) {
                outputValue *= -1.0f;
            }

            return outputValue;
        }
    }

    public static class GetOutputAngle implements PhysicsValueGetter {
        @Override
        public float getValue(
            CubismVector2 translation,
            List<CubismPhysicsInternal.CubismPhysicsParticle> particles,
            int baseParticleIndex,
            int particleIndex,
            boolean isInverted,
            CubismVector2 parentGravity
        ) {
            float outputValue;
            tmpGravity.x = parentGravity.x;
            tmpGravity.y = parentGravity.y;

            if (particleIndex >= 2) {
                tmpGravity.x = particles.get(baseParticleIndex + particleIndex - 1).position.x - particles.get(baseParticleIndex + particleIndex - 2).position.x;
                tmpGravity.y = particles.get(baseParticleIndex + particleIndex - 1).position.y - particles.get(baseParticleIndex + particleIndex - 2).position.y;

            } else {
                tmpGravity.multiply(-1.0f);
            }

            outputValue = CubismMath.directionToRadian(tmpGravity, translation);

            if (isInverted) {
                outputValue *= -1.0f;
            }

            return outputValue;
        }
    }

    private static final CubismVector2 tmpGravity = new CubismVector2();

    public static class GetOutputScaleTranslationX implements PhysicsScaleGetter {
        @Override
        public float getScale(CubismVector2 translationScale, float angleScale) {
            return translationScale.x;
        }
    }

    public static class GetOutputScaleTranslationY implements PhysicsScaleGetter {
        @Override
        public float getScale(CubismVector2 translationScale, float angleScale) {
            return translationScale.y;
        }
    }

    public static class GetOutputScaleAngle implements PhysicsScaleGetter {
        @Override
        public float getScale(CubismVector2 translationScale, float angleScale) {
            return angleScale;
        }
    }

    private static float normalizeParameterValue(
        float value,
        float parameterMinimum,
        float parameterMaximum,
        float parameterDefault,
        float normalizedMinimum,
        float normalizedMaximum,
        float normalizedDefault,
        boolean isInverted
    ) {
        float result = 0.0f;

        final float maxValue = Math.max(parameterMaximum, parameterMinimum);

        if (maxValue < value) {
            value = maxValue;
        }

        final float minValue = Math.min(parameterMaximum, parameterMinimum);

        if (minValue > value) {
            value = minValue;
        }

        final float minNormValue = Math.min(normalizedMinimum, normalizedMaximum);
        final float maxNormValue = Math.max(normalizedMinimum, normalizedMaximum);
        final float middleNormValue = normalizedDefault;

        final float middleValue = getDefaultValue(minValue, maxValue);
        final float paramValue = value - middleValue;


        switch ((int) Math.signum(paramValue)) {
            case 1: {
                final float nLength = maxNormValue - middleNormValue;
                final float pLength = maxValue - middleValue;

                if (pLength != 0.0f) {
                    result = paramValue * (nLength / pLength);
                    result += middleNormValue;
                }
                break;
            }
            case -1: {
                final float nLength = minNormValue - middleNormValue;
                final float pLength = minValue - middleValue;

                if (pLength != 0.0f) {
                    result = paramValue * (nLength / pLength);
                    result += middleNormValue;
                }
                break;
            }
            case 0: {
                result = middleNormValue;
                break;
            }
            default: {
                break;
            }
        }
        return (isInverted) ? result
                            : (result * (-1.0f));
    }

    private static float getRangeValue(float min, float max) {
        float maxValue = Math.max(min, max);
        float minValue = Math.min(min, max);

        return CubismMath.absF(maxValue - minValue);
    }

    private static float getDefaultValue(float min, float max) {
        float minValue = Math.min(min, max);
        return minValue + (getRangeValue(min, max) / 2.0f);
    }
}
