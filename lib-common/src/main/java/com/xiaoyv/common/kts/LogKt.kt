package com.xiaoyv.common.kts

import android.util.Log
import com.blankj.utilcode.util.AppUtils


/**
 * @author why
 * @since 11/24/23
 */
fun debugLog(msg: Any? = null, tag: String = "Debug") {
    if (AppUtils.isAppDebug().not()) return
    Log.i(tag, msg.toString())
}

inline fun debugLog(tag: String = "Debug", value: () -> Any?) {
    if (AppUtils.isAppDebug().not()) return
    Log.i(tag, value().toString())
}