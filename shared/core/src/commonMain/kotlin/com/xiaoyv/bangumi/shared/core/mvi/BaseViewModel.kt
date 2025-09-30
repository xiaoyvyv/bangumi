package com.xiaoyv.bangumi.shared.core.mvi

import androidx.annotation.CallSuper
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.SettingsBuilder
import org.orbitmvi.orbit.annotation.OrbitDsl
import org.orbitmvi.orbit.container
import org.orbitmvi.orbit.syntax.Syntax


abstract class BaseViewModel<STATE : Any, SIDE_EFFECT : Any, EVENT : Any>(val stateHandle: SavedStateHandle) :
    ContainerHost<BaseState<STATE>, BaseSideEffect<SIDE_EFFECT>>, ViewModel() {

    override val container: Container<BaseState<STATE>, BaseSideEffect<SIDE_EFFECT>> by lazy {
        viewModelScope.container(
            initialState = initBaseState(),
            buildSettings = { buildSettings() },
            onCreate = { onCreate() }
        )
    }

    val BaseState<STATE>.content: STATE
        get() = payload ?: initSate(onCreate = false)

    open fun initBaseState(): BaseState<STATE> = BaseState.Success(initSate(onCreate = true))

    abstract fun initSate(onCreate: Boolean): STATE

    abstract fun onEvent(event: EVENT)

    open fun SettingsBuilder.buildSettings() {}

    open suspend fun Syntax<BaseState<STATE>, BaseSideEffect<SIDE_EFFECT>>.onCreate() {
        createBaseSyntaxWrap().refreshSync()
    }

    open suspend fun BaseSyntax<STATE, SIDE_EFFECT>.refreshSync() {}

    fun Syntax<BaseState<STATE>, BaseSideEffect<SIDE_EFFECT>>.createBaseSyntaxWrap() = BaseSyntax(
        syntax = { this },
        onCreateState = { initSate(false) }
    )

    @OrbitDsl
    inline fun action(
        registerIdling: Boolean = true,
        crossinline transformer: suspend BaseSyntax<STATE, SIDE_EFFECT>.() -> Unit,
    ): Job = intent(registerIdling) {
        transformer(createBaseSyntaxWrap())
    }

    @OrbitDsl
    suspend fun subAction(
        transformer: suspend BaseSyntax<STATE, SIDE_EFFECT>.() -> Unit,
    ) = subIntent {
        transformer(createBaseSyntaxWrap())
    }

    open fun refresh(loading: Boolean) = action {
        if (loading) reduceLoading { true }
        refreshSync()
    }

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        container.cancel()
    }
}