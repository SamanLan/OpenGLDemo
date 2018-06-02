attribute vec4 a_position;
attribute vec2 a_t_position;
uniform mat4 vMatrix;
varying vec2 v_t_position;

void main() {
    gl_Position = vMatrix * a_position;
    // 纹理坐标
    v_t_position = a_t_position;
}
