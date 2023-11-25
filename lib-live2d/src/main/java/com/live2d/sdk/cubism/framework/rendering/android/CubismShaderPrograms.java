/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.rendering.android;

/**
 * This class has raw GLSL codes.
 * Number of shaders = for generating mask + (normal + adding + premultiplication) * (without mask + with mask + with mask inverted + without mask for premultiplied alpha + with mask for premultiplied alpha + with mask inverted for premultiplied alpha)
 */
class CubismShaderPrograms {
    /**
     * The enum class that represents the precision of floating point numbers used in GLSL.
     */
    public enum CsmFragmentShaderFpPrecision {
        HIGH("highp"),
        MID("mediump"),
        LOW("lowp");

        private final String value;

        CsmFragmentShaderFpPrecision(String value) {
            this.value = value;
        }
    }

    /**
     * Number of GLSL codes.
     * <p>
     * Number of the shaders = for generating masks + (Normal + Add + Multiply) * (No mask + mask + inverted mask + no mask for premultiplied alpha + mask for premultiplied alpha + inverted mask for premultiplied alpha)
     * </p>
     */
    public static final int SHADER_COUNT = 19;

    /**
     * Floating point number's precision used in GLSL. (Default value: "highp")
     */
    private static CsmFragmentShaderFpPrecision precision = CsmFragmentShaderFpPrecision.HIGH;

    /**
     * Vertex shader code for setting up mask.
     */
    public static final String VERT_SHADER_SRC_SETUP_MASK =
        "#version 100\n"
            + "attribute vec4 a_position;\n"
            + "attribute vec2 a_texCoord;\n"
            + "varying vec2 v_texCoord;\n"
            + "varying vec4 v_myPos;\n"
            + "uniform mat4 u_clipMatrix;\n"
            + "void main()\n"
            + "{\n"
            + "gl_Position = u_clipMatrix * a_position;\n"
            + "v_myPos = u_clipMatrix * a_position;\n"
            + "v_texCoord = a_texCoord;\n"
            + "v_texCoord.y = 1.0 - v_texCoord.y;\n"
            + "}";
    /**
     * Fragment shader code for setting up mask.
     */
    public static final String FRAG_SHADER_SRC_SETUP_MASK =
        "#version 100\n"
            + "precision " + precision.value + " float;\n"
            + "varying vec2 v_texCoord;\n"
            + "varying vec4 v_myPos;\n"
            + "uniform sampler2D s_texture0;\n"
            + "uniform vec4 u_channelFlag;\n"
            + "uniform vec4 u_baseColor;\n"
            + "void main()\n"
            + "{\n"
            + "float isInside = "
            + "step(u_baseColor.x, v_myPos.x/v_myPos.w)"
            + "* step(u_baseColor.y, v_myPos.y/v_myPos.w)"
            + "* step(v_myPos.x/v_myPos.w, u_baseColor.z)"
            + "* step(v_myPos.y/v_myPos.w, u_baseColor.w);\n"
            + "gl_FragColor = u_channelFlag * texture2D(s_texture0 , v_texCoord).a * isInside;\n"
            + "}";
    /**
     * Fragment shader code for setting up mask for Tegra
     */
    public static final String FRAG_SHADER_SRC_SETUP_MASK_TEGRA =
        "#version 100\n"
            + "#extension GL_NV_shader_framebuffer_fetch : enable\n"
            + "precision " + precision.value + " float;\n"
            + "varying vec2 v_texCoord;\n"
            + "varying vec4 v_myPos;\n"
            + "uniform sampler2D s_texture0;\n"
            + "uniform vec4 u_channelFlag;\n"
            + "uniform vec4 u_baseColor;\n"
            + "void main()\n"
            + "{\n"
            + "float isInside = "
            + "step(u_baseColor.x, v_myPos.x/v_myPos.w)"
            + "* step(u_baseColor.y, v_myPos.y/v_myPos.w)"
            + "* step(v_myPos.x/v_myPos.w, u_baseColor.z)"
            + "* step(v_myPos.y/v_myPos.w, u_baseColor.w);\n"

            + "gl_FragColor = u_channelFlag * texture2D(s_texture0 , v_texCoord).a * isInside;\n"
            + "}";

    //----- Vertex Shader Programs -----
    // Normal & Add & Multi common part
    /**
     * Vertex shader code.
     */
    public static final String VERT_SHADER_SRC =
        "#version 100\n"
            + "attribute vec4 a_position;\n"  // v.vertex
            + "attribute vec2 a_texCoord;\n"  // v.texcoord
            + "varying vec2 v_texCoord;\n"  // v2f.texcoord
            + "uniform mat4 u_matrix;\n"
            + "void main()\n"
            + "{\n"
            + "gl_Position = u_matrix * a_position;\n"
            + "v_texCoord = a_texCoord;\n"
            + "v_texCoord.y = 1.0 - v_texCoord.y;\n"
            + "}";

    /**
     * Vertex shader code for masked things.
     */
    public static final String VERT_SHADER_SRC_MASKED =
        "#version 100\n"
            + "attribute vec4 a_position;\n"
            + "attribute vec2 a_texCoord;\n"
            + "varying vec2 v_texCoord;\n"
            + "varying vec4 v_clipPos;\n"
            + "uniform mat4 u_matrix;\n"
            + "uniform mat4 u_clipMatrix;\n"
            + "void main()\n"
            + "{\n"
            + "gl_Position = u_matrix * a_position;\n"
            + "v_clipPos = u_clipMatrix * a_position;\n"
            + "v_texCoord = a_texCoord;\n"
            + "v_texCoord.y = 1.0 - v_texCoord.y;\n"
            + "}";

    //----- Fragment Shader Programs -----
    // Normal & Add & Mult common part
    /**
     * Fragment shader code.
     */
    public static final String FRAG_SHADER_SRC =
        "#version 100\n"
            + "precision " + precision.value + " float;\n"
            + "varying vec2 v_texCoord;\n"  // v2f.texcoord
            + "uniform sampler2D s_texture0;\n"  // _MainTex
            + "uniform vec4 u_baseColor;\n"  // v2f.color
            + "uniform vec4 u_multiplyColor;\n"
            + "uniform vec4 u_screenColor;\n"
            + "void main()\n"
            + "{\n"
            + "vec4 texColor = texture2D(s_texture0, v_texCoord);\n"
            + "texColor.rgb = texColor.rgb * u_multiplyColor.rgb;\n"
            + "texColor.rgb = texColor.rgb + u_screenColor.rgb - (texColor.rgb * u_screenColor.rgb);\n"
            + "vec4 color = texColor * u_baseColor;\n"
            + "gl_FragColor = vec4(color.rgb * color.a,  color.a);\n"
            + "}";
    /**
     * Fragment shader code for Tegra.
     */
    public static final String FRAG_SHADER_SRC_TEGRA =
        "#version 100\n"
            + "#extension GL_NV_shader_framebuffer_fetch : enable\n"
            + "precision " + precision.value + " float;\n"
            + "varying vec2 v_texCoord;\n"  //v2f.texcoord
            + "uniform sampler2D s_texture0;\n"  //_MainTex
            + "uniform vec4 u_baseColor;\n"  //v2f.color
            + "uniform vec4 u_multiplyColor;\n"
            + "uniform vec4 u_screenColor;\n"
            + "void main()\n"
            + "{\n"
            + "vec4 texColor = texture2D(s_texture0, v_texCoord);\n"
            + "texColor.rgb = texColor.rgb * u_multiplyColor.rgb;\n"
            + "texColor.rgb = texColor.rgb + u_screenColor.rgb - (texColor.rgb * u_screenColor.rgb);\n"
            + "vec4 color = texColor * u_baseColor;\n"
            + "gl_FragColor = vec4(color.rgb * color.a,  color.a);\n"
            + "}";

    // Normal & Add & Mult common (PremultipliedAlpha)
    /**
     * Fragment shader code for premultipiled alpha.
     */
    public static final String FRAG_SHADER_SRC_PREMULTIPLIED_ALPHA =
        "#version 100\n"
            + "precision " + precision.value + " float;\n"
            + "varying vec2 v_texCoord;\n"  // v2f.texcoord
            + "uniform sampler2D s_texture0;\n"  // _MainTex
            + "uniform vec4 u_baseColor;\n"  // v2f.color
            + "uniform vec4 u_multiplyColor;\n"
            + "uniform vec4 u_screenColor;\n"
            + "void main()\n"
            + "{\n"
            + "vec4 texColor = texture2D(s_texture0, v_texCoord);\n"
            + "texColor.rgb = texColor.rgb * u_multiplyColor.rgb;\n"
            + "texColor.rgb = (texColor.rgb + u_screenColor.rgb * texColor.a) - (texColor.rgb * u_screenColor.rgb);\n"
            + "gl_FragColor = texColor * u_baseColor;\n"
            + "}";
    /**
     * Fragment shader code for premultiplied alpha for Tegra.
     */
    public static final String FRAG_SHADER_SRC_PREMULTIPLIED_ALPHA_TEGRA =
        "#version 100\n"
            + "#extension GL_NV_shader_framebuffer_fetch : enable\n"
            + "precision " + precision.value + " float;\n"
            + "varying vec2 v_texCoord;\n"  //v2f.texcoord
            + "uniform sampler2D s_texture0;\n"  //_MainTex
            + "uniform vec4 u_baseColor;\n"  //v2f.color
            + "uniform vec4 u_multiplyColor;\n"
            + "uniform vec4 u_screenColor;\n"
            + "void main()\n"
            + "{\n"
            + "vec4 texColor = texture2D(s_texture0, v_texCoord);\n"
            + "texColor.rgb = texColor.rgb * u_multiplyColor.rgb;\n"
            + "texColor.rgb = (texColor.rgb + u_screenColor.rgb * texColor.a) - (texColor.rgb * u_screenColor.rgb);\n"
            + "gl_FragColor = texColor * u_baseColor;\n"
            + "}";

    // Normal & Add & Mult common(for drawing the clipped thing)
    /**
     * Fragment shader code for masked things.
     */
    public static final String FRAG_SHADER_SRC_MASK =
        "#version 100\n"
            + "precision " + precision.value + " float;\n"
            + "varying vec2 v_texCoord;\n"
            + "varying vec4 v_clipPos;\n"
            + "uniform sampler2D s_texture0;\n"
            + "uniform sampler2D s_texture1;\n"
            + "uniform vec4 u_channelFlag;\n"
            + "uniform vec4 u_baseColor;\n"
            + "uniform vec4 u_multiplyColor;\n"
            + "uniform vec4 u_screenColor;\n"
            + "void main()\n"
            + "{\n"
            + "vec4 texColor = texture2D(s_texture0, v_texCoord);\n"
            + "texColor.rgb = texColor.rgb * u_multiplyColor.rgb;\n"
            + "texColor.rgb = texColor.rgb + u_screenColor.rgb - (texColor.rgb * u_screenColor.rgb);\n"
            + "vec4 col_formask = texColor * u_baseColor;\n"
            + "col_formask.rgb = col_formask.rgb  * col_formask.a;\n"
            + "vec4 clipMask = (1.0 - texture2D(s_texture1, v_clipPos.xy / v_clipPos.w)) * u_channelFlag;\n"
            + "float maskVal = clipMask.r + clipMask.g + clipMask.b + clipMask.a;\n"
            + "col_formask = col_formask * maskVal;\n"
            + "gl_FragColor = col_formask;\n"
            + "}";

    /**
     * Fragment shader code for masked things. (for Tegra)
     */
    public static final String FRAG_SHADER_SRC_MASK_TEGRA =
        "#version 100\n"
            + "#extension GL_NV_shader_framebuffer_fetch : enable\n"
            + "precision " + precision.value + " float;\n"
            + "varying vec2 v_texCoord;\n"
            + "varying vec4 v_clipPos;\n"
            + "uniform sampler2D s_texture0;\n"
            + "uniform sampler2D s_texture1;\n"
            + "uniform vec4 u_channelFlag;\n"
            + "uniform vec4 u_baseColor;\n"
            + "void main()\n"
            + "{\n"
            + "vec4 col_formask = texture2D(s_texture0 , v_texCoord) * u_baseColor;\n"
            + "col_formask.rgb = col_formask.rgb  * col_formask.a;\n"
            + "vec4 clipMask = (1.0 - texture2D(s_texture1, v_clipPos.xy / v_clipPos.w)) * u_channelFlag;\n"
            + "float maskVal = clipMask.r + clipMask.g + clipMask.b + clipMask.a;\n"
            + "col_formask = col_formask * maskVal;\n"
            + "gl_FragColor = col_formask;\n"
            + "}";

    // Normal & Add & Mult common (For clipped and reversed use drawing)
    /**
     * Fragment shader code for inverted mask.
     */
    public static final String FRAG_SHADER_SRC_MASK_INVERTED =
        "#version 100\n"
            + "precision " + precision.value + " float;\n"
            + "varying vec2 v_texCoord;\n"
            + "varying vec4 v_clipPos;\n"
            + "uniform sampler2D s_texture0;\n"
            + "uniform sampler2D s_texture1;\n"
            + "uniform vec4 u_channelFlag;\n"
            + "uniform vec4 u_baseColor;\n"
            + "uniform vec4 u_multiplyColor;\n"
            + "uniform vec4 u_screenColor;\n"
            + "void main()\n"
            + "{\n"
            + "vec4 texColor = texture2D(s_texture0, v_texCoord);\n"
            + "texColor.rgb = texColor.rgb * u_multiplyColor.rgb;\n"
            + "texColor.rgb = texColor.rgb + u_screenColor.rgb - (texColor.rgb * u_screenColor.rgb);\n"
            + "vec4 col_formask = texColor * u_baseColor;\n"
            + "col_formask.rgb = col_formask.rgb  * col_formask.a;\n"
            + "vec4 clipMask = (1.0 - texture2D(s_texture1, v_clipPos.xy / v_clipPos.w)) * u_channelFlag;\n"
            + "float maskVal = clipMask.r + clipMask.g + clipMask.b + clipMask.a;\n"
            + "col_formask = col_formask * (1.0 - maskVal);\n"
            + "gl_FragColor = col_formask;\n"
            + "}";

    /**
     * Fragment shader code for inverted mask. (for Tegra)
     */
    public static final String FRAG_SHADER_SRC_MASK_INVERTED_TEGRA =
        "#version 100\n"
            + "#extension GL_NV_shader_framebuffer_fetch : enable\n"
            + "precision " + precision.value + " float;\n"
            + "varying vec2 v_texCoord;\n"
            + "varying vec4 v_clipPos;\n"
            + "uniform sampler2D s_texture0;\n"
            + "uniform sampler2D s_texture1;\n"
            + "uniform vec4 u_channelFlag;\n"
            + "uniform vec4 u_baseColor;\n"
            + "void main()\n"
            + "{\n"
            + "vec4 col_formask = texture2D(s_texture0 , v_texCoord) * u_baseColor;\n"
            + "col_formask.rgb = col_formask.rgb  * col_formask.a;\n"
            + "vec4 clipMask = (1.0 - texture2D(s_texture1, v_clipPos.xy / v_clipPos.w)) * u_channelFlag;\n"
            + "float maskVal = clipMask.r + clipMask.g + clipMask.b + clipMask.a;\n"
            + "col_formask = col_formask * (1.0 - maskVal);\n"
            + "gl_FragColor = col_formask;\n"
            + "}";

    /**
     * Fragment shader code for the masked things at premultiplied alpha.
     */
    public static final String FRAG_SHADER_SRC_MASK_PREMULTIPLIED_ALPHA =
        "#version 100\n"
            + "precision " + precision.value + " float;\n"
            + "varying vec2 v_texCoord;\n"
            + "varying vec4 v_clipPos;\n"
            + "uniform sampler2D s_texture0;\n"
            + "uniform sampler2D s_texture1;\n"
            + "uniform vec4 u_channelFlag;\n"
            + "uniform vec4 u_baseColor;\n"
            + "uniform vec4 u_multiplyColor;\n"
            + "uniform vec4 u_screenColor;\n"
            + "void main()\n"
            + "{\n"
            + "vec4 texColor = texture2D(s_texture0, v_texCoord);\n"
            + "texColor.rgb = texColor.rgb * u_multiplyColor.rgb;\n"
            + "texColor.rgb = (texColor.rgb + u_screenColor.rgb * texColor.a) - (texColor.rgb * u_screenColor.rgb);\n"
            + "vec4 col_formask = texColor * u_baseColor;\n"
            + "vec4 clipMask = (1.0 - texture2D(s_texture1, v_clipPos.xy / v_clipPos.w)) * u_channelFlag;\n"
            + "float maskVal = clipMask.r + clipMask.g + clipMask.b + clipMask.a;\n"
            + "col_formask = col_formask * maskVal;\n"
            + "gl_FragColor = col_formask;\n"
            + "}";

    /**
     * Fragment shader code for the masked things at premultiplied alpha. (for Tegra)
     */
    public static final String FRAG_SHADER_SRC_MASK_PREMULTIPLIED_ALPHA_TEGRA =
        "#version 100\n"
            + "#extension GL_NV_shader_framebuffer_fetch : enable\n"
            + "precision " + precision.value + " float;\n"
            + "varying vec2 v_texCoord;\n"
            + "varying vec4 v_clipPos;\n"
            + "uniform sampler2D s_texture0;\n"
            + "uniform sampler2D s_texture1;\n"
            + "uniform vec4 u_channelFlag;\n"
            + "uniform vec4 u_baseColor;\n"
            + "void main()\n"
            + "{\n"
            + "vec4 col_formask = texture2D(s_texture0 , v_texCoord) * u_baseColor;\n"
            + "vec4 clipMask = (1.0 - texture2D(s_texture1, v_clipPos.xy / v_clipPos.w)) * u_channelFlag;\n"
            + "float maskVal = clipMask.r + clipMask.g + clipMask.b + clipMask.a;\n"
            + "col_formask = col_formask * maskVal;\n"
            + "gl_FragColor = col_formask;\n"
            + "}";

    // Normal & Add & Mult common (For clipped and reversed use drawing, in the case of PremultipliedAlpha)
    /**
     * Fragment shader code for inverted mask at premultiplied alpha.
     */
    public static final String FRAG_SHADER_SRC_MASK_INVERTED_PREMULTIPLIED_ALPHA =
        "#version 100\n"
            + "precision " + precision.value + " float;\n"
            + "varying vec2 v_texCoord;\n"
            + "varying vec4 v_clipPos;\n"
            + "uniform sampler2D s_texture0;\n"
            + "uniform sampler2D s_texture1;\n"
            + "uniform vec4 u_channelFlag;\n"
            + "uniform vec4 u_baseColor;\n"
            + "uniform vec4 u_multiplyColor;\n"
            + "uniform vec4 u_screenColor;\n"
            + "void main()\n"
            + "{\n"
            + "vec4 texColor = texture2D(s_texture0, v_texCoord);\n"
            + "texColor.rgb = texColor.rgb * u_multiplyColor.rgb;\n"
            + "texColor.rgb = (texColor.rgb + u_screenColor.rgb * texColor.a) - (texColor.rgb * u_screenColor.rgb);\n"
            + "vec4 col_formask = texColor * u_baseColor;\n"
            + "vec4 clipMask = (1.0 - texture2D(s_texture1, v_clipPos.xy / v_clipPos.w)) * u_channelFlag;\n"
            + "float maskVal = clipMask.r + clipMask.g + clipMask.b + clipMask.a;\n"
            + "col_formask = col_formask * (1.0 - maskVal);\n"
            + "gl_FragColor = col_formask;\n"
            + "}";

    /**
     * Fragment shader code for inverted mask at premultiplied alpha(for Tegra)
     */
    public static final String FRAG_SHADER_SRC_MASK_INVERTED_PREMULTIPLIED_ALPHA_TEGRA =
        "#version 100\n"
            + "#extension GL_NV_shader_framebuffer_fetch : enable\n"
            + "precision " + precision.value + " float;\n"
            + "varying vec2 v_texCoord;\n"
            + "varying vec4 v_clipPos;\n"
            + "uniform sampler2D s_texture0;\n"
            + "uniform sampler2D s_texture1;\n"
            + "uniform vec4 u_channelFlag;\n"
            + "uniform vec4 u_baseColor;\n"
            + "void main()\n"
            + "{\n"
            + "vec4 col_formask = texture2D(s_texture0 , v_texCoord) * u_baseColor;\n"
            + "vec4 clipMask = (1.0 - texture2D(s_texture1, v_clipPos.xy / v_clipPos.w)) * u_channelFlag;\n"
            + "float maskVal = clipMask.r + clipMask.g + clipMask.b + clipMask.a;\n"
            + "col_formask = col_formask * (1.0 - maskVal);\n"
            + "gl_FragColor = col_formask;\n"
            + "}";

    /**
     * Set the floating point number's precision used in GLSL.
     *
     * @param p precision
     */
    public void setFragmentShaderFpPrecision(CubismShaderPrograms.CsmFragmentShaderFpPrecision p) {
        precision = p;
    }
}
