package com.xiaoyv.bangumi.features.main.business

import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager

/**
 * [MainViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class MainViewModel(
    stateHandle: SavedStateHandle,
    private val userManager: UserManager,
) : BaseViewModel<MainState, MainSideEffect, MainEvent>(stateHandle) {
    override fun initSate(onCreate: Boolean) = MainState(
        defaultSelected = userManager.settings.homeTab.defaultSelected
    )

    override fun onEvent(event: MainEvent) {

    }

}