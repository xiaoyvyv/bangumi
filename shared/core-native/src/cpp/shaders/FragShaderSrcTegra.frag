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
