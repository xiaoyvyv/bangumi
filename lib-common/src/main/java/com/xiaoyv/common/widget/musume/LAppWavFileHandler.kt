/*
 *
 *  * Copyright(c) Live2D Inc. All rights reserved.
 *  *
 *  * Use of this source code is governed by the Live2D Open Software license
 *  * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 *
 */
package com.xiaoyv.common.widget.musume

import android.media.MediaPlayer

class LAppWavFileHandler(private val filePath: String) : Thread() {
    private val mediaPlayer = MediaPlayer()

    override fun run() {
        runCatching {
            val afd = LAppDelegate.getInstance().activity.assets.openFd(filePath)

            // 设置音频文件路径
            mediaPlayer.setDataSource(afd)

            // 准备并开始播放音频
            mediaPlayer.prepare()
            mediaPlayer.start()

            // 阻塞线程，直到音频播放完成
            mediaPlayer.setOnCompletionListener {
                // 释放MediaPlayer资源
                mediaPlayer.release()
            }
        }
    }
}