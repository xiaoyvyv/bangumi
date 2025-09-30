package com.xiaoyv.bangumi.shared.core.mvi

import androidx.compose.runtime.Immutable

/**
 * [BaseState]
 *
 * @author why
 * @since 9/14/24
 */
@Immutable
sealed class BaseState<T> {
    val payload: T?
        get() = (this as? Success)?.data

    inline fun <R> content(block: T.() -> R) = payload?.block()

    data class Loading<T>(val loading: Boolean = true) : BaseState<T>()

    data class Success<T>(val data: T, val refreshKey: Long = 0L) : BaseState<T>()

    data class Error<T>(val error: Throwable? = null) : BaseState<T>()
}