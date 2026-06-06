#version 330

#moj_import <fog.glsl>

uniform sampler2D MaskTexture;

uniform vec4  ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4  FogColor;
uniform vec4  HDR;

uniform float OutlineThickness;
uniform float OutlineSharpness;
uniform float DiscardThreshold;

in float vertexDistance;
in vec2  texCoord0;
in vec4  vertexColor;

out vec4 fragColor;

void main() {

    vec2 texelSize = vec2(dFdx(texCoord0.x), dFdy(texCoord0.y)) * OutlineThickness;
    float center = texture(MaskTexture, texCoord0).r;

    float n  = texture(MaskTexture, texCoord0 + vec2( 0.0,         texelSize.y)).r;
    float s  = texture(MaskTexture, texCoord0 + vec2( 0.0,        -texelSize.y)).r;
    float e  = texture(MaskTexture, texCoord0 + vec2( texelSize.x,  0.0       )).r;
    float w  = texture(MaskTexture, texCoord0 + vec2(-texelSize.x,  0.0       )).r;
    float ne = texture(MaskTexture, texCoord0 + vec2( texelSize.x,  texelSize.y)).r;
    float nw = texture(MaskTexture, texCoord0 + vec2(-texelSize.x,  texelSize.y)).r;
    float se = texture(MaskTexture, texCoord0 + vec2( texelSize.x, -texelSize.y)).r;
    float sw = texture(MaskTexture, texCoord0 + vec2(-texelSize.x, -texelSize.y)).r;

    float maxNeighbour = max(max(max(n, s), max(e, w)),
                             max(max(ne, nw), max(se, sw)));

    float edge = maxNeighbour - center;
    edge = pow(clamp(edge, 0.0, 1.0), OutlineSharpness);
    vec4 color;
    color.rgb = HDR.rgb * HDR.a;
    color.a   = edge * vertexColor.a;

    if (color.a < DiscardThreshold) discard;

    fragColor = linear_fog(color * ColorModulator, vertexDistance, FogStart, FogEnd, FogColor);
}
