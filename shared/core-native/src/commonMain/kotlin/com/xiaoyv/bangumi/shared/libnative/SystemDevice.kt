package com.xiaoyv.bangumi.shared.libnative

expect class SystemDevice() {
    val os: String
    val systemVersion: String
    val deviceModel: String
}
