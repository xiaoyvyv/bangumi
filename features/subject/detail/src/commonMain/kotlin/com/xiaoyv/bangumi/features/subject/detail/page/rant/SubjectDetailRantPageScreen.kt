package com.xiaoyv.bangumi.features.subject.detail.page.rant

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.PagingData
import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailEvent
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.types.ReportType
import com.xiaoyv.bangumi.shared.data.model.PreviewComposeReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLazyColumn
import com.xiaoyv.bangumi.shared.ui.component.layout.state.rememberCacheWindowLazyListState
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.paging.LazyPagingItems
import com.xiaoyv.bangumi.shared.ui.component.paging.collectAsLazyPagingItems
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import com.xiaoyv.bangumi.shared.ui.view.comment.CommentReplyItem
import kotlinx.coroutines.flow.flowOf

private const val ItemCommentItem = "KeyCommentTip"

@Composable
fun SubjectDetailRantPageRoute(
    subjectId: Long,
    @CollectionType type: Int,
    onUiEvent: (SubjectDetailEvent.UI) -> Unit
) {
    val viewModel = koinSubjectDetailRantViewModel(
        subjectId = subjectId,
        type = type
    )
    val commentPagingItems = viewModel.subjectComments.collectAsLazyPagingItems()

    viewModel.collectBaseSideEffect {

    }

    SubjectDetailRantPageScreen(
        commentPagingItems = commentPagingItems,
        onUiEvent = onUiEvent,
        onActionEvent = viewModel::onEvent
    )
}


@Composable
fun SubjectDetailRantPageScreen(
    commentPagingItems: LazyPagingItems<ComposeReply>,
    onActionEvent: (SubjectDetailRantEvent) -> Unit,
    onUiEvent: (SubjectDetailEvent.UI) -> Unit
) {
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
                    onActionEvent(SubjectDetailRantEvent.OnReactionClick(item, it))
                }
            )
            HorizontalDivider()
        }
    )
}


@Composable
@Preview
private fun PreviewSubjectDetailRantPageScreen() {
    PreviewColumn(modifier = Modifier.fillMaxSize()) {
        SubjectDetailRantPageScreen(
            commentPagingItems = flowOf(PagingData.from(listOf(PreviewComposeReply))).collectAsLazyPagingItems(),
            onUiEvent = { },
            onActionEvent = {}
        )
    }
}