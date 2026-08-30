package com.xiaoyv.bangumi.shared

import java.net.JarURLConnection
import java.util.jar.Attributes

const val IMPLEMENTATION_VERSION_CODE = "Implementation-Version-Code"

/**
 * JVM 当前应用版本信息。
 */
actual object AppVersion {
    private val attributes: Attributes? by lazy {
        (AppVersion::class.java.getResource("AppVersion.class")?.openConnection() as? JarURLConnection)
            ?.manifest
            ?.mainAttributes
    }

    actual val versionCode: Long
        get() = attributes?.getValue(IMPLEMENTATION_VERSION_CODE)?.toLongOrNull() ?: 0L

    actual val versionName: String
        get() = attributes?.getValue(Attributes.Name.IMPLEMENTATION_VERSION).orEmpty()
}
