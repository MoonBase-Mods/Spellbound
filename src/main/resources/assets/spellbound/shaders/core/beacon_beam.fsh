#version 330 core

uniform float GameTime;
uniform float BeamSpeed;
uniform float NoiseAmount;
uniform vec4 ColorHDR;

uniform sampler2D BeamTexture;

in float vertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

// 随机值生成（哈希函数）
float noise_randomValue(vec2 uv) {
    return fract(sin(dot(uv, vec2(12.9898, 78.233))) * 43758.5453);
}

// 插值函数
float noise_interpolate(float a, float b, float t) {
    return mix(a, b, t); // GLSL 内建线性插值函数
}

// 单层值噪声
float valueNoise(vec2 uv) {
    vec2 i = floor(uv);
    vec2 f = fract(uv);
    f = f * f * (3.0 - 2.0 * f); // Hermite 插值

    // 采样 4 个邻居格点
    vec2 c0 = i + vec2(0.0, 0.0);
    vec2 c1 = i + vec2(1.0, 0.0);
    vec2 c2 = i + vec2(0.0, 1.0);
    vec2 c3 = i + vec2(1.0, 1.0);

    float r0 = noise_randomValue(c0);
    float r1 = noise_randomValue(c1);
    float r2 = noise_randomValue(c2);
    float r3 = noise_randomValue(c3);

    float bottom = mix(r0, r1, f.x);
    float top    = mix(r2, r3, f.x);
    float t      = mix(bottom, top, f.y);
    return t;
}

// 主函数：SimpleNoise，叠加 3 层值噪声
float simpleNoise(vec2 uv, float scale) {
    float t = 0.0;
    for(int i = 0; i < 3; i++)
    {
        float freq = pow(2.0, float(i));
        float amp = pow(0.5, float(3-i));
        t += valueNoise(vec2(uv.x*scale/freq, uv.y*scale/freq))*amp;
    }
    return t + 0.2;
}

void main() {
    vec2 offset = vec2(-0.5 * GameTime * 2000 * BeamSpeed, 0);
    float noise = pow(simpleNoise(texCoord0, 25), 2);
    vec2 lerp =  mix(texCoord0, vec2(noise), NoiseAmount);
    vec4 beam = texture(BeamTexture, lerp + offset);
    vec3 color = beam.rgb * ColorHDR.rgb * ColorHDR.a;
    fragColor = vec4(color, length(beam.rgb) * vertexColor.a);
}