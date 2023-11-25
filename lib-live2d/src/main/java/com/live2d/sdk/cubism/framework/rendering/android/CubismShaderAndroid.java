/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.rendering.android;

import com.live2d.sdk.cubism.framework.math.CubismMatrix44;
import com.live2d.sdk.cubism.framework.rendering.CubismRenderer;
import com.live2d.sdk.cubism.framework.type.csmRectF;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.*;
import static com.live2d.sdk.cubism.framework.rendering.android.CubismShaderPrograms.*;
import static com.live2d.sdk.cubism.framework.utils.CubismDebug.cubismLogError;

/**
 * This class manage a shader program for Android(OpenGL ES 2.0). This is singleton.
 */
class CubismShaderAndroid {
    /**
     * Tegra processor support. Enable/Disable drawing by extension method.
     *
     * @param extMode Whether to draw using the extended method.
     * @param extPAMode Enables/disables the PA setting for the extension method.
     */
    public static void setExtShaderMode(boolean extMode, boolean extPAMode) {
        CubismShaderAndroid.EXT_MODE = extMode;
        CubismShaderAndroid.EXT_PA_MODE = extPAMode;
    }

    /**
     * Get this singleton instance.
     *
     * @return singleton instance
     */
    public static CubismShaderAndroid getInstance() {
        if (s_instance == null) {
            s_instance = new CubismShaderAndroid();
        }

        return s_instance;
    }

    /**
     * Delete this singleton instance.
     */
    public static void deleteInstance() {
        s_instance = null;
    }

    /**
     * Setup shader program.
     *
     * @param renderer renderer instance
     * @param textureId texture ID of GPU
     * @param vertexCount number of vertices
     * @param vertexArrayBuffer vertex array of polygon mesh
     * @param uvArrayBuffer UV array
     * @param blendMode mode of color blending
     * @param baseColor base color
     * @param multiplyColor multiply color
     * @param screenColor screen color
     * @param isPremultipliedAlpha whether it is premultiplied alpha
     * @param matrix44 Model-View-Projection Matrix
     * @param isInvertedMask whether mask is inverted
     */
    public void setupShaderProgram(
        CubismRendererAndroid renderer,
        int textureId,
        int vertexCount,
        FloatBuffer vertexArrayBuffer,
        FloatBuffer uvArrayBuffer,
        float opacity,
        CubismRenderer.CubismBlendMode blendMode,
        CubismRenderer.CubismTextureColor baseColor,
        CubismRenderer.CubismTextureColor multiplyColor,
        CubismRenderer.CubismTextureColor screenColor,
        boolean isPremultipliedAlpha,
        CubismMatrix44 matrix44,
        boolean isInvertedMask
    ) {
        if (shaderSets.isEmpty()) {
            generateShaders();
        }

        // Blending
        int srcColor;
        int dstColor;
        int srcAlpha;
        int dstAlpha;

        // At generting mask
        if (renderer.getClippingContextBufferForMask() != null) {
            CubismShaderSet shaderSet = shaderSets.get(ShaderNames.SETUP_MASK.getId());
            glUseProgram(shaderSet.shaderProgram);

            // texture setting
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, textureId);
            glUniform1i(shaderSet.samplerTexture0Location, 0);

            // setting of vertex array
            glEnableVertexAttribArray(shaderSet.attributePositionLocation);
            glVertexAttribPointer(
                shaderSet.attributePositionLocation,
                2,
                GL_FLOAT,
                false,
                Float.SIZE / Byte.SIZE * 2,
                vertexArrayBuffer
            );

            // setting of texture vertex
            glEnableVertexAttribArray(shaderSet.attributeTexCoordLocation);
            glVertexAttribPointer(
                shaderSet.attributeTexCoordLocation,
                2,
                GL_FLOAT,
                false,
                Float.SIZE / Byte.SIZE * 2,
                uvArrayBuffer
            );

            // channels
            final int channelNumber =
                renderer.getClippingContextBufferForMask()
                        .layoutChannelNo;
            CubismRenderer.CubismTextureColor colorChannel =
                renderer.getClippingContextBufferForMask()
                        .getClippingManager()
                        .getChannelFlagAsColor(channelNumber);
            glUniform4f(
                shaderSet.uniformChannelFlagLocation,
                colorChannel.r,
                colorChannel.g,
                colorChannel.b,
                colorChannel.a
            );

            glUniformMatrix4fv(
                shaderSet.uniformClipMatrixLocation,
                1,
                false,
                renderer.getClippingContextBufferForMask().matrixForMask.getArray(),
                0
            );

            csmRectF rect =
                renderer.getClippingContextBufferForMask()
                        .layoutBounds;

            glUniform4f(
                shaderSet.uniformBaseColorLocation,
                rect.getX() * 2.0f - 1.0f,
                rect.getY() * 2.0f - 1.0f,
                rect.getRight() * 2.0f - 1.0f,
                rect.getBottom() * 2.0f - 1.0f
            );
            glUniform4f(
                shaderSet.uniformMultiplyColorLocation,
                multiplyColor.r,
                multiplyColor.g,
                multiplyColor.b,
                multiplyColor.a
            );
            glUniform4f(
                shaderSet.uniformScreenColorLocation,
                screenColor.r,
                screenColor.g,
                screenColor.b,
                screenColor.a
            );

            srcColor = GL_ZERO;
            dstColor = GL_ONE_MINUS_SRC_COLOR;
            srcAlpha = GL_ZERO;
            dstAlpha = GL_ONE_MINUS_SRC_ALPHA;
        }
        // except for mask generation
        else {
            // Whether this drawing object is to be masked.
            final boolean isMasked = renderer.getClippingContextBufferForDraw() != null;
            int offset;
            if (isMasked) {
                if (isInvertedMask) {
                    offset = 2;
                } else {
                    offset = 1;
                }
            } else {
                offset = 0;
            }
            if (isPremultipliedAlpha) {
                offset += 3;
            }

            CubismShaderSet shaderSet;

            switch (blendMode) {
                case NORMAL:
                default:
                    shaderSet = shaderSets.get(ShaderNames.NORMAL.getId() + offset);
                    srcColor = GL_ONE;
                    dstColor = GL_ONE_MINUS_SRC_ALPHA;
                    srcAlpha = GL_ONE;
                    dstAlpha = GL_ONE_MINUS_SRC_ALPHA;
                    break;

                case ADDITIVE:
                    shaderSet = shaderSets.get(ShaderNames.ADD.getId() + offset);
                    srcColor = GL_ONE;
                    dstColor = GL_ONE;
                    srcAlpha = GL_ZERO;
                    dstAlpha = GL_ONE;
                    break;

                case MULTIPLICATIVE:
                    shaderSet = shaderSets.get(ShaderNames.MULT.getId() + offset);
                    srcColor = GL_DST_COLOR;
                    dstColor = GL_ONE_MINUS_SRC_ALPHA;
                    srcAlpha = GL_ZERO;
                    dstAlpha = GL_ONE;
                    break;
            }

            glUseProgram(shaderSet.shaderProgram);

            // setting of vertex array
            glEnableVertexAttribArray(shaderSet.attributePositionLocation);
            glVertexAttribPointer(
                shaderSet.attributePositionLocation,
                2,
                GL_FLOAT,
                false,
                Float.SIZE / Byte.SIZE * 2,
                vertexArrayBuffer
            );

            // setting of texture vertex
            glEnableVertexAttribArray(shaderSet.attributeTexCoordLocation);
            glVertexAttribPointer(
                shaderSet.attributeTexCoordLocation,
                2,
                GL_FLOAT,
                false,
                Float.SIZE / Byte.SIZE * 2,
                uvArrayBuffer
            );

            if (isMasked) {
                glActiveTexture(GL_TEXTURE1);
                // Framebufferに描かれたテクスチャ
                int tex = renderer.getMaskBuffer(renderer.getClippingContextBufferForDraw().bufferIndex).getColorBuffer()[0];
                glBindTexture(GL_TEXTURE_2D, tex);
                glUniform1i(shaderSet.samplerTexture1Location, 1);

                // set up a matrix to convert View-coordinates to ClippingContext coordinates
                glUniformMatrix4fv(
                    shaderSet.uniformClipMatrixLocation,
                    1,
                    false,
                    renderer.getClippingContextBufferForDraw().matrixForDraw.getArray(),
                    0
                );

                // Set used color channel.
                final int channelNumber =
                    renderer.getClippingContextBufferForDraw()
                            .layoutChannelNo;
                CubismRenderer.CubismTextureColor colorChannel =
                    renderer.getClippingContextBufferForDraw()
                            .getClippingManager()
                            .getChannelFlagAsColor(channelNumber);
                glUniform4f(
                    shaderSet.uniformChannelFlagLocation,
                    colorChannel.r,
                    colorChannel.g,
                    colorChannel.b,
                    colorChannel.a
                );
            }

            // texture setting
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, textureId);
            glUniform1i(shaderSet.samplerTexture0Location, 0);

            // coordinate transformation
            glUniformMatrix4fv(
                shaderSet.uniformMatrixLocation,
                1,
                false,
                matrix44.getArray(),
                0
            );

            glUniform4f(
                shaderSet.uniformBaseColorLocation,
                baseColor.r,
                baseColor.g,
                baseColor.b,
                baseColor.a
            );
            glUniform4f(
                shaderSet.uniformMultiplyColorLocation,
                multiplyColor.r,
                multiplyColor.g,
                multiplyColor.b,
                multiplyColor.a
            );
            glUniform4f(
                shaderSet.uniformScreenColorLocation,
                screenColor.r,
                screenColor.g,
                screenColor.b,
                screenColor.a
            );
        }

        glBlendFuncSeparate(srcColor, dstColor, srcAlpha, dstAlpha);
    }

    /**
     * Singleton instance.
     */
    private static CubismShaderAndroid s_instance;

    /**
     * Tegra support. Drawing with Extended Method.
     */
    private static boolean EXT_MODE;
    /**
     * Variable for setting the PA of the extension method.
     */
    private static boolean EXT_PA_MODE;

    /**
     * Shader names
     */
    private enum ShaderNames {
        // Setup Mask
        SETUP_MASK(0),

        // Normal
        NORMAL(1),
        NORMAL_MASKED(2),
        NORMAL_MASKED_INVERTED(3),
        NORMAL_PREMULTIPLIED_ALPHA(4),
        NORMAL_MASKED_PREMULTIPLIED_ALPHA(5),
        NORMAL_MASKED_INVERTED_PREMULTIPLIED_ALPHA(6),

        // Add
        ADD(7),
        ADD_MASKED(8),
        ADD_MASKED_INVERTED(9),
        ADD_PREMULTIPLIED_ALPHA(10),
        ADD_MASKED_PREMULTIPLIED_ALPHA(11),
        ADD_MASKED_PREMULTIPLIED_ALPHA_INVERTED(12),

        // Multi
        MULT(13),
        MULT_MASKED(14),
        MULT_MASKED_INVERTED(15),
        MULT_PREMULTIPLIED_ALPHA(16),
        MULT_MASKED_PREMULTIPLIED_ALPHA(17),
        MULT_MASKED_PREMULTIPLIED_ALPHA_INVERTED(18);

        private final int id;

        ShaderNames(int id) {
            this.id = id;
        }

        private int getId() {
            return id;
        }
    }

    /**
     * Data class that holds the addresses of shader programs and shader variables
     */
    private static class CubismShaderSet {
        /**
         * address of shader program.
         */
        int shaderProgram;
        /**
         * Address of the variable to be passed to the shader program (Position)
         */
        int attributePositionLocation;
        /**
         * Address of the variable to be passed to the shader program (TexCoord)
         */
        int attributeTexCoordLocation;
        /**
         * Address of the variable to be passed to the shader program (Matrix)
         */
        int uniformMatrixLocation;
        /**
         * Address of the variable to be passed to the shader program (ClipMatrix)
         */
        int uniformClipMatrixLocation;
        /**
         * Address of the variable to be passed to the shader program (Texture0)
         */
        int samplerTexture0Location;
        /**
         * Address of the variable to be passed to the shader program (Texture1)
         */
        int samplerTexture1Location;
        /**
         * Address of the variable to be passed to the shader program (BaseColor)
         */
        int uniformBaseColorLocation;
        /**
         * Address of the variable to be passed to the shader program (MultiplyColor)
         */
        int uniformMultiplyColorLocation;
        /**
         * Address of the variable to be passed to the shader program (ScreenColor)
         */
        int uniformScreenColorLocation;
        /**
         * Address of the variable to be passed to the shader program (ChannelFlag)
         */
        int uniformChannelFlagLocation;
    }

    /**
     * private constructor.
     */
    private CubismShaderAndroid() {}

    /**
     * Release shader programs.
     */
    private void releaseShaderProgram() {
        for (CubismShaderSet shaderSet : shaderSets) {
            glDeleteProgram(shaderSet.shaderProgram);
            shaderSet.shaderProgram = 0;
        }
        shaderSets.clear();
    }

    /**
     * Initialize and generate shader programs.
     */
    private void generateShaders() {
        for (int i = 0; i < SHADER_COUNT; i++) {
            shaderSets.add(new CubismShaderSet());
        }

        if (EXT_MODE) {
            shaderSets.get(0).shaderProgram = loadShaderProgram(VERT_SHADER_SRC_SETUP_MASK, FRAG_SHADER_SRC_SETUP_MASK_TEGRA);
            shaderSets.get(1).shaderProgram = loadShaderProgram(VERT_SHADER_SRC, FRAG_SHADER_SRC_TEGRA);
            shaderSets.get(2).shaderProgram = loadShaderProgram(VERT_SHADER_SRC_MASKED, FRAG_SHADER_SRC_MASK_TEGRA);
            shaderSets.get(3).shaderProgram = loadShaderProgram(VERT_SHADER_SRC_MASKED, FRAG_SHADER_SRC_MASK_INVERTED_TEGRA);
            shaderSets.get(4).shaderProgram = loadShaderProgram(VERT_SHADER_SRC, FRAG_SHADER_SRC_PREMULTIPLIED_ALPHA_TEGRA);
            shaderSets.get(5).shaderProgram = loadShaderProgram(VERT_SHADER_SRC_MASKED, FRAG_SHADER_SRC_MASK_PREMULTIPLIED_ALPHA_TEGRA);
            shaderSets.get(6).shaderProgram = loadShaderProgram(VERT_SHADER_SRC_MASKED, FRAG_SHADER_SRC_MASK_INVERTED_PREMULTIPLIED_ALPHA_TEGRA);
        } else {
            shaderSets.get(0).shaderProgram = loadShaderProgram(VERT_SHADER_SRC_SETUP_MASK, FRAG_SHADER_SRC_SETUP_MASK);
            shaderSets.get(1).shaderProgram = loadShaderProgram(VERT_SHADER_SRC, FRAG_SHADER_SRC);
            shaderSets.get(2).shaderProgram = loadShaderProgram(VERT_SHADER_SRC_MASKED, FRAG_SHADER_SRC_MASK);
            shaderSets.get(3).shaderProgram = loadShaderProgram(VERT_SHADER_SRC_MASKED, FRAG_SHADER_SRC_MASK_INVERTED);
            shaderSets.get(4).shaderProgram = loadShaderProgram(VERT_SHADER_SRC, FRAG_SHADER_SRC_PREMULTIPLIED_ALPHA);
            shaderSets.get(5).shaderProgram = loadShaderProgram(VERT_SHADER_SRC_MASKED, FRAG_SHADER_SRC_MASK_PREMULTIPLIED_ALPHA);
            shaderSets.get(6).shaderProgram = loadShaderProgram(VERT_SHADER_SRC_MASKED, FRAG_SHADER_SRC_MASK_INVERTED_PREMULTIPLIED_ALPHA);
        }

        // TODO: javadoc
        // 加算も通常と同じシェーダーを利用する
        shaderSets.get(7).shaderProgram = shaderSets.get(1).shaderProgram;
        shaderSets.get(8).shaderProgram = shaderSets.get(2).shaderProgram;
        shaderSets.get(9).shaderProgram = shaderSets.get(3).shaderProgram;
        shaderSets.get(10).shaderProgram = shaderSets.get(4).shaderProgram;
        shaderSets.get(11).shaderProgram = shaderSets.get(5).shaderProgram;
        shaderSets.get(12).shaderProgram = shaderSets.get(6).shaderProgram;

        // 乗算も通常と同じシェーダーを利用する
        shaderSets.get(13).shaderProgram = shaderSets.get(1).shaderProgram;
        shaderSets.get(14).shaderProgram = shaderSets.get(2).shaderProgram;
        shaderSets.get(15).shaderProgram = shaderSets.get(3).shaderProgram;
        shaderSets.get(16).shaderProgram = shaderSets.get(4).shaderProgram;
        shaderSets.get(17).shaderProgram = shaderSets.get(5).shaderProgram;
        shaderSets.get(18).shaderProgram = shaderSets.get(6).shaderProgram;

        // Setup mask
        shaderSets.get(0).attributePositionLocation = glGetAttribLocation(shaderSets.get(0).shaderProgram, "a_position");
        shaderSets.get(0).attributeTexCoordLocation = glGetAttribLocation(shaderSets.get(0).shaderProgram, "a_texCoord");
        shaderSets.get(0).samplerTexture0Location = glGetUniformLocation(shaderSets.get(0).shaderProgram, "s_texture0");
        shaderSets.get(0).uniformClipMatrixLocation = glGetUniformLocation(shaderSets.get(0).shaderProgram, "u_clipMatrix");
        shaderSets.get(0).uniformChannelFlagLocation = glGetUniformLocation(shaderSets.get(0).shaderProgram, "u_channelFlag");
        shaderSets.get(0).uniformBaseColorLocation = glGetUniformLocation(shaderSets.get(0).shaderProgram, "u_baseColor");
        shaderSets.get(0).uniformMultiplyColorLocation = glGetUniformLocation(shaderSets.get(0).shaderProgram, "u_multiplyColor");
        shaderSets.get(0).uniformScreenColorLocation = glGetUniformLocation(shaderSets.get(0).shaderProgram, "u_screenColor");

        // 通常
        shaderSets.get(1).attributePositionLocation = glGetAttribLocation(shaderSets.get(1).shaderProgram, "a_position");
        shaderSets.get(1).attributeTexCoordLocation = glGetAttribLocation(shaderSets.get(1).shaderProgram, "a_texCoord");
        shaderSets.get(1).samplerTexture0Location = glGetUniformLocation(shaderSets.get(1).shaderProgram, "s_texture0");
        shaderSets.get(1).uniformMatrixLocation = glGetUniformLocation(shaderSets.get(1).shaderProgram, "u_matrix");
        shaderSets.get(1).uniformBaseColorLocation = glGetUniformLocation(shaderSets.get(1).shaderProgram, "u_baseColor");
        shaderSets.get(1).uniformMultiplyColorLocation = glGetUniformLocation(shaderSets.get(1).shaderProgram, "u_multiplyColor");
        shaderSets.get(1).uniformScreenColorLocation = glGetUniformLocation(shaderSets.get(1).shaderProgram, "u_screenColor");

        // 通常（クリッピング）
        shaderSets.get(2).attributePositionLocation = glGetAttribLocation(shaderSets.get(2).shaderProgram, "a_position");
        shaderSets.get(2).attributeTexCoordLocation = glGetAttribLocation(shaderSets.get(2).shaderProgram, "a_texCoord");
        shaderSets.get(2).samplerTexture0Location = glGetUniformLocation(shaderSets.get(2).shaderProgram, "s_texture0");
        shaderSets.get(2).samplerTexture1Location = glGetUniformLocation(shaderSets.get(2).shaderProgram, "s_texture1");
        shaderSets.get(2).uniformMatrixLocation = glGetUniformLocation(shaderSets.get(2).shaderProgram, "u_matrix");
        shaderSets.get(2).uniformClipMatrixLocation = glGetUniformLocation(shaderSets.get(2).shaderProgram, "u_clipMatrix");
        shaderSets.get(2).uniformChannelFlagLocation = glGetUniformLocation(shaderSets.get(2).shaderProgram, "u_channelFlag");
        shaderSets.get(2).uniformBaseColorLocation = glGetUniformLocation(shaderSets.get(2).shaderProgram, "u_baseColor");
        shaderSets.get(2).uniformMultiplyColorLocation = glGetUniformLocation(shaderSets.get(2).shaderProgram, "u_multiplyColor");
        shaderSets.get(2).uniformScreenColorLocation = glGetUniformLocation(shaderSets.get(2).shaderProgram, "u_screenColor");

        // 通常（クリッピング・反転）
        shaderSets.get(3).attributePositionLocation = glGetAttribLocation(shaderSets.get(3).shaderProgram, "a_position");
        shaderSets.get(3).attributeTexCoordLocation = glGetAttribLocation(shaderSets.get(3).shaderProgram, "a_texCoord");
        shaderSets.get(3).samplerTexture0Location = glGetUniformLocation(shaderSets.get(3).shaderProgram, "s_texture0");
        shaderSets.get(3).samplerTexture1Location = glGetUniformLocation(shaderSets.get(3).shaderProgram, "s_texture1");
        shaderSets.get(3).uniformMatrixLocation = glGetUniformLocation(shaderSets.get(3).shaderProgram, "u_matrix");
        shaderSets.get(3).uniformClipMatrixLocation = glGetUniformLocation(shaderSets.get(3).shaderProgram, "u_clipMatrix");
        shaderSets.get(3).uniformChannelFlagLocation = glGetUniformLocation(shaderSets.get(3).shaderProgram, "u_channelFlag");
        shaderSets.get(3).uniformBaseColorLocation = glGetUniformLocation(shaderSets.get(3).shaderProgram, "u_baseColor");
        shaderSets.get(3).uniformMultiplyColorLocation = glGetUniformLocation(shaderSets.get(3).shaderProgram, "u_multiplyColor");
        shaderSets.get(3).uniformScreenColorLocation = glGetUniformLocation(shaderSets.get(3).shaderProgram, "u_screenColor");

        // 通常（PremultipliedAlpha）
        shaderSets.get(4).attributePositionLocation = glGetAttribLocation(shaderSets.get(4).shaderProgram, "a_position");
        shaderSets.get(4).attributeTexCoordLocation = glGetAttribLocation(shaderSets.get(4).shaderProgram, "a_texCoord");
        shaderSets.get(4).samplerTexture0Location = glGetUniformLocation(shaderSets.get(4).shaderProgram, "s_texture0");
        shaderSets.get(4).uniformMatrixLocation = glGetUniformLocation(shaderSets.get(4).shaderProgram, "u_matrix");
        shaderSets.get(4).uniformBaseColorLocation = glGetUniformLocation(shaderSets.get(4).shaderProgram, "u_baseColor");
        shaderSets.get(4).uniformMultiplyColorLocation = glGetUniformLocation(shaderSets.get(4).shaderProgram, "u_multiplyColor");
        shaderSets.get(4).uniformScreenColorLocation = glGetUniformLocation(shaderSets.get(4).shaderProgram, "u_screenColor");

        // 通常（クリッピング、PremultipliedAlpha）
        shaderSets.get(5).attributePositionLocation = glGetAttribLocation(shaderSets.get(5).shaderProgram, "a_position");
        shaderSets.get(5).attributeTexCoordLocation = glGetAttribLocation(shaderSets.get(5).shaderProgram, "a_texCoord");
        shaderSets.get(5).samplerTexture0Location = glGetUniformLocation(shaderSets.get(5).shaderProgram, "s_texture0");
        shaderSets.get(5).samplerTexture1Location = glGetUniformLocation(shaderSets.get(5).shaderProgram, "s_texture1");
        shaderSets.get(5).uniformMatrixLocation = glGetUniformLocation(shaderSets.get(5).shaderProgram, "u_matrix");
        shaderSets.get(5).uniformClipMatrixLocation = glGetUniformLocation(shaderSets.get(5).shaderProgram, "u_clipMatrix");
        shaderSets.get(5).uniformChannelFlagLocation = glGetUniformLocation(shaderSets.get(5).shaderProgram, "u_channelFlag");
        shaderSets.get(5).uniformBaseColorLocation = glGetUniformLocation(shaderSets.get(5).shaderProgram, "u_baseColor");
        shaderSets.get(5).uniformMultiplyColorLocation = glGetUniformLocation(shaderSets.get(5).shaderProgram, "u_multiplyColor");
        shaderSets.get(5).uniformScreenColorLocation = glGetUniformLocation(shaderSets.get(5).shaderProgram, "u_screenColor");

        // 通常（クリッピング・反転、PremultipliedAlpha）
        shaderSets.get(6).attributePositionLocation = glGetAttribLocation(shaderSets.get(6).shaderProgram, "a_position");
        shaderSets.get(6).attributeTexCoordLocation = glGetAttribLocation(shaderSets.get(6).shaderProgram, "a_texCoord");
        shaderSets.get(6).samplerTexture0Location = glGetUniformLocation(shaderSets.get(6).shaderProgram, "s_texture0");
        shaderSets.get(6).samplerTexture1Location = glGetUniformLocation(shaderSets.get(6).shaderProgram, "s_texture1");
        shaderSets.get(6).uniformMatrixLocation = glGetUniformLocation(shaderSets.get(6).shaderProgram, "u_matrix");
        shaderSets.get(6).uniformClipMatrixLocation = glGetUniformLocation(shaderSets.get(6).shaderProgram, "u_clipMatrix");
        shaderSets.get(6).uniformChannelFlagLocation = glGetUniformLocation(shaderSets.get(6).shaderProgram, "u_channelFlag");
        shaderSets.get(6).uniformBaseColorLocation = glGetUniformLocation(shaderSets.get(6).shaderProgram, "u_baseColor");
        shaderSets.get(6).uniformMultiplyColorLocation = glGetUniformLocation(shaderSets.get(6).shaderProgram, "u_multiplyColor");
        shaderSets.get(6).uniformScreenColorLocation = glGetUniformLocation(shaderSets.get(6).shaderProgram, "u_screenColor");

        // 加算
        shaderSets.get(7).attributePositionLocation = glGetAttribLocation(shaderSets.get(7).shaderProgram, "a_position");
        shaderSets.get(7).attributeTexCoordLocation = glGetAttribLocation(shaderSets.get(7).shaderProgram, "a_texCoord");
        shaderSets.get(7).samplerTexture0Location = glGetUniformLocation(shaderSets.get(7).shaderProgram, "s_texture0");
        shaderSets.get(7).uniformMatrixLocation = glGetUniformLocation(shaderSets.get(7).shaderProgram, "u_matrix");
        shaderSets.get(7).uniformBaseColorLocation = glGetUniformLocation(shaderSets.get(7).shaderProgram, "u_baseColor");
        shaderSets.get(7).uniformMultiplyColorLocation = glGetUniformLocation(shaderSets.get(7).shaderProgram, "u_multiplyColor");
        shaderSets.get(7).uniformScreenColorLocation = glGetUniformLocation(shaderSets.get(7).shaderProgram, "u_screenColor");

        // 加算（クリッピング）
        shaderSets.get(8).attributePositionLocation = glGetAttribLocation(shaderSets.get(8).shaderProgram, "a_position");
        shaderSets.get(8).attributeTexCoordLocation = glGetAttribLocation(shaderSets.get(8).shaderProgram, "a_texCoord");
        shaderSets.get(8).samplerTexture0Location = glGetUniformLocation(shaderSets.get(8).shaderProgram, "s_texture0");
        shaderSets.get(8).samplerTexture1Location = glGetUniformLocation(shaderSets.get(8).shaderProgram, "s_texture1");
        shaderSets.get(8).uniformMatrixLocation = glGetUniformLocation(shaderSets.get(8).shaderProgram, "u_matrix");
        shaderSets.get(8).uniformClipMatrixLocation = glGetUniformLocation(shaderSets.get(8).shaderProgram, "u_clipMatrix");
        shaderSets.get(8).uniformChannelFlagLocation = glGetUniformLocation(shaderSets.get(8).shaderProgram, "u_channelFlag");
        shaderSets.get(8).uniformBaseColorLocation = glGetUniformLocation(shaderSets.get(8).shaderProgram, "u_baseColor");
        shaderSets.get(8).uniformMultiplyColorLocation = glGetUniformLocation(shaderSets.get(8).shaderProgram, "u_multiplyColor");
        shaderSets.get(8).uniformScreenColorLocation = glGetUniformLocation(shaderSets.get(8).shaderProgram, "u_screenColor");

        // 加算（クリッピング・反転）
        shaderSets.get(9).attributePositionLocation = glGetAttribLocation(shaderSets.get(9).shaderProgram, "a_position");
        shaderSets.get(9).attributeTexCoordLocation = glGetAttribLocation(shaderSets.get(9).shaderProgram, "a_texCoord");
        shaderSets.get(9).samplerTexture0Location = glGetUniformLocation(shaderSets.get(9).shaderProgram, "s_texture0");
        shaderSets.get(9).samplerTexture1Location = glGetUniformLocation(shaderSets.get(9).shaderProgram, "s_texture1");
        shaderSets.get(9).uniformMatrixLocation = glGetUniformLocation(shaderSets.get(9).shaderProgram, "u_matrix");
        shaderSets.get(9).uniformClipMatrixLocation = glGetUniformLocation(shaderSets.get(9).shaderProgram, "u_clipMatrix");
        shaderSets.get(9).uniformChannelFlagLocation = glGetUniformLocation(shaderSets.get(9).shaderProgram, "u_channelFlag");
        shaderSets.get(9).uniformBaseColorLocation = glGetUniformLocation(shaderSets.get(9).shaderProgram, "u_baseColor");
        shaderSets.get(9).uniformMultiplyColorLocation = glGetUniformLocation(shaderSets.get(9).shaderProgram, "u_multiplyColor");
        shaderSets.get(9).uniformScreenColorLocation = glGetUniformLocation(shaderSets.get(9).shaderProgram, "u_screenColor");

        // 加算（PremultipliedAlpha）
        shaderSets.get(10).attributePositionLocation = glGetAttribLocation(shaderSets.get(10).shaderProgram, "a_position");
        shaderSets.get(10).attributeTexCoordLocation = glGetAttribLocation(shaderSets.get(10).shaderProgram, "a_texCoord");
        shaderSets.get(10).samplerTexture0Location = glGetUniformLocation(shaderSets.get(10).shaderProgram, "s_texture0");
        shaderSets.get(10).uniformMatrixLocation = glGetUniformLocation(shaderSets.get(10).shaderProgram, "u_matrix");
        shaderSets.get(10).uniformBaseColorLocation = glGetUniformLocation(shaderSets.get(10).shaderProgram, "u_baseColor");
        shaderSets.get(10).uniformMultiplyColorLocation = glGetUniformLocation(shaderSets.get(10).shaderProgram, "u_multiplyColor");
        shaderSets.get(10).uniformScreenColorLocation = glGetUniformLocation(shaderSets.get(10).shaderProgram, "u_screenColor");

        // 加算（クリッピング、PremultipliedAlpha）
        shaderSets.get(11).attributePositionLocation = glGetAttribLocation(shaderSets.get(11).shaderProgram, "a_position");
        shaderSets.get(11).attributeTexCoordLocation = glGetAttribLocation(shaderSets.get(11).shaderProgram, "a_texCoord");
        shaderSets.get(11).samplerTexture0Location = glGetUniformLocation(shaderSets.get(11).shaderProgram, "s_texture0");
        shaderSets.get(11).samplerTexture1Location = glGetUniformLocation(shaderSets.get(11).shaderProgram, "s_texture1");
        shaderSets.get(11).uniformMatrixLocation = glGetUniformLocation(shaderSets.get(11).shaderProgram, "u_matrix");
        shaderSets.get(11).uniformClipMatrixLocation = glGetUniformLocation(shaderSets.get(11).shaderProgram, "u_clipMatrix");
        shaderSets.get(11).uniformChannelFlagLocation = glGetUniformLocation(shaderSets.get(11).shaderProgram, "u_channelFlag");
        shaderSets.get(11).uniformBaseColorLocation = glGetUniformLocation(shaderSets.get(11).shaderProgram, "u_baseColor");
        shaderSets.get(11).uniformMultiplyColorLocation = glGetUniformLocation(shaderSets.get(11).shaderProgram, "u_multiplyColor");
        shaderSets.get(11).uniformScreenColorLocation = glGetUniformLocation(shaderSets.get(11).shaderProgram, "u_screenColor");

        // 加算（クリッピング・反転、PremultipliedAlpha）
        shaderSets.get(12).attributePositionLocation = glGetAttribLocation(shaderSets.get(12).shaderProgram, "a_position");
        shaderSets.get(12).attributeTexCoordLocation = glGetAttribLocation(shaderSets.get(12).shaderProgram, "a_texCoord");
        shaderSets.get(12).samplerTexture0Location = glGetUniformLocation(shaderSets.get(12).shaderProgram, "s_texture0");
        shaderSets.get(12).samplerTexture1Location = glGetUniformLocation(shaderSets.get(12).shaderProgram, "s_texture1");
        shaderSets.get(12).uniformMatrixLocation = glGetUniformLocation(shaderSets.get(12).shaderProgram, "u_matrix");
        shaderSets.get(12).uniformClipMatrixLocation = glGetUniformLocation(shaderSets.get(12).shaderProgram, "u_clipMatrix");
        shaderSets.get(12).uniformChannelFlagLocation = glGetUniformLocation(shaderSets.get(12).shaderProgram, "u_channelFlag");
        shaderSets.get(12).uniformBaseColorLocation = glGetUniformLocation(shaderSets.get(12).shaderProgram, "u_baseColor");
        shaderSets.get(12).uniformMultiplyColorLocation = glGetUniformLocation(shaderSets.get(12).shaderProgram, "u_multiplyColor");
        shaderSets.get(12).uniformScreenColorLocation = glGetUniformLocation(shaderSets.get(12).shaderProgram, "u_screenColor");

        // 乗算
        shaderSets.get(13).attributePositionLocation = glGetAttribLocation(shaderSets.get(13).shaderProgram, "a_position");
        shaderSets.get(13).attributeTexCoordLocation = glGetAttribLocation(shaderSets.get(13).shaderProgram, "a_texCoord");
        shaderSets.get(13).samplerTexture0Location = glGetUniformLocation(shaderSets.get(13).shaderProgram, "s_texture0");
        shaderSets.get(13).uniformMatrixLocation = glGetUniformLocation(shaderSets.get(13).shaderProgram, "u_matrix");
        shaderSets.get(13).uniformBaseColorLocation = glGetUniformLocation(shaderSets.get(13).shaderProgram, "u_baseColor");
        shaderSets.get(13).uniformMultiplyColorLocation = glGetUniformLocation(shaderSets.get(13).shaderProgram, "u_multiplyColor");
        shaderSets.get(13).uniformScreenColorLocation = glGetUniformLocation(shaderSets.get(13).shaderProgram, "u_screenColor");

        // 乗算（クリッピング）
        shaderSets.get(14).attributePositionLocation = glGetAttribLocation(shaderSets.get(14).shaderProgram, "a_position");
        shaderSets.get(14).attributeTexCoordLocation = glGetAttribLocation(shaderSets.get(14).shaderProgram, "a_texCoord");
        shaderSets.get(14).samplerTexture0Location = glGetUniformLocation(shaderSets.get(14).shaderProgram, "s_texture0");
        shaderSets.get(14).samplerTexture1Location = glGetUniformLocation(shaderSets.get(14).shaderProgram, "s_texture1");
        shaderSets.get(14).uniformMatrixLocation = glGetUniformLocation(shaderSets.get(14).shaderProgram, "u_matrix");
        shaderSets.get(14).uniformClipMatrixLocation = glGetUniformLocation(shaderSets.get(14).shaderProgram, "u_clipMatrix");
        shaderSets.get(14).uniformChannelFlagLocation = glGetUniformLocation(shaderSets.get(14).shaderProgram, "u_channelFlag");
        shaderSets.get(14).uniformBaseColorLocation = glGetUniformLocation(shaderSets.get(14).shaderProgram, "u_baseColor");
        shaderSets.get(14).uniformMultiplyColorLocation = glGetUniformLocation(shaderSets.get(14).shaderProgram, "u_multiplyColor");
        shaderSets.get(14).uniformScreenColorLocation = glGetUniformLocation(shaderSets.get(14).shaderProgram, "u_screenColor");

        // 乗算（クリッピング・反転）
        shaderSets.get(15).attributePositionLocation = glGetAttribLocation(shaderSets.get(15).shaderProgram, "a_position");
        shaderSets.get(15).attributeTexCoordLocation = glGetAttribLocation(shaderSets.get(15).shaderProgram, "a_texCoord");
        shaderSets.get(15).samplerTexture0Location = glGetUniformLocation(shaderSets.get(15).shaderProgram, "s_texture0");
        shaderSets.get(15).samplerTexture1Location = glGetUniformLocation(shaderSets.get(15).shaderProgram, "s_texture1");
        shaderSets.get(15).uniformMatrixLocation = glGetUniformLocation(shaderSets.get(15).shaderProgram, "u_matrix");
        shaderSets.get(15).uniformClipMatrixLocation = glGetUniformLocation(shaderSets.get(15).shaderProgram, "u_clipMatrix");
        shaderSets.get(15).uniformChannelFlagLocation = glGetUniformLocation(shaderSets.get(15).shaderProgram, "u_channelFlag");
        shaderSets.get(15).uniformBaseColorLocation = glGetUniformLocation(shaderSets.get(15).shaderProgram, "u_baseColor");
        shaderSets.get(15).uniformMultiplyColorLocation = glGetUniformLocation(shaderSets.get(15).shaderProgram, "u_multiplyColor");
        shaderSets.get(15).uniformScreenColorLocation = glGetUniformLocation(shaderSets.get(15).shaderProgram, "u_screenColor");

        // 乗算（PremultipliedAlpha）
        shaderSets.get(16).attributePositionLocation = glGetAttribLocation(shaderSets.get(16).shaderProgram, "a_position");
        shaderSets.get(16).attributeTexCoordLocation = glGetAttribLocation(shaderSets.get(16).shaderProgram, "a_texCoord");
        shaderSets.get(16).samplerTexture0Location = glGetUniformLocation(shaderSets.get(16).shaderProgram, "s_texture0");
        shaderSets.get(16).uniformMatrixLocation = glGetUniformLocation(shaderSets.get(16).shaderProgram, "u_matrix");
        shaderSets.get(16).uniformBaseColorLocation = glGetUniformLocation(shaderSets.get(16).shaderProgram, "u_baseColor");
        shaderSets.get(16).uniformMultiplyColorLocation = glGetUniformLocation(shaderSets.get(16).shaderProgram, "u_multiplyColor");
        shaderSets.get(16).uniformScreenColorLocation = glGetUniformLocation(shaderSets.get(16).shaderProgram, "u_screenColor");

        // 乗算（クリッピング、PremultipliedAlpha）
        shaderSets.get(17).attributePositionLocation = glGetAttribLocation(shaderSets.get(17).shaderProgram, "a_position");
        shaderSets.get(17).attributeTexCoordLocation = glGetAttribLocation(shaderSets.get(17).shaderProgram, "a_texCoord");
        shaderSets.get(17).samplerTexture0Location = glGetUniformLocation(shaderSets.get(17).shaderProgram, "s_texture0");
        shaderSets.get(17).samplerTexture1Location = glGetUniformLocation(shaderSets.get(17).shaderProgram, "s_texture1");
        shaderSets.get(17).uniformMatrixLocation = glGetUniformLocation(shaderSets.get(17).shaderProgram, "u_matrix");
        shaderSets.get(17).uniformClipMatrixLocation = glGetUniformLocation(shaderSets.get(17).shaderProgram, "u_clipMatrix");
        shaderSets.get(17).uniformChannelFlagLocation = glGetUniformLocation(shaderSets.get(17).shaderProgram, "u_channelFlag");
        shaderSets.get(17).uniformBaseColorLocation = glGetUniformLocation(shaderSets.get(17).shaderProgram, "u_baseColor");
        shaderSets.get(17).uniformMultiplyColorLocation = glGetUniformLocation(shaderSets.get(17).shaderProgram, "u_multiplyColor");
        shaderSets.get(17).uniformScreenColorLocation = glGetUniformLocation(shaderSets.get(17).shaderProgram, "u_screenColor");

        // 乗算（クリッピング・反転、PremultipliedAlpha）
        shaderSets.get(18).attributePositionLocation = glGetAttribLocation(shaderSets.get(18).shaderProgram, "a_position");
        shaderSets.get(18).attributeTexCoordLocation = glGetAttribLocation(shaderSets.get(18).shaderProgram, "a_texCoord");
        shaderSets.get(18).samplerTexture0Location = glGetUniformLocation(shaderSets.get(18).shaderProgram, "s_texture0");
        shaderSets.get(18).samplerTexture1Location = glGetUniformLocation(shaderSets.get(18).shaderProgram, "s_texture1");
        shaderSets.get(18).uniformMatrixLocation = glGetUniformLocation(shaderSets.get(18).shaderProgram, "u_matrix");
        shaderSets.get(18).uniformClipMatrixLocation = glGetUniformLocation(shaderSets.get(18).shaderProgram, "u_clipMatrix");
        shaderSets.get(18).uniformChannelFlagLocation = glGetUniformLocation(shaderSets.get(18).shaderProgram, "u_channelFlag");
        shaderSets.get(18).uniformBaseColorLocation = glGetUniformLocation(shaderSets.get(18).shaderProgram, "u_baseColor");
        shaderSets.get(18).uniformMultiplyColorLocation = glGetUniformLocation(shaderSets.get(18).shaderProgram, "u_multiplyColor");
        shaderSets.get(18).uniformScreenColorLocation = glGetUniformLocation(shaderSets.get(18).shaderProgram, "u_screenColor");
    }

    private void setAttribLocation(final int shaderIndex) {
        CubismShaderSet shader = shaderSets.get(shaderIndex);

        shader.attributePositionLocation = glGetAttribLocation(shader.shaderProgram, "a_position");
        shader.attributeTexCoordLocation = glGetAttribLocation(shader.shaderProgram, "a_texCoord");
        shader.samplerTexture0Location = glGetUniformLocation(shader.shaderProgram, "s_texture0");
        shader.uniformMatrixLocation = glGetUniformLocation(shader.shaderProgram, "u_matrix");
        shader.uniformBaseColorLocation = glGetUniformLocation(shader.shaderProgram, "u_baseColor");
    }

    private void setAttribLocationClipping(final int shaderIndex) {
        CubismShaderSet shader = shaderSets.get(shaderIndex);

        shader.attributePositionLocation = glGetAttribLocation(shader.shaderProgram, "a_position");
        shader.attributeTexCoordLocation = glGetAttribLocation(shader.shaderProgram, "a_texCoord");
        shader.samplerTexture0Location = glGetUniformLocation(shader.shaderProgram, "s_texture0");
        shader.samplerTexture1Location = glGetUniformLocation(shader.shaderProgram, "s_texture1");
        shader.uniformMatrixLocation = glGetUniformLocation(shader.shaderProgram, "u_matrix");
        shader.uniformClipMatrixLocation = glGetUniformLocation(shader.shaderProgram, "u_clipMatrix");
        shader.uniformChannelFlagLocation = glGetUniformLocation(shader.shaderProgram, "u_channelFlag");
        shader.uniformBaseColorLocation = glGetUniformLocation(shader.shaderProgram, "u_baseColor");
    }

    /**
     * Load shader program.
     *
     * @param vertShaderSrc source of vertex shader
     * @param fragShaderSrc source of fragment shader
     * @return reference value to the shader program
     */
    private int loadShaderProgram(final String vertShaderSrc, final String fragShaderSrc) {
        int[] vertShader = new int[1];
        int[] fragShader = new int[1];

        // Create shader program.
        int shaderProgram = glCreateProgram();

        if (!compileShaderSource(vertShader, GL_VERTEX_SHADER, vertShaderSrc)) {
            cubismLogError("Vertex shader compile error!");
            return 0;
        }

        // Create and compile fragment shader.
        if (!compileShaderSource(fragShader, GL_FRAGMENT_SHADER, fragShaderSrc)) {
            cubismLogError("Fragment shader compile error!");
            return 0;
        }

        // Attach vertex shader to program.
        glAttachShader(shaderProgram, vertShader[0]);
        // Attach fragment shader to program.
        glAttachShader(shaderProgram, fragShader[0]);

        // Link program.
        if (!linkProgram(shaderProgram)) {
            cubismLogError("Failed to link program: " + shaderProgram);

            glDeleteShader(vertShader[0]);
            glDeleteShader(fragShader[0]);
            glDeleteProgram(shaderProgram);

            return 0;
        }

        // Release vertex and fragment shaders.
        glDetachShader(shaderProgram, vertShader[0]);
        glDeleteShader(vertShader[0]);

        glDetachShader(shaderProgram, fragShader[0]);
        glDeleteShader(fragShader[0]);

        return shaderProgram;
    }

    /**
     * Compile shader program.
     *
     * @param shader reference value to compiled shader program
     * @param shaderType shader type(Vertex/Fragment)
     * @param shaderSource source of shader program
     * @return If compilling succeeds, return true
     */
    private boolean compileShaderSource(int[] shader, int shaderType, final String shaderSource) {
        if (shader == null || shader.length == 0) {
            return false;
        }

        shader[0] = glCreateShader(shaderType);

        glShaderSource(shader[0], shaderSource);
        glCompileShader(shader[0]);

        int[] logLength = new int[1];
        glGetShaderiv(shader[0], GL_INFO_LOG_LENGTH, IntBuffer.wrap(logLength));
        if (logLength[0] > 0) {
            String log = glGetShaderInfoLog(shader[0]);
            cubismLogError("Shader compile log: " + log);
        }

        int[] status = new int[1];
        glGetShaderiv(shader[0], GL_COMPILE_STATUS, IntBuffer.wrap(status));
        if (status[0] == GL_FALSE) {
            glDeleteShader(shader[0]);
            return false;
        }
        return true;
    }

    /**
     * Link shader program.
     *
     * @param shaderProgram reference value to a shader program to link
     * @return If linking succeeds, return true
     */
    private boolean linkProgram(int shaderProgram) {
        glLinkProgram(shaderProgram);

        int[] logLength = new int[1];
        glGetProgramiv(shaderProgram, GL_INFO_LOG_LENGTH, IntBuffer.wrap(logLength));
        if (logLength[0] > 0) {
            String log = glGetProgramInfoLog(shaderProgram);
            cubismLogError("Program link log: " + log);
        }

        int[] status = new int[1];
        glGetProgramiv(shaderProgram, GL_LINK_STATUS, IntBuffer.wrap(status));
        return status[0] != GL_FALSE;
    }

    /**
     * Validate shader program.
     *
     * @param shaderProgram reference value to shader program to be validated
     * @return If there is no problem, return true
     */
    private boolean validateProgram(int shaderProgram) {
        glValidateProgram(shaderProgram);

        int[] logLength = new int[1];
        glGetProgramiv(shaderProgram, GL_INFO_LOG_LENGTH, IntBuffer.wrap(logLength));
        if (logLength[0] > 0) {
            String log = glGetProgramInfoLog(shaderProgram);
            cubismLogError("Validate program log: " + log);
        }

        int[] status = new int[1];
        glGetProgramiv(shaderProgram, GL_VALIDATE_STATUS, IntBuffer.wrap(status));
        return status[0] != GL_FALSE;
    }


    /**
     * Variable that holds the loaded shader program.
     */
    private final List<CubismShaderSet> shaderSets = new ArrayList<CubismShaderSet>();
}
