package com.xiaoyv.bangumi.features.mono.detail.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.xiaoyv.bangumi.features.mono.detail.business.MonoDetailEvent
import com.xiaoyv.bangumi.features.mono.detail.business.MonoDetailState
import com.xiaoyv.bangumi.features.preivew.album.PreviewAlbumRoute
import com.xiaoyv.bangumi.shared.core.types.list.ListAlbumType
import com.xiaoyv.bangumi.shared.data.model.request.list.album.ListAlbumParam

/**
 * [MonoDetailPicturesScreen]
 *
 * @since 2025/5/18
 */
@Composable
fun MonoDetailAlbumScreen(
    state: MonoDetailState,
    onUiEvent: (MonoDetailEvent.UI) -> Unit,
    onActionEvent: (MonoDetailEvent.Action) -> Unit,
) {
    PreviewAlbumRoute(
        param = remember {
            ListAlbumParam(
                type = ListAlbumType.CHARACTER_ALBUM,
                characterId = state.id
            )
        },
        onNavScreen = {
            onUiEvent(MonoDetailEvent.UI.OnNavScreen(it))
        }
    )
}