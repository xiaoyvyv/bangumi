package com.xiaoyv.bangumi.shared

import platform.UIKit.UIDevice

actual class SystemDevice {
    actual val os: String = "ios"

    actual val systemVersion: String
        get() = UIDevice.currentDevice.systemVersion

    actual val deviceModel: String
        get() = UIDevice.currentDevice.model
}