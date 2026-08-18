package com.xiaoyv.bangumi.shared.sni

actual class AntiSniWebProxy actual constructor(
    initialHosts: Map<String, List<String>>,
    tlsFragmentationDomains: Collection<String>,
    connectTimeoutMillis: Int,
    headerTimeoutMillis: Int,
    errorHandler: (Throwable) -> Unit
) : AutoCloseable {
    actual fun start(): Int {
        return -1
    }

    actual override fun close() {

    }
}