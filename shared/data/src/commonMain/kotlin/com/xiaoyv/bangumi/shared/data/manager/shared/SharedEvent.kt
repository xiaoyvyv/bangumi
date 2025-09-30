package com.xiaoyv.bangumi.shared.data.manager.shared

/**
 * [SharedEvent]
 *
 * @author why
 * @since 2025/1/15
 */
sealed class SharedEvent {
    data class OnRefresh(val loading: Boolean) : SharedEvent()
}