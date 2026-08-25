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
