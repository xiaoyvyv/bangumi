package com.xiaoyv.common.widget.web

import androidx.appcompat.app.AppCompatDelegate


/**
 * Class: [WebConfig]
 *
 * @author why
 * @since 12/2/23
 */
object WebConfig {
    fun page(page: String): String {
        return baseH5Url + page
    }

    private const val DEBUG = true

    private val baseH5Url
        get() = if (DEBUG) "http://192.168.0.102:5173/#/" else "file:///android_asset/h5/index.html#"

    /**
     * 获取当前是否开启深色模式
     */
    val nightMode: Boolean
        get() {
            val currentNightMode = AppCompatDelegate.getDefaultNightMode()
            return currentNightMode == AppCompatDelegate.MODE_NIGHT_YES
        }
}
