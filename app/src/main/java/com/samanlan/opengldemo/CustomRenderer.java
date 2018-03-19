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
    // 用来在本地内存存储数据
    private final FloatBuffer mVertexData;

    private Context mContext;

    public CustomRenderer(Context context) {
        mContext = context;

        // 浮点数表示顶点数据，逆时针排列顶点，卷曲顺序
        float[] tableVertices = {0f, 0f, 9f, 14f, 0f, 14f // 第一个三角形
                , 0f, 0f, 9f, 0f, 9f, 14f};// 第二个三角形


        mVertexData = ByteBuffer
                .allocateDirect(tableVertices.length * BYTE_FOR_FLOAT)// 分配本地内存，不会被gc回收
                .order(ByteOrder.nativeOrder())// 告诉字节缓冲区按照本地字节序组织它的内容
                .asFloatBuffer();

        // 顶点着色器vertex shader。生成每个顶点的最终位置，针对每个顶点它都会执行一次。
        int vertexShader = GLHelper.compileShader(GLES20.GL_VERTEX_SHADER, GLHelper.readTextFileFromResource(mContext, R.raw.custom_vertex_shader));

        // 片段着色器fragment shader。为组成点、直线或者三角形的每个片段生成最终的颜色，针对每个片段它都会执行一次。
        int fragmentShader = GLHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, GLHelper.readTextFileFromResource(mContext, R.raw.custom_fragment_shader));
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        // 用这个颜色清空屏幕，参数对应rgba
        GLES20.glClearColor(1f, 0f, 0f, 0f);


    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
        // 设置视图大小
        GLES20.glViewport(0, 0, i, i1);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        // 擦除屏幕所有颜色，并用之前glClearColor定义的颜色填充屏幕
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }
}
