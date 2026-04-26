package com.xiaoyv.bangumi.features.index.detail.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.core.types.IndexCatType
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.types.TopicDetailType
import com.xiaoyv.bangumi.shared.data.model.PreviewComposeIndexRelatedLazyItems
import com.xiaoyv.bangumi.shared.data.model.request.list.index.ListIndexRelatedParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeBlogDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.index.ComposeIndexRelated
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoInfo
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubjectDisplay
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLazyColumn
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.paging.LazyPagingItems
import com.xiaoyv.bangumi.shared.ui.component.paging.collectAsLazyPagingItems
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import com.xiaoyv.bangumi.shared.ui.view.episode.EpisodeItem
import com.xiaoyv.bangumi.shared.ui.view.mono.MonoLineItem
import com.xiaoyv.bangumi.shared.ui.view.subject.SubjectBlogItem
import com.xiaoyv.bangumi.shared.ui.view.subject.SubjectLineItem
import com.xiaoyv.bangumi.shared.ui.view.topic.TopicPageItem

@Composable
fun IndexDetailPageScreen(
    param: ListIndexRelatedParam,
    onNavScreen: (Screen) -> Unit,
) {
    if (LocalInspectionMode.current) return
    val viewModel = koinIndexDetailPageViewModel(param)
    val pagingItems = viewModel.indexRelated.collectAsLazyPagingItems()

    IndexDetailPageScreenContent(pagingItems, onNavScreen)
}

@Composable
private fun IndexDetailPageScreenContent(
    pagingItems: LazyPagingItems<ComposeIndexRelated>,
    onNavScreen: (Screen) -> Unit,
) {
    StateLazyColumn(
        modifier = Modifier.fillMaxSize(),
        pagingItems = pagingItems,
        key = { item, _ -> item.id },
        contentType = { pagingItems.peek(it)?.cat },
    ) { item, _ ->
        Column(modifier = Modifier.fillMaxWidth()) {
            IndexDetailPageRelatedContent(item, onNavScreen)

            if (item.comment.isNotBlank()) {
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = LayoutPadding)
                        .padding(vertical = LayoutPaddingHalf),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = item.comment,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun IndexDetailPageRelatedContent(
    item: ComposeIndexRelated,
    onNavScreen: (Screen) -> Unit,
) {
    when (item.cat) {
        IndexCatType.SUBJECT -> {
            SubjectLineItem(
                modifier = Modifier.fillMaxWidth(),
                display = ComposeSubjectDisplay(subject = item.subject),
                contentPadding = PaddingValues(horizontal = LayoutPadding, vertical = 12.dp),
                onClick = {
                    onNavScreen(Screen.SubjectDetail(item.subject.id))
                }
            )
        }

        IndexCatType.CHARACTER -> {
            MonoLineItem(
                modifier = Modifier.fillMaxWidth(),
                item = ComposeMonoDisplay(
                    type = MonoType.CHARACTER,
                    info = ComposeMonoInfo(mono = item.character)
                ),
                onClick = { id, type ->
                    onNavScreen(Screen.MonoDetail(id, type))
                }
            )
        }

        IndexCatType.PERSON -> {
            MonoLineItem(
                modifier = Modifier.fillMaxWidth(),
                item = ComposeMonoDisplay(
                    type = MonoType.PERSON,
                    info = ComposeMonoInfo(mono = item.person)
                ),
                onClick = { id, type ->
                    onNavScreen(Screen.MonoDetail(id, type))
                }
            )
        }

        IndexCatType.EP -> {
            EpisodeItem(
                modifier = Modifier.fillMaxWidth(),
                subjectType = item.type,
                item = item.episode,
                contentPadding = PaddingValues(horizontal = LayoutPadding, vertical = 12.dp),
                onClick = {
                    onNavScreen(Screen.Article(item.episode.id, TopicDetailType.TYPE_EP))
                }
            )
        }

        IndexCatType.BLOG -> {
            SubjectBlogItem(
                modifier = Modifier.fillMaxWidth(),
                item = ComposeBlogDisplay(
                    blog = item.blog,
                    user = item.blog.user,
                ),
                onClick = {
                    onNavScreen(Screen.Article(item.blog.id, TopicDetailType.TYPE_BLOG))
                },
                onClickUser = {
                    onNavScreen(Screen.UserDetail(item.blog.user.username))
                }
            )
        }

        IndexCatType.GROUP_TOPIC -> {
            TopicPageItem(
                modifier = Modifier.fillMaxWidth(),
                item = item.groupTopic.copy(topicType = TopicDetailType.TYPE_GROUP),
                onClick = {
                    onNavScreen(Screen.Article(item.groupTopic.id, TopicDetailType.TYPE_GROUP))
                }
            )
        }

        IndexCatType.SUBJECT_TOPIC -> {
            TopicPageItem(
                modifier = Modifier.fillMaxWidth(),
                item = item.subjectTopic.copy(topicType = TopicDetailType.TYPE_SUBJECT),
                onClick = {
                    onNavScreen(Screen.Article(item.subjectTopic.id, TopicDetailType.TYPE_SUBJECT))
                }
            )
        }
    }
}

@Composable
private fun CardContainer(
    contentPadding: PaddingValues = PaddingValues(12.dp),
    content: @Composable BoxScope.() -> Unit,
) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}


@Composable
@Preview
private fun PreviewIndexDetailPageScreenContent() {
    PreviewColumn(modifier = Modifier.fillMaxSize()) {
        IndexDetailPageScreenContent(
            pagingItems = PreviewComposeIndexRelatedLazyItems.collectAsLazyPagingItems(),
            onNavScreen = {

            }
        )
    }
}