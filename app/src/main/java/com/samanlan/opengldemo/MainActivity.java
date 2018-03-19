package com.samanlan.opengldemo;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    boolean isRenderSet = false;
    GLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLSurfaceView = new GLSurfaceView(this);
        mGLSurfaceView.setRenderer(new CustomRenderer(this));
        isRenderSet = true;
        setContentView(mGLSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isRenderSet) {
            mGLSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isRenderSet) {
            mGLSurfaceView.onResume();
        }
    }
}
