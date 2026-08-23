package com.xiaoyv.bangumi.features.pixiv.illust.page.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [IllustPageEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class IllustPageEvent {
    sealed class UI : IllustPageEvent() {
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : IllustPageEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
    }
}
