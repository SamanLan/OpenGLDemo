package com.samanlan.opengldemo.render;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.samanlan.opengldemo.GLHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Square implements GLSurfaceView.Renderer {

    // float32位精度，一个byte8位精度，每个浮点数占4个字节
    private static final int BYTE_FOR_FLOAT = 4;

    // 表明使用2个分量表示与一个顶点关联。但a_position被定义为vec4，它有四个分量，在默认情况下前三个设为0，最后一个设为1
    private static final int POSITION_COMPONENT_COUNT = 2;

    private final FloatBuffer mFloatBuffer;

    private float[] mVertexData = new float[]{
            -0.5f, -0.5f,
            -0.5f, 0.5f,
            0.5f, 0.5f,
            0.5f, -0.5f
    };

    private final String VertexRender = "attribute vec4 a_Position;\n" +
            "\n" +
            "void main(){\n" +
            "    gl_Position = a_Position;\n" +
            "}";
    private final String FragmentRender = "precision mediump float;\n" +
            "uniform vec4 u_Color;\n" +
            "\n" +
            "void main(){\n" +
            "    gl_FragColor = u_Color;\n" +
            "}";

    int mProgramId;
    int aPosition;
    int uColor;

    public Square() {
        mFloatBuffer = ByteBuffer.allocateDirect(mVertexData.length * BYTE_FOR_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mFloatBuffer.put(mVertexData);
        mFloatBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        mProgramId = GLHelper.linkProgram(GLHelper.compileShader(GLES20.GL_VERTEX_SHADER, VertexRender),
                GLHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, FragmentRender));

        GLES20.glUseProgram(mProgramId);

        aPosition = GLES20.glGetAttribLocation(mProgramId, "a_Position");
        uColor = GLES20.glGetUniformLocation(mProgramId, "u_Color");

        GLES20.glVertexAttribPointer(aPosition, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, POSITION_COMPONENT_COUNT * BYTE_FOR_FLOAT, mFloatBuffer);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glEnableVertexAttribArray(aPosition);

        GLES20.glUniform4f(uColor, 1f, 1f, 1f, 1f);

        // 新知识点
        // GLES20.glDrawArrays的第一个参数表示绘制方式，第二个参数表示偏移量，第三个参数表示顶点个数。
        // 绘制方式有：
        // int GL_POINTS       //将传入的顶点坐标作为单独的点绘制
        // int GL_LINES        //将传入的坐标作为单独线条绘制，ABCDEFG六个顶点，绘制AB、CD、EF三条线
        // int GL_LINE_STRIP   //将传入的顶点作为折线绘制，ABCD四个顶点，绘制AB、BC、CD三条线
        // int GL_LINE_LOOP    //将传入的顶点作为闭合折线绘制，ABCD四个顶点，绘制AB、BC、CD、DA四条线。
        // int GL_TRIANGLES    //将传入的顶点作为单独的三角形绘制，ABCDEF绘制ABC,DEF两个三角形
        // int GL_TRIANGLE_FAN    //将传入的顶点作为扇面绘制，ABCDEF绘制ABC、ACD、ADE、AEF四个三角形
        // int GL_TRIANGLE_STRIP   //将传入的顶点作为三角条带绘制，ABCDEF绘制ABC,BCD,CDE,DEF四个三角形

        GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, mVertexData.length / POSITION_COMPONENT_COUNT);

        GLES20.glDisableVertexAttribArray(aPosition);
    }
}
