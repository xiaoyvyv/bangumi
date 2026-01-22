package com.xiaoyv.bangumi.shared

expect class SystemDevice() {
    val os: String
    val systemVersion: String
    val deviceModel: String
}
