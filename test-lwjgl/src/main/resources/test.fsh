#version 330 core

in layout(location = 0) vec4 fColor;

out vec4 outColor;

void main() {
    outColor = fColor;
}
