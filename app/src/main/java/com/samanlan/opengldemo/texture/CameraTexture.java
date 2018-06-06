package com.samanlan.opengldemo.texture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.samanlan.opengldemo.GLHelper;
import com.samanlan.opengldemo.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenFramebuffers;
import static android.opengl.GLES20.glVertexAttribPointer;

public class CameraTexture implements GLSurfaceView.Renderer {

    private SurfaceTexture mSurfaceTexture;
    private int mOESTextureId;
    private SurfaceTexture.OnFrameAvailableListener mOnFrameAvailableListener;
    private OnSurfaceCallback mOnSurfaceCallback;

    Context mContext;
    int mProgram;
    int vPositionHandle;
    int tPositionHandle;
    int tHandle;
    int vMatrixHandle;

    FloatBuffer vBuffer;
    FloatBuffer tBuffer;

    float[] vPosition = new float[]{
            1f, 1f, 1f, 1f,
            -1f, 1f, 0f, 1f,
            -1f, -1f, 0f, 0f,
            1f, 1f, 1f, 1f,
            -1f, -1f, 0f, 0f,
            1f, -1f, 1f, 0f
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
    private int[] mFBOIds = new int[1];

    public CameraTexture(Context context) {
        mContext = context;
        vBuffer = ByteBuffer.allocateDirect(4 * vPosition.length).order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vBuffer.put(vPosition);
        vBuffer.position(0);
        tBuffer = ByteBuffer.allocateDirect(4 * tPosition.length).order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        tBuffer.put(tPosition);
        tBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        int vertexId = GLHelper.compileShader(GLES20.GL_VERTEX_SHADER, GLHelper.readTextFileFromResource(mContext, R.raw.texture3_vertex_shader));
        int fragmentId = GLHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, GLHelper.readTextFileFromResource(mContext, R.raw.texture3_fragment_shader));
        mProgram = GLHelper.linkProgram(vertexId, fragmentId);
        mOESTextureId = createOESTextureObject();
        GLES20.glUseProgram(mProgram);

//        glGenFramebuffers(1, mFBOIds, 0);
//        glBindFramebuffer(GL_FRAMEBUFFER, mFBOIds[0]);
        initSurfaceTexture();
        if (mOnSurfaceCallback != null && mSurfaceTexture != null) {
            mOnSurfaceCallback.onSurfaceCreated(mSurfaceTexture);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
//
//        int w = 100;
//        int h = 100;
//        float sWH = w / (float) h;
//        float sWidthHeight = width / (float) height;
//        if (width > height) {
//            if (sWH > sWidthHeight) {
//                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight * sWH, sWidthHeight * sWH, -1, 1, 3, 7);
//            } else {
//                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight / sWH, sWidthHeight / sWH, -1, 1, 3, 7);
//            }
//        } else {
//            if (sWH > sWidthHeight) {
//                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -1 / sWidthHeight * sWH, 1 / sWidthHeight * sWH, 3, 7);
//            } else {
//                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -sWH / sWidthHeight, sWH / sWidthHeight, 3, 7);
//            }
//        }
//        //设置相机位置
//        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
//        //计算变换矩阵
//        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
        if (mOnSurfaceCallback != null) {
            mOnSurfaceCallback.onSurfaceChanged();
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mSurfaceTexture != null) {
            //更新数据，其实也是消耗数据，将上一帧的数据处理或者抛弃掉，要不然SurfaceTexture是接收不到最新数据
            mSurfaceTexture.updateTexImage();
            mSurfaceTexture.getTransformMatrix(mMVPMatrix);
        }

        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
        vPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        tPositionHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoordinate");
        vMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uTextureMatrix");
        tHandle = GLES20.glGetUniformLocation(mProgram, "uTextureSampler");
        glActiveTexture(GL_TEXTURE_EXTERNAL_OES);
        glBindTexture(GL_TEXTURE_EXTERNAL_OES, mOESTextureId);
        GLES20.glUniform1i(tHandle, 0);
        GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glEnableVertexAttribArray(vPositionHandle);

        vBuffer.position(0);
        glEnableVertexAttribArray(vPositionHandle);
        glVertexAttribPointer(vPositionHandle, 2, GL_FLOAT, false, 16, vBuffer);

        GLES20.glEnableVertexAttribArray(tPositionHandle);
        vBuffer.position(2);
        glEnableVertexAttribArray(tPositionHandle);
        glVertexAttribPointer(tPositionHandle, 2, GL_FLOAT, false, 16, vBuffer);

        glDrawArrays(GL_TRIANGLES, 0, 6);

//        GLES20.glVertexAttribPointer(vPositionHandle, 2, GLES20.GL_FLOAT, false, 0, vBuffer);
//        GLES20.glVertexAttribPointer(tPositionHandle, 2, GLES20.GL_FLOAT, false, 0, tBuffer);
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
//        GLES20.glDisableVertexAttribArray(vPositionHandle);
//        GLES20.glDisableVertexAttribArray(tPositionHandle);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        if (mOnSurfaceCallback != null) {
            mOnSurfaceCallback.onDrawFrame();
        }
    }

    public static int createOESTextureObject() {
        int[] tex = new int[1];
        GLES20.glGenTextures(1, tex, 0);
        glBindTexture(GL_TEXTURE_EXTERNAL_OES, tex[0]);
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return tex[0];
    }

    public boolean initSurfaceTexture() {
        //根据OES纹理ID实例化SurfaceTexture
        mSurfaceTexture = new SurfaceTexture(mOESTextureId);
        //当SurfaceTexture接收到一帧数据时，请求OpenGL ES进行渲染
        if (mOnFrameAvailableListener != null) {
            mSurfaceTexture.setOnFrameAvailableListener(mOnFrameAvailableListener);
        }
        return true;
    }

    public void setOnFrameAvailableListener(SurfaceTexture.OnFrameAvailableListener onFrameAvailableListener) {
        mOnFrameAvailableListener = onFrameAvailableListener;
    }

    public void setOnSurfaceCallback(OnSurfaceCallback onSurfaceCallback) {
        mOnSurfaceCallback = onSurfaceCallback;
    }

    public interface OnSurfaceCallback{
        void onSurfaceCreated(SurfaceTexture surfaceTexture);

        void onSurfaceChanged();

        void onDrawFrame();
    }
}
