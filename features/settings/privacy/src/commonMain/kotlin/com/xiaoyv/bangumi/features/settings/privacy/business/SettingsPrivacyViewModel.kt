package com.xiaoyv.bangumi.features.settings.privacy.business

import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_ok
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.mvi.postToast
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.reduceError
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUserPrivacy
import com.xiaoyv.bangumi.shared.data.repository.UserRepository
import org.jetbrains.compose.resources.getString
import org.orbitmvi.orbit.syntax.Syntax

/**
 * [SettingsPrivacyViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class SettingsPrivacyViewModel(
    private val userRepository: UserRepository,
) : BaseViewModel<SettingsPrivacyState, SettingsPrivacySideEffect, SettingsPrivacyEvent.Action>() {

    override fun initBaseState(): UiState<SettingsPrivacyState> =
        initBaseLoadingState()

    override fun createInitialState() = SettingsPrivacyState()

    override suspend fun Syntax<UiState<SettingsPrivacyState>, UiSideEffect<SettingsPrivacySideEffect>>.refreshSync() {
        userRepository.fetchUserPrivacy()
            .onFailure { reduceError { it } }
            .onSuccess { privacy ->
                reduceData { state.copy(privacy = privacy) }
            }
    }

    override fun onEvent(event: SettingsPrivacyEvent.Action) {
        when (event) {
            is SettingsPrivacyEvent.Action.OnRefresh -> refresh(contentLoading = event.loading)
            is SettingsPrivacyEvent.Action.OnUpdatePrivacy -> onUpdatePrivacy(event.privacy)
        }
    }

    private fun onUpdatePrivacy(privacy: ComposeUserPrivacy) = intent {
        val previousPrivacy = state.data.privacy
        reduceData { state.copy(privacy = privacy, loading = true) }

        userRepository.submitUserPrivacy(privacy)
            .onFailure {
                reduceData { state.copy(privacy = previousPrivacy, loading = false) }
                postToast { it.errMsg }
            }
            .onSuccess { updated ->
                reduceData { state.copy(privacy = updated, loading = false) }
                postToast { getString(Res.string.global_ok) }
            }
    }
}