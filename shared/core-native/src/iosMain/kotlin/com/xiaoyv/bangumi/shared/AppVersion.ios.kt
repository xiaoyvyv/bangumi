package com.xiaoyv.bangumi.shared

import platform.Foundation.NSBundle

/**
 * iOS 当前应用版本信息。
 */
actual object AppVersion {
    actual val versionCode: Long
        get() = (NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleVersion") as? String)
            ?.toLongOrNull()
            ?: 0L

    actual val versionName: String
        get() = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String
            ?: ""
}
