package com.xiaoyv.bangumi.features.blog.page.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [BlogPageEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class BlogPageEvent {
    sealed class UI : BlogPageEvent() {
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : BlogPageEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
    }
}