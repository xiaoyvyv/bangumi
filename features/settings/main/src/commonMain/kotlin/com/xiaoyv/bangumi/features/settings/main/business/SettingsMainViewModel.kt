package com.xiaoyv.bangumi.features.settings.main.business

import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.settings_clean_cache_success
import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.mvi.postToast
import com.xiaoyv.bangumi.shared.core.mvi.withActionLoading
import com.xiaoyv.bangumi.shared.core.utils.runResult
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.data.repository.DatabaseRepository
import org.jetbrains.compose.resources.getString
import org.orbitmvi.orbit.syntax.Syntax

/**
 * [SettingsMainViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class SettingsMainViewModel(
    private val userManager: UserManager,
    private val databaseRepository: DatabaseRepository,
) : BaseViewModel<SettingsMainState, SettingsMainSideEffect, SettingsMainEvent.Action>() {

    override fun createInitialState() = SettingsMainState()

    override fun onEvent(event: SettingsMainEvent.Action) {
        when (event) {
            is SettingsMainEvent.Action.OnRefresh -> refresh(event.loading)
            SettingsMainEvent.Action.OnLogout -> onLogout()
            SettingsMainEvent.Action.OnCleanCache -> onCleanCache()
        }
    }

    override suspend fun Syntax<UiState<SettingsMainState>, UiSideEffect<SettingsMainSideEffect>>.refreshSync() {

    }

    private fun onLogout() = intent {
        withActionLoading { userManager.logout() }
    }

    private fun onCleanCache() = intent {
        withActionLoading {
            runResult {
                System.cleanCache().getOrThrow()
                databaseRepository.clearSubjectPreviewMappings()
            }
        }.onSuccess {
            postToast { getString(Res.string.settings_clean_cache_success) }
        }
    }
}