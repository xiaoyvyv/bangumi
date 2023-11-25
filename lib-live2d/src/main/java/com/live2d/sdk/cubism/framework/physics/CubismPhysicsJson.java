/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.physics;

import com.live2d.sdk.cubism.framework.CubismFramework;
import com.live2d.sdk.cubism.framework.id.CubismId;
import com.live2d.sdk.cubism.framework.math.CubismVector2;
import com.live2d.sdk.cubism.framework.utils.jsonparser.CubismJson;

/**
 * A manager of physics3.json.
 */
class CubismPhysicsJson {
    /**
     * Constructor
     *
     * @param buffer a buffer where physics3.json is loaded.
     */
    public CubismPhysicsJson(final byte[] buffer) {
        json = CubismJson.create(buffer);
    }

    /**
     * Get the gravity vector.
     *
     * @return gravity vector
     */
    public CubismVector2 getGravity() {
        CubismVector2 gravity = new CubismVector2();

        gravity.x = json.getRoot().get(JsonKey.META.key).get(JsonKey.EFFECTIVE_FORCES.key).get(JsonKey.GRAVITY.key).get(JsonKey.X.key).toFloat();
        gravity.y = json.getRoot().get(JsonKey.META.key).get(JsonKey.EFFECTIVE_FORCES.key).get(JsonKey.GRAVITY.key).get(JsonKey.Y.key).toFloat();

        return gravity;
    }

    /**
     * Get the wind vector.
     *
     * @return wind vector
     */
    public CubismVector2 getWind() {
        CubismVector2 wind = new CubismVector2();

        wind.x = json.getRoot().get(JsonKey.META.key).get(JsonKey.EFFECTIVE_FORCES.key).get(JsonKey.WIND.key).get(JsonKey.X.key).toFloat();
        wind.y = json.getRoot().get(JsonKey.META.key).get(JsonKey.EFFECTIVE_FORCES.key).get(JsonKey.WIND.key).get(JsonKey.Y.key).toFloat();

        return wind;
    }

    /**
     * Get the assumed FPS of physics operations.
     * If there is no FPS information in physics3.json, return 0.0f.
     *
     * @return physics operation FPS
     */
    public float getFps() {
        // If FPS information does not exist in physics3.json, 0.0f is returned.
        return json.getRoot().get(JsonKey.META.key).get(JsonKey.FPS.key).toFloat(0.0f);
    }

    /**
     * Get the number of physics settings.
     *
     * @return the number of physics settings.
     */
    public int getSubRigCount() {
        return json.getRoot().get(JsonKey.META.key).get(JsonKey.PHYSICS_SETTING_COUNT.key).toInt();
    }

    /**
     * Get the total number of inputs.
     *
     * @return total number of inputs
     */
    public int getTotalInputCount() {
        return json.getRoot().get(JsonKey.META.key).get(JsonKey.TOTAL_INPUT_COUNT.key).toInt();
    }

    /**
     * Get the total number of outputs.
     *
     * @return the total number of outputs
     */
    public int getTotalOutputCount() {
        return json.getRoot().get(JsonKey.META.key).get(JsonKey.TOTAL_OUTPUT_COUNT.key).toInt();
    }

    /**
     * Get the number of vertices.
     *
     * @return the number of vertices
     */
    public int getVertexCount() {
        return json.getRoot().get(JsonKey.META.key).get(JsonKey.VERTEX_COUNT.key).toInt();
    }

    /**
     * Get the minimum value of normalized position.
     *
     * @param physicsSettingIndex physics setting index
     * @return the minimum value of normalized position
     */
    public float getNormalizationPositionMinimumValue(int physicsSettingIndex) {
        return json.getRoot().get(JsonKey.PHYSICS_SETTINGS.key).get(physicsSettingIndex).get(JsonKey.NORMALIZATION.key).get(JsonKey.POSITION.key).get(JsonKey.MINIMUM.key).toFloat();
    }

    /**
     * Get the maximum value of normalized position.
     *
     * @param physicsSettingIndex physics setting index
     * @return the maximum value of normalized position
     */
    public float getNormalizationPositionMaximumValue(int physicsSettingIndex) {
        return json.getRoot().get(JsonKey.PHYSICS_SETTINGS.key).get(physicsSettingIndex).get(JsonKey.NORMALIZATION.key).get(JsonKey.POSITION.key).get(JsonKey.MAXIMUM.key).toFloat();
    }

    /**
     * Get the default value of normalized position.
     *
     * @param physicsSettingIndex physics setting index
     * @return the maximum value of normalized position
     */
    public float getNormalizationPositionDefaultValue(int physicsSettingIndex) {
        return json.getRoot().get(JsonKey.PHYSICS_SETTINGS.key).get(physicsSettingIndex).get(JsonKey.NORMALIZATION.key).get(JsonKey.POSITION.key).get(JsonKey.DEFAULT.key).toFloat();
    }

    /**
     * Get the minimum value of normalized angle.
     *
     * @param physicsSettingIndex physics setting index
     * @return the minimum value of normalized angle
     */
    public float getNormalizationAngleMinimumValue(int physicsSettingIndex) {
        return json.getRoot().get(JsonKey.PHYSICS_SETTINGS.key).get(physicsSettingIndex).get(JsonKey.NORMALIZATION.key).get(JsonKey.ANGLE.key).get(JsonKey.MINIMUM.key).toFloat();
    }

    /**
     * Get the maximum value of normalized angle.
     *
     * @param physicsSettingIndex physics setting index
     * @return the maximum value of normalized angle
     */
    public float getNormalizationAngleMaximumValue(int physicsSettingIndex) {
        return json.getRoot().get(JsonKey.PHYSICS_SETTINGS.key).get(physicsSettingIndex).get(JsonKey.NORMALIZATION.key).get(JsonKey.ANGLE.key).get(JsonKey.MAXIMUM.key).toFloat();
    }

    /**
     * Get the default value of normalized angle.
     *
     * @param physicsSettingIndex physics setting index
     * @return the default value of normalized angle
     */
    public float getNormalizationAngleDefaultValue(int physicsSettingIndex) {
        return json.getRoot().get(JsonKey.PHYSICS_SETTINGS.key).get(physicsSettingIndex).get(JsonKey.NORMALIZATION.key).get(JsonKey.ANGLE.key).get(JsonKey.DEFAULT.key).toFloat();
    }

    /**
     * Get the number of the input.
     *
     * @param physicsSettingIndex physics setting index
     * @return the number of inputs
     */
    public int getInputCount(int physicsSettingIndex) {
        return json.getRoot().get(JsonKey.PHYSICS_SETTINGS.key).get(physicsSettingIndex).get(JsonKey.INPUT.key).getList().size();
    }

    /**
     * Get the weight of the input.
     *
     * @param physicsSettingIndex physics setting index
     * @param inputIndex an input index
     * @return the weight of inputs
     */
    public float getInputWeight(int physicsSettingIndex, int inputIndex) {
        return json.getRoot().get(JsonKey.PHYSICS_SETTINGS.key).get(physicsSettingIndex).get(JsonKey.INPUT.key).get(inputIndex).get(JsonKey.WEIGHT.key).toFloat();
    }

    /**
     * Get the inversion of the input.
     *
     * @param physicsSettingIndex physics setting index
     * @param inputIndex an input index
     * @return the inversion of the input
     */
    public boolean getInputReflect(int physicsSettingIndex, int inputIndex) {
        return json.getRoot().get(JsonKey.PHYSICS_SETTINGS.key).get(physicsSettingIndex).get(JsonKey.INPUT.key).get(inputIndex).get(JsonKey.REFLECT.key).toBoolean();
    }

    /**
     * Get the kind of the input.
     *
     * @param physicsSettingIndex physics setting index
     * @param inputIndex an input index
     * @return 入力の種類 the kind of the input
     */
    public String getInputType(int physicsSettingIndex, int inputIndex) {
        return json.getRoot().get(JsonKey.PHYSICS_SETTINGS.key).get(physicsSettingIndex).get(JsonKey.INPUT.key).get(inputIndex).get(JsonKey.TYPE.key).getString();

    }

    /**
     * Get the ID of the input destination.
     *
     * @param physicsSettingIndex physics setting index
     * @param inputIndex an input index
     * @return the input destination ID
     */
    public CubismId getInputSourceId(int physicsSettingIndex, int inputIndex) {
        return CubismFramework.getIdManager().getId(json.getRoot().get(JsonKey.PHYSICS_SETTINGS.key).get(physicsSettingIndex).get(JsonKey.INPUT.key).get(inputIndex).get(JsonKey.SOURCE.key).get(JsonKey.ID.key).getString());
    }

    /**
     * Get the number of outputs.
     *
     * @param physicsSettingIndex physics setting index
     * @return the number of outputs
     */
    public int getOutputCount(int physicsSettingIndex) {
        return json.getRoot().get(JsonKey.PHYSICS_SETTINGS.key).get(physicsSettingIndex).get(JsonKey.OUTPUT.key).getList().size();
    }

    /**
     * Get the index of output vertices.
     *
     * @param physicsSettingIndex physics setting index
     * @param outputIndex an output index
     * @return the index of output vertices
     */
    public int getOutputVertexIndex(int physicsSettingIndex, int outputIndex) {
        return json.getRoot().get(JsonKey.PHYSICS_SETTINGS.key).get(physicsSettingIndex).get(JsonKey.OUTPUT.key).get(outputIndex).get(JsonKey.VERTEX_INDEX.key).toInt();
    }

    /**
     * Get the scale of output angle.
     *
     * @param physicsSettingIndex physics setting index
     * @param outputIndex output index
     * @return the scale of output angle
     */
    public float getOutputAngleScale(int physicsSettingIndex, int outputIndex) {
        return json.getRoot().get(JsonKey.PHYSICS_SETTINGS.key).get(physicsSettingIndex).get(JsonKey.OUTPUT.key).get(outputIndex).get(JsonKey.SCALE.key).toFloat();
    }

    /**
     * Get the weight of the output.
     *
     * @param physicsSettingIndex physics setting index
     * @param outputIndex an output index
     * @return the weight of the output
     */
    public float getOutputWeight(int physicsSettingIndex, int outputIndex) {
        return json.getRoot().get(JsonKey.PHYSICS_SETTINGS.key).get(physicsSettingIndex).get(JsonKey.OUTPUT.key).get(outputIndex).get(JsonKey.WEIGHT.key).toFloat();
    }

    /**
     * Get the ID of the output destination.
     *
     * @param physicsSettingIndex physics setting index
     * @param outputIndex an output index
     * @return the output destination ID
     */
    public CubismId getOutputsDestinationId(int physicsSettingIndex, int outputIndex) {
        return CubismFramework.getIdManager().getId(json.getRoot().get(JsonKey.PHYSICS_SETTINGS.key).get(physicsSettingIndex).get(JsonKey.OUTPUT.key).get(outputIndex).get(JsonKey.DESTINATION.key).get(JsonKey.ID.key).getString());
    }

    /**
     * Get the kind of the output.
     *
     * @param physicsSettingIndex physics setting index
     * @param outputIndex output index
     * @return the kind of the output
     */
    public String getOutputType(int physicsSettingIndex, int outputIndex) {
        return json.getRoot().get(JsonKey.PHYSICS_SETTINGS.key).get(physicsSettingIndex).get(JsonKey.OUTPUT.key).get(outputIndex).get(JsonKey.TYPE.key).getString();
    }

    /**
     * Get the inversion of the output.
     *
     * @param physicsSettingIndex physics setting index
     * @param outputIndex output index
     * @return the inversion of the output
     */
    public boolean getOutputReflect(int physicsSettingIndex, int outputIndex) {
        return json.getRoot().get(JsonKey.PHYSICS_SETTINGS.key).get(physicsSettingIndex).get(JsonKey.OUTPUT.key).get(outputIndex).get(JsonKey.REFLECT.key).toBoolean();
    }

    /**
     * Get the number of particles.
     *
     * @param physicsSettingIndex physics setting index
     * @return the number of particles
     */
    public int getParticleCount(int physicsSettingIndex) {
        return json.getRoot().get(JsonKey.PHYSICS_SETTINGS.key).get(physicsSettingIndex).get(JsonKey.VERTICES.key).getList().size();
    }

    /**
     * Get the mobility of the particles.
     *
     * @param physicsSettingIndex physics setting index
     * @param vertexIndex the vertex index
     * @return the mobility of the particles
     */
    public float getParticleMobility(int physicsSettingIndex, int vertexIndex) {
        return json.getRoot().get(JsonKey.PHYSICS_SETTINGS.key).get(physicsSettingIndex).get(JsonKey.VERTICES.key).get(vertexIndex).get(JsonKey.MOBILITY.key).toFloat();
    }

    /**
     * Get the delay of the particle.
     *
     * @param physicsSettingIndex physics setting index
     * @param vertexIndex the vertex index
     * @return the delay of the particle
     */
    public float getParticleDelay(int physicsSettingIndex, int vertexIndex) {
        return json.getRoot().get(JsonKey.PHYSICS_SETTINGS.key).get(physicsSettingIndex).get(JsonKey.VERTICES.key).get(vertexIndex).get(JsonKey.DELAY.key).toFloat();
    }

    /**
     * Get the acceleration of the particle.
     *
     * @param physicsSettingIndex physics setting index
     * @param vertexIndex vertex index
     * @return the acceleration of the particle.
     */
    public float getParticleAcceleration(int physicsSettingIndex, int vertexIndex) {
        return json.getRoot().get(JsonKey.PHYSICS_SETTINGS.key).get(physicsSettingIndex).get(JsonKey.VERTICES.key).get(vertexIndex).get(JsonKey.ACCELERATION.key).toFloat();
    }

    /**
     * Get the distance of the particle.
     *
     * @param physicsSettingIndex physics setting index
     * @param vertexIndex vertex index
     * @return the distance of the particle
     */
    public float getParticleRadius(int physicsSettingIndex, int vertexIndex) {
        return json.getRoot().get(JsonKey.PHYSICS_SETTINGS.key).get(physicsSettingIndex).get(JsonKey.VERTICES.key).get(vertexIndex).get(JsonKey.RADIUS.key).toFloat();
    }

    /**
     * Get the position of the particle.
     *
     * @param physicsSettingIndex physics setting index
     * @param vertexIndex vertex index
     * @return the position of the particle
     */
    public CubismVector2 getParticlePosition(int physicsSettingIndex, int vertexIndex) {
        float x = json.getRoot().get(JsonKey.PHYSICS_SETTINGS.key).get(physicsSettingIndex).get(JsonKey.VERTICES.key).get(vertexIndex).get(JsonKey.POSITION.key).get(JsonKey.X.key).toFloat();
        float y = json.getRoot().get(JsonKey.PHYSICS_SETTINGS.key).get(physicsSettingIndex).get(JsonKey.VERTICES.key).get(vertexIndex).get(JsonKey.POSITION.key).get(JsonKey.Y.key).toFloat();

        return new CubismVector2(x, y);
    }


    //----- private constants -----
    private enum JsonKey {
        POSITION("Position"),
        X("X"),
        Y("Y"),
        ANGLE("Angle"),
        TYPE("Type"),
        ID("Id"),

        // Meta
        META("Meta"),
        EFFECTIVE_FORCES("EffectiveForces"),
        TOTAL_INPUT_COUNT("TotalInputCount"),
        TOTAL_OUTPUT_COUNT("TotalOutputCount"),
        PHYSICS_SETTING_COUNT("PhysicsSettingCount"),
        GRAVITY("Gravity"),
        WIND("Wind"),
        VERTEX_COUNT("VertexCount"),
        FPS("Fps"),

        // Physics Settings
        PHYSICS_SETTINGS("PhysicsSettings"),
        NORMALIZATION("Normalization"),
        MINIMUM("Minimum"),
        MAXIMUM("Maximum"),
        DEFAULT("Default"),
        REFLECT("Reflect"),
        WEIGHT("Weight"),

        // Input
        INPUT("Input"),
        SOURCE("Source"),

        // Output
        OUTPUT("Output"),
        SCALE("Scale"),
        VERTEX_INDEX("VertexIndex"),
        DESTINATION("Destination"),

        // Particle
        VERTICES("Vertices"),
        MOBILITY("Mobility"),
        DELAY("Delay"),
        RADIUS("Radius"),
        ACCELERATION("Acceleration");

        private final String key;

        JsonKey(String key) {
            this.key = key;
        }
    }

    private CubismPhysicsJson(CubismJson json) {
        this.json = json;
    }

    /**
     * physics3.json data
     */
    private final CubismJson json;
}
