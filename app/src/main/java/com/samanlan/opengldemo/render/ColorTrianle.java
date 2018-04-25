package com.samanlan.opengldemo.render;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.samanlan.opengldemo.GLHelper;
import com.samanlan.opengldemo.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ColorTrianle implements GLSurfaceView.Renderer {

    // float32位精度，一个byte8位精度，每个浮点数占4个字节
    private static final int BYTE_FOR_FLOAT = 4;

    // 表明使用3个分量表示与一个顶点关联。但a_position被定义为vec4，它有四个分量，在默认情况下前三个设为0，最后一个设为1
    private static final int POSITION_COMPONENT_COUNT = 3;

    // 用来在本地内存存储数据
    private final FloatBuffer mVertexData;
    private final FloatBuffer mVertexColor;
    private final Context mContext;
    // 工程 ID
    private int mProgramId;
    private static final String A_POSITION = "a_Position";
    private static final String A_COLOR = "a_Color";
    // 顶点中a_Position句柄
    private int aPositionHandle;
    private int aColorHandle;
    private float triangleCoords[] = {
            0.5f,  0.5f, 0.0f, // top
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f  // bottom right
    };
    //设置颜色
    float color[] = {
            0.0f, 1.0f, 0.0f, 1.0f ,
            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f
    };

    public ColorTrianle(Context context) {
        mContext = context;
        mVertexData = ByteBuffer
                .allocateDirect(triangleCoords.length * BYTE_FOR_FLOAT)// 分配本地内存，不会被gc回收
                .order(ByteOrder.nativeOrder())// 告诉字节缓冲区按照本地字节序组织它的内容
                .asFloatBuffer();
        mVertexColor = ByteBuffer
                .allocateDirect(color.length * BYTE_FOR_FLOAT)// 分配本地内存，不会被gc回收
                .order(ByteOrder.nativeOrder())// 告诉字节缓冲区按照本地字节序组织它的内容
                .asFloatBuffer();
        mVertexData.put(triangleCoords);
        // 从0开始读数据
        mVertexData.position(0);
        mVertexColor.put(color);
        mVertexColor.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // 顶点着色器vertex shader。生成每个顶点的最终位置，针对每个顶点它都会执行一次。
        int vertexShader = GLHelper.compileShader(GLES20.GL_VERTEX_SHADER, GLHelper.readTextFileFromResource(mContext, R.raw.custom_vertex_color_shader));

        // 片段着色器fragment shader。为组成点、直线或者三角形的每个片段生成最终的颜色，针对每个片段它都会执行一次。
        int fragmentShader = GLHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, GLHelper.readTextFileFromResource(mContext, R.raw.custom_fragment_color_shader));

        mProgramId = GLHelper.linkProgram(vertexShader, fragmentShader);

        GLES20.glUseProgram(mProgramId);

        // 获取顶点着色器的vPosition成员句柄
        // 在使用属性之前也要获取它的句柄，我们可以让OpenGL自动给这些属性分配句柄编号，或者在着色器被链接到一起的之前，可以通过
        // 调用glBindAttribLocation由我们给它们分配位置句柄
        aPositionHandle = GLES20.glGetAttribLocation(mProgramId, A_POSITION);

        aColorHandle = GLES20.glGetAttribLocation(mProgramId, A_COLOR);

        // 填充顶点坐标数据，这里表示3个数据为一个顶点，每个顶点大小为3*4个字节大小
        GLES20.glVertexAttribPointer(aPositionHandle, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, POSITION_COMPONENT_COUNT * BYTE_FOR_FLOAT, mVertexData);

        GLES20.glVertexAttribPointer(aColorHandle, 4, GLES20.GL_FLOAT, false, 4 * BYTE_FOR_FLOAT, mVertexColor);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {

        GLES20.glEnableVertexAttribArray(aColorHandle);
        // 启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(aPositionHandle);
        // 绘制
        // 告诉OpenGL我们想画三角形，从顶点的开头处开始读，读入3个顶点，最终会画出1个三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, triangleCoords.length / POSITION_COMPONENT_COUNT);
        // 禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(aPositionHandle);
        GLES20.glDisableVertexAttribArray(aColorHandle);
    }
}
