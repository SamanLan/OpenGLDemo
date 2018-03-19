package com.samanlan.opengldemo;

import android.content.Context;
import android.opengl.GLES20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * <code>GLHelper</code>{}
 *
 * @author :LanSaman
 * @date :2018/3/19
 * @description :
 */
public class GLHelper {

    /**
     * 读取文件
     */
    public static String readTextFileFromResource(Context context, int resourceId) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream inputStream = context.getResources().openRawResource(resourceId);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String nextLine;
            while ((nextLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(nextLine);
                stringBuilder.append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static int compileShader(int type, String shaderCode) {
        if (type == GLES20.GL_VERTEX_SHADER || type == GLES20.GL_FRAGMENT_SHADER) {
            return 0;
        }
        // 创建一个新的着色器对象
        final int shaderObjectId = GLES20.glCreateShader(type);
        if (shaderObjectId==0) {
            return 0;
        }
        // 有了对象就上传源代码，将对象与代码关联起来
        GLES20.glShaderSource(shaderObjectId, shaderCode);
        final int[] compileStatus = new int[1];
        // 读取与shaderObjectId关联的编译状态，并将它写入到compileStatus的第0个元素
        GLES20.glGetShaderiv(shaderObjectId, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0) {
            // 编译失败删除shader对象
            GLES20.glDeleteShader(shaderObjectId);
            return 0;
        }
        return shaderObjectId;
    }
}
