package com.xiaoyv.bangumi.features.splash.business

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class SplashState(
    val nodes: PersistentList<DnsNodeState> = persistentListOf(),
    val activeHostname: String = "",
    val completedCount: Int = 0,
    val failureCount: Int = 0,
    val isComplete: Boolean = false,
) {
    val progress: Float
        get() = if (nodes.isEmpty()) 0f else completedCount.toFloat() / nodes.size

    val isResolving: Boolean
        get() = !isComplete && nodes.any { it.status == DnsNodeStatus.Resolving }
}

@Immutable
data class DnsNodeState(
    val hostname: String,
    val addresses: List<String>,
    val status: DnsNodeStatus = DnsNodeStatus.Queued,
)

enum class DnsNodeStatus {
    Queued,
    Resolving,
    Resolved,
    Fallback,
}
