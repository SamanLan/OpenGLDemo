package com.samanlan.opengldemo;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.samanlan.opengldemo.render.ColorTrianle;
import com.samanlan.opengldemo.render.Cube;
import com.samanlan.opengldemo.render.Square;
import com.samanlan.opengldemo.render.Trianle;
import com.samanlan.opengldemo.texture.Texture1;

public class MainActivity extends AppCompatActivity {

    boolean isRenderSet = false;
    GLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLSurfaceView = new GLSurfaceView(this);
        // 设置OpenGLES版本为2.0
        mGLSurfaceView.setEGLContextClientVersion(2);
//        mGLSurfaceView.setRenderer(new Trianle(this));
        mGLSurfaceView.setRenderer(new Texture1(this));

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
