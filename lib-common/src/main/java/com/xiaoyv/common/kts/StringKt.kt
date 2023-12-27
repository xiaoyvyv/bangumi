package com.xiaoyv.common.kts

import android.os.Build
import com.blankj.utilcode.util.ClipboardUtils
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
    showToastCompat("复制成功")
}