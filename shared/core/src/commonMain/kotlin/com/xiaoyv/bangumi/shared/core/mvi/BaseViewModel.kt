package com.xiaoyv.bangumi.shared.core.mvi

import androidx.annotation.CallSuper
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import org.orbitmvi.orbit.OrbitContainerHost
import org.orbitmvi.orbit.orbitContainer
import org.orbitmvi.orbit.syntax.Syntax

abstract class BaseViewModel<STATE : Any, SIDE_EFFECT : Any, EVENT : Any>(val stateHandle: SavedStateHandle) :
    OrbitContainerHost<UiState<STATE>, UiState<STATE>, UiSideEffect<SIDE_EFFECT>>, ViewModel() {

    override val container by lazy {
        viewModelScope.orbitContainer(
            initialState = initBaseState(),
            transformState = { it },
            onCreate = { onCreate() }
        )
    }

    open fun initBaseState(): UiState<STATE> = UiState(data = createInitialState())

    abstract fun createInitialState(): STATE

    abstract fun onEvent(event: EVENT)

    open suspend fun Syntax<UiState<STATE>, UiSideEffect<SIDE_EFFECT>>.onCreate() {
        runRefresh()
    }

    open suspend fun Syntax<UiState<STATE>, UiSideEffect<SIDE_EFFECT>>.refreshSync() {}

    open fun refresh(loading: Boolean) = intent {
        if (loading) reduceStatus { PageStatus.Loading }
        runRefresh()
    }

    private suspend fun Syntax<UiState<STATE>, UiSideEffect<SIDE_EFFECT>>.runRefresh() {
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
