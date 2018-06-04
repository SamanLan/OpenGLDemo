package com.samanlan.opengldemo;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.samanlan.opengldemo.render.ColorTrianle;
import com.samanlan.opengldemo.render.Cube;
import com.samanlan.opengldemo.render.Square;
import com.samanlan.opengldemo.render.Trianle;
import com.samanlan.opengldemo.texture.Texture1;
import com.samanlan.opengldemo.texture.Texture2;

public class MainActivity extends AppCompatActivity {

    boolean isRenderSet = false;
    GLSurfaceView mGLSurfaceView;
    Texture2 mTexture2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLSurfaceView = new GLSurfaceView(this);
        // 设置OpenGLES版本为2.0
        mGLSurfaceView.setEGLContextClientVersion(2);
//        mGLSurfaceView.setRenderer(new Trianle(this));
        mTexture2= new Texture2(this);
        mGLSurfaceView.setRenderer(mTexture2);

        isRenderSet = true;
        setContentView(R.layout.activity_main);
        ((ViewGroup) findViewById(R.id.content)).addView(mGLSurfaceView);
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

    int index = 0;
    public void onclick(View view) {
        mTexture2.changeColor(index % 4);
        index++;
    }
}
