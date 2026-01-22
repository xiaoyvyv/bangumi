package com.xiaoyv.bangumi.shared

import android.os.Build

actual class SystemDevice {
    actual val os: String = "android"

    actual val systemVersion: String
        get() = Build.VERSION.RELEASE ?: "Unknown"

    actual val deviceModel: String
        get() = "${Build.MANUFACTURER} ${Build.MODEL}"
}