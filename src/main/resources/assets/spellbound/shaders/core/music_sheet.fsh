#version 330

#moj_import <fog.glsl>

uniform sampler2D SheetTexture;
uniform sampler2D MaskTexture;

uniform vec4  ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4  FogColor;
uniform float GameTime;
uniform vec4  HDR;


uniform float ScrollSpeed;
//vertical/horizontal repeat
uniform float TilingX;
uniform float TilingY;

uniform float WaveFrequency;
uniform float WaveAmplitude;
uniform float WaveSpeed;

uniform float WobbleFrequency;
uniform float WobbleAmplitude;
uniform float WobbleSpeed;

uniform float Opacity;
uniform float DiscardThreshold;

in float vertexDistance;
in vec2  texCoord0;
in vec4  vertexColor;

out vec4 fragColor;

void main() {
    float time = GameTime * 2000.0;
    float mask = texture(MaskTexture, texCoord0).r;
    float wave1 = sin(texCoord0.x * WaveFrequency * 6.2831853
                      + time * WaveSpeed) * WaveAmplitude;
    float wave2 = sin(texCoord0.x * WobbleFrequency * 6.2831853
                      - time * WobbleSpeed * 0.7
                      + 1.3) * WobbleAmplitude;

    float totalWarp = wave1 + wave2;
    vec2 sheetUV = vec2(
        texCoord0.x * TilingX + time * ScrollSpeed,   // horizontal scroll
        texCoord0.y * TilingY + totalWarp              // vertical wave
    );

    vec4 sheet = texture(SheetTexture, sheetUV);
    float sheetAlpha = dot(sheet.rgb, vec3(0.299, 0.587, 0.114));
    float finalAlpha = sheetAlpha * mask * vertexColor.a * Opacity;

    vec4 color;
    color.rgb = sheet.rgb;
    color.a   = finalAlpha;

    if (color.a < DiscardThreshold) discard;
    color.rgb *= HDR.rgb * HDR.a;

    fragColor = linear_fog(color * ColorModulator, vertexDistance, FogStart, FogEnd, FogColor);
}
