package com.samanlan.opengldemo;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * <code>CustomGLSurfaceView</code>{}
 *
 * @author :LanSaman
 * @date :2018/3/19
 * @description :
 */
public class CustomRenderer implements GLSurfaceView.Renderer {

    // float32位精度，一个byte8位精度，每个浮点数占4个字节
    private static final int BYTE_FOR_FLOAT = 4;
    // 表明使用两个分量表示与一个顶点关联。但a_position被定义为vec4，它有四个分量，在默认情况下前三个设为0，最后一个设为1
    private static final int POSITION_COMPONENT_COUNT = 2;
    // 用来在本地内存存储数据
    private final FloatBuffer mVertexData;
    private int programId;
    private Context mContext;
    private static final String U_COLOR = "u_Color";
    private static final String A_POSITION = "a_Position";
    private int uColorLocation;
    private int aPosition;

    public CustomRenderer(Context context) {
        mContext = context;

        // 浮点数表示顶点数据，逆时针排列顶点，卷曲顺序
        float[] tableVertices = {0f, 0f, 9f, 14f, 0f, 14f // 第一个三角形
                , 0f, 0f, 9f, 0f, 9f, 14f};// 第二个三角形


        mVertexData = ByteBuffer
                .allocateDirect(tableVertices.length * BYTE_FOR_FLOAT)// 分配本地内存，不会被gc回收
                .order(ByteOrder.nativeOrder())// 告诉字节缓冲区按照本地字节序组织它的内容
                .asFloatBuffer();
        mVertexData.put(tableVertices);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        // 用这个颜色清空屏幕，参数对应rgba
        GLES20.glClearColor(1f, 0f, 0f, 0f);

        // 顶点着色器vertex shader。生成每个顶点的最终位置，针对每个顶点它都会执行一次。
        int vertexShader = GLHelper.compileShader(GLES20.GL_VERTEX_SHADER, GLHelper.readTextFileFromResource(mContext, R.raw.custom_vertex_shader));

        // 片段着色器fragment shader。为组成点、直线或者三角形的每个片段生成最终的颜色，针对每个片段它都会执行一次。
        int fragmentShader = GLHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, GLHelper.readTextFileFromResource(mContext, R.raw.custom_fragment_shader));

        programId = GLHelper.linkProgram(vertexShader, fragmentShader);

        GLHelper.validateProgram(programId);



    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
        // 设置视图大小
        GLES20.glViewport(0, 0, i, i1);
    }
    float color[] = { 1.0f, 1.0f, 1.0f, 1.0f };
    @Override
    public void onDrawFrame(GL10 gl10) {
        // 擦除屏幕所有颜色，并用之前glClearColor定义的颜色填充屏幕
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUseProgram(programId);

        // 获取uinform位置并存入uColorLocation
        uColorLocation = GLES20.glGetUniformLocation(programId, U_COLOR);

        // 获取属性位置
        // 在使用属性之前也要获取它的位置，我们可以让OpenGL自动给这些属性分配位置编号，或者在着色器被链接到一起的之前，可以通过
        // 调用glBindAttribLocation由我们给它们分配位置编号
        aPosition = GLES20.glGetAttribLocation(programId, A_POSITION);
        // 使顶点数组能使用
        GLES20.glEnableVertexAttribArray(aPosition);
        // 关联属性与顶点数据的数组
        // 确保从开头开始读取数据
        mVertexData.position(0);
        GLES20.glVertexAttribPointer(aPosition, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, 2*4, mVertexData);

        // 绘制
        GLES20.glUniform4f(uColorLocation, 1f, 1f, 1f, 1f);
//        GLES20.glUniform4fv(uColorLocation, 1, color, 0);
        // 告诉OpenGL我们想画三角形，从顶点的开头处开始读，读入6个顶点，最终会画出两个三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
        GLES20.glDisableVertexAttribArray(aPosition);
    }
}
