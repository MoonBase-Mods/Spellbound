#version 330

#moj_import <fog.glsl>

uniform sampler2D NoiseTexture;
uniform sampler2D MaskTexture;

uniform vec4  ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4  FogColor;
uniform float GameTime;
uniform vec4  HDR;

uniform float ScrollSpeedX;
uniform float ScrollSpeedY;
uniform float TilingX;
uniform float TilingY;
uniform float NoiseAmount;
uniform float PulseSpeed;
uniform float PulseStrength;
uniform float RimSharpness;
uniform float DiscardThreshold;

in float vertexDistance;
in vec2  texCoord0;
in vec4  vertexColor;

out vec4 fragColor;

void main() {
    float time = GameTime * 2000.0;

    vec4 maskSample = texture(MaskTexture, texCoord0);
    float mask = maskSample.r;

    vec2 noiseUV = vec2(
        texCoord0.x * TilingX + time * ScrollSpeedX,
        texCoord0.y * TilingY + time * ScrollSpeedY
    );

    float noiseWarp = texture(NoiseTexture, noiseUV).r;
    vec2 warpedUV   = mix(noiseUV, vec2(noiseWarp), NoiseAmount);
    float beam      = texture(NoiseTexture, warpedUV).r;

    float rim  = pow(1.0 - mask, RimSharpness);
    float aura = beam * 0.7 + rim * 0.3;


    float pulse = 1.0 - PulseStrength * (0.5 - 0.5 * sin(GameTime * 20.0 * PulseSpeed));
    aura *= pulse;


    vec4 color = vec4(aura, aura, aura, vertexColor.a * mask);

    if (color.a < DiscardThreshold) discard;

    color.xyz *= HDR.a * HDR.rgb;

    fragColor = linear_fog(color * vertexColor * ColorModulator, vertexDistance, FogStart, FogEnd, FogColor);
}
