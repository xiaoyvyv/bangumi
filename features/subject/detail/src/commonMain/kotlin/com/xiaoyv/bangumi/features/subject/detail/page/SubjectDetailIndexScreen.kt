package com.xiaoyv.bangumi.features.subject.detail.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.xiaoyv.bangumi.features.index.page.page.IndexPageRoute
import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailEvent
import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailState
import com.xiaoyv.bangumi.shared.core.types.list.ListIndexType
import com.xiaoyv.bangumi.shared.data.model.request.list.index.IndexRelatedBody
import com.xiaoyv.bangumi.shared.data.model.request.list.index.ListIndexParam

@Composable
fun SubjectDetailIndexScreen(
    state: SubjectDetailState,
    onUiEvent: (SubjectDetailEvent.UI) -> Unit,
    onActionEvent: (SubjectDetailEvent.Action) -> Unit,
) {
    IndexPageRoute(
        param = remember {
            ListIndexParam(
                type = ListIndexType.SUBJECT_RELATED,
                related = IndexRelatedBody(subjectId = state.id)
            )
        },
        onNavScreen = { screen -> onUiEvent(SubjectDetailEvent.UI.OnNavScreen(screen)) },
    )
}
