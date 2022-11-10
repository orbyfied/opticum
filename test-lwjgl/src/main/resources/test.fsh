#version 330 core

in layout(location = 0) vec4 fColor;

void main() {
    gl_FragColor = fColor;
}
