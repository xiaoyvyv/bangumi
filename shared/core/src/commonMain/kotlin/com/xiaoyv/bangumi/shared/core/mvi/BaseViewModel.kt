package com.xiaoyv.bangumi.shared.core.mvi

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import org.orbitmvi.orbit.OrbitContainer
import org.orbitmvi.orbit.OrbitContainerHost
import org.orbitmvi.orbit.orbitContainer
import org.orbitmvi.orbit.syntax.Syntax

/**
 * Base ViewModel with distinct internal and externally exposed UI state.
 *
 * Orbit intents operate on [INTERNAL_STATE], while UI collectors observe
 * [UI_STATE] produced by [transformState].
 */
abstract class BaseViewModelWithUiState<INTERNAL_STATE : Any, UI_STATE : Any, SIDE_EFFECT : Any, EVENT : Any> :
    OrbitContainerHost<UiState<INTERNAL_STATE>, UiState<UI_STATE>, UiSideEffect<SIDE_EFFECT>>, ViewModel() {

    override val container: OrbitContainer<
            UiState<INTERNAL_STATE>,
            UiState<UI_STATE>,
            UiSideEffect<SIDE_EFFECT>,
            > by lazy {
        viewModelScope.orbitContainer(
            initialState = initBaseState(),
            transformState = { transformState(it) },
            onCreate = { onCreate() }
        )
    }

    open fun initBaseState(): UiState<INTERNAL_STATE> = UiState(data = createInitialState())

    abstract fun createInitialState(): INTERNAL_STATE

    abstract fun onEvent(event: EVENT)

    /** Maps business data to the state exposed to UI collectors. */
    protected abstract fun transformData(state: INTERNAL_STATE): UI_STATE

    /** Override when page status or revision also needs custom mapping. */
    protected open fun transformState(state: UiState<INTERNAL_STATE>): UiState<UI_STATE> = UiState(
        data = transformData(state.data),
        status = state.status,
        revision = state.revision,
    )

    open suspend fun Syntax<UiState<INTERNAL_STATE>, UiSideEffect<SIDE_EFFECT>>.onCreate() {
        runRefresh()
    }

    open suspend fun Syntax<UiState<INTERNAL_STATE>, UiSideEffect<SIDE_EFFECT>>.refreshSync() {

    }

    open fun refresh(loading: Boolean) = intent {
        if (loading) reduceStatus { PageStatus.Loading }
        runRefresh()
    }

    private suspend fun Syntax<UiState<INTERNAL_STATE>, UiSideEffect<SIDE_EFFECT>>.runRefresh() {
        try {
            refreshSync()
            if (state.status == PageStatus.Loading) reduceStatus { PageStatus.Idle }
        } catch (throwable: CancellationException) {
            throw throwable
        } catch (throwable: Throwable) {
            reduceError { throwable }
        }
    }

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        container.cancel()
    }
}

/**
 * Compatibility base for screens whose internal and external state are identical.
 */
abstract class BaseViewModel<STATE : Any, SIDE_EFFECT : Any, EVENT : Any> :
    BaseViewModelWithUiState<STATE, STATE, SIDE_EFFECT, EVENT>() {

    final override fun transformData(state: STATE): STATE = state
}
