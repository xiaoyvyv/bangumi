/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.effect;

import com.live2d.sdk.cubism.framework.id.CubismId;
import com.live2d.sdk.cubism.framework.model.CubismModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.live2d.sdk.cubism.framework.math.CubismMath.PI;
import static com.live2d.sdk.cubism.framework.math.CubismMath.sinF;

/**
 * This class offers the breath function.
 */
public class CubismBreath {
    /**
     * This inner class has breath parameter information.
     */
    public static class BreathParameterData {
        /**
         * Constructor
         *
         * @param parameterId A parameter ID bound breathing info.
         * @param offset A wave offset when breathing is taken as a sine wave
         * @param peak A wave height when breathing is taken as a sine wave
         * @param cycle A wave cycle when breathing is taken as a sine wave
         * @param weight A weight to a parameter
         */
        public BreathParameterData(
            CubismId parameterId,
            float offset,
            float peak,
            float cycle,
            float weight
        ) {
            this.parameterId = parameterId;
            this.offset = offset;
            this.peak = peak;
            this.cycle = cycle;
            this.weight = weight;
        }

        /**
         * Copy constructor
         *
         * @param data BreathParameterData instance
         */
        public BreathParameterData(BreathParameterData data) {
            this.parameterId = data.parameterId;
            this.offset = data.offset;
            this.peak = data.peak;
            this.cycle = data.cycle;
            this.weight = data.weight;
        }

        /**
         * A parameter ID bound breath info.
         */
        public final CubismId parameterId;
        /**
         * A wave offset when breathing is taken as a sine wave
         */
        public final float offset;
        /**
         * A wave height when breathing is taken as a sine wave
         */
        public final float peak;
        /**
         * A wave cycle when breathing is taken as a sine wave
         */
        public final float cycle;
        /**
         * A weight to a parameter
         */
        public final float weight;
    }

    /**
     * Creates a {@code CubismBreath} instance.
     *
     * @return a {@code CubismBreath} instance
     */
    public static CubismBreath create() {
        return new CubismBreath();
    }

    /**
     * Updates the parameters of the model.
     *
     * @param model the target model
     * @param deltaTimeSeconds the delta time[s]
     */
    public void updateParameters(CubismModel model, float deltaTimeSeconds) {
        userTimeSeconds += deltaTimeSeconds;
        final float t = userTimeSeconds * 2.0f * PI;

        for (int i = 0; i < breathParameters.size(); i++) {
            BreathParameterData breathData = breathParameters.get(i);

            final float value = breathData.offset + (breathData.peak * sinF(t / breathData.cycle));

            model.addParameterValue(
                breathData.parameterId,
                value,
                breathData.weight);
        }
    }

    /**
     * Bind parameters of breath.
     *
     * @param breathParameters A parameters list bound breath
     * @throws IllegalArgumentException if an argument is null
     */
    public void setParameters(List<BreathParameterData> breathParameters) {
        if (breathParameters == null) {
            throw new IllegalArgumentException("breathParameters is null.");
        }

        this.breathParameters = breathParameters;
        areBreathParametersChanged = true;
    }

    /**
     * Returns the parameters bound breath.
     * <p>
     * This method returns the copy of list of breath parameters. It is recommended that this method not be used where it is executed every frame.
     * </p>
     *
     * @return The parameters bound breath
     */
    public List<BreathParameterData> getParameters() {
        // If there is a change in the parameters list, the read-only list is made.
        if (areBreathParametersChanged) {
            cachedImmutableBreathParameters = Collections.unmodifiableList(breathParameters);
            areBreathParametersChanged = false;
        }
        return cachedImmutableBreathParameters;
    }

    /**
     * private constructor
     */
    private CubismBreath() {}

    /**
     * A parameters list bound breath
     */
    private List<BreathParameterData> breathParameters = new ArrayList<BreathParameterData>();
    private boolean areBreathParametersChanged = true;
    private List<BreathParameterData> cachedImmutableBreathParameters;
    /**
     * total elapsed time[s]
     */
    private float userTimeSeconds;
}

