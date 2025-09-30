package com.xiaoyv.bangumi.shared.core.mvi

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.SettingsBuilder
import org.orbitmvi.orbit.container
import org.orbitmvi.orbit.syntax.Syntax

abstract class BaseMviViewModel<STATE : Any, SIDE_EFFECT : Any, EVENT : Any>(val stateHandle: SavedStateHandle) :
    ViewModel(), ContainerHost<STATE, SIDE_EFFECT> {

    override val container: Container<STATE, SIDE_EFFECT> by lazy {
        viewModelScope.container(
            initialState = initSate(true),
            buildSettings = { buildSettings() },
            onCreate = { onCreate() }
        )
    }

    open fun SettingsBuilder.buildSettings() {}

    open suspend fun Syntax<STATE, SIDE_EFFECT>.onCreate() {
        refreshSync()
    }

    abstract fun initSate(onCreate: Boolean): STATE

    abstract fun onEvent(event: EVENT)
    open suspend fun Syntax<STATE, SIDE_EFFECT>.refreshSync() {}
}
