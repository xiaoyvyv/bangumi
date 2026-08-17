package com.xiaoyv.bangumi.shared.core.mvi

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.orbitmvi.orbit.OrbitContainer
import org.orbitmvi.orbit.OrbitContainerHost
import org.orbitmvi.orbit.SettingsBuilder
import org.orbitmvi.orbit.orbitContainer
import org.orbitmvi.orbit.syntax.Syntax

abstract class BaseMviViewModel<STATE : Any, SIDE_EFFECT : Any, EVENT : Any>(val stateHandle: SavedStateHandle) :
    ViewModel(), OrbitContainerHost<STATE, STATE, SIDE_EFFECT> {

    override val container: OrbitContainer<STATE, STATE, SIDE_EFFECT> by lazy {
        viewModelScope.orbitContainer(
            initialState = createInitialState(),
            buildSettings = { buildSettings() },
            transformState = { it },
            onCreate = { onCreate() }
        )
    }

    open fun SettingsBuilder.buildSettings() {}

    open suspend fun Syntax<STATE, SIDE_EFFECT>.onCreate() {
        refreshSync()
    }

    abstract fun createInitialState(): STATE

    abstract fun onEvent(event: EVENT)
    open suspend fun Syntax<STATE, SIDE_EFFECT>.refreshSync() {}
}
