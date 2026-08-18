package com.xiaoyv.bangumi.features.main.tab.rakuen.business

import com.xiaoyv.bangumi.shared.core.types.RakuenType
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [RaKuenEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class RaKuenEvent {
    sealed class UI : RaKuenEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : RaKuenEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data class OnChangeType(@field:RakuenType val type: String) : Action()
    }
}