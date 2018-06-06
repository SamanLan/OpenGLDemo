package com.samanlan.opengldemo;

import android.graphics.Camera;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import com.samanlan.opengldemo.texture.CameraTexture;
import com.samanlan.opengldemo.utils.Camera2Utils;

public class CameraActivity extends AppCompatActivity implements SurfaceTexture.OnFrameAvailableListener {

    private GLSurfaceView mGLSurfaceView;
    private CameraTexture mRender;
    private Camera2Utils mCamera2Utils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLSurfaceView = new GLSurfaceView(this);
        // 设置OpenGLES版本为2.0
        mGLSurfaceView.setEGLContextClientVersion(2);
        final DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mGLSurfaceView.setRenderer(mRender = new CameraTexture(this));
        mRender.setOnFrameAvailableListener(this);
        mRender.setOnSurfaceCallback(new CameraTexture.OnSurfaceCallback() {
            @Override
            public void onSurfaceCreated(SurfaceTexture surfaceTexture) {
                if (mCamera2Utils == null) {
                    mCamera2Utils = new Camera2Utils(CameraActivity.this);
                }
                mCamera2Utils.openCamera(surfaceTexture, dm.widthPixels, dm.heightPixels);
            }

            @Override
            public void onSurfaceChanged() {

            }

            @Override
            public void onDrawFrame() {

            }
        });
        setContentView(mGLSurfaceView);
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        mGLSurfaceView.requestRender();
    }
}
