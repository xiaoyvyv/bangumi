package com.xiaoyv.bangumi.features.main.tab.rakuen.page

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.xiaoyv.bangumi.features.main.tab.rakuen.business.RaKuenEvent
import com.xiaoyv.bangumi.shared.core.types.RakuenTab
import com.xiaoyv.bangumi.shared.core.types.TopicDetailType
import com.xiaoyv.bangumi.shared.data.model.response.bgm.topic.ComposeTopic
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import com.xiaoyv.bangumi.shared.ui.component.divider.BgmHorizontalDivider
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLazyColumn
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.paging.collectAsLazyPagingItems
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import com.xiaoyv.bangumi.shared.ui.view.topic.TopicPageItem

private const val CONTENT_TYPE_RAKUEN = "CONTENT_TYPE_RAKUEN"

@Composable
fun RaKuenPageScreen(
    @RakuenTab type: String,
    viewModel: TopicPageViewModel = koinTopicPageViewModel(type),
    onUiEvent: (RaKuenEvent.UI) -> Unit,
    onActionEvent: (RaKuenEvent.Action) -> Unit,
) {
    StateLazyColumn(
        modifier = Modifier.fillMaxSize(),
        pagingItems = viewModel.topicFlow.collectAsLazyPagingItems(),
        showScrollUpBtn = true,
        key = { item, _ -> item.id },
        contentType = { CONTENT_TYPE_RAKUEN }
    ) { item, _ ->
        TopicPageItem(
            modifier = Modifier.fillMaxWidth(),
            item = item,
            onClick = { onUiEvent(RaKuenEvent.UI.OnNavScreen(Screen.TopicDetail(it.id, it.topicType))) },
            onClickUser = { onUiEvent(RaKuenEvent.UI.OnNavScreen(Screen.UserDetail(it.username))) },
            onClickSubject = { onUiEvent(RaKuenEvent.UI.OnNavScreen(Screen.SubjectDetail(it.id))) },
            onClickMono = { onUiEvent(RaKuenEvent.UI.OnNavScreen(Screen.MonoDetail(it.id, it.type))) },
            onReport = { }
        )
        BgmHorizontalDivider()
    }
}


@Preview
@Composable
fun PreviewRaKuenPageItem() {
    PreviewColumn {
        TopicPageItem(
            modifier = Modifier.fillMaxWidth(),
            item = ComposeTopic(
                creator = ComposeUser(
                    nickname = "小夜",
                ),
                replyCount = 100,
                title = "葬送的芙莉莲",
                topicType = TopicDetailType.TYPE_GROUP
            )
        )
    }
}
