package com.xiaoyv.bangumi.features.index.page.dialog

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [IndexDialogEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class IndexDialogEvent {
    sealed class UI : IndexDialogEvent() {
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : IndexDialogEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data object OnRefreshCollection : Action()

        data class OnSaveToCollection(val indexId: Long) : Action()
    }
}