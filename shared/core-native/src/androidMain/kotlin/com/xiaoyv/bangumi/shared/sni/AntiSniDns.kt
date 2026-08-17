package com.xiaoyv.bangumi.shared.sni

import okhttp3.Dns
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.concurrent.ConcurrentHashMap

/**
 * 支持自定义 Hosts 映射的 OkHttp Dns 实现
 */
class AntiSniDns(
    initialHosts: Map<String, List<String>> = emptyMap(),
    private val delegate: Dns = Dns.SYSTEM
) : Dns {
    private val hostsMap = ConcurrentHashMap<String, List<String>>().apply {
        putAll(initialHosts)
    }

    /**
     * 动态设置/更新单个域名的 IP 映射
     */
    fun setHost(hostname: String, vararg ips: String) {
        hostsMap[hostname] = ips.toList()
    }

    /**
     * 批量更新 Hosts 映射表
     */
    fun updateHosts(newHosts: Map<String, List<String>>) {
        hostsMap.clear()
        hostsMap.putAll(newHosts)
    }

    /**
     * 移除特定域名的自定义映射
     */
    fun removeHost(hostname: String) {
        hostsMap.remove(hostname)
    }

    override fun lookup(hostname: String): List<InetAddress> {
        val customIps = hostsMap[hostname]

        // 若命中自定义 Hosts，优先解析预置 IP 列表
        if (!customIps.isNullOrEmpty()) {
            val addresses = customIps.mapNotNull { ip ->
                try {
                    // 使用 getByAddress 绑定物理 IP 与逻辑 Hostname，避免二次 DNS 递归查询
                    InetAddress.getByAddress(hostname, InetAddress.getByName(ip).address)
                } catch (_: Exception) {
                    null
                }
            }

            if (addresses.isNotEmpty()) {
                return addresses
            }
        }

        // 未命中或自定义 IP 格式无效时，安全降级回退到系统默认 DNS
        return try {
            delegate.lookup(hostname)
        } catch (e: Exception) {
            throw UnknownHostException("Unable to resolve host: $hostname. Cause: ${e.message}")
        }
    }
}
