#version 330

#moj_import <fog.glsl>

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform float Power;
uniform vec4 HDRColor;
uniform float GameTime;
uniform float ScrollSpeedX;
uniform float ScrollSpeedY;
uniform float TilingX;
uniform float TilingY;
uniform float DiscardThreshold;

in float vertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

vec2 random(vec2 uv) {
    return vec2(fract(sin(dot(uv.xy, vec2(12.9898, 78.233))) * 43758.5453123));
}

float worley(vec2 uv, float columns, float rows) {
    vec2 index_uv = floor(vec2(uv.x * columns, uv.y * rows));
    vec2 fract_uv = fract(vec2(uv.x * columns, uv.y * rows));

    float min_dist = 1.0;
    for (int y = -1; y <= 1; y++) {
        for (int x = -1; x <= 1; x++) {
            vec2 neighbor = vec2(float(x), float(y));
            vec2 point = random(index_uv + neighbor);

            vec2 diff = neighbor + point - fract_uv;
            float dist = length(diff);
            min_dist = min(min_dist, dist);
        }
    }

    return min_dist;
}

void main() {
    float time = GameTime * 20;
    vec2 scrolledTex = vec2(texCoord0.x + time * ScrollSpeedX, texCoord0.y + time * ScrollSpeedY);
    float worley = worley(scrolledTex, TilingX, TilingY);
    vec4 color = vec4(vec3(worley), worley) * vertexColor * ColorModulator;
    if (color.a < DiscardThreshold) {
        discard;
    }
    color.xyz *= HDRColor.rgb * HDRColor.a;
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}