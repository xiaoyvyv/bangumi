#ifndef LIVE2D_SHADERS_H
#define LIVE2D_SHADERS_H

#include <map>
#include <string>

// Auto-generated 36 GLSL StandardES Framework Shaders from Cubism SDK
static const std::map<std::string, std::string> g_embeddedShaders = {
    {"FragShaderSrc.frag", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#version 100

precision highp float;

varying vec2 v_texCoord; //v2f.texcoord
uniform sampler2D s_texture0; //_MainTex
uniform vec4 u_baseColor; //v2f.color
uniform vec4 u_multiplyColor;
uniform vec4 u_screenColor;

void main()
{
    vec4 texColor = texture2D(s_texture0, v_texCoord);
    texColor.rgb = texColor.rgb * u_multiplyColor.rgb;
    texColor.rgb = texColor.rgb + u_screenColor.rgb - (texColor.rgb * u_screenColor.rgb);
    vec4 color = texColor * u_baseColor;
    gl_FragColor = vec4(color.rgb * color.a, color.a);
}
)"},
    {"FragShaderSrcAlphaBlend.frag", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#if !defined CSM_ALPHA_BLEND_MODE
#error not define symbol: CSM_ALPHA_BLEND_MODE.

#else
vec4 OverlapRgba(vec3 color, vec3 colorSource, vec3 colorDestination, vec3 parameter)
{
    vec3 rgb = color * parameter.x + colorSource * parameter.y + colorDestination * parameter.z;
    float alpha = parameter.x + parameter.y + parameter.z;
    return vec4(rgb, alpha);
}

#if CSM_ALPHA_BLEND_MODE == 0 // OVER
vec4 AlphaBlend(vec3 color, vec4 colorSource, vec4 colorDestination)
{
    vec3 parameter = vec3(colorSource.a * colorDestination.a, colorSource.a * (1.0 - colorDestination.a), colorDestination.a * (1.0 - colorSource.a));
    return OverlapRgba(color, colorSource.rgb, colorDestination.rgb, parameter);
}

#elif CSM_ALPHA_BLEND_MODE == 1 // ATOP
vec4 AlphaBlend(vec3 color, vec4 colorSource, vec4 colorDestination)
{
    vec3 parameter = vec3(colorSource.a * colorDestination.a, 0, colorDestination.a * (1.0 - colorSource.a));
    return OverlapRgba(color, colorSource.rgb, colorDestination.rgb, parameter);
}

#elif CSM_ALPHA_BLEND_MODE == 2 // OUT
vec4 AlphaBlend(vec3 color, vec4 colorSource, vec4 colorDestination)
{
    vec3 parameter = vec3(0.0, 0.0, colorDestination.a * (1.0 - colorSource.a));
    return OverlapRgba(color, colorSource.rgb, colorDestination.rgb, parameter);
}

#elif CSM_ALPHA_BLEND_MODE == 3 // CONJOINT OVER
vec4 AlphaBlend(vec3 color, vec4 colorSource, vec4 colorDestination)
{
    vec3 parameter = vec3(min(colorSource.a, colorDestination.a), max(colorSource.a - colorDestination.a, 0.0), max(colorDestination.a - colorSource.a, 0.0));
    return OverlapRgba(color, colorSource.rgb, colorDestination.rgb, parameter);
}

#elif CSM_ALPHA_BLEND_MODE == 4 // DISJOINT OVER
vec4 AlphaBlend(vec3 color, vec4 colorSource, vec4 colorDestination)
{
    vec3 parameter = vec3(max(colorSource.a + colorDestination.a - 1.0, 0.0), min(colorSource.a, 1.0 - colorDestination.a), min(colorDestination.a, 1.0 - colorSource.a));
    return OverlapRgba(color, colorSource.rgb, colorDestination.rgb, parameter);
}

#else
#error not supported blend function

#endif
#endif
)"},
    {"FragShaderSrcBlend.frag", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#version 100

precision highp float;

varying vec2 v_texCoord; //v2f.texcoord
varying vec2 v_blendCoord;
uniform sampler2D s_texture0; //_MainTex
uniform sampler2D s_blendTexture;
uniform vec4 u_baseColor; //v2f.color
uniform vec4 u_multiplyColor;
uniform vec4 u_screenColor;

vec4 ConvertPremultipliedToStraight(vec4 source);
vec3 ColorBlend(vec3 colorSource, vec3 colorDestination);
vec4 AlphaBlend(vec3 color, vec4 colorSource, vec4 colorDestination);

void main()
{
    vec4 texColor = texture2D(s_texture0, v_texCoord);
    texColor.rgb = texColor.rgb * u_multiplyColor.rgb;
    texColor.rgb = texColor.rgb + u_screenColor.rgb - (texColor.rgb * u_screenColor.rgb);
    vec4 colorSource = texColor * u_baseColor;
    vec4 colorDestination = ConvertPremultipliedToStraight(texture2D(s_blendTexture, v_blendCoord));
    gl_FragColor = AlphaBlend(ColorBlend(colorSource.rgb, colorDestination.rgb), colorSource, colorDestination);
}

)"},
    {"FragShaderSrcBlendTegra.frag", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#version 100
#extension GL_NV_shader_framebuffer_fetch : enable

precision highp float;

varying vec2 v_texCoord; //v2f.texcoord
varying vec2 v_blendCoord;
uniform sampler2D s_texture0; //_MainTex
uniform sampler2D s_blendTexture;
uniform vec4 u_baseColor; //v2f.color
uniform vec4 u_multiplyColor;
uniform vec4 u_screenColor;

vec4 ConvertPremultipliedToStraight(vec4 source);
vec3 ColorBlend(vec3 colorSource, vec3 colorDestination);
vec4 AlphaBlend(vec3 color, vec4 colorSource, vec4 colorDestination);

void main()
{
    vec4 texColor = texture2D(s_texture0, v_texCoord);
    texColor.rgb = texColor.rgb * u_multiplyColor.rgb;
    texColor.rgb = texColor.rgb + u_screenColor.rgb - (texColor.rgb * u_screenColor.rgb);
    vec4 colorSource = texColor * u_baseColor;
    vec4 colorDestination = ConvertPremultipliedToStraight(texture2D(s_blendTexture, v_blendCoord));
    gl_FragColor = AlphaBlend(ColorBlend(colorSource.rgb, colorDestination.rgb), colorSource, colorDestination);
}

)"},
    {"FragShaderSrcColorBlend.frag", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

vec4 ConvertPremultipliedToStraight(vec4 source)
{
    if (abs(source.a) < 0.00001)
    {
        source.rgb = vec3(0.0, 0.0, 0.0);
    }
    else
    {
        source.rgb /= source.a;
    }

    return source;
}

#if !defined CSM_COLOR_BLEND_MODE
#error not define symbol: CSM_COLOR_BLEND_MODE.

#elif CSM_COLOR_BLEND_MODE == 0 // NORMAL
vec3 ColorBlend(vec3 colorSource, vec3 colorDestination)
{
    return colorSource;
}

#elif CSM_COLOR_BLEND_MODE == 1 // ADD
vec3 ColorBlend(vec3 colorSource, vec3 colorDestination)
{
    return min(colorSource + colorDestination, 1.0);
}

#elif CSM_COLOR_BLEND_MODE == 2 // ADD(GLOW)
vec3 ColorBlend(vec3 colorSource, vec3 colorDestination)
{
    return colorSource + colorDestination;
}

#elif CSM_COLOR_BLEND_MODE == 3 // DARKEN
vec3 ColorBlend(vec3 colorSource, vec3 colorDestination)
{
    return min(colorSource, colorDestination);
}

#elif CSM_COLOR_BLEND_MODE == 4 // MULTIPLY
vec3 ColorBlend(vec3 colorSource, vec3 colorDestination)
{
    return colorSource * colorDestination;
}

#elif CSM_COLOR_BLEND_MODE == 5 // COLOR BURN
float ColorBurn(float colorSource, float colorDestination)
{
    if (abs(colorDestination - 1.0) < 0.000001)
    {
        return 1.0;
    }
    else if (abs(colorSource) < 0.000001)
    {
        return 0.0;
    }

    return 1.0 - min(1.0, (1.0 - colorDestination) / colorSource);
}

vec3 ColorBlend(vec3 colorSource, vec3 colorDestination)
{
    return vec3(
        ColorBurn(colorSource.r, colorDestination.r),
        ColorBurn(colorSource.g, colorDestination.g),
        ColorBurn(colorSource.b, colorDestination.b)
    );
}

#elif CSM_COLOR_BLEND_MODE == 6 // LINEAR BURN
vec3 ColorBlend(vec3 colorSource, vec3 colorDestination)
{
    return max(vec3(0.0), colorSource + colorDestination - 1.0);
}

#elif CSM_COLOR_BLEND_MODE == 7 // LIGHTEN
vec3 ColorBlend(vec3 colorSource, vec3 colorDestination)
{
    return max(colorSource, colorDestination);
}

#elif CSM_COLOR_BLEND_MODE == 8 // SCREEN
vec3 ColorBlend(vec3 colorSource, vec3 colorDestination)
{
    return colorSource + colorDestination - colorSource * colorDestination;
}

#elif CSM_COLOR_BLEND_MODE == 9 // COLOR DODGE
float ColorDodge(float colorSource, float colorDestination)
{
    if (colorDestination <= 0.0)
    {
        return 0.0;
    }
    else if (colorSource == 1.0)
    {
        return 1.0;
    }

    return min(1.0, colorDestination / (1.0 - colorSource));
}

vec3 ColorBlend(vec3 colorSource, vec3 colorDestination)
{
    return vec3(
        ColorDodge(colorSource.r, colorDestination.r),
        ColorDodge(colorSource.g, colorDestination.g),
        ColorDodge(colorSource.b, colorDestination.b)
    );
}

#elif CSM_COLOR_BLEND_MODE == 10 // OVERLAY
float Overlay(float colorSource, float colorDestination)
{
    float mul = 2.0 * colorSource * colorDestination;
    float scr = 1.0 - 2.0 * (1.0 - colorSource) * (1.0 - colorDestination) ;
    return colorDestination < 0.5 ? mul : scr ;
}

vec3 ColorBlend(vec3 colorSource, vec3 colorDestination)
{
    return vec3(
        Overlay(colorSource.r, colorDestination.r),
        Overlay(colorSource.g, colorDestination.g),
        Overlay(colorSource.b, colorDestination.b)
    );
}

#elif CSM_COLOR_BLEND_MODE == 11 // SOFT LIGHT
float SoftLight(float colorSource, float colorDestination)
{
    float val1 = colorDestination - (1.0 - 2.0 * colorSource) * colorDestination * (1.0 - colorDestination);
    float val2 = colorDestination + (2.0 * colorSource - 1.0) * colorDestination * ((16.0 * colorDestination - 12.0) * colorDestination + 3.0);
    float val3 = colorDestination + (2.0 * colorSource - 1.0) * (sqrt(colorDestination) - colorDestination);

    if (colorSource <= 0.5)
    {
        return val1;
    }
    else if (colorDestination <= 0.25)
    {
        return val2;
    }

    return val3;
}

vec3 ColorBlend(vec3 colorSource, vec3 colorDestination)
{
    return vec3(
        SoftLight(colorSource.r, colorDestination.r),
        SoftLight(colorSource.g, colorDestination.g),
        SoftLight(colorSource.b, colorDestination.b)
    );
}

#elif CSM_COLOR_BLEND_MODE == 12 // HARD LIGHT
float HardLight(float colorSource, float colorDestination)
{
    float mul = 2.0 * colorSource * colorDestination;
    float scr = 1.0 - 2.0 * (1.0 - colorSource) * (1.0 - colorDestination);

    if (colorSource < 0.5)
    {
        return mul;
    }

    return scr;
}

vec3 ColorBlend(vec3 colorSource, vec3 colorDestination)
{
    return vec3(
        HardLight(colorSource.r, colorDestination.r),
        HardLight(colorSource.g, colorDestination.g),
        HardLight(colorSource.b, colorDestination.b)
    );
}

#elif CSM_COLOR_BLEND_MODE == 13 // LINEAR LIGHT
float LinearLight(float colorSource, float colorDestination)
{
    float burn = max(0.0, 2.0 * colorSource + colorDestination - 1.0);
    float dodge = min(1.0, 2.0 * (colorSource - 0.5) + colorDestination);

    if (colorSource < 0.5)
    {
        return burn;
    }

    return dodge;
}

vec3 ColorBlend(vec3 colorSource, vec3 colorDestination)
{
    return vec3(
        LinearLight(colorSource.r, colorDestination.r),
        LinearLight(colorSource.g, colorDestination.g),
        LinearLight(colorSource.b, colorDestination.b)
    );
}

#elif CSM_COLOR_BLEND_MODE == 14 || CSM_COLOR_BLEND_MODE == 15
const float rCoeff = 0.30;
const float gCoeff = 0.59;
const float bCoeff = 0.11;

float GetMax(vec3 rgbC)
{
    return max(rgbC.r, max(rgbC.g, rgbC.b));
}

float GetMin(vec3 rgbC)
{
    return min(rgbC.r, min(rgbC.g, rgbC.b));
}

float GetRange(vec3 rgbC)
{
    return max(rgbC.r, max(rgbC.g, rgbC.b)) - min(rgbC.r, min(rgbC.g, rgbC.b));
}

float Saturation(vec3 rgbC)
{
    return GetRange(rgbC);
}

float Luma(vec3 rgbC)
{
    return rCoeff * rgbC.r + gCoeff * rgbC.g + bCoeff * rgbC.b;
}

vec3 ClipColor(vec3 rgbC)
{
    float   luma = Luma(rgbC);
    float   maxv = GetMax(rgbC);
    float   minv = GetMin(rgbC);
    vec3    outputColor = rgbC;

    outputColor = minv < 0.0 ? luma + (outputColor - luma) * luma / (luma - minv) : outputColor;
    outputColor = maxv > 1.0 ? luma + (outputColor - luma) * (1.0 - luma) / (maxv - luma) : outputColor;

    return outputColor;
}

vec3 SetLuma(vec3 rgbC, float luma)
{
    return ClipColor(rgbC + (luma - Luma(rgbC)));
}

vec3 SetSaturation(vec3 rgbC, float saturation)
{
    float maxv = GetMax(rgbC);
    float minv = GetMin(rgbC);
    float medv = rgbC.r + rgbC.g + rgbC.b - maxv - minv;
    float outputMax, outputMed, outputMin;

    outputMax = minv < maxv ? saturation : 0.0;
    outputMed = minv < maxv ? (medv - minv) * saturation / (maxv - minv) : 0.0;
    outputMin = 0.0;

    if(rgbC.r == maxv)
    {
        return rgbC.b < rgbC.g ? vec3(outputMax, outputMed, outputMin) : vec3(outputMax, outputMin, outputMed);
    }
    else if(rgbC.g == maxv)
    {
        return rgbC.r < rgbC.b ? vec3(outputMin, outputMax, outputMed) : vec3(outputMed, outputMax, outputMin);
    }
    else // if(rgbC.b == maxv)
    {
        return rgbC.g < rgbC.r ? vec3(outputMed, outputMin, outputMax) : vec3(outputMin, outputMed, outputMax);
    }
}

#if CSM_COLOR_BLEND_MODE == 14 // HUE
vec3 ColorBlend(vec3 colorSource, vec3 colorDestination)
{
    return SetLuma(SetSaturation(colorSource, Saturation(colorDestination)), Luma(colorDestination));
}

#elif CSM_COLOR_BLEND_MODE == 15 // COLOR
vec3 ColorBlend(vec3 colorSource, vec3 colorDestination)
{
    return SetLuma(colorSource, Luma(colorDestination)) ;
}

#endif

#else
#error not supported blend function.

#endif
)"},
    {"FragShaderSrcCopy.frag", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#version 100

precision highp float;

varying vec2 v_texCoord;
uniform sampler2D s_texture0;
uniform vec4 u_baseColor;

void main()
{
    gl_FragColor = texture2D(s_texture0, v_texCoord) * u_baseColor;
}
)"},
    {"FragShaderSrcCopyTegra.frag", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#version 100
#extension GL_NV_shader_framebuffer_fetch : enable

precision highp float;

varying vec2 v_texCoord;
uniform sampler2D s_texture0;
uniform vec4 u_baseColor;

void main()
{
    gl_FragColor = texture2D(s_texture0, v_texCoord) * u_baseColor;
}
)"},
    {"FragShaderSrcMask.frag", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#version 100

precision highp float;

varying vec2 v_texCoord;
varying vec4 v_clipPos;
uniform sampler2D s_texture0;
uniform sampler2D s_texture1;
uniform vec4 u_channelFlag;
uniform vec4 u_baseColor;
uniform vec4 u_multiplyColor;
uniform vec4 u_screenColor;

void main()
{
    vec4 texColor = texture2D(s_texture0, v_texCoord);
    texColor.rgb = texColor.rgb * u_multiplyColor.rgb;
    texColor.rgb = texColor.rgb + u_screenColor.rgb - (texColor.rgb * u_screenColor.rgb);
    vec4 col_formask = texColor * u_baseColor;
    col_formask.rgb = col_formask.rgb  * col_formask.a;
    vec4 clipMask = (1.0 - texture2D(s_texture1, v_clipPos.xy / v_clipPos.w)) * u_channelFlag;
    float maskVal = clipMask.r + clipMask.g + clipMask.b + clipMask.a;
    col_formask = col_formask * maskVal;
    gl_FragColor = col_formask;
}
)"},
    {"FragShaderSrcMaskBlend.frag", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#version 100

precision highp float;

varying vec2 v_texCoord;
varying vec2 v_blendCoord;
varying vec4 v_clipPos;
uniform sampler2D s_texture0;
uniform sampler2D s_texture1;
uniform sampler2D s_blendTexture;
uniform vec4 u_channelFlag;
uniform vec4 u_baseColor;
uniform vec4 u_multiplyColor;
uniform vec4 u_screenColor;

vec4 ConvertPremultipliedToStraight(vec4 source);
vec3 ColorBlend(vec3 colorSource, vec3 colorDestination);
vec4 AlphaBlend(vec3 color, vec4 colorSource, vec4 colorDestination);

void main()
{
    vec4 texColor = texture2D(s_texture0, v_texCoord);
    texColor.rgb = texColor.rgb * u_multiplyColor.rgb;
    texColor.rgb = texColor.rgb + u_screenColor.rgb - (texColor.rgb * u_screenColor.rgb);
    vec4 col_formask = texColor * u_baseColor;
    vec4 clipMask = (1.0 - texture2D(s_texture1, v_clipPos.xy / v_clipPos.w)) * u_channelFlag;
    float maskVal = clipMask.r + clipMask.g + clipMask.b + clipMask.a;
    vec4 colorSource = vec4(col_formask.rgb, col_formask.a * maskVal);
    vec4 colorDestination = ConvertPremultipliedToStraight(texture2D(s_blendTexture, v_blendCoord));
    gl_FragColor = AlphaBlend(ColorBlend(colorSource.rgb, colorDestination.rgb), colorSource, colorDestination);
}
)"},
    {"FragShaderSrcMaskBlendTegra.frag", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#version 100
#extension GL_NV_shader_framebuffer_fetch : enable

precision highp float;

varying vec2 v_texCoord;
varying vec2 v_blendCoord;
varying vec4 v_clipPos;
uniform sampler2D s_texture0;
uniform sampler2D s_texture1;
uniform sampler2D s_blendTexture;
uniform vec4 u_channelFlag;
uniform vec4 u_baseColor;
uniform vec4 u_multiplyColor;
uniform vec4 u_screenColor;

vec4 ConvertPremultipliedToStraight(vec4 source);
vec3 ColorBlend(vec3 colorSource, vec3 colorDestination);
vec4 AlphaBlend(vec3 color, vec4 colorSource, vec4 colorDestination);

void main()
{
    vec4 texColor = texture2D(s_texture0, v_texCoord);
    texColor.rgb = texColor.rgb * u_multiplyColor.rgb;
    texColor.rgb = texColor.rgb + u_screenColor.rgb - (texColor.rgb * u_screenColor.rgb);
    vec4 col_formask = texColor * u_baseColor;
    vec4 clipMask = (1.0 - texture2D(s_texture1, v_clipPos.xy / v_clipPos.w)) * u_channelFlag;
    float maskVal = clipMask.r + clipMask.g + clipMask.b + clipMask.a;
    vec4 colorSource = vec4(col_formask.rgb, col_formask.a * maskVal);
    vec4 colorDestination = ConvertPremultipliedToStraight(texture2D(s_blendTexture, v_blendCoord));
    gl_FragColor = AlphaBlend(ColorBlend(colorSource.rgb, colorDestination.rgb), colorSource, colorDestination);
}
)"},
    {"FragShaderSrcMaskInverted.frag", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#version 100

precision highp float;

varying vec2 v_texCoord;
varying vec4 v_clipPos;
uniform sampler2D s_texture0;
uniform sampler2D s_texture1;
uniform vec4 u_channelFlag;
uniform vec4 u_baseColor;
uniform vec4 u_multiplyColor;
uniform vec4 u_screenColor;

void main()
{
    vec4 texColor = texture2D(s_texture0, v_texCoord);
    texColor.rgb = texColor.rgb * u_multiplyColor.rgb;
    texColor.rgb = texColor.rgb + u_screenColor.rgb - (texColor.rgb * u_screenColor.rgb);
    vec4 col_formask = texColor * u_baseColor;
    col_formask.rgb = col_formask.rgb  * col_formask.a;
    vec4 clipMask = (1.0 - texture2D(s_texture1, v_clipPos.xy / v_clipPos.w)) * u_channelFlag;
    float maskVal = clipMask.r + clipMask.g + clipMask.b + clipMask.a;
    col_formask = col_formask * (1.0 - maskVal);
    gl_FragColor = col_formask;
}
)"},
    {"FragShaderSrcMaskInvertedBlend.frag", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#version 100

precision highp float;

varying vec2 v_texCoord;
varying vec2 v_blendCoord;
varying vec4 v_clipPos;
uniform sampler2D s_texture0;
uniform sampler2D s_texture1;
uniform sampler2D s_blendTexture;
uniform vec4 u_channelFlag;
uniform vec4 u_baseColor;
uniform vec4 u_multiplyColor;
uniform vec4 u_screenColor;

vec4 ConvertPremultipliedToStraight(vec4 source);
vec3 ColorBlend(vec3 colorSource, vec3 colorDestination);
vec4 AlphaBlend(vec3 color, vec4 colorSource, vec4 colorDestination);

void main()
{
    vec4 texColor = texture2D(s_texture0, v_texCoord);
    texColor.rgb = texColor.rgb * u_multiplyColor.rgb;
    texColor.rgb = texColor.rgb + u_screenColor.rgb - (texColor.rgb * u_screenColor.rgb);
    vec4 col_formask = texColor * u_baseColor;
    vec4 clipMask = (1.0 - texture2D(s_texture1, v_clipPos.xy / v_clipPos.w)) * u_channelFlag;
    float maskVal = clipMask.r + clipMask.g + clipMask.b + clipMask.a;
    vec4 colorSource = vec4(col_formask.rgb, col_formask.a * (1.0 - maskVal));
    vec4 colorDestination = ConvertPremultipliedToStraight(texture2D(s_blendTexture, v_blendCoord));
    gl_FragColor = AlphaBlend(ColorBlend(colorSource.rgb, colorDestination.rgb), colorSource, colorDestination);
}
)"},
    {"FragShaderSrcMaskInvertedBlendTegra.frag", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#version 100
#extension GL_NV_shader_framebuffer_fetch : enable

precision highp float;

varying vec2 v_texCoord;
varying vec2 v_blendCoord;
varying vec4 v_clipPos;
uniform sampler2D s_texture0;
uniform sampler2D s_texture1;
uniform sampler2D s_blendTexture;
uniform vec4 u_channelFlag;
uniform vec4 u_baseColor;
uniform vec4 u_multiplyColor;
uniform vec4 u_screenColor;

vec4 ConvertPremultipliedToStraight(vec4 source);
vec3 ColorBlend(vec3 colorSource, vec3 colorDestination);
vec4 AlphaBlend(vec3 color, vec4 colorSource, vec4 colorDestination);

void main()
{
    vec4 texColor = texture2D(s_texture0, v_texCoord);
    texColor.rgb = texColor.rgb * u_multiplyColor.rgb;
    texColor.rgb = texColor.rgb + u_screenColor.rgb - (texColor.rgb * u_screenColor.rgb);
    vec4 col_formask = texColor * u_baseColor;
    vec4 clipMask = (1.0 - texture2D(s_texture1, v_clipPos.xy / v_clipPos.w)) * u_channelFlag;
    float maskVal = clipMask.r + clipMask.g + clipMask.b + clipMask.a;
    vec4 colorSource = vec4(col_formask.rgb, col_formask.a * (1.0 - maskVal));
    vec4 colorDestination = ConvertPremultipliedToStraight(texture2D(s_blendTexture, v_blendCoord));
    gl_FragColor = AlphaBlend(ColorBlend(colorSource.rgb, colorDestination.rgb), colorSource, colorDestination);
}
)"},
    {"FragShaderSrcMaskInvertedPremultipliedAlpha.frag", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#version 100

precision highp float;

varying vec2 v_texCoord;
varying vec4 v_clipPos;
uniform sampler2D s_texture0;
uniform sampler2D s_texture1;
uniform vec4 u_channelFlag;
uniform vec4 u_baseColor;
uniform vec4 u_multiplyColor;
uniform vec4 u_screenColor;

void main()
{
    vec4 texColor = texture2D(s_texture0, v_texCoord);
    texColor.rgb = texColor.rgb * u_multiplyColor.rgb;
    texColor.rgb = (texColor.rgb + u_screenColor.rgb * texColor.a) - (texColor.rgb * u_screenColor.rgb);
    vec4 col_formask = texColor * u_baseColor;
    vec4 clipMask = (1.0 - texture2D(s_texture1, v_clipPos.xy / v_clipPos.w)) * u_channelFlag;
    float maskVal = clipMask.r + clipMask.g + clipMask.b + clipMask.a;
    col_formask = col_formask * (1.0 - maskVal);
    gl_FragColor = col_formask;
}
)"},
    {"FragShaderSrcMaskInvertedPremultipliedAlphaBlend.frag", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#version 100

precision highp float;

varying vec2 v_texCoord;
varying vec2 v_blendCoord;
varying vec4 v_clipPos;
uniform sampler2D s_texture0;
uniform sampler2D s_texture1;
uniform sampler2D s_blendTexture;
uniform vec4 u_channelFlag;
uniform vec4 u_baseColor;
uniform vec4 u_multiplyColor;
uniform vec4 u_screenColor;

vec4 ConvertPremultipliedToStraight(vec4 source);
vec3 ColorBlend(vec3 colorSource, vec3 colorDestination);
vec4 AlphaBlend(vec3 color, vec4 colorSource, vec4 colorDestination);

void main()
{
    vec4 texColor = texture2D(s_texture0, v_texCoord);
    texColor.rgb = texColor.rgb * u_multiplyColor.rgb;
    texColor.rgb = (texColor.rgb + u_screenColor.rgb * texColor.a) - (texColor.rgb * u_screenColor.rgb);
    vec4 col_formask = ConvertPremultipliedToStraight(texColor * u_baseColor);
    vec4 clipMask = (1.0 - texture2D(s_texture1, v_clipPos.xy / v_clipPos.w)) * u_channelFlag;
    float maskVal = clipMask.r + clipMask.g + clipMask.b + clipMask.a;
    vec4 colorSource = vec4(col_formask.rgb, col_formask.a * (1.0 - maskVal));
    vec4 colorDestination = ConvertPremultipliedToStraight(texture2D(s_blendTexture, v_blendCoord));
    gl_FragColor = AlphaBlend(ColorBlend(colorSource.rgb, colorDestination.rgb), colorSource, colorDestination);
}
)"},
    {"FragShaderSrcMaskInvertedPremultipliedAlphaBlendTegra.frag", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#version 100
#extension GL_NV_shader_framebuffer_fetch : enable

precision highp float;

varying vec2 v_texCoord;
varying vec2 v_blendCoord;
varying vec4 v_clipPos;
uniform sampler2D s_texture0;
uniform sampler2D s_texture1;
uniform sampler2D s_blendTexture;
uniform vec4 u_channelFlag;
uniform vec4 u_baseColor;
uniform vec4 u_multiplyColor;
uniform vec4 u_screenColor;

vec4 ConvertPremultipliedToStraight(vec4 source);
vec3 ColorBlend(vec3 colorSource, vec3 colorDestination);
vec4 AlphaBlend(vec3 color, vec4 colorSource, vec4 colorDestination);

void main()
{
    vec4 texColor = texture2D(s_texture0, v_texCoord);
    texColor.rgb = texColor.rgb * u_multiplyColor.rgb;
    texColor.rgb = (texColor.rgb + u_screenColor.rgb * texColor.a) - (texColor.rgb * u_screenColor.rgb);
    vec4 col_formask = ConvertPremultipliedToStraight(texColor * u_baseColor);
    vec4 clipMask = (1.0 - texture2D(s_texture1, v_clipPos.xy / v_clipPos.w)) * u_channelFlag;
    float maskVal = clipMask.r + clipMask.g + clipMask.b + clipMask.a;
    vec4 colorSource = vec4(col_formask.rgb, col_formask.a * (1.0 - maskVal));
    vec4 colorDestination = ConvertPremultipliedToStraight(texture2D(s_blendTexture, v_blendCoord));
    gl_FragColor = AlphaBlend(ColorBlend(colorSource.rgb, colorDestination.rgb), colorSource, colorDestination);
}
)"},
    {"FragShaderSrcMaskInvertedPremultipliedAlphaTegra.frag", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#version 100
#extension GL_NV_shader_framebuffer_fetch : enable

precision highp float;

varying vec2 v_texCoord;
varying vec4 v_clipPos;
uniform sampler2D s_texture0;
uniform sampler2D s_texture1;
uniform vec4 u_channelFlag;
uniform vec4 u_baseColor;
uniform vec4 u_multiplyColor;
uniform vec4 u_screenColor;

void main()
{
    vec4 texColor = texture2D(s_texture0, v_texCoord);
    texColor.rgb = texColor.rgb * u_multiplyColor.rgb;
    texColor.rgb = (texColor.rgb + u_screenColor.rgb * texColor.a) - (texColor.rgb * u_screenColor.rgb);
    vec4 col_formask = texColor * u_baseColor;
    vec4 clipMask = (1.0 - texture2D(s_texture1, v_clipPos.xy / v_clipPos.w)) * u_channelFlag;
    float maskVal = clipMask.r + clipMask.g + clipMask.b + clipMask.a;
    col_formask = col_formask * (1.0 - maskVal);
    gl_FragColor = col_formask;
}
)"},
    {"FragShaderSrcMaskInvertedTegra.frag", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#version 100
#extension GL_NV_shader_framebuffer_fetch : enable

precision highp float;

varying vec2 v_texCoord;
varying vec4 v_clipPos;
uniform sampler2D s_texture0;
uniform sampler2D s_texture1;
uniform vec4 u_channelFlag;
uniform vec4 u_baseColor;
uniform vec4 u_multiplyColor;
uniform vec4 u_screenColor;

void main()
{
    vec4 texColor = texture2D(s_texture0, v_texCoord);
    texColor.rgb = texColor.rgb * u_multiplyColor.rgb;
    texColor.rgb = texColor.rgb + u_screenColor.rgb - (texColor.rgb * u_screenColor.rgb);
    vec4 col_formask = texColor * u_baseColor;
    col_formask.rgb = col_formask.rgb  * col_formask.a;
    vec4 clipMask = (1.0 - texture2D(s_texture1, v_clipPos.xy / v_clipPos.w)) * u_channelFlag;
    float maskVal = clipMask.r + clipMask.g + clipMask.b + clipMask.a;
    col_formask = col_formask * (1.0 - maskVal);
    gl_FragColor = col_formask;
}
)"},
    {"FragShaderSrcMaskPremultipliedAlpha.frag", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#version 100

precision highp float;

varying vec2 v_texCoord;
varying vec4 v_clipPos;
uniform sampler2D s_texture0;
uniform sampler2D s_texture1;
uniform vec4 u_channelFlag;
uniform vec4 u_baseColor;
uniform vec4 u_multiplyColor;
uniform vec4 u_screenColor;

void main()
{
    vec4 texColor = texture2D(s_texture0, v_texCoord);
    texColor.rgb = texColor.rgb * u_multiplyColor.rgb;
    texColor.rgb = (texColor.rgb + u_screenColor.rgb * texColor.a) - (texColor.rgb * u_screenColor.rgb);
    vec4 col_formask = texColor * u_baseColor;
    vec4 clipMask = (1.0 - texture2D(s_texture1, v_clipPos.xy / v_clipPos.w)) * u_channelFlag;
    float maskVal = clipMask.r + clipMask.g + clipMask.b + clipMask.a;
    col_formask = col_formask * maskVal;
    gl_FragColor = col_formask;
}
)"},
    {"FragShaderSrcMaskPremultipliedAlphaBlend.frag", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#version 100

precision highp float;

varying vec2 v_texCoord;
varying vec2 v_blendCoord;
varying vec4 v_clipPos;
uniform sampler2D s_texture0;
uniform sampler2D s_texture1;
uniform sampler2D s_blendTexture;
uniform vec4 u_channelFlag;
uniform vec4 u_baseColor;
uniform vec4 u_multiplyColor;
uniform vec4 u_screenColor;

vec4 ConvertPremultipliedToStraight(vec4 source);
vec3 ColorBlend(vec3 colorSource, vec3 colorDestination);
vec4 AlphaBlend(vec3 color, vec4 colorSource, vec4 colorDestination);

void main()
{
    vec4 texColor = texture2D(s_texture0, v_texCoord);
    texColor.rgb = texColor.rgb * u_multiplyColor.rgb;
    texColor.rgb = (texColor.rgb + u_screenColor.rgb * texColor.a) - (texColor.rgb * u_screenColor.rgb);
    vec4 col_formask = ConvertPremultipliedToStraight(texColor * u_baseColor);
    vec4 clipMask = (1.0 - texture2D(s_texture1, v_clipPos.xy / v_clipPos.w)) * u_channelFlag;
    float maskVal = clipMask.r + clipMask.g + clipMask.b + clipMask.a;
    vec4 colorSource = vec4(col_formask.rgb, col_formask.a * maskVal);
    vec4 colorDestination = ConvertPremultipliedToStraight(texture2D(s_blendTexture, v_blendCoord));
    gl_FragColor = AlphaBlend(ColorBlend(colorSource.rgb, colorDestination.rgb), colorSource, colorDestination);
}
)"},
    {"FragShaderSrcMaskPremultipliedAlphaBlendTegra.frag", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#version 100
#extension GL_NV_shader_framebuffer_fetch : enable

precision highp float;

varying vec2 v_texCoord;
varying vec2 v_blendCoord;
varying vec4 v_clipPos;
uniform sampler2D s_texture0;
uniform sampler2D s_texture1;
uniform sampler2D s_blendTexture;
uniform vec4 u_channelFlag;
uniform vec4 u_baseColor;
uniform vec4 u_multiplyColor;
uniform vec4 u_screenColor;

vec4 ConvertPremultipliedToStraight(vec4 source);
vec3 ColorBlend(vec3 colorSource, vec3 colorDestination);
vec4 AlphaBlend(vec3 color, vec4 colorSource, vec4 colorDestination);

void main()
{
    vec4 texColor = texture2D(s_texture0, v_texCoord);
    texColor.rgb = texColor.rgb * u_multiplyColor.rgb;
    texColor.rgb = (texColor.rgb + u_screenColor.rgb * texColor.a) - (texColor.rgb * u_screenColor.rgb);
    vec4 col_formask = ConvertPremultipliedToStraight(texColor * u_baseColor);
    vec4 clipMask = (1.0 - texture2D(s_texture1, v_clipPos.xy / v_clipPos.w)) * u_channelFlag;
    float maskVal = clipMask.r + clipMask.g + clipMask.b + clipMask.a;
    vec4 colorSource = vec4(col_formask.rgb, col_formask.a * maskVal);
    vec4 colorDestination = ConvertPremultipliedToStraight(texture2D(s_blendTexture, v_blendCoord));
    gl_FragColor = AlphaBlend(ColorBlend(colorSource.rgb, colorDestination.rgb), colorSource, colorDestination);
}
)"},
    {"FragShaderSrcMaskPremultipliedAlphaTegra.frag", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#version 100
#extension GL_NV_shader_framebuffer_fetch : enable

precision highp float;

varying vec2 v_texCoord;
varying vec4 v_clipPos;
uniform sampler2D s_texture0;
uniform sampler2D s_texture1;
uniform vec4 u_channelFlag;
uniform vec4 u_baseColor;
uniform vec4 u_multiplyColor;
uniform vec4 u_screenColor;

void main()
{
    vec4 texColor = texture2D(s_texture0, v_texCoord);
    texColor.rgb = texColor.rgb * u_multiplyColor.rgb;
    texColor.rgb = (texColor.rgb + u_screenColor.rgb * texColor.a) - (texColor.rgb * u_screenColor.rgb);
    vec4 col_formask = texColor * u_baseColor;
    vec4 clipMask = (1.0 - texture2D(s_texture1, v_clipPos.xy / v_clipPos.w)) * u_channelFlag;
    float maskVal = clipMask.r + clipMask.g + clipMask.b + clipMask.a;
    col_formask = col_formask * maskVal;
    gl_FragColor = col_formask;
}
)"},
    {"FragShaderSrcMaskTegra.frag", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#version 100
#extension GL_NV_shader_framebuffer_fetch : enable

precision highp float;

varying vec2 v_texCoord;
varying vec4 v_clipPos;
uniform sampler2D s_texture0;
uniform sampler2D s_texture1;
uniform vec4 u_channelFlag;
uniform vec4 u_baseColor;
uniform vec4 u_multiplyColor;
uniform vec4 u_screenColor;

void main()
{
    vec4 texColor = texture2D(s_texture0, v_texCoord);
    texColor.rgb = texColor.rgb * u_multiplyColor.rgb;
    texColor.rgb = texColor.rgb + u_screenColor.rgb - (texColor.rgb * u_screenColor.rgb);
    vec4 col_formask = texColor * u_baseColor;
    col_formask.rgb = col_formask.rgb  * col_formask.a;
    vec4 clipMask = (1.0 - texture2D(s_texture1, v_clipPos.xy / v_clipPos.w)) * u_channelFlag;
    float maskVal = clipMask.r + clipMask.g + clipMask.b + clipMask.a;
    col_formask = col_formask * maskVal;
    gl_FragColor = col_formask;
}
)"},
    {"FragShaderSrcPremultipliedAlpha.frag", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#version 100

precision highp float;

varying vec2 v_texCoord; //v2f.texcoord
uniform sampler2D s_texture0; //_MainTex
uniform vec4 u_baseColor; //v2f.color
uniform vec4 u_multiplyColor;
uniform vec4 u_screenColor;

void main()
{
    vec4 texColor = texture2D(s_texture0, v_texCoord);
    texColor.rgb = texColor.rgb * u_multiplyColor.rgb;
    texColor.rgb = (texColor.rgb + u_screenColor.rgb * texColor.a) - (texColor.rgb * u_screenColor.rgb);
    gl_FragColor = texColor * u_baseColor;
}
)"},
    {"FragShaderSrcPremultipliedAlphaBlend.frag", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#version 100

precision highp float;

varying vec2 v_texCoord; //v2f.texcoord
varying vec2 v_blendCoord;
uniform sampler2D s_texture0; //_MainTex
uniform sampler2D s_blendTexture;
uniform vec4 u_baseColor; //v2f.color
uniform vec4 u_multiplyColor;
uniform vec4 u_screenColor;

vec4 ConvertPremultipliedToStraight(vec4 source);
vec3 ColorBlend(vec3 colorSource, vec3 colorDestination);
vec4 AlphaBlend(vec3 color, vec4 colorSource, vec4 colorDestination);

void main()
{
    vec4 texColor = texture2D(s_texture0, v_texCoord);
    texColor.rgb = texColor.rgb * u_multiplyColor.rgb;
    texColor.rgb = (texColor.rgb + u_screenColor.rgb * texColor.a) - (texColor.rgb * u_screenColor.rgb);
    vec4 colorSource = ConvertPremultipliedToStraight(texColor * u_baseColor);
    vec4 colorDestination = ConvertPremultipliedToStraight(texture2D(s_blendTexture, v_blendCoord));
    gl_FragColor = AlphaBlend(ColorBlend(colorSource.rgb, colorDestination.rgb), colorSource, colorDestination);
}
)"},
    {"FragShaderSrcPremultipliedAlphaBlendTegra.frag", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#version 100
#extension GL_NV_shader_framebuffer_fetch : enable

precision highp float;

varying vec2 v_texCoord; //v2f.texcoord
varying vec2 v_blendCoord;
uniform sampler2D s_texture0; //_MainTex
uniform sampler2D s_blendTexture;
uniform vec4 u_baseColor; //v2f.color
uniform vec4 u_multiplyColor;
uniform vec4 u_screenColor;

vec4 ConvertPremultipliedToStraight(vec4 source);
vec3 ColorBlend(vec3 colorSource, vec3 colorDestination);
vec4 AlphaBlend(vec3 color, vec4 colorSource, vec4 colorDestination);

void main()
{
    vec4 texColor = texture2D(s_texture0, v_texCoord);
    texColor.rgb = texColor.rgb * u_multiplyColor.rgb;
    texColor.rgb = (texColor.rgb + u_screenColor.rgb * texColor.a) - (texColor.rgb * u_screenColor.rgb);
    vec4 colorSource = ConvertPremultipliedToStraight(texColor * u_baseColor);
    vec4 colorDestination = ConvertPremultipliedToStraight(texture2D(s_blendTexture, v_blendCoord));
    gl_FragColor = AlphaBlend(ColorBlend(colorSource.rgb, colorDestination.rgb), colorSource, colorDestination);
}
)"},
    {"FragShaderSrcPremultipliedAlphaTegra.frag", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#version 100
#extension GL_NV_shader_framebuffer_fetch : enable

precision highp float;

varying vec2 v_texCoord; //v2f.texcoord
uniform sampler2D s_texture0; //_MainTex
uniform vec4 u_baseColor; //v2f.color
uniform vec4 u_multiplyColor;
uniform vec4 u_screenColor;

void main()
{
    vec4 texColor = texture2D(s_texture0, v_texCoord);
    texColor.rgb = texColor.rgb * u_multiplyColor.rgb;
    texColor.rgb = (texColor.rgb + u_screenColor.rgb * texColor.a) - (texColor.rgb * u_screenColor.rgb);
    gl_FragColor = texColor * u_baseColor;
}
)"},
    {"FragShaderSrcSetupMask.frag", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#version 100

precision highp float;

varying vec2 v_texCoord;
varying vec4 v_myPos;
uniform sampler2D s_texture0;
uniform vec4 u_channelFlag;
uniform vec4 u_baseColor;

void main()
{
    float isInside =
        step(u_baseColor.x, v_myPos.x/v_myPos.w)
        * step(u_baseColor.y, v_myPos.y/v_myPos.w)
        * step(v_myPos.x/v_myPos.w, u_baseColor.z)
        * step(v_myPos.y/v_myPos.w, u_baseColor.w);

    gl_FragColor = u_channelFlag * texture2D(s_texture0, v_texCoord).a * isInside;
}
)"},
    {"FragShaderSrcSetupMaskTegra.frag", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#version 100
#extension GL_NV_shader_framebuffer_fetch : enable

precision highp float;

varying vec2 v_texCoord;
varying vec4 v_myPos;
uniform sampler2D s_texture0;
uniform vec4 u_channelFlag;
uniform vec4 u_baseColor;

void main()
{
    float isInside =
        step(u_baseColor.x, v_myPos.x/v_myPos.w)
        * step(u_baseColor.y, v_myPos.y/v_myPos.w)
        * step(v_myPos.x/v_myPos.w, u_baseColor.z)
        * step(v_myPos.y/v_myPos.w, u_baseColor.w);

    gl_FragColor = u_channelFlag * texture2D(s_texture0, v_texCoord).a * isInside;
}
)"},
    {"FragShaderSrcTegra.frag", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#version 100
#extension GL_NV_shader_framebuffer_fetch : enable

precision highp float;

varying vec2 v_texCoord; //v2f.texcoord
uniform sampler2D s_texture0; //_MainTex
uniform vec4 u_baseColor; //v2f.color
uniform vec4 u_multiplyColor;
uniform vec4 u_screenColor;

void main()
{
    vec4 texColor = texture2D(s_texture0, v_texCoord);
    texColor.rgb = texColor.rgb * u_multiplyColor.rgb;
    texColor.rgb = texColor.rgb + u_screenColor.rgb - (texColor.rgb * u_screenColor.rgb);
    vec4 color = texColor * u_baseColor;
    gl_FragColor = vec4(color.rgb * color.a, color.a);
}
)"},
    {"VertShaderSrc.vert", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#version 100

attribute vec4 a_position; //v.vertex
attribute vec2 a_texCoord; //v.texcoord
varying vec2 v_texCoord; //v2f.texcoord
uniform mat4 u_matrix;

void main()
{
    gl_Position = u_matrix * a_position;
    v_texCoord = a_texCoord;
    v_texCoord.y = 1.0 - v_texCoord.y;
}
)"},
    {"VertShaderSrcBlend.vert", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#version 100

attribute vec4 a_position; //v.vertex
attribute vec2 a_texCoord; //v.texcoord
varying vec2 v_texCoord; //v2f.texcoord
varying vec2 v_blendCoord;
uniform mat4 u_matrix;

void main()
{
    gl_Position = u_matrix * a_position;
    v_texCoord = a_texCoord;
    v_texCoord.y = 1.0 - v_texCoord.y;
    vec2 ndcPos = gl_Position.xy / gl_Position.w;
    v_blendCoord = ndcPos * 0.5 + 0.5;
}
)"},
    {"VertShaderSrcCopy.vert", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#version 100

attribute vec4 a_position;
attribute vec2 a_texCoord;
varying vec2 v_texCoord;

void main()
{
    v_texCoord = a_texCoord;
    gl_Position = a_position;
}
)"},
    {"VertShaderSrcMasked.vert", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#version 100

attribute vec4 a_position;
attribute vec2 a_texCoord;
varying vec2 v_texCoord;
varying vec4 v_clipPos;
uniform mat4 u_matrix;
uniform mat4 u_clipMatrix;

void main()
{
    gl_Position = u_matrix * a_position;
    v_clipPos = u_clipMatrix * a_position;
    v_texCoord = a_texCoord;
    v_texCoord.y = 1.0 - v_texCoord.y;
}
)"},
    {"VertShaderSrcMaskedBlend.vert", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#version 100

attribute vec4 a_position;
attribute vec2 a_texCoord;
varying vec2 v_texCoord;
varying vec2 v_blendCoord;
varying vec4 v_clipPos;
uniform mat4 u_matrix;
uniform mat4 u_clipMatrix;

void main()
{
    gl_Position = u_matrix * a_position;
    v_clipPos = u_clipMatrix * a_position;
    v_texCoord = a_texCoord;
    v_texCoord.y = 1.0 - v_texCoord.y;
    vec2 ndcPos = gl_Position.xy / gl_Position.w;
    v_blendCoord = ndcPos * 0.5 + 0.5;
}
)"},
    {"VertShaderSrcSetupMask.vert", R"(
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#version 100

attribute vec4 a_position;
attribute vec2 a_texCoord;
varying vec2 v_texCoord;
varying vec4 v_myPos;
uniform mat4 u_clipMatrix;

void main()
{
    gl_Position = u_clipMatrix * a_position;
    v_myPos = u_clipMatrix * a_position;
    v_texCoord = a_texCoord;
    v_texCoord.y = 1.0 - v_texCoord.y;
}
)"}
};

#endif // LIVE2D_SHADERS_H
