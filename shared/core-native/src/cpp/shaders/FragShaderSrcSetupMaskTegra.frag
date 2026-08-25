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
