precision mediump float;
varying vec2 v_t_position;
uniform sampler2D u_sampler;

void main() {
//当前片元颜色 = 2D纹理采样(取样器, 纹理坐标)
    gl_FragColor = texture2D(u_sampler, v_t_position);
}
