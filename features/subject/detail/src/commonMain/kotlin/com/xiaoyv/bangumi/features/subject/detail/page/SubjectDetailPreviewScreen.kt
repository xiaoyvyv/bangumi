package com.xiaoyv.bangumi.features.subject.detail.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.xiaoyv.bangumi.features.preivew.album.PreviewAlbumRoute
import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailEvent
import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailState
import com.xiaoyv.bangumi.shared.core.types.LoadingState
import com.xiaoyv.bangumi.shared.core.types.list.ListAlbumType
import com.xiaoyv.bangumi.shared.data.model.request.list.album.ListAlbumParam
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLoadingLayout

/**
 * [SubjectDetailTopicScreen]
 *
 * @since 2025/5/11
 */
@Composable
fun SubjectDetailPreviewScreen(
    state: SubjectDetailState,
    onUiEvent: (SubjectDetailEvent.UI) -> Unit,
    onActionEvent: (SubjectDetailEvent.Action) -> Unit,
) {
    if (state.previewLoading == LoadingState.Loading) {
        StateLoadingLayout()
    } else {
        PreviewAlbumRoute(
            param = remember(state.photo.doubanMediaId, state.photo.doubanMediaType) {
                ListAlbumParam(
                    type = ListAlbumType.SUBJECT_PREVIEW,
                    doubanId = state.photo.doubanMediaId,
                    doubanType = state.photo.doubanMediaType
                )
            },
            onNavScreen = {
                onUiEvent(SubjectDetailEvent.UI.OnNavScreen(it))
            }
        )
    }
}