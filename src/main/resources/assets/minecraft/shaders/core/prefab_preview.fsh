#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec4 normal;

out vec4 fragColor;

float maxComponent(vec3 v) {
    return max(max(v.r, v.g), v.b);
}

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;

    // Gather
    vec4 premultipliedColor = vec4(color.rgb * color.a, color.a);
    float a = min(1.0, premultipliedColor.a) * 8.0 + 0.01;
    float b = -vertexDistance * 0.95 + 1.0;
    float w = clamp(a * a * a * 1e8 * b * b * b, 1e-2, 3e2);

    vec4 accumulatedColor = premultipliedColor * w;
    float revealage = premultipliedColor.a;

    // Resolve
    if (revealage > 0.9999) {
        discard;
    }
    if (isinf(maxComponent(abs(accumulatedColor.rgb)))) {
        accumulatedColor.rgb = vec3(accumulatedColor.a);
    }

    vec4 finalColor = vec4(accumulatedColor.rgb / max(accumulatedColor.a, 0.00001), 1.0 - revealage);
    fragColor = linear_fog(finalColor, vertexDistance, FogStart, FogEnd, FogColor);
}