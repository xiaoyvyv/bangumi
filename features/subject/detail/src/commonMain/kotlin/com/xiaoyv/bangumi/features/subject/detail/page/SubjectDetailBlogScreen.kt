package com.xiaoyv.bangumi.features.subject.detail.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.xiaoyv.bangumi.features.blog.page.BlogPageRoute
import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailEvent
import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailState
import com.xiaoyv.bangumi.shared.core.types.list.ListBlogType
import com.xiaoyv.bangumi.shared.data.model.request.list.blog.ListBlogParam

/**
 * [SubjectDetailBlogScreen]
 *
 * @since 2025/5/11
 */
@Composable
fun SubjectDetailBlogScreen(
    state: SubjectDetailState,
    onUiEvent: (SubjectDetailEvent.UI) -> Unit,
    onActionEvent: (SubjectDetailEvent.Action) -> Unit,
) {
    BlogPageRoute(
        param = remember { ListBlogParam(type = ListBlogType.SUBJECT_RELATED, subjectId = state.id) },
        onNavScreen = {
            onUiEvent(SubjectDetailEvent.UI.OnNavScreen(it))
        }
    )
}