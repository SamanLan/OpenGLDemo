precision mediump float;
varying vec2 v_t_position;
uniform sampler2D u_sampler;
uniform vec3 changeColor;

vec4 dealColor(vec4 originalColor, vec3 changeColor){
    originalColor += vec4(changeColor,0.0);
    originalColor.r=max(min(originalColor.r,1.0),0.0);
    originalColor.g=max(min(originalColor.g,1.0),0.0);
    originalColor.b=max(min(originalColor.b,1.0),0.0);
    originalColor.a=max(min(originalColor.a,1.0),0.0);
    return originalColor;
}

void main() {
//当前片元颜色 = 2D纹理采样(取样器, 纹理坐标)
    vec4 color = texture2D(u_sampler, v_t_position);
    color = dealColor(color, changeColor);
    gl_FragColor = color;
}
