attribute vec4 a_Position;
attribute vec4 a_Color;
// varying一般用于从顶点着色器传入到片元着色器的量。
varying vec4 u_Color;

void main(){
    gl_Position = a_Position;
    u_Color = a_Color;
}