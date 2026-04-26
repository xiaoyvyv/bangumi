package com.xiaoyv.bangumi.features.main.tab.topic.page

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.xiaoyv.bangumi.features.main.tab.topic.business.TopicEvent
import com.xiaoyv.bangumi.shared.core.types.TopicDetailType
import com.xiaoyv.bangumi.shared.core.types.RakuenTab
import com.xiaoyv.bangumi.shared.data.model.response.bgm.topic.ComposeTopic
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import com.xiaoyv.bangumi.shared.ui.component.divider.BgmHorizontalDivider
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLazyColumn
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.paging.collectAsLazyPagingItems
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import com.xiaoyv.bangumi.shared.ui.view.topic.TopicPageItem

private const val CONTENT_TYPE_TOPIC = "CONTENT_TYPE_TOPIC"

@Composable
fun TopicPageScreen(
    @RakuenTab type: String,
    viewModel: TopicPageViewModel = koinTopicPageViewModel(type),
    onUiEvent: (TopicEvent.UI) -> Unit,
    onActionEvent: (TopicEvent.Action) -> Unit,
) {
    StateLazyColumn(
        modifier = Modifier.fillMaxSize(),
        pagingItems = viewModel.topicFlow.collectAsLazyPagingItems(),
        showScrollUpBtn = true,
        key = { item, _ -> item.id },
        contentType = { CONTENT_TYPE_TOPIC }
    ) { item, _ ->
        TopicPageItem(
            modifier = Modifier.fillMaxWidth(),
            item = item,
            onClick = { onUiEvent(TopicEvent.UI.OnNavScreen(Screen.TopicDetail(it.id, it.topicType))) },
            onClickUser = { onUiEvent(TopicEvent.UI.OnNavScreen(Screen.UserDetail(it.username))) },
            onClickSubject = { onUiEvent(TopicEvent.UI.OnNavScreen(Screen.SubjectDetail(it.id))) },
            onClickMono = { onUiEvent(TopicEvent.UI.OnNavScreen(Screen.MonoDetail(it.id, it.type))) },
            onReport = { }
        )
        BgmHorizontalDivider()
    }
}


@Preview
@Composable
fun PreviewTopicPageItem() {
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
