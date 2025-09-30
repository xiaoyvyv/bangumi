package com.xiaoyv.bangumi.shared.core.mvi

sealed class BaseSideEffect<T> {
    data class Loading<T>(val isLoading: Boolean) : BaseSideEffect<T>()
    data class Toast<T>(val message: String) : BaseSideEffect<T>()
    data class Wrapped<T>(val effect: T) : BaseSideEffect<T>()
}