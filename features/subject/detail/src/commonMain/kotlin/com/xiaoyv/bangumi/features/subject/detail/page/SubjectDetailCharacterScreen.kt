package com.xiaoyv.bangumi.features.subject.detail.page

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.xiaoyv.bangumi.features.mono.page.MonoPageRoute
import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailEvent
import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailState
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.types.list.ListMonoType
import com.xiaoyv.bangumi.shared.data.model.request.list.mono.ListMonoParam
import com.xiaoyv.bangumi.shared.data.model.request.list.mono.MonoSubjectBody
import com.xiaoyv.bangumi.shared.ui.component.pager.BgmChipHorizontalPager
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens.subjectCharacterRoleFilters

@Composable
fun SubjectDetailCharacterScreen(
    state: SubjectDetailState,
    onUiEvent: (SubjectDetailEvent.UI) -> Unit,
    onActionEvent: (SubjectDetailEvent.Action) -> Unit,
) {
    BgmChipHorizontalPager(
        modifier = Modifier.fillMaxSize(),
        tabs = subjectCharacterRoleFilters,
        initialPage = 1
    ) {
        MonoPageRoute(
            param = remember {
                ListMonoParam(
                    type = ListMonoType.SUBJECT_CHARACTER,
                    subject = MonoSubjectBody(
                        subjectId = state.id,
                        monoType = MonoType.CHARACTER,
                        monoVoiceType = subjectCharacterRoleFilters[it].type
                    )
                )
            },
            onNavScreen = { screen -> onUiEvent(SubjectDetailEvent.UI.OnNavScreen(screen)) },
        )
    }
}
