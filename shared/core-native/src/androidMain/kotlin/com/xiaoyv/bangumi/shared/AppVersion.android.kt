package com.xiaoyv.bangumi.shared

import android.content.pm.PackageInfo
import android.os.Build

/**
 * Android 当前应用版本信息。
 */
actual object AppVersion {
    private val packageInfo: PackageInfo by lazy {
        application.packageManager.getPackageInfo(application.packageName, 0)
    }

    actual val versionCode: Long
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            packageInfo.versionCode.toLong()
        }

    actual val versionName: String
        get() = packageInfo.versionName.orEmpty()
}
