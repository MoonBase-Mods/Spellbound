#version 330

#moj_import <fog.glsl>
#moj_import <photon:particle.glsl>

uniform sampler2D Sampler2;
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform int FogShape;

out float vertexDistance;
out vec2 texCoord0;
out vec4 vertexColor;
out vec3 ViewDir;
out vec3 ViewNormal;

void main() {
    ParticleData data = getParticleData();

    vec4 ViewPos4 = ModelViewMat * vec4(data.Position, 1.0);
    ViewDir = -normalize(ViewPos4.xyz);
    ViewNormal = normalize(mat3(ModelViewMat) * data.Normal);

    vertexDistance = fog_distance(ViewPos4.xyz, FogShape);
    texCoord0 = data.UV;
    vertexColor = data.Color * texelFetch(Sampler2, data.LightUV / 16, 0);

    gl_Position = ProjMat * ViewPos4;
}