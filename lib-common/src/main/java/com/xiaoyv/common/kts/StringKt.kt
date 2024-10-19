package com.xiaoyv.common.kts

import android.os.Build
import androidx.annotation.StringRes
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.StringUtils
import com.xiaoyv.widget.kts.showToastCompat
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

/**
 * 解码 URL
 */
fun String.decodeUrl(): String {
    return runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            URLDecoder.decode(this, StandardCharsets.UTF_8.name())
        } else {
            URLDecoder.decode(this)
        }
    }.getOrDefault(this)
}

fun copyText(text: String) {
    ClipboardUtils.copyText(text)
    showToastCompat(i18n(CommonString.common_copy_success))
}

/**
 * 获取磁力链接 hash
 */
fun String.magnetHash(): String {
    return "magnet:\\?xt=urn:btih:(\\w+)"
        .toRegex(RegexOption.IGNORE_CASE)
        .groupValueOne(this)
        .uppercase()
}

fun i18n(@StringRes id: Int, vararg args: Any?): String {
    return StringUtils.getString(id, *args)
}