#version 330 core

in layout(location = 0) vec3 position;
in layout(location = 1) vec4 col;

out layout(location = 0) vec4 fColor;

void main() {
    gl_Position = vec4(position, 1);
    fColor      = col;
}