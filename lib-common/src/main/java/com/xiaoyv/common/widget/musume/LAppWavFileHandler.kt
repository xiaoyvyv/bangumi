package com.xiaoyv.common.widget.musume

import com.xiaoyv.common.helper.AudioPlayer
import com.xiaoyv.common.helper.ConfigHelper

class LAppWavFileHandler(private val filePath: String) : Thread() {

    override fun run() {
        if (ConfigHelper.isRobotVoiceDisable()) return

        runCatching {
            AudioPlayer.instance
                .play(LAppDelegate.getInstance().activity.assets.openFd(filePath))
        }
    }
}