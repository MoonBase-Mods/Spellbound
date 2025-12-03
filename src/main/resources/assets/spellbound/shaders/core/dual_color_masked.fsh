#version 330

#moj_import <fog.glsl>

uniform sampler2D MaskTexture;
uniform vec4 Color1;
uniform vec4 Color2;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform vec4 HDR1;
uniform vec4 HDR2;

in float vertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec4 color = texture(MaskTexture, texCoord0) * vertexColor * ColorModulator;
    vec4 color1 = Color1;
    vec4 color2 = Color2;
    color1.xyz *= HDR1.a * HDR1.rgb;
    color2.xyz *= HDR2.a * HDR2.rgb;
    color = mix(color1, color2, color.r);
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}