package com.xiaoyv.common.kts

import android.util.Log


/**
 * @author why
 * @since 11/24/23
 */
fun debugLog(msg: Any? = null, tag: String = "Debug") {
    Log.i(tag, msg.toString())
}

inline fun debugLog(tag: String = "Debug", value: () -> Any?) {
    Log.i(tag, value().toString())
}