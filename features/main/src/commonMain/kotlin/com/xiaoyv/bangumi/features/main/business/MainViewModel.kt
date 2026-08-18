package com.xiaoyv.bangumi.features.main.business

import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager

/**
 * [MainViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class MainViewModel(
    private val userManager: UserManager,
) : BaseViewModel<MainState, MainSideEffect, MainEvent>() {
    override fun createInitialState() = MainState(
        defaultSelected = userManager.settings.homeTab.defaultSelected
    )

    override fun onEvent(event: MainEvent) {

    }

}