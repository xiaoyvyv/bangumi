package com.xiaoyv.bangumi.shared.core.mvi

import androidx.annotation.CheckResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect.Loading
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect.Toast
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect.Wrapped
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import org.orbitmvi.orbit.syntax.IntentContext
import org.orbitmvi.orbit.syntax.Syntax

/** Updates business data without changing the current page status. */
suspend inline fun <T : Any, SIDE_EFFECT : Any> Syntax<UiState<T>, UiSideEffect<SIDE_EFFECT>>.reduceData(
    forceRefresh: Boolean = false,
    crossinline reducer: IntentContext<T>.() -> T,
) = reduce {
    state.copy(
        data = IntentContext(state.data).reducer(),
        revision = if (forceRefresh) state.revision + 1 else state.revision,
    )
}

/**
 * Updates only the page-level status.
 */
suspend inline fun <T : Any, SIDE_EFFECT : Any> Syntax<UiState<T>, UiSideEffect<SIDE_EFFECT>>.reduceStatus(
    crossinline reducer: IntentContext<PageStatus>.() -> PageStatus,
) = reduce {
    state.copy(status = IntentContext(state.status).reducer())
}

suspend inline fun <T : Any, SIDE_EFFECT : Any> Syntax<UiState<T>, UiSideEffect<SIDE_EFFECT>>.reduceError(
    crossinline reducer: IntentContext<UiState<T>>.() -> Throwable,
) = reduce {
    val throwable = IntentContext(state).reducer()
    state.copy(status = PageStatus.Error(throwable.errMsg, throwable))
}

suspend inline fun <T : Any, SIDE_EFFECT : Any> Syntax<UiState<T>, UiSideEffect<SIDE_EFFECT>>.postToast(
    crossinline block: suspend IntentContext<T>.() -> String,
) = postSideEffect(Toast(block(IntentContext(state.data))))

suspend inline fun <T : Any, SIDE_EFFECT : Any> Syntax<UiState<T>, UiSideEffect<SIDE_EFFECT>>.postLoading(
    crossinline block: suspend IntentContext<T>.() -> Boolean,
) = postSideEffect(Loading(block(IntentContext(state.data))))

suspend inline fun <T : Any, SIDE_EFFECT : Any> Syntax<UiState<T>, UiSideEffect<SIDE_EFFECT>>.postEffect(
    crossinline block: suspend IntentContext<T>.() -> SIDE_EFFECT,
) = postSideEffect(Wrapped(block(IntentContext(state.data))))

/**
 * Runs an action with the app-wide loading side effect.
 */
@CheckResult
suspend inline fun <T : Any, SIDE_EFFECT : Any, R> Syntax<UiState<T>, UiSideEffect<SIDE_EFFECT>>.withActionLoading(
    showError: Boolean = true,
    showLoading: Boolean = true,
    autoDismiss: Boolean = true,
    crossinline block: suspend IntentContext<T>.() -> Result<R>,
): Result<R> {
    if (showLoading) postLoading { true }

    return block(IntentContext(state.data))
        .onFailure {
            if (showLoading) postLoading { false }
            if (showError) postToast { it.errMsg }
        }
        .onSuccess {
            if (showLoading && autoDismiss) postLoading { false }
        }
}

@Composable
fun <T : Any> rememberInterceptEvent(
    onUiEvent: (T) -> Unit,
    interceptor: (T) -> Boolean,
): (T) -> Unit {
    val currentOnUiEvent = rememberUpdatedState(onUiEvent)
    val currentInterceptor = rememberUpdatedState(interceptor)

    return remember {
        { event -> if (!currentInterceptor.value(event)) currentOnUiEvent.value(event) }
    }
}
