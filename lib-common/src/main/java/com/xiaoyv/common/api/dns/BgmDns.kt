package com.xiaoyv.common.api.dns

import com.xiaoyv.common.helper.ConfigHelper
import okhttp3.Dns
import java.net.InetAddress

/**
 * 自定义 DNS 解析器
 *
 * Class: [BgmDns]
 */
class BgmDns : Dns {
    /**
     * Hosts
     */
    private val hostsContent: String
        get() = ConfigHelper.netHosts

    override fun lookup(hostname: String): List<InetAddress> {
        // 先尝试从 hosts 文件中查找
        val addresses = lookupInHostsFile(hostname)
        if (addresses.isEmpty()) {
            // 如果 hosts 文件中没有找到，则使用系统默认的 DNS 解析
            runCatching {
                return Dns.SYSTEM.lookup(hostname)
            }
        }

        return addresses
    }

    /**
     * 从 hosts 文件内容中查找主机名对应的 IP 地址
     */
    private fun lookupInHostsFile(hostname: String): List<InetAddress> {
        val addresses = mutableListOf<InetAddress>()
        val lines = hostsContent.lines()
        for (line in lines) {
            val trimmedLine = line.trim()
            if (!trimmedLine.startsWith("#") && trimmedLine.isNotEmpty()) {
                val parts = trimmedLine.split("\\s+".toRegex())
                if (parts.size >= 2) {
                    val ip = parts[0]
                    val hostnames = parts.subList(1, parts.size)
                    if (hostname in hostnames) {
                        addresses.add(InetAddress.getByName(ip))
                    }
                }
            }
        }
        return addresses
    }

    companion object {
        /**
         * 104.16.86.20 cdn.jsdelivr.net
         */
        val DEFAULT_HOSTS = """
            172.67.98.15 share.dmhy.org
            172.67.152.186 bangumi.moe
            185.199.110.133 raw.githubusercontent.com
        """.trimIndent()
    }
}