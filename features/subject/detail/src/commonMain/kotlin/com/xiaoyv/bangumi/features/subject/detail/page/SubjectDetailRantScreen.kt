package com.xiaoyv.bangumi.features.subject.detail.page

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailEvent
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeComment
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLazyColumn
import com.xiaoyv.bangumi.shared.ui.component.layout.state.rememberCacheWindowLazyListState
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.paging.LazyPagingItems
import com.xiaoyv.bangumi.shared.ui.view.comment.CommentItem

private const val ItemCommentItem = "KeyCommentTip"

/**
 * [SubjectDetailTopicScreen]
 *
 * @since 2025/5/11
 */
@Composable
fun SubjectDetailRantScreen(
    commentPagingItems: LazyPagingItems<ComposeComment>,
    onUiEvent: (SubjectDetailEvent.UI) -> Unit,
) {
    StateLazyColumn(
        modifier = Modifier.fillMaxSize(),
        pagingItems = commentPagingItems,
        state = rememberCacheWindowLazyListState(),
        userScrollEnabled = true,
        key = { item, _ -> item.id },
        contentType = { _ -> ItemCommentItem },
        itemContent = { item, _ ->
            CommentItem(
                modifier = Modifier.fillMaxWidth(),
                item = item,
                onClickUser = { onUiEvent(SubjectDetailEvent.UI.OnNavScreen(Screen.UserDetail(it))) },
                onClick = {

                }
            )
        }
    )
}