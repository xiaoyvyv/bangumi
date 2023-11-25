/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.rendering.android;

import com.live2d.sdk.cubism.framework.math.CubismVector2;

import java.nio.IntBuffer;
import java.util.Arrays;

import static android.opengl.GLES20.*;

/**
 * This class is for drawing offscreen.
 **/
public class CubismOffscreenSurfaceAndroid {
    /**
     * Constructor
     */
    public CubismOffscreenSurfaceAndroid() {}

    /**
     * Copy constructor
     *
     * @param offscreenFrame offscreen frame buffer
     */
    public CubismOffscreenSurfaceAndroid(CubismOffscreenSurfaceAndroid offscreenFrame) {
        renderTexture = Arrays.copyOf(offscreenFrame.renderTexture, offscreenFrame.renderTexture.length);
        colorBuffer = Arrays.copyOf(offscreenFrame.colorBuffer, offscreenFrame.colorBuffer.length);
        oldFBO = Arrays.copyOf(offscreenFrame.oldFBO, offscreenFrame.oldFBO.length);

        bufferWidth = offscreenFrame.bufferWidth;
        bufferHeight = offscreenFrame.bufferHeight;
        isColorBufferInherited = offscreenFrame.isColorBufferInherited;
    }

    /**
     * Begin drawing to the specific drawing target.
     *
     * @param restoreFBO If it is not "null", EndDraw will run glBindFrameBuffer this value.
     **/
    public void beginDraw(int[] restoreFBO) {
        if (renderTexture == null) {
            return;
        }

        // Remember the back buffer surface.
        if (restoreFBO == null) {
            glGetIntegerv(GL_FRAMEBUFFER_BINDING, IntBuffer.wrap(oldFBO));
        } else {
            oldFBO = restoreFBO;
        }

        // Set the RenderTexture for the mask to active.
        glBindFramebuffer(GL_FRAMEBUFFER, renderTexture[0]);
    }

    /**
     * Begin drawing to the specific drawing target.
     * <p>
     * This method reproduces default argument of C++. The users can use this method instead of specifying null as an argument.
     * </p>
     */
    public void beginDraw() {
        beginDraw(null);
    }

    /**
     * Finish drawing.
     **/
    public void endDraw() {
        if (renderTexture == null) {
            return;
        }

        // Return the drawing target.
        glBindFramebuffer(GL_FRAMEBUFFER, oldFBO[0]);
    }

    /**
     * Clear the rendering target.
     * Note: Call this after BeginDraw().
     *
     * @param r red(0.0~1.0)
     * @param g green(0.0~1.0)
     * @param b blue(0.0~1.0)
     * @param a α(0.0~1.0)
     */
    public void clear(final float r, final float g, final float b, final float a) {
        glClearColor(r, g, b, a);
        glClear(GL_COLOR_BUFFER_BIT);
    }

    /**
     * Create CubismOffscreenFrame.
     * <p>
     * This method reproduces default argument of C++. The users can use this method instead of specifying null as colorBuffer(2rd) argument.
     * </p>
     *
     * @param displayBufferSize buffer size(Vector type)
     */
    public void createOffscreenFrame(CubismVector2 displayBufferSize) {
        createOffscreenFrame((int) displayBufferSize.x, (int) displayBufferSize.y, null);
    }

    /**
     * Create CubismOffscreenFrame.
     *
     * @param displayBufferSize buffer size
     * @param colorBuffer if non-zero, use colorBuffer as pixel storage area.
     */
    public void createOffscreenFrame(final CubismVector2 displayBufferSize, final int[] colorBuffer) {
        createOffscreenFrame((int) displayBufferSize.x, (int) displayBufferSize.y, colorBuffer);
    }

    /**
     * Create CubismOffscreenFrame.
     * <p>
     * This method reproduces default argument of C++. The users can use this method instead of specifying null as colorBuffer(3rd) argument.
     * </p>
     *
     * @param displayBufferWidth buffer width
     * @param displayBufferHeight buffer height
     */
    public void createOffscreenFrame(int displayBufferWidth, int displayBufferHeight) {
        createOffscreenFrame(displayBufferWidth, displayBufferHeight, null);
    }

    /**
     * Create CubismOffscreenFrame.
     *
     * @param displayBufferWidth buffer width
     * @param displayBufferHeight buffer height
     * @param colorBuffer if non-zero, use colorBuffer as pixel storage area.
     */
    public void createOffscreenFrame(final int displayBufferWidth, final int displayBufferHeight, final int[] colorBuffer) {
        // いったん削除
        destroyOffscreenFrame();

        int[] ret = new int[1];

        // Create new offscreen surface
        if (colorBuffer == null) {
            this.colorBuffer = new int[1];
            glGenTextures(1, this.colorBuffer, 0);

            glBindTexture(GL_TEXTURE_2D, this.colorBuffer[0]);
            glTexImage2D(
                GL_TEXTURE_2D,
                0,
                GL_RGBA,
                displayBufferWidth,
                displayBufferHeight,
                0,
                GL_RGBA,
                GL_UNSIGNED_BYTE,
                null);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glBindTexture(GL_TEXTURE_2D, 0);

            isColorBufferInherited = false;
        }
        // Use the designated color buffer.
        else {
            this.colorBuffer = colorBuffer;
            isColorBufferInherited = true;
        }

        int[] tmpFBO = new int[1];

        glGetIntegerv(GL_FRAMEBUFFER_BINDING, tmpFBO, 0);

        glGenFramebuffers(1, ret, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, ret[0]);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.colorBuffer[0], 0);
        glBindFramebuffer(GL_FRAMEBUFFER, tmpFBO[0]);

        this.renderTexture = new int[1];
        this.renderTexture[0] = ret[0];
        bufferWidth = displayBufferWidth;
        bufferHeight = displayBufferHeight;
    }

    /**
     * Destroy CubismOffscreenFrame
     */
    public void destroyOffscreenFrame() {
        if (!isColorBufferInherited && (colorBuffer != null)) {
            glDeleteTextures(1, colorBuffer, 0);
            colorBuffer = null;
        }

        if (renderTexture != null) {
            glDeleteFramebuffers(1, renderTexture, 0);
            renderTexture = null;
        }
    }

    /**
     * レンダーテクスチャのアドレス（intの配列型）を取得する。
     *
     * @return レンダーテクスチャのアドレス
     */
    public int[] getRenderTexture() {
        return renderTexture;
    }


    /**
     * Get color buffer.
     *
     * @return color buffer
     */

    public int[] getColorBuffer() {
        return colorBuffer;
    }


    /**
     * Get buffer width
     *
     * @return buffer width
     */
    public int getBufferWidth() {
        return bufferWidth;
    }


    /**
     * Get buffer height.
     *
     * @return buffer height
     */
    public int getBufferHeight() {
        return bufferHeight;
    }


    /**
     * Whether render texture is valid.
     *
     * @return If it is valid, return true
     */

    public boolean isValid() {
        return renderTexture != null;
    }

    /**
     * Whether buffer size is the same.
     *
     * @param bufferSize buffer size
     * @return Whether buffer size is the same
     */
    public boolean isSameSize(final CubismVector2 bufferSize) {
        int width = (int) bufferSize.x;
        int height = (int) bufferSize.y;

        return (width == bufferWidth && height == bufferHeight);
    }

    /**
     * Whether buffer size is the same.
     *
     * @param width buffer width
     * @param height buffer height
     * @return Whether buffer size is the same
     */
    public boolean isSameSize(final int width, final int height) {
        return (width == bufferWidth && height == bufferHeight);
    }


    /**
     * texture as rendering target. It is called frame buffer.
     */
    private int[] renderTexture;

    /**
     * color buffer
     */
    private int[] colorBuffer = new int[1];

    /**
     * old frame buffer
     */
    private int[] oldFBO = new int[1];

    /**
     * width specified at Create() method
     */
    private int bufferWidth;

    /**
     * height specified at Create() method
     */
    private int bufferHeight;

    /**
     * Whether the color buffer is the one set by the argument
     */
    private boolean isColorBufferInherited;
}
