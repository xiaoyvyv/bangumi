package com.xiaoyv.bangumi.features.splash.business

import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.shared.core.mvi.BaseSyntax
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSetting
import com.xiaoyv.bangumi.shared.data.repository.ChoreRepository
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withTimeoutOrNull

class SplashViewModel(
    savedStateHandle: SavedStateHandle,
    private val choreRepository: ChoreRepository,
    private val userManager: UserManager,
) : BaseViewModel<SplashState, SplashSideEffect, SplashEvent.Action>(savedStateHandle) {

    override fun initSate(onCreate: Boolean): SplashState {
        val configuredHosts = userManager.settings.network.hosts
        return SplashState(
            nodes = ComposeSetting.NetworkConfig.DefaultHosts.keys.map { hostname ->
                DnsNodeState(
                    hostname = hostname,
                    addresses = configuredHosts[hostname]
                        ?: ComposeSetting.NetworkConfig.DefaultHosts.getValue(hostname),
                )
            }.toPersistentList()
        )
    }

    override fun onEvent(event: SplashEvent.Action) {
        when (event) {
            SplashEvent.Action.OnLaunch -> action {
                if (stateRaw.isComplete) postEffect { SplashSideEffect.NavigateMain }
            }
        }
    }

    override suspend fun BaseSyntax<SplashState, SplashSideEffect>.refreshSync() {
        val fallbackHosts = ComposeSetting.NetworkConfig.DefaultHosts.builder().apply {
            putAll(userManager.settings.network.hosts)
        }.build()
        val refreshedHosts = fallbackHosts.builder()
        var failedCount = 0
        val hostnames = ComposeSetting.NetworkConfig.DefaultHosts.keys.toList()
        val pendingHostnames = hostnames.toMutableSet()

        reduceContent {
            state.copy(
                activeHostname = hostnames.firstOrNull().orEmpty(),
                nodes = state.nodes.map { it.copy(status = DnsNodeStatus.Resolving) }.toPersistentList(),
            )
        }

        supervisorScope {
            val resultChannel = Channel<DnsResolveResult>(hostnames.size)
            val querySemaphore = Semaphore(MAX_CONCURRENT_DNS_QUERIES)
            hostnames.forEach { hostname ->
                launch {
                    val result = querySemaphore.withPermit {
                        withTimeoutOrNull(DNS_QUERY_TIMEOUT_MILLIS) {
                            choreRepository.fetchDns(hostname)
                        } ?: Result.failure(
                            IllegalStateException("DNS query timed out for $hostname")
                        )
                    }
                    resultChannel.send(DnsResolveResult(hostname, result))
                }
            }

            repeat(hostnames.size) { index ->
                val (hostname, result) = resultChannel.receive()
                pendingHostnames.remove(hostname)
                result.fold(
                    onSuccess = { (_, addresses) ->
                        refreshedHosts[hostname] = addresses
                        updateNode(hostname) {
                            copy(addresses = addresses, status = DnsNodeStatus.Resolved)
                        }
                    },
                    onFailure = {
                        failedCount++
                        updateNode(hostname) {
                            copy(
                                addresses = fallbackHosts[hostname].orEmpty(),
                                status = DnsNodeStatus.Fallback,
                            )
                        }
                    }
                )

                reduceContent {
                    state.copy(
                        activeHostname = pendingHostnames.firstOrNull().orEmpty(),
                        completedCount = index + 1,
                        failureCount = failedCount,
                    )
                }
            }
            resultChannel.close()
        }

        userManager.updateSettings { settings ->
            settings.copy(
                network = settings.network.copy(hosts = refreshedHosts.build())
            )
        }
        reduceContent {
            state.copy(
                activeHostname = "",
                isComplete = true,
            )
        }
    }

    private suspend fun BaseSyntax<SplashState, SplashSideEffect>.updateNode(
        hostname: String,
        transform: DnsNodeState.() -> DnsNodeState,
    ) {
        reduceContent {
            state.copy(
                nodes = state.nodes.map { node ->
                    if (node.hostname == hostname) node.transform() else node
                }.toPersistentList()
            )
        }
    }

    private companion object {
        const val MAX_CONCURRENT_DNS_QUERIES = 3
        const val DNS_QUERY_TIMEOUT_MILLIS = 20_000L
    }

    private data class DnsResolveResult(
        val hostname: String,
        val result: Result<Pair<String, List<String>>>,
    )
}
