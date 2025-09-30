package com.xiaoyv.bangumi.features.settings.account.business

import com.xiaoyv.bangumi.shared.core.types.EditInfoType
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import io.github.vinceglb.filekit.PlatformFile

/**
 * [SettingsAccountEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class SettingsAccountEvent {
    sealed class UI : SettingsAccountEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
        data object OnPickAvatar : UI()
    }

    sealed class Action : SettingsAccountEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data class OnEditInfo(@field:EditInfoType val type: String, val data: String) : Action()
        data class OnPickAvatar(val file: PlatformFile) : Action()
        data object OnSave : Action()
    }
}