package com.xiaoyv.bangumi.features.subject.detail.page

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailEvent
import com.xiaoyv.bangumi.shared.core.types.ReportType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLazyColumn
import com.xiaoyv.bangumi.shared.ui.component.layout.state.rememberCacheWindowLazyListState
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.paging.LazyPagingItems
import com.xiaoyv.bangumi.shared.ui.view.comment.CommentReplyItem
import com.xiaoyv.bangumi.shared.ui.view.comment.LocalCommentSubjectType

private const val ItemCommentItem = "KeyCommentTip"

/**
 * [SubjectDetailTopicScreen]
 *
 * @since 2025/5/11
 */
@Composable
fun SubjectDetailRantScreen(
    @SubjectType subjectType: Int,
    commentPagingItems: LazyPagingItems<ComposeReply>,
    onUiEvent: (SubjectDetailEvent.UI) -> Unit,
    onActionEvent: (SubjectDetailEvent.Action) -> Unit,
) {
    CompositionLocalProvider(LocalCommentSubjectType provides subjectType) {
        StateLazyColumn(
            modifier = Modifier.fillMaxSize(),
            pagingItems = commentPagingItems,
            state = rememberCacheWindowLazyListState(),
            userScrollEnabled = true,
            key = { item, _ -> item.id },
            contentType = { _ -> ItemCommentItem },
            itemContent = { item, _ ->
                CommentReplyItem(
                    modifier = Modifier.fillMaxWidth(),
                    item = item,
                    level = 0,
                    isLikeable = true,
                    onClickUser = { onUiEvent(SubjectDetailEvent.UI.OnNavScreen(Screen.UserDetail(it))) },
                    onClickReport = {
                        onUiEvent(SubjectDetailEvent.UI.OnNavScreen(Screen.Report(ReportType.USER, item.user.id)))
                    },
                    onClickReaction = {
                        onActionEvent(SubjectDetailEvent.Action.OnReactionClick(item, it))
                    }
                )
                HorizontalDivider()
            }
        )
    }
}