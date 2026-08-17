package com.xiaoyv.bangumi.shared.sni

import java.net.InetAddress
import java.net.Socket
import javax.net.SocketFactory

class AntiSniSocketFactory(
    private val fragmentationPolicy: TlsFragmentationPolicy = TlsFragmentationPolicy.NONE,
) : SocketFactory() {
    private val defaultFactory = getDefault()

    override fun createSocket(): Socket {
        return AntiSniSocket(defaultFactory.createSocket(), fragmentationPolicy)
    }

    override fun createSocket(host: String, port: Int): Socket {
        val socket = defaultFactory.createSocket(host, port)
        return AntiSniSocket(socket, fragmentationPolicy, host)
    }

    override fun createSocket(host: String, port: Int, localHost: InetAddress, localPort: Int): Socket {
        val socket = defaultFactory.createSocket(host, port, localHost, localPort)
        return AntiSniSocket(socket, fragmentationPolicy, host)
    }

    override fun createSocket(host: InetAddress, port: Int): Socket {
        val socket = defaultFactory.createSocket(host, port)
        return AntiSniSocket(socket, fragmentationPolicy, host.hostName)
    }

    override fun createSocket(address: InetAddress, port: Int, localAddress: InetAddress, localPort: Int): Socket {
        val socket = defaultFactory.createSocket(address, port, localAddress, localPort)
        return AntiSniSocket(socket, fragmentationPolicy, address.hostName)
    }
}
