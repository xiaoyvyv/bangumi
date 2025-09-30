package com.xiaoyv.bangumi.features.preivew.album.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [PreviewAlbumEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class PreviewAlbumEvent {
    sealed class UI : PreviewAlbumEvent() {
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : PreviewAlbumEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
    }
}