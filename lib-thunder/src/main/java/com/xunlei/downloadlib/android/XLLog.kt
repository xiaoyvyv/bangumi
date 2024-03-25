package com.xunlei.downloadlib.android

import android.util.Log

object XLLog {
    @JvmField
    internal var canLog: Boolean = false

    @JvmStatic
    fun i(tag: String, message: String) {
        log(LogLevel.LOG_LEVEL_INFO, tag, message)
    }

    @JvmStatic
    fun d(tag: String, message: String) {
        log(LogLevel.LOG_LEVEL_DEBUG, tag, message)
    }

    @JvmStatic
    fun w(tag: String, message: String) {
        log(LogLevel.LOG_LEVEL_WARN, tag, message)
    }

    @JvmStatic
    fun e(tag: String, message: String) {
        log(LogLevel.LOG_LEVEL_ERROR, tag, message)
    }

    @JvmStatic
    fun w(tag: String, message: String, th: Throwable) {
        log(LogLevel.LOG_LEVEL_WARN, tag, "$message: $th")
    }

    @JvmStatic
    fun v(tag: String, message: String) {
        d(tag, message)
    }

    @JvmStatic
    fun wtf(tag: String, message: String, th: Throwable) {
        log(LogLevel.LOG_LEVEL_WARN, tag, "$message: $th")
    }

    @JvmStatic
    fun printStackTrace(th: Throwable) {
        if (canLog) {
            th.printStackTrace()
        }
    }

    @JvmStatic
    fun log(logLevel: LogLevel, tag: String, message: String) {
        if (canLog) {
            Log.println(logLevel.value, tag, formatMessage(message))
        }
    }

    @JvmStatic
    private fun formatMessage(message: String): String {
        val stringBuilder = StringBuilder(message + "\t")
        runCatching {
            val stackTrace = Thread.currentThread().stackTrace
            if (stackTrace.size > 5) {
                val stackTraceElement = stackTrace[5]
                stringBuilder.append("[" + stackTraceElement.fileName + ":")
                stringBuilder.append(stackTraceElement.lineNumber.toString() + "]")
            } else {
                stringBuilder.append("[stack=" + stackTrace.size + "]")
            }
        }
        return stringBuilder.toString()
    }

    enum class LogLevel(val value: Int) {
        LOG_LEVEL_DEBUG(3),
        LOG_LEVEL_INFO(4),
        LOG_LEVEL_WARN(5),
        LOG_LEVEL_ERROR(6);
    }
}