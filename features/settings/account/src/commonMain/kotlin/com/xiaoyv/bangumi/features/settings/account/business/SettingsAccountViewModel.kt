package com.xiaoyv.bangumi.features.settings.account.business

import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_ok
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.mvi.BaseSyntax
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
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

/**
 * [SettingsAccountViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class SettingsAccountViewModel(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val userManager: UserManager,
) : BaseViewModel<SettingsAccountState, SettingsAccountSideEffect, SettingsAccountEvent.Action>(
    savedStateHandle
) {
    override fun initBaseState(): BaseState<SettingsAccountState> = BaseState.Loading()

    override fun initSate(onCreate: Boolean) = SettingsAccountState()

    override suspend fun BaseSyntax<SettingsAccountState, SettingsAccountSideEffect>.refreshSync() {
        userRepository.fetchUserEditInfo()
            .onFailure { reduceError { it } }
            .onSuccess {
                reduceContent {
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
            is SettingsAccountEvent.Action.OnRefresh -> TODO()
            is SettingsAccountEvent.Action.OnSave -> onSaveInfo()
            is SettingsAccountEvent.Action.OnPickAvatar -> onPickAvatarResult(event.file)
        }
    }

    private fun onPickAvatarResult(file: PlatformFile) = action {
        val avatarBytes = withContext(Dispatchers.IO) {
            file.readBytes()
        }
        reduceContent { state.copy(avatarBytes = avatarBytes) }
    }

    private fun onEditInfo(type: String, data: String) = action {
        val items = state.content.items.toMutableMap()
        if (items.contains(type)) items[type] = data

        val networkItems = state.content.networkItems.toMutableMap()
        if (networkItems.contains(type)) networkItems[type] = data

        reduceContent {
            state.copy(
                avatar = if (type == EditInfoType.TYPE_AVATAR) data else state.avatar,
                items = items.toImmutableMap(),
                networkItems = networkItems.toImmutableMap()
            )
        }
    }

    private fun onSaveInfo() = action {
        val data = (state.content.items + state.content.networkItems).toMutableMap()
        data[EditInfoType.TYPE_FORM_HASH] = userManager.userInfo.formHash
        data[EditInfoType.TYPE_SUBMIT] = "submit"

        reduceContent { state.copy(loading = true) }

        userRepository.submitUserInfoUpdate(state.content.avatarBytes, data.toImmutableMap())
            .onFailure {
                reduceContent { state.copy(loading = false) }
                postToast { it.errMsg }
            }
            .onSuccess {
                reduceContent { state.copy(loading = false) }
                postToast { getString(Res.string.global_ok) }
                postEffect { SettingsAccountSideEffect.OnNavUp }
            }
    }
}