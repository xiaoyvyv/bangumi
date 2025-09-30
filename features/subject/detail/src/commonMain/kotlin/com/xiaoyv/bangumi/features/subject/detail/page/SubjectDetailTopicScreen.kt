package com.xiaoyv.bangumi.features.subject.detail.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailEvent
import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailState
import com.xiaoyv.bangumi.features.topic.page.TopicPageRoute
import com.xiaoyv.bangumi.shared.core.types.list.ListTopicType
import com.xiaoyv.bangumi.shared.data.model.request.list.topic.ListTopicParam

/**
 * [SubjectDetailTopicScreen]
 *
 * @since 2025/5/11
 */
@Composable
fun SubjectDetailTopicScreen(
    state: SubjectDetailState,
    onUiEvent: (SubjectDetailEvent.UI) -> Unit,
    onActionEvent: (SubjectDetailEvent.Action) -> Unit,
) {
    TopicPageRoute(
        param = remember {
            ListTopicParam(
                type = ListTopicType.SUBJECT_TARGET,
                subjectID = state.subject.id
            )
        },
        onNavScreen = {
            onUiEvent(SubjectDetailEvent.UI.OnNavScreen(it))
        }
    )
}