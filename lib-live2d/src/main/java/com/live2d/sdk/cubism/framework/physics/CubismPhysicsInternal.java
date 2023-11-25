/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.physics;

import com.live2d.sdk.cubism.framework.id.CubismId;
import com.live2d.sdk.cubism.framework.math.CubismVector2;

import java.util.ArrayList;
import java.util.List;

/**
 * Internal data of CubismPhysics.
 */
class CubismPhysicsInternal {
    /**
     * Types of physics operations to be applied.
     */
    public enum CubismPhysicsTargetType {
        /**
         * Apply physics operation to parameters
         */
        PARAMETER
    }

    /**
     * Types of input for physics operations.
     */
    public enum CubismPhysicsSource {
        /**
         * From X-axis
         */
        X,
        /**
         * From Y-axis
         */
        Y,
        /**
         * From angle
         */
        ANGLE
    }

    /**
     * External forces used in physics operations.
     */
    public static class PhysicsJsonEffectiveForces {
        /**
         * Gravity
         */
        public CubismVector2 gravity;
        /**
         * Wind
         */
        public CubismVector2 wind;
    }

    /**
     * Parameter information for physics operations.
     */
    public static class CubismPhysicsParameter {
        /**
         * Parameter ID
         */
        public CubismId Id;
        /**
         * Type of destination
         */
        public CubismPhysicsTargetType targetType;
    }

    /**
     * Normalization information for physics operations.
     */
    public static class CubismPhysicsNormalization {
        /**
         * Minimum value
         */
        public float minimumValue;
        /**
         * Maximum value
         */
        public float maximumValue;
        /**
         * Default value
         */
        public float defaultValue;
    }

    /**
     * Information on a particle used for physics operations.
     */
    public static class CubismPhysicsParticle {
        /**
         * Initial position
         */
        public CubismVector2 initialPosition = new CubismVector2();
        /**
         * Mobility
         */
        public float mobility;
        /**
         * Delay
         */
        public float delay;
        /**
         * Acceleration
         */
        public float acceleration;
        /**
         * Distance
         */
        public float radius;
        /**
         * Current position
         */
        public CubismVector2 position = new CubismVector2();
        /**
         * Last position
         */
        public CubismVector2 lastPosition = new CubismVector2();
        /**
         * Last gravity
         */
        public CubismVector2 lastGravity = new CubismVector2();
        /**
         * Current force
         */
        public CubismVector2 force = new CubismVector2();
        /**
         * Current velocity
         */
        public CubismVector2 velocity = new CubismVector2();
    }

    /**
     * Manager of phycal points in physics operations.
     */
    public static class CubismPhysicsSubRig {
        /**
         * number of inputs
         */
        public int inputCount;
        /**
         * number of outputs
         */
        public int outputCount;
        /**
         * number of particles
         */
        public int particleCount;
        /**
         * First index of inputs
         */
        public int baseInputIndex;
        /**
         * First index of outputs
         */
        public int baseOutputIndex;
        /**
         * First index of particles
         */
        public int baseParticleIndex;
        /**
         * Normalized position
         */
        public CubismPhysicsNormalization normalizationPosition = new CubismPhysicsNormalization();
        /**
         * Normalized angle
         */
        public CubismPhysicsNormalization normalizationAngle = new CubismPhysicsNormalization();
    }

    /**
     * Input information for physics operations.
     */
    public static class CubismPhysicsInput {
        /**
         * Input source parameter
         */
        public CubismPhysicsParameter source = new CubismPhysicsParameter();
        /**
         * Index of input source parameter
         */
        public int sourceParameterIndex;
        /**
         * Weight
         */
        public float weight;
        /**
         * Type of input
         */
        public CubismPhysicsSource type;
        /**
         * Whether the value is inverted.
         */
        public boolean reflect;
        /**
         * Function to get normalized parameter values
         */
        public NormalizedPhysicsParameterValueGetter getNormalizedParameterValue;
    }

    /**
     * Output information for physics operations.
     */
    public static class CubismPhysicsOutput {
        /**
         * Output destination parameter
         */
        public CubismPhysicsParameter destination = new CubismPhysicsParameter();
        /**
         * Index of output destination parameter
         */
        public int destinationParameterIndex;
        /**
         * Pendulum index
         */
        public int vertexIndex;
        /**
         * transition scale
         */
        public CubismVector2 transitionScale = new CubismVector2();
        /**
         * Angle scale
         */
        public float angleScale;
        /**
         * Weight
         */
        public float weight;
        /**
         * Type of output
         */
        public CubismPhysicsSource type;
        /**
         * Whether the value is inverted
         */
        public boolean reflect;
        /**
         * Value when the value is below the minimum value
         */
        public float valueBelowMinimum;
        /**
         * Value when the maximum value is exceeded.
         */
        public float valueExceededMaximum;
        /**
         * Function to get the value for physics operation.
         */
        public PhysicsValueGetter getValue;
        /**
         * Function to get the scale value for physics operation
         */
        public PhysicsScaleGetter getScale;
    }

    /**
     * Physics operation data
     */
    public static class CubismPhysicsRig {
        /**
         * Number of physics point for physics operation
         */
        public int subRigCount;
        /**
         * List of physics point management for physics operation
         */
        public List<CubismPhysicsSubRig> settings = new ArrayList<CubismPhysicsSubRig>();
        /**
         * List of inputs for physics operation
         */
        public List<CubismPhysicsInput> inputs = new ArrayList<CubismPhysicsInput>();
        /**
         * List of outputs for physics operation
         */
        public List<CubismPhysicsOutput> outputs = new ArrayList<CubismPhysicsOutput>();
        /**
         * List of particles for physics operation
         */
        public List<CubismPhysicsParticle> particles = new ArrayList<CubismPhysicsParticle>();
        /**
         * Gravity
         */
        public CubismVector2 gravity = new CubismVector2();
        /**
         * Wind
         */
        public CubismVector2 wind = new CubismVector2();
        /**
         * Physics operation FPS
         */
        public float fps;
    }

    /**
     * Functional interface with a function which gets normalized parameters.
     */
    public interface NormalizedPhysicsParameterValueGetter {
        /**
         * Get normalized parameters.
         *
         * @param targetTransition the move value of the calculation result
         * @param targetAngle the angle of the calculation result
         * @param value the value of the parameter
         * @param parameterMinimumValue the minimum value of the parameter
         * @param parameterMaximumValue the maximum value of the parameter
         * @param parameterDefaultValue the default value of the parameter
         * @param normalizationPosition the normalized position
         * @param normalizationAngle the normalized angle
         * @param isInverted Whether the value is inverted
         * @param weight a weight
         */
        void getNormalizedParameterValue(
            CubismVector2 targetTransition,
            float[] targetAngle,
            float value,
            float parameterMinimumValue,
            float parameterMaximumValue,
            float parameterDefaultValue,
            CubismPhysicsNormalization normalizationPosition,
            CubismPhysicsNormalization normalizationAngle,
            boolean isInverted,
            float weight
        );
    }

    /**
     * Functional interface with a function for getting values of physics operations.
     */
    public interface PhysicsValueGetter {
        /**
         * Get values of physics operations.
         *
         * @param transition a transition value
         * @param particles a particles list
         * @param particleIndex a particle index
         * @param isInverted Whether the value is inverted
         * @param parentGravity a gravity
         * @return the value
         */
        float getValue(
            CubismVector2 transition,
            List<CubismPhysicsParticle> particles,
            int baseParticleIndex,
            int particleIndex,
            boolean isInverted,
            CubismVector2 parentGravity
        );
    }

    /**
     * Functional interface with a function for getting the scale of physics operations.
     */
    public interface PhysicsScaleGetter {
        /**
         * Get a scale of physics operations.
         *
         * @param transitionScale transition scale
         * @param angleScale angle scale
         * @return scale value
         */
        float getScale(CubismVector2 transitionScale, float angleScale);
    }

}
