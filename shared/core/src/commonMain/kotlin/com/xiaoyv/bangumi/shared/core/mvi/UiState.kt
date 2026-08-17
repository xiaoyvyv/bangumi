package com.xiaoyv.bangumi.shared.core.mvi

import androidx.compose.runtime.Immutable

/**
 * Page-level loading and error status.
 */
@Immutable
sealed interface PageStatus {
    data object Idle : PageStatus

    data object Loading : PageStatus

    data class Error(
        val message: String,
        val throwable: Throwable? = null,
    ) : PageStatus
}

/**
 * Keeps page data available while its loading status changes.
 *
 * [revision] forces observers to receive successful refreshes whose data is
 * structurally equal to the previous value.
 */
@Immutable
data class UiState<T>(
    val data: T,
    val status: PageStatus = PageStatus.Idle,
    val revision: Long = 0L,
) {
    val isLoading: Boolean
        get() = status is PageStatus.Loading

    val isError: Boolean
        get() = status is PageStatus.Error

    val errorMessage: String?
        get() = (status as? PageStatus.Error)?.message
}
