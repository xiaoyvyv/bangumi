/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.xiaoyv.common.widget.musume;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glClearDepthf;
import static android.opengl.GLES20.glCreateShader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.live2d.sdk.cubism.framework.CubismFramework;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class LAppDelegate implements GLSurfaceView.Renderer {

    @SuppressLint("StaticFieldLeak")
    private static LAppDelegate s_instance;

    private Context context;

    private final CubismFramework.Option cubismOption = new CubismFramework.Option();

    private LAppTextureManager textureManager;
    private LAppView view;
    private int windowWidth;
    private int windowHeight;
    private boolean isActive = true;

    /**
     * Model Scene Index
     */
    private LAppDefine.ModelDir currentModel;

    /**
     * Are you clicking?
     */
    private boolean isCaptured;
    /**
     * X coordinate of the mouse
     */
    private float mouseX;

    /**
     * Y coordinate of the mouse
     */
    private float mouseY;

    private LAppDelegate() {
        currentModel = LAppDefine.ModelDir.values()[0];

        // Set up Cubism SDK framework.
        cubismOption.logFunction = new LAppPal.PrintLogFunction();
        cubismOption.loggingLevel = LAppDefine.cubismLoggingLevel;

        CubismFramework.cleanUp();
        CubismFramework.startUp(cubismOption);
    }


    public static LAppDelegate getInstance() {
        if (s_instance == null) {
            s_instance = new LAppDelegate();
        }
        return s_instance;
    }

    /**
     * Release an instance of a class (singleton).
     */
    public static void releaseInstance() {
        if (s_instance != null) {
            s_instance = null;
        }
    }

    /**
     * Deactivate an application
     */
    public void deactivateApp() {
        isActive = false;
    }

    public void onResume(Context context) {
        textureManager = new LAppTextureManager();

        view = new LAppView();

        this.context = context;

        LAppPal.updateTime();
    }

    public void onPause() {
        view = null;
        textureManager = null;

        LAppLive2DManager.releaseInstance();
        CubismFramework.dispose();
    }

    public void onDestroy() {
        releaseInstance();
    }

    public void refreshModel() {
        currentModel = LAppLive2DManager.getInstance().getCurrentModel();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Texture sampling settings
        GLES20.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        GLES20.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

        // 透過設定
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // 初始化Cubism SDK框架
        CubismFramework.initialize();

        // Shader initialization
        view.initializeShader();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 描画範囲指定
        GLES20.glViewport(0, 0, width, height);
        windowWidth = width;
        windowHeight = height;

        // Initializing App View
        view.initialize();
        view.initializeSprite();

        // load models
        if (LAppLive2DManager.getInstance().getCurrentModel() != currentModel) {
            LAppLive2DManager.getInstance().changeScene(currentModel.getOrder());
        }

        isActive = true;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 時間更新
        LAppPal.updateTime();

        // 画面初期化
        glClearColor(1.0f, 1.0f, 1.0f, .0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearDepthf(1.0f);

        if (view != null) {
            view.render();
        }

        // 停用应用程序
        if (!isActive) {
            // activity.finishAndRemoveTask();
            // System.exit(0);
        }
    }


    public void onTouchBegan(float x, float y) {
        mouseX = x;
        mouseY = y;

        if (view != null) {
            isCaptured = true;
            view.onTouchesBegan(mouseX, mouseY);
        }
    }

    public void onTouchEnd(float x, float y) {
        mouseX = x;
        mouseY = y;

        if (view != null) {
            isCaptured = false;
            view.onTouchesEnded(mouseX, mouseY);
        }
    }

    public void onTouchMoved(float x, float y) {
        mouseX = x;
        mouseY = y;

        if (isCaptured && view != null) {
            view.onTouchesMoved(mouseX, mouseY);
        }
    }

    // Register a shader
    public int createShader() {
        int vertexShaderId = glCreateShader(GLES20.GL_VERTEX_SHADER);
        final String vertexShader =
                "#version 100\n"
                        + "attribute vec3 position;"
                        + "attribute vec2 uv;"
                        + "varying vec2 vuv;"
                        + "void main(void){"
                        + "gl_Position = vec4(position, 1.0);"
                        + "vuv = uv;"
                        + "}";

        GLES20.glShaderSource(vertexShaderId, vertexShader);
        GLES20.glCompileShader(vertexShaderId);

        // Compiling a Fragment Shader
        int fragmentShaderId = glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        final String fragmentShader =
                "#version 100\n"
                        + "precision mediump float;"
                        + "uniform sampler2D texture;"
                        + "varying vec2 vuv;"
                        + "uniform vec4 baseColor;"
                        + "void main(void){"
                        + "gl_FragColor = texture2D(texture, vuv) * baseColor;"
                        + "}";

        GLES20.glShaderSource(fragmentShaderId, fragmentShader);
        GLES20.glCompileShader(fragmentShaderId);

        // Creating Program Objects
        int programId = GLES20.glCreateProgram();

        // Set Program Shaders
        GLES20.glAttachShader(programId, vertexShaderId);
        GLES20.glAttachShader(programId, fragmentShaderId);

        GLES20.glLinkProgram(programId);

        GLES20.glUseProgram(programId);

        return programId;
    }

    // getter, setter群
    public Context getActivity() {
        return context;
    }

    public LAppTextureManager getTextureManager() {
        return textureManager;
    }

    public LAppView getView() {
        return view;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

}
