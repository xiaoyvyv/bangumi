package com.xiaoyv.common.helper

import android.view.KeyEvent

/**
 * 快速打开设置页
 */
class VolumeButtonHelper(private val onSecretCodeListener: OnSecretCodeListener) {
    private var inputSequence = StringBuilder()

    fun handleVolumeButton(event: KeyEvent) {
        if (event.keyCode == KeyEvent.KEYCODE_VOLUME_UP || event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (event.action == KeyEvent.ACTION_DOWN) {
                val keyChar = if (event.keyCode == KeyEvent.KEYCODE_VOLUME_UP) '+' else '-'
                inputSequence.append(keyChar)

                checkSecretCode()
            }
        }
    }

    private fun checkSecretCode() {
        if (inputSequence.toString().contains(SECRET_CODE)) {
            onSecretCodeListener.onSecretCodeEntered()
            inputSequence.clear()
        }
    }

    interface OnSecretCodeListener {
        fun onSecretCodeEntered()
    }

    private companion object {
        private const val SECRET_CODE = "++--"
    }
}
