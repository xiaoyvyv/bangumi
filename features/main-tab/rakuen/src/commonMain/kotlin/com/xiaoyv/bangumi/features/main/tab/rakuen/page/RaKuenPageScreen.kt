package com.xiaoyv.bangumi.features.main.tab.rakuen.page

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.compose.collectAsLazyPagingItems
import com.xiaoyv.bangumi.features.main.tab.rakuen.business.RaKuenEvent
import com.xiaoyv.bangumi.shared.core.types.RakuenType
import com.xiaoyv.bangumi.shared.core.types.TopicType
import com.xiaoyv.bangumi.shared.data.model.response.bgm.topic.ComposeTopicDetail
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import com.xiaoyv.bangumi.shared.ui.component.divider.BgmHorizontalDivider
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLazyColumn
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import com.xiaoyv.bangumi.shared.ui.view.rakuen.RakuenPageItem
import com.xiaoyv.bangumi.shared.ui.view.topic.TopicPageItem

private const val CONTENT_TYPE_RAKUEN = "CONTENT_TYPE_RAKUEN"

@Composable
fun RaKuenPageScreen(
    @RakuenType type: String,
    viewModel: RaKuenPageViewModel = koinRaKuenPageViewModel(type),
    onUiEvent: (RaKuenEvent.UI) -> Unit,
    onActionEvent: (RaKuenEvent.Action) -> Unit,
) {
    StateLazyColumn(
        state = com.xiaoyv.bangumi.shared.ui.component.scroll.rememberScrollUpLazyListState(),
        modifier = Modifier.fillMaxSize(),
        pagingItems = viewModel.rakuenFlow.collectAsLazyPagingItems(),
        key = { item, _ -> item.key },
        contentType = { CONTENT_TYPE_RAKUEN }
    ) { item, _ ->
        RakuenPageItem(
            modifier = Modifier.fillMaxWidth(),
            item = item,
            needShowCategory = type == RakuenType.ALL,
            onClick = { onUiEvent(RaKuenEvent.UI.OnNavScreen(Screen.TopicDetail(it.id, it.topicType))) },
            onClickUser = { onUiEvent(RaKuenEvent.UI.OnNavScreen(Screen.UserDetail(it.username))) },
            onClickSubject = { onUiEvent(RaKuenEvent.UI.OnNavScreen(Screen.SubjectDetail(it.id))) },
            onClickMono = { onUiEvent(RaKuenEvent.UI.OnNavScreen(Screen.MonoDetail(it.id, it.type))) },
            onClickGroup = { onUiEvent(RaKuenEvent.UI.OnNavScreen(Screen.GroupDetail(it.name))) },
            onReport = { onUiEvent(RaKuenEvent.UI.OnNavScreen(Screen.Report(item.reportType, item.id))) },
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
            item = ComposeTopicDetail(
                creator = ComposeUser(nickname = "小夜"),
                replyCount = 100,
                title = "葬送的芙莉莲",
                topicType = TopicType.TYPE_GROUP
            )
        )
    }
}
