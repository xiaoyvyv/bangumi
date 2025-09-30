@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.core.utils

import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.core.types.AppDsl

@AppDsl
data class LogScope(private var tag: String = "BangumiApp") {
    fun setTag(block: () -> String) {
        tag = block()
    }

    fun getTag() = tag
}

@AppDsl
inline fun debugLog(crossinline message: LogScope.() -> Any) {
    if (System.isDebugType) {
        val logScope = LogScope()
        val content = logScope.message()
        System.log(logScope.getTag(), if (content is Throwable) content.stackTraceToString() else content.toString())
    }
}