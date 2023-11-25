package com.xiaoyv.common.widget.musume

import com.xiaoyv.common.helper.AudioPlayer

class LAppWavFileHandler(private val filePath: String) : Thread() {

    override fun run() {
        runCatching {
            AudioPlayer.instance
                .play(LAppDelegate.getInstance().activity.assets.openFd(filePath))
        }
    }
}