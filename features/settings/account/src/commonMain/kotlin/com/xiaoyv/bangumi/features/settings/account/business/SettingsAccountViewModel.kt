package com.xiaoyv.bangumi.features.settings.account.business

import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_ok
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.PageStatus
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.mvi.postEffect
import com.xiaoyv.bangumi.shared.core.mvi.postToast
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.reduceError
import com.xiaoyv.bangumi.shared.core.types.EditInfoType
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.data.repository.UserRepository
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.readBytes
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.getString
import org.orbitmvi.orbit.syntax.Syntax

/**
 * [SettingsAccountViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class SettingsAccountViewModel(
    private val userRepository: UserRepository,
    private val userManager: UserManager,
) : BaseViewModel<SettingsAccountState, SettingsAccountSideEffect, SettingsAccountEvent.Action>() {
    override fun initBaseState(): UiState<SettingsAccountState> = UiState(data = createInitialState(), status = PageStatus.Loading)

    override fun createInitialState() = SettingsAccountState()

    override suspend fun Syntax<UiState<SettingsAccountState>, UiSideEffect<SettingsAccountSideEffect>>.refreshSync() {
        userRepository.fetchUserEditInfo()
            .onFailure { reduceError { it } }
            .onSuccess {
                reduceData {
                    state.copy(
                        avatar = it.avatar,
                        items = mapOf(
                            EditInfoType.TYPE_NICKNAME to it.nickname,
                            EditInfoType.TYPE_SIGN to it.sign,
                            EditInfoType.TYPE_TIMEZONE to it.timezone,
                            EditInfoType.TYPE_SITE to it.site,
                            EditInfoType.TYPE_INTRO to it.intro
                        ),
                        networkItems = mapOf(
                            EditInfoType.TYPE_INTERNET_PSN to it.internetPsn,
                            EditInfoType.TYPE_INTERNET_XBOX to it.internetXbox,
                            EditInfoType.TYPE_INTERNET_STEAM to it.internetSteam,
                            EditInfoType.TYPE_INTERNET_PIXI to it.internetPixi,
                            EditInfoType.TYPE_INTERNET_GITHUB to it.internetGithub,
                            EditInfoType.TYPE_INTERNET_TWITTER to it.internetTwitter,
                            EditInfoType.TYPE_INTERNET_INS to it.internetIns,
                        )
                    )
                }
            }
    }

    override fun onEvent(event: SettingsAccountEvent.Action) {
        when (event) {
            is SettingsAccountEvent.Action.OnEditInfo -> onEditInfo(event.type, event.data)
            is SettingsAccountEvent.Action.OnRefresh -> refresh(false)
            is SettingsAccountEvent.Action.OnSave -> onSaveInfo()
            is SettingsAccountEvent.Action.OnPickAvatar -> onPickAvatarResult(event.file)
        }
    }

    private fun onPickAvatarResult(file: PlatformFile) = intent {
        val avatarBytes = withContext(Dispatchers.IO) {
            file.readBytes()
        }
        reduceData { state.copy(avatarBytes = avatarBytes) }
    }

    private fun onEditInfo(type: String, data: String) = intent {
        val items = state.data.items.toMutableMap()
        if (items.contains(type)) items[type] = data

        val networkItems = state.data.networkItems.toMutableMap()
        if (networkItems.contains(type)) networkItems[type] = data

        reduceData {
            state.copy(
                avatar = if (type == EditInfoType.TYPE_AVATAR) data else state.avatar,
                items = items.toImmutableMap(),
                networkItems = networkItems.toImmutableMap()
            )
        }
    }

    private fun onSaveInfo() = intent {
        val data = (state.data.items + state.data.networkItems).toMutableMap()
        data[EditInfoType.TYPE_FORM_HASH] = userManager.userInfo.formHash
        data[EditInfoType.TYPE_SUBMIT] = "submit"

        reduceData { state.copy(loading = true) }

        userRepository.submitUserInfoUpdate(state.data.avatarBytes, data.toImmutableMap())
            .onFailure {
                reduceData { state.copy(loading = false) }
                postToast { it.errMsg }
            }
            .onSuccess {
                reduceData { state.copy(loading = false) }
                postToast { getString(Res.string.global_ok) }
                postEffect { SettingsAccountSideEffect.OnNavUp }
            }
    }
}
