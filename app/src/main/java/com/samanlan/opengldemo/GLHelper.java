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
        if (type != GLES20.GL_VERTEX_SHADER && type != GLES20.GL_FRAGMENT_SHADER) {
            return 0;
        }
        // 创建一个新的着色器对象
        final int shaderObjectId = GLES20.glCreateShader(type);
        if (shaderObjectId==0) {
            System.out.println("创建着色器失败");
            return 0;
        }
        // 有了对象就上传源代码，将对象与代码关联起来
        GLES20.glShaderSource(shaderObjectId, shaderCode);
        // 关联后要完成
        GLES20.glCompileShader(shaderObjectId);
        final int[] compileStatus = new int[1];
        // 读取与shaderObjectId关联的编译状态，并将它写入到compileStatus的第0个元素
        GLES20.glGetShaderiv(shaderObjectId, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0) {
            System.out.println("编译失败，原因：" + GLES20.glGetShaderInfoLog(shaderObjectId));
            // 编译失败删除shader对象
            GLES20.glDeleteShader(shaderObjectId);
            return 0;
        }
        return shaderObjectId;
    }

    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        // 新建程序对象
        final int progamObjectId = GLES20.glCreateProgram();
        if (progamObjectId == 0) {
            System.out.println("创建工程失败" + GLES20.glGetError());
            return 0;
        }

        // 附着上着色器
        GLES20.glAttachShader(progamObjectId, vertexShaderId);
        GLES20.glAttachShader(progamObjectId, fragmentShaderId);

        // 链接program
        GLES20.glLinkProgram(progamObjectId);

        final int[] linkStatus = new int[1];
        // 读取与progamObjectId关联的连接状态，并将它写入到linkStatus的第0个元素
        GLES20.glGetProgramiv(progamObjectId, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == 0) {
            System.out.println("连接program失败，原因：" + GLES20.glGetProgramInfoLog(progamObjectId));
            // 连接失败则删除program对象
            GLES20.glDeleteProgram(progamObjectId);
            return 0;
        }
        return progamObjectId;
    }

    public static boolean validateProgram(int programObjectId) {
        // 验证一下OpenGL程序是否有效，是否低效率、无法运行等
        // 调试期开启即可
        GLES20.glValidateProgram(programObjectId);
        final int[] validateStatus = new int[1];
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_VALIDATE_STATUS, validateStatus, 0);
        System.out.println("验证消息：" + GLES20.glGetProgramInfoLog(programObjectId));
        return validateStatus[0] != 0;
    }
}
