package com.xiaoyv.bangumi.shared.sni

expect class AntiSniWebProxy(
    initialHosts: Map<String, List<String>>,
    tlsFragmentationDomains: Collection<String>,
    connectTimeoutMillis: Int,
    headerTimeoutMillis: Int,
    errorHandler: (Throwable) -> Unit,
) : AutoCloseable {

    fun start(): Int

    override fun close()
}