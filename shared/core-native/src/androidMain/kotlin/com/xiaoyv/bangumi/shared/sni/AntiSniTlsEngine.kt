package com.xiaoyv.bangumi.shared.sni

import org.conscrypt.Conscrypt
import java.security.KeyStore
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


internal val antiSniTlsEngine by lazy {
    val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
    trustManagerFactory.init(null as KeyStore?)
    val trustManager = trustManagerFactory.trustManagers
        .filterIsInstance<X509TrustManager>()
        .singleOrNull()
        ?: error("Unable to obtain the default X509TrustManager")

    val sslSocketFactory = SSLContext.getInstance("TLS", Conscrypt.newProvider())
        .apply {
            init(null, arrayOf(trustManager), null)
        }
        .socketFactory
        .also { Conscrypt.setUseEngineSocket(it, true) }

    AntiSniTlsEngine(sslSocketFactory, trustManager)
}

internal class AntiSniTlsEngine(
    val socketFactory: SSLSocketFactory,
    val trustManager: X509TrustManager,
)
