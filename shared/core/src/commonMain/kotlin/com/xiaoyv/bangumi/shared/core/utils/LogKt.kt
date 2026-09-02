@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.core.utils

import com.xiaoyv.bangumi.shared.libnative.System

@DslMarker
annotation class LogScopeDsl

@LogScopeDsl
data class LogScope(var tag: String = "BangumiApp")

inline fun debugLog(crossinline message: LogScope.() -> Any) {
    if (System.isDebugType) {
        val logScope = LogScope()
        val content = logScope.message()
        System.log(logScope.tag, if (content is Throwable) content.stackTraceToString() else content.toString())
    }
}