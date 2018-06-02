package com.samanlan.opengldemo.texture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.samanlan.opengldemo.GLHelper;
import com.samanlan.opengldemo.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Texture1 implements GLSurfaceView.Renderer {

    Context mContext;
    Bitmap mBitmap;
    int mProgram;
    int vPositionHandle;
    int tPositionHandle;
    int tHandle;
    int vMatrixHandle;

    FloatBuffer vBuffer;
    FloatBuffer tBuffer;

    float[] vPosition = new float[]{
            -1f, 1f,
            -1f, -1f,
            1f, 1f,
            1f, -1f
    };

    float[] tPosition = new float[]{
            0f, 0f,
            0f, 1f,
            1f, 0f,
            1f, 1f,
    };

    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    public Texture1(Context context) {
        vBuffer = ByteBuffer.allocateDirect(4 * vPosition.length).order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vBuffer.put(vPosition);
        vBuffer.position(0);
        tBuffer = ByteBuffer.allocateDirect(4 * tPosition.length).order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        tBuffer.put(tPosition);
        tBuffer.position(0);
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.texture2);
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        int vertexId = GLHelper.compileShader(GLES20.GL_VERTEX_SHADER, GLHelper.readTextFileFromResource(mContext, R.raw.texture1_vertex_shader));
        int fragmentId = GLHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, GLHelper.readTextFileFromResource(mContext, R.raw.texture1_fragment_shader));
        mProgram = GLHelper.linkProgram(vertexId, fragmentId);
        GLES20.glUseProgram(mProgram);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();
        float sWH = w / (float) h;
        float sWidthHeight = width / (float) height;
        if (width > height) {
            if (sWH > sWidthHeight) {
                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight * sWH, sWidthHeight * sWH, -1, 1, 3, 7);
            } else {
                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight / sWH, sWidthHeight / sWH, -1, 1, 3, 7);
            }
        } else {
            if (sWH > sWidthHeight) {
                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -1 / sWidthHeight * sWH, 1 / sWidthHeight * sWH, 3, 7);
            } else {
                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -sWH / sWidthHeight, sWH / sWidthHeight, 3, 7);
            }
        }
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
        vPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_position");
        tPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_t_position");
        vMatrixHandle = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        tHandle = GLES20.glGetUniformLocation(mProgram, "u_sampler");
        GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glEnableVertexAttribArray(vPositionHandle);
        GLES20.glEnableVertexAttribArray(tPositionHandle);
        GLES20.glUniform1i(tHandle, 0);
        createTexture();
        GLES20.glVertexAttribPointer(vPositionHandle, 2, GLES20.GL_FLOAT, false, 0, vBuffer);
        GLES20.glVertexAttribPointer(tPositionHandle, 2, GLES20.GL_FLOAT, false, 0, tBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(vPositionHandle);
        GLES20.glDisableVertexAttribArray(tPositionHandle);
    }

    private int createTexture() {
        int[] texture = new int[1];
        if (mBitmap != null && !mBitmap.isRecycled()) {
            //生成纹理
            GLES20.glGenTextures(1, texture, 0);
            //生成纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
            mBitmap.recycle();
            return texture[0];
        }
        return 0;
    }
}
