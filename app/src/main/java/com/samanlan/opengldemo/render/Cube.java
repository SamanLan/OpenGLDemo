package com.samanlan.opengldemo.render;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.samanlan.opengldemo.GLHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Cube implements GLSurfaceView.Renderer {

    private float[] mPoints = new float[]{
            -1.0f,1.0f,1.0f,    //正面左上0
            -1.0f,-1.0f,1.0f,   //正面左下1
            1.0f,-1.0f,1.0f,    //正面右下2
            1.0f,1.0f,1.0f,     //正面右上3
            -1.0f,1.0f,-1.0f,    //反面左上4
            -1.0f,-1.0f,-1.0f,   //反面左下5
            1.0f,-1.0f,-1.0f,    //反面右下6
            1.0f,1.0f,-1.0f     //反面右上7
    };

    private short[] mIndex = new short[]{
            0,3,2,0,2,1,    //正面
            0,1,5,0,5,4,    //左面
            0,7,3,0,4,7,    //上面
            6,7,4,6,4,5,    //后面
            6,3,7,6,2,3,    //右面
            6,5,1,6,1,2     //下面
    };

    //八个顶点的颜色，与顶点坐标一一对应
    float mColor[] = {
            0f,1f,0f,1f,
            0f,1f,0f,1f,
            0f,1f,0f,1f,
            0f,1f,0f,1f,
            1f,0f,0f,1f,
            1f,0f,0f,1f,
            1f,0f,0f,1f,
            1f,0f,0f,1f,
    };

    private final String vertexShaderCode =
            "attribute vec4 vPosition;\n" +
                    "uniform mat4 vMatrix;\n" +
                    "varying  vec4 vColor;\n" +
                    "attribute vec4 aColor;\n" +
                    "void main(){\n" +
                    "    gl_Position = vMatrix*vPosition;\n" +
                    "    vColor = aColor;\n" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;\n" +
                    "varying vec4 vColor;\n" +
                    "\n" +
                    "void main(){\n" +
                    "    gl_FragColor = vColor;\n" +
                    "}";

    private static final int BYTE_FOR_FLOAT = 4;

    private static final int POSITION_COMPONENT_COUNT = 3;

    private final FloatBuffer mPositionFloatBuffer;
    /**
     * 顶点索引要用 shortbuffer
     */
    private ShortBuffer mIndexFloatBuffer;
    private final FloatBuffer mColorFloatBuffer;

    int mProgramId;
    private static final String V_COLOR = "aColor";
    private static final String V_POSITION = "vPosition";
    private static final String V_MATRIX = "vMatrix";
    // 片段中u_Color的句柄
    private int vColorHandle;
    // 顶点中a_Position句柄
    private int vPositionHandle;
    private int vMatrix;

    private float[] mViewMatrix=new float[16];
    private float[] mProjectMatrix=new float[16];
    private float[] mMVPMatrix=new float[16];

    public Cube() {
        mPositionFloatBuffer = ByteBuffer
                .allocateDirect(mPoints.length * BYTE_FOR_FLOAT)// 分配本地内存，不会被gc回收
                .order(ByteOrder.nativeOrder())// 告诉字节缓冲区按照本地字节序组织它的内容
                .asFloatBuffer();
        mPositionFloatBuffer.put(mPoints);
        mPositionFloatBuffer.position(0);

        mIndexFloatBuffer = ByteBuffer
                .allocateDirect(mIndex.length * 2)// 分配本地内存，不会被gc回收
                .order(ByteOrder.nativeOrder())// 告诉字节缓冲区按照本地字节序组织它的内容
                .asShortBuffer();
        mIndexFloatBuffer.put(mIndex);
        mIndexFloatBuffer.position(0);

        mColorFloatBuffer = ByteBuffer
                .allocateDirect(mColor.length * BYTE_FOR_FLOAT)// 分配本地内存，不会被gc回收
                .order(ByteOrder.nativeOrder())// 告诉字节缓冲区按照本地字节序组织它的内容
                .asFloatBuffer();
        mColorFloatBuffer.put(mColor);
        mColorFloatBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mProgramId = GLHelper.linkProgram(GLHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode),
                GLHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode));

        GLES20.glUseProgram(mProgramId);

        vPositionHandle = GLES20.glGetAttribLocation(mProgramId, V_POSITION);
        vColorHandle = GLES20.glGetAttribLocation(mProgramId, V_COLOR);
        vMatrix = GLES20.glGetUniformLocation(mProgramId, V_MATRIX);
        //开启深度测试
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
//计算宽高比
        float ratio = (float) width / height;
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 20);
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 5.0f, 5.0f, 10.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT| GLES20.GL_DEPTH_BUFFER_BIT);
// 启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(vPositionHandle);
        GLES20.glEnableVertexAttribArray(vColorHandle);

        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(vMatrix, 1, false, mMVPMatrix, 0);
        GLES20.glVertexAttribPointer(vColorHandle, 4, GLES20.GL_FLOAT, false, 0, mColorFloatBuffer);
        GLES20.glVertexAttribPointer(vPositionHandle, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, 0, mPositionFloatBuffer);
        //索引法绘制正方体
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mIndex.length, GLES20.GL_UNSIGNED_SHORT, mIndexFloatBuffer);
        // 禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(vPositionHandle);
        GLES20.glDisableVertexAttribArray(vColorHandle);
    }
}
