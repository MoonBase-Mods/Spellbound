#version 330

#moj_import <fog.glsl>

uniform sampler2D MaskTexture;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform vec4 HDR;

in float vertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec4 color = texture(MaskTexture, texCoord0);
    color = vec4(color.xyz, vertexColor.a * color.r);
    color.xyz *= HDR.a * HDR.rgb;
    fragColor = linear_fog(color * vertexColor * ColorModulator, vertexDistance, FogStart, FogEnd, FogColor);
}