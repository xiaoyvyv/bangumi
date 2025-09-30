package com.xiaoyv.bangumi.features.gallery.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [GalleryEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class GalleryEvent {
    sealed class UI : GalleryEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : GalleryEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
    }
}