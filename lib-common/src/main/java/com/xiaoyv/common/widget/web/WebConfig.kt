package com.xiaoyv.common.widget.web

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

    private const val DEBUG = false

    private val baseH5Url
        get() = if (DEBUG) "http://192.168.0.102:5173/#/" else "file:///android_asset/h5/index.html#"
}