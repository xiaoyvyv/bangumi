package com.xiaoyv.bangumi.shared.sni

fun interface TlsFragmentationPolicy {
    fun shouldFragment(hostname: String?): Boolean

    companion object {
        val NONE = TlsFragmentationPolicy { false }
    }
}

class DomainTlsFragmentationPolicy(domains: Collection<String>) : TlsFragmentationPolicy {
    private val normalizedDomains = domains
        .asSequence()
        .map(String::normalizeHostname)
        .filter(String::isNotEmpty)
        .toSet()

    override fun shouldFragment(hostname: String?): Boolean {
        val normalizedHost = hostname?.normalizeHostname().orEmpty()
        if (normalizedHost.isEmpty()) return false
        if (normalizedHost in normalizedDomains) return true

        for (domain in normalizedDomains) {
            val separatorIndex = normalizedHost.length - domain.length - 1
            if (separatorIndex >= 0 &&
                normalizedHost[separatorIndex] == '.' &&
                normalizedHost.regionMatches(separatorIndex + 1, domain, 0, domain.length)
            ) {
                return true
            }
        }
        return false
    }
}

private fun String.normalizeHostname(): String = trim().lowercase().removeSuffix(".")
