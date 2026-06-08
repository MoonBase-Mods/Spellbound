#version 330

#moj_import <fog.glsl>

uniform sampler2D MaskTexture;
uniform vec4  ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4  FogColor;
uniform float GameTime;

// body colour
uniform vec4  Color1;
// edge colour
uniform vec4  Color2;

uniform float RingSpeed;
uniform float RingScale;
uniform float NoiseScrollSpeed;
uniform float DiscardThreshold;

in float vertexDistance;
in vec2  texCoord0;
in vec4  vertexColor;

out vec4 fragColor;

float hash(vec2 p) {
    p = fract(p * vec2(127.1, 311.7));
    p += dot(p, p + 19.19);
    return fract(p.x * p.y);
}

float vnoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);
    float a = hash(i);
    float b = hash(i + vec2(1.0, 0.0));
    float c = hash(i + vec2(0.0, 1.0));
    float d = hash(i + vec2(1.0, 1.0));
    return mix(mix(a, b, u.x), mix(c, d, u.x), u.y);
}

float fbm(vec2 p) {
    float val = 0.0, amp = 0.5, freq = 1.0;
    for (int i = 0; i < 5; i++) {
        val  += vnoise(p * freq) * amp;
        freq *= 2.1;
        amp  *= 0.48;
    }
    return val;
}

float soundwaveRings(vec2 uv, float time) {
    vec2  centered = uv - 0.5;
    float dist     = length(centered);

    float wave  = sin(dist * RingScale        - time * RingSpeed)        * 0.5 + 0.5;
    float wave2 = sin(dist * RingScale * 0.5  - time * RingSpeed * 0.67 + 1.2) * 0.5 + 0.5;
    float wave3 = sin(dist * RingScale * 1.5  - time * RingSpeed * 1.33 + 2.4) * 0.5 + 0.5;
    float combined = wave * 0.5 + wave2 * 0.3 + wave3 * 0.2;

    float radialFade = smoothstep(0.0, 0.15, dist) * smoothstep(0.52, 0.35, dist);
    return combined * radialFade;
}

void main() {

    float time = GameTime * 2000.0;
    vec4  maskSample = texture(MaskTexture, texCoord0);
    float mask       = maskSample.r;
    float maskAlpha  = mask * vertexColor.a * ColorModulator.a;

    if (maskAlpha < DiscardThreshold) discard;

    vec2  noiseUV  = texCoord0 * 3.5 + vec2(time * NoiseScrollSpeed, time * NoiseScrollSpeed * 0.6);
    float noiseA   = fbm(noiseUV);
    float noiseB   = fbm(noiseUV + vec2(5.2, 1.3));
    vec2  displaced = texCoord0 + vec2(noiseA - 0.5, noiseB - 0.5) * 0.04;

    float rings = soundwaveRings(displaced, time);

    float slowNoise = fbm(texCoord0 * 1.8 + vec2(time * 0.007 * NoiseScrollSpeed, -time * 0.005 * NoiseScrollSpeed));
    slowNoise = smoothstep(0.35, 0.65, slowNoise);

    float airEffect = mix(rings, slowNoise, 0.3);
    airEffect = clamp(airEffect, 0.0, 1.0);
    float fresnel = pow(length(texCoord0 - 0.5) * 2.0, 1.8);
    fresnel = clamp(fresnel, 0.0, 1.0);

    vec4  shimmerColor = mix(Color1, Color2, fresnel);
    float alpha        = airEffect * maskAlpha * mix(0.25, 0.85, fresnel);

    vec4 color = vec4(shimmerColor.rgb, alpha);
    fragColor  = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}