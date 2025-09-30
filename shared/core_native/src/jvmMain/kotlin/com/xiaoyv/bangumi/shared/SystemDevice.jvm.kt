package com.xiaoyv.bangumi.shared

import java.lang.System
import kotlin.String
import kotlin.text.contains
import kotlin.text.lowercase
import kotlin.with

actual class SystemDevice {

    actual val os: String
        get() = with(System.getProperty("os.name").lowercase()) {
            when {
                contains("windows") -> "windows"
                contains("mac") -> "mac"
                contains("linux") -> "linux"
                else -> "unknown"
            }
        }

    actual val systemVersion: String
        get() = System.getProperty("os.name") + " " + System.getProperty("os.version")

    actual val deviceModel: String
        get() = System.getProperty("os.arch")
}