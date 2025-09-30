package com.xiaoyv.bangumi.shared.core.types

sealed class LoadingState {
    data object Loading : LoadingState()
    data object NotLoading : LoadingState()
    data class Error(val error: Throwable) : LoadingState()
}