/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.xiaoyv.common.widget.musume;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.live2d.sdk.cubism.framework.CubismFramework;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 负责管理纹理的类
 */
public class LAppTextureManager {

    /**
     * 图片信息的列表
     */
    private final List<TextureInfo> textures = new ArrayList<>();

    // 图片信息数据类
    public static class TextureInfo {
        public int id;  // 纹理ID
        public int width;   // 宽度
        public int height;  // 高度
        public String filePath; // 文件名
    }

    // 读取图片
    // imageFileOffset: 用于保存由 glGenTextures 创建的纹理的位置
    public TextureInfo createTextureFromPngFile(String filePath) {
        // 查找已加载的纹理
        for (TextureInfo textureInfo : textures) {
            if (textureInfo.filePath.equals(filePath)) {
                return textureInfo;
            }
        }

        // 从 assets 文件夹中的图像创建位图
        AssetManager assetManager = LAppDelegate.getInstance().getActivity().getAssets();
        InputStream stream = null;
        try {
            stream = assetManager.open(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // decodeStream 似乎以预乘阿尔法的方式读取图像
        Bitmap bitmap = BitmapFactory.decodeStream(stream);

        // 激活 Texture0
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        // 在 OpenGL 中生成纹理
        int[] textureId = new int[1];
        GLES20.glGenTextures(1, textureId, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);

        // 将内存中的 2D 图像分配给纹理
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        // 生成 Mipmap
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

        // 缩小时的插值设置
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        // 放大时的插值设置
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        TextureInfo textureInfo = new TextureInfo();
        textureInfo.filePath = filePath;
        textureInfo.width = bitmap.getWidth();
        textureInfo.height = bitmap.getHeight();
        textureInfo.id = textureId[0];

        textures.add(textureInfo);

        // 释放位图
        bitmap.recycle();
        bitmap = null;

        if (LAppDefine.DEBUG_LOG_ENABLE) {
            CubismFramework.coreLogFunction("创建纹理: " + filePath);
        }

        return textureInfo;
    }
}
