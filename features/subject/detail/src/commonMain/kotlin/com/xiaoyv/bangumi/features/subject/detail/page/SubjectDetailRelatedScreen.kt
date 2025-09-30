package com.xiaoyv.bangumi.features.subject.detail.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailEvent
import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailState
import com.xiaoyv.bangumi.features.subject.page.SubjectPageRoute
import com.xiaoyv.bangumi.shared.core.types.list.ListSubjectType
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.ListSubjectParam
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.SubjectRelatedBody
import com.xiaoyv.bangumi.shared.data.model.ui.PageUI

/**
 * [SubjectDetailTopicScreen]
 *
 * @since 2025/5/11
 */
@Composable
fun SubjectDetailRelatedScreen(
    state: SubjectDetailState,
    onUiEvent: (SubjectDetailEvent.UI) -> Unit,
    onActionEvent: (SubjectDetailEvent.Action) -> Unit,
) {
    SubjectPageRoute(
        param = remember {
            ListSubjectParam(
                type = ListSubjectType.SUBJECT_RELATED,
                ui = PageUI(gridLayout = true),
                related = SubjectRelatedBody(
                    subjectId = state.id
                )
            )
        },
        onNavScreen = {
            onUiEvent(SubjectDetailEvent.UI.OnNavScreen(it))
        }
    )
}