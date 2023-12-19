package com.xiaoyv.common.helper

import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.xiaoyv.common.config.annotation.TimelinePageType

/**
 * Class: [ConfigHelper]
 *
 * @author why
 * @since 12/10/23
 */
object ConfigHelper {
    private const val KEY_BAIDU_TRANSLATE_APP_ID = "baidu-translate-app-id"
    private const val KEY_BAIDU_TRANSLATE_APP_SECRET = "baidu-translate-secret"
    private const val KEY_ROBOT_DISABLE = "robot-disable"
    private const val KEY_ROBOT_VOICE_DISABLE = "robot-voice-disable"
    private const val KEY_IMAGE_ANIMATION = "image-animation"
    private const val KEY_IMAGE_COMPRESS = "image-compress"
    private const val KEY_TIMELINE_TYPE = "timeline"
    private val KEY_VERSION_TIP get() = "version-tip-" + AppUtils.getAppVersionCode()

    fun configBaiduTranslateId(appId: String) {
        SPStaticUtils.put(KEY_BAIDU_TRANSLATE_APP_ID, appId)
    }

    fun configBaiduTranslateSecret(secret: String) {
        SPStaticUtils.put(KEY_BAIDU_TRANSLATE_APP_SECRET, secret)
    }

    fun readBaiduTranslateConfig(): Pair<String, String> {
        return SPStaticUtils.getString(KEY_BAIDU_TRANSLATE_APP_ID).orEmpty() to
                SPStaticUtils.getString(KEY_BAIDU_TRANSLATE_APP_SECRET).orEmpty()
    }

    fun isRobotDisable(): Boolean {
        return SPStaticUtils.getBoolean(KEY_ROBOT_DISABLE, true)
    }

    fun setRobotDisable(disable: Boolean) {
        return SPStaticUtils.put(KEY_ROBOT_DISABLE, disable)
    }

    fun isRobotVoiceDisable(): Boolean {
        return SPStaticUtils.getBoolean(KEY_ROBOT_VOICE_DISABLE, true)
    }

    fun setRobotVoiceDisable(disable: Boolean) {
        return SPStaticUtils.put(KEY_ROBOT_VOICE_DISABLE, disable)
    }

    fun isImageAnimation(): Boolean {
        return SPStaticUtils.getBoolean(KEY_IMAGE_ANIMATION, true)
    }

    fun setImageAnimation(enable: Boolean) {
        return SPStaticUtils.put(KEY_IMAGE_ANIMATION, enable)
    }

    fun isImageCompress(): Boolean {
        return SPStaticUtils.getBoolean(KEY_IMAGE_COMPRESS, true)
    }

    fun setImageCompress(enable: Boolean) {
        return SPStaticUtils.put(KEY_IMAGE_COMPRESS, enable)
    }

    /**
     * 时间线默认展示好友还是全部
     */
    var timelinePageType: Int
        get() = SPStaticUtils.getInt(KEY_TIMELINE_TYPE, TimelinePageType.TYPE_ALL)
        set(value) {
            SPStaticUtils.put(KEY_TIMELINE_TYPE, value)
        }

    /**
     * 是否展示启动提示弹窗
     */
    var showVersionTip: Boolean
        get() = SPStaticUtils.getBoolean(KEY_VERSION_TIP, true)
        set(value) {
            SPStaticUtils.put(KEY_VERSION_TIP, value)
        }
}