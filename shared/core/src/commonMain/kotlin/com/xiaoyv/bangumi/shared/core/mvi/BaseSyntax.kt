package com.xiaoyv.bangumi.shared.core.mvi

import androidx.annotation.CheckResult
import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.core.mvi.BaseSideEffect.Loading
import com.xiaoyv.bangumi.shared.core.mvi.BaseSideEffect.Toast
import com.xiaoyv.bangumi.shared.core.mvi.BaseSideEffect.Wrapped
import com.xiaoyv.bangumi.shared.core.types.AppDsl
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import org.orbitmvi.orbit.syntax.Syntax

@AppDsl
data class IntentContext<STATE : Any>(val state: STATE)

@AppDsl
class BaseSyntax<STATE : Any, SIDE_EFFECT : Any>(
    private val syntax: () -> Syntax<BaseState<STATE>, BaseSideEffect<SIDE_EFFECT>>,
    val onCreateState: () -> STATE,
) {
    val scope get() = syntax()
    val state get() = syntax().state
    val stateRaw get() = state.payload ?: onCreateState()

    @AppDsl
    suspend inline fun reduceContent(
        forceRefresh: Boolean = false,
        crossinline block: IntentContext<STATE>.() -> STATE,
    ) = scope.reduce { BaseState.Success(IntentContext(stateRaw).block(), if (forceRefresh) System.currentTimeMillis() else 0) }

    @AppDsl
    suspend inline fun reduceError(
        crossinline error: IntentContext<BaseState<STATE>>.() -> Throwable,
    ) = scope.reduce { BaseState.Error(IntentContext(state).error()) }

    @AppDsl
    suspend inline fun reduceLoading(
        crossinline block: () -> Boolean,
    ) = scope.reduce { BaseState.Loading(block()) }

    @AppDsl
    suspend inline fun postToast(
        crossinline block: suspend () -> String,
    ) = scope.postSideEffect(Toast(block()))

    @AppDsl
    suspend inline fun postLoading(
        crossinline block: suspend () -> Boolean,
    ) = scope.postSideEffect(Loading(block()))

    @AppDsl
    suspend inline fun postEffect(
        crossinline block: suspend () -> SIDE_EFFECT,
    ) = scope.postSideEffect(Wrapped(block()))

    @AppDsl
    @CheckResult
    suspend inline fun <T> withActionLoading(
        showError: Boolean = true,
        showLoading: Boolean = true,
        autoDismiss: Boolean = true,
        crossinline block: suspend () -> Result<T>,
    ): Result<T> {
        if (showLoading) postLoading { true }
        return block()
            .onFailure {
                if (showLoading) postLoading { false }
                if (showError) postToast { it.errMsg }
            }
            .onSuccess { if (showLoading && autoDismiss) postLoading { false } }
    }

    suspend inline fun <T> Result<T>.onSuccessWithErrorToast(action: (value: T) -> Unit): Result<T> {
        return onFailure { postToast { it.errMsg } }
            .onSuccess(action)
    }
}

inline fun <T : Any> interceptEvent(
    crossinline onUiEvent: (T) -> Unit,
    crossinline interceptor: (T) -> Boolean,
): (T) -> Unit = { event ->
    if (!interceptor(event)) onUiEvent(event)
}

