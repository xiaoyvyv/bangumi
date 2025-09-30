package com.xiaoyv.bangumi.features.settings.main.business

import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.settings_clean_cache_success
import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.core.mvi.BaseSyntax
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import org.jetbrains.compose.resources.getString

/**
 * [SettingsMainViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class SettingsMainViewModel(
    savedStateHandle: SavedStateHandle,
    private val userManager: UserManager,
) : BaseViewModel<SettingsMainState, SettingsMainSideEffect, SettingsMainEvent.Action>(savedStateHandle) {

    override fun initSate(onCreate: Boolean) = SettingsMainState()

    override fun onEvent(event: SettingsMainEvent.Action) {
        when (event) {
            is SettingsMainEvent.Action.OnRefresh -> refresh(event.loading)
            SettingsMainEvent.Action.OnLogout -> onLogout()
            SettingsMainEvent.Action.OnCleanCache -> onCleanCache()
        }
    }

    override suspend fun BaseSyntax<SettingsMainState, SettingsMainSideEffect>.refreshSync() {

    }

    private fun onLogout() = action {
        withActionLoading { userManager.logout() }
    }

    private fun onCleanCache() = action {
        withActionLoading { System.cleanCache() }
            .onSuccess {
                postToast { getString(Res.string.settings_clean_cache_success) }
            }
    }
}