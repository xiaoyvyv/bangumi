/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */
@file:Suppress("KotlinConstantConditions")

package com.live2d.sdk.cubism.view

import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import com.live2d.sdk.cubism.framework.CubismFramework

// テクスチャの管理を行うクラス
class LAppTextureManager {
    private val textures: MutableList<TextureInfo> = arrayListOf()

    // 画像情報データクラス
    class TextureInfo {
        @JvmField
        var id: Int = 0 // テクスチャID
        var width: Int = 0 // 横幅
        var height: Int = 0 // 高さ
        var filePath: String? = null // ファイル名
    }

    // 画像読み込み
    // imageFileOffset: glGenTexturesで作成したテクスチャの保存場所
    fun createTextureFromPngFile(filePath: String): TextureInfo {
        // search loaded texture already
        for (textureInfo in textures) {
            if (textureInfo.filePath == filePath) {
                return textureInfo
            }
        }

        // assetsフォルダの画像からビットマップを作成する
        val bytes = LAppPal.loadFileAsBytes(filePath)
        // decodeStreamは乗算済みアルファとして画像を読み込むようである
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

        LAppPal.printLog("ThreadId -> ${Thread.currentThread()}")

        // Texture0をアクティブにする
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)

        // OpenGLにテクスチャを生成
        val textureId = IntArray(1)
        GLES20.glGenTextures(1, textureId, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0])

        // メモリ上の2D画像をテクスチャに割り当てる
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

        // ミップマップを生成する
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D)

        // 縮小時の補間設定
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR)
        // 拡大時の補間設定
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

        val textureInfo = TextureInfo()
        textureInfo.filePath = filePath
        textureInfo.width = bitmap.getWidth()
        textureInfo.height = bitmap.getHeight()
        textureInfo.id = textureId[0]

        textures.add(textureInfo)

        // bitmap解放
        bitmap.recycle()

        if (LAppDefine.DEBUG_LOG_ENABLE) {
            CubismFramework.coreLogFunction("Create texture: $filePath")
        }

        return textureInfo
    }
}
