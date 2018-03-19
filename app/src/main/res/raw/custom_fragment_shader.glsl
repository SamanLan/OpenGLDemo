// 精度限定符，定义了所有浮点类型的默认精度
// 可以选择lowp、mediump、highp
// 高精度更加精确，但耗性能，对于片段着色器为了最大兼容性考虑使用mediump
// 顶点着色器默认highp
precision mediump float;
// uniform会让每一个顶点都使用同一个值，除非我们再次改变它。
uniform vec4 u_Color;

// 着色器的主要入口
void main(){
    // 着色器会使用这个颜色为片段的最终颜色
    gl_FragColor = u_Color;
}