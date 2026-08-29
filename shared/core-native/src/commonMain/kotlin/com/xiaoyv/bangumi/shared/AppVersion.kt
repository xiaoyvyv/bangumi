package com.xiaoyv.bangumi.shared

/**
 * 当前应用的版本信息。
 */
expect object AppVersion {
    val versionCode: Long

    val versionName: String
}
