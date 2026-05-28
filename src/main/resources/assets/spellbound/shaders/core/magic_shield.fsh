#version 330

#moj_import <fog.glsl>


uniform sampler2D ParticleTexture;
uniform sampler2D GlintTexture;


uniform vec4  ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4  FogColor;
uniform float GameTime;
uniform vec4  HDR;


uniform float GlintSpeed;
uniform float GlintScale;
uniform float GlintStrength;
uniform float GlintAngle;
uniform float DiscardThreshold;

in float vertexDistance;
in vec2  texCoord0;
in vec4  vertexColor;

out vec4 fragColor;



vec2 rotateUV(vec2 uv, float angle) {
    float s = sin(angle);
    float c = cos(angle);
    // rotate around centre (0.5, 0.5)
    uv -= 0.5;
    uv = vec2(c * uv.x - s * uv.y, s * uv.x + c * uv.y);
    uv += 0.5;
    return uv;
}

vec3 glintPass(vec2 uv, float time, float angle, float speed, float scale) {
    vec2 scrollUV = rotateUV(uv * scale, angle);
    scrollUV.x += time * speed;
    scrollUV.y += time * speed * 0.5;
    return texture(GlintTexture, scrollUV).rgb;
}

void main() {
    float time = GameTime * 2000.0;


    vec4 base = texture(ParticleTexture, texCoord0);

    if (base.a * vertexColor.a < DiscardThreshold) discard;

    vec3 glint1 = glintPass(texCoord0, time,  0.0,        GlintSpeed,       GlintScale);
    vec3 glint2 = glintPass(texCoord0, time,  GlintAngle, GlintSpeed * 0.7, GlintScale * 0.8);

    vec3 glint = (glint1 + glint2) * 0.5;

    glint *= HDR.rgb * HDR.a * GlintStrength;

    vec4 color;
    color.rgb = base.rgb + glint;
    color.a   = base.a * vertexColor.a;

    fragColor = linear_fog(color * ColorModulator, vertexDistance, FogStart, FogEnd, FogColor);
}
