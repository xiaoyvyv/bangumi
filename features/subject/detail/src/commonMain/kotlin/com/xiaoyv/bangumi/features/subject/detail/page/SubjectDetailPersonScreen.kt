package com.xiaoyv.bangumi.features.subject.detail.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.xiaoyv.bangumi.features.mono.page.MonoPageRoute
import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailEvent
import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailState
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.types.PersonPositionType
import com.xiaoyv.bangumi.shared.core.types.list.ListMonoType
import com.xiaoyv.bangumi.shared.data.model.request.list.mono.ListMonoParam
import com.xiaoyv.bangumi.shared.data.model.request.list.mono.MonoSubjectBody

@Composable
fun SubjectDetailPersonScreen(
    state: SubjectDetailState,
    onUiEvent: (SubjectDetailEvent.UI) -> Unit,
    onActionEvent: (SubjectDetailEvent.Action) -> Unit,
) {
    MonoPageRoute(
        param = remember {
            ListMonoParam(
                type = ListMonoType.SUBJECT_PERSON,
                subject = MonoSubjectBody(
                    subjectId = state.id,
                    monoType = MonoType.PERSON,
                    personPosition = PersonPositionType.UNKNOWN
                )
            )
        },
        onNavScreen = { screen -> onUiEvent(SubjectDetailEvent.UI.OnNavScreen(screen)) },
    )
}