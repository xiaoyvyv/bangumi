package com.xiaoyv.bangumi.features.index.detail.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.xiaoyv.bangumi.shared.core.types.IndexCatType
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.types.TopicType
import com.xiaoyv.bangumi.shared.data.model.PreviewComposeIndexRelatedLazyItems
import com.xiaoyv.bangumi.shared.data.model.request.list.index.ListIndexRelatedParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeBlogDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoInfo
import com.xiaoyv.bangumi.shared.data.model.response.bgm.index.ComposeIndexRelated
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubjectRelation
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLazyColumn
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.kts.HideInPreview
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginGrid
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import com.xiaoyv.bangumi.shared.ui.view.episode.EpisodeItem
import com.xiaoyv.bangumi.shared.ui.view.index.IndexRelatedItem
import com.xiaoyv.bangumi.shared.ui.view.mono.MonoLineItem
import com.xiaoyv.bangumi.shared.ui.view.subject.SubjectBlogItem
import com.xiaoyv.bangumi.shared.ui.view.subject.SubjectLineItem
import com.xiaoyv.bangumi.shared.ui.view.topic.TopicPageItem

/**
 * Renders one tab of an index detail page as a paged collection of related entries.
 *
 * @param param The query parameters for the selected index tab.
 * @param onNavScreen Handles navigation initiated by a collected entry.
 */
@Composable
fun IndexDetailPageScreen(
    param: ListIndexRelatedParam,
    onNavScreen: (Screen) -> Unit,
) = HideInPreview {
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
        contentPadding = PaddingValues(ContentMarginGrid),
        verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
    ) { item, _ ->
        IndexPageItem(item, onNavScreen)
    }
}

@Composable
private fun IndexPageItem(
    item: ComposeIndexRelated,
    onNavScreen: (Screen) -> Unit
) {
    IndexRelatedItem(comment = item.comment) {
        IndexPageItemContent(item, onNavScreen)
    }
}

/**
 * Renders the type-specific content of an index entry.
 *
 * Navigation remains owned by the feature while the surrounding shared component provides the
 * consistent index-entry visual treatment.
 *
 * @param item The collected entry to display.
 * @param onNavScreen Handles navigation initiated by the entry.
 */
@Composable
fun IndexPageItemContent(
    item: ComposeIndexRelated,
    onNavScreen: (Screen) -> Unit,
) {
    when (item.cat) {
        IndexCatType.SUBJECT -> {
            SubjectLineItem(
                modifier = Modifier.fillMaxWidth(),
                display = ComposeSubjectRelation(subject = item.subject),
                contentPadding = PaddingValues(ContentMargin),
                onClick = {
                    if (item.subject != ComposeSubject.Empty) {
                        onNavScreen(Screen.SubjectDetail(item.subject.id))
                    }
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
                showDivider = false,
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
                showDivider = false,
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
                contentPadding = PaddingValues(ContentMargin),
                onClick = {
                    onNavScreen(Screen.TopicDetail(item.episode.id, TopicType.TYPE_EP))
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
                    onNavScreen(Screen.TopicDetail(item.blog.id, TopicType.TYPE_BLOG))
                },
                onClickUser = {
                    onNavScreen(Screen.UserDetail(item.blog.user.username))
                }
            )
        }

        IndexCatType.GROUP_TOPIC -> {
            TopicPageItem(
                modifier = Modifier.fillMaxWidth(),
                showMenu = false,
                item = item.groupTopic.copy(topicType = TopicType.TYPE_GROUP),
                onClick = {
                    onNavScreen(Screen.TopicDetail(item.groupTopic.id, TopicType.TYPE_GROUP))
                }
            )
        }

        IndexCatType.SUBJECT_TOPIC -> {
            TopicPageItem(
                modifier = Modifier.fillMaxWidth(),
                showMenu = false,
                item = item.subjectTopic.copy(topicType = TopicType.TYPE_SUBJECT),
                onClick = {
                    onNavScreen(Screen.TopicDetail(item.subjectTopic.id, TopicType.TYPE_SUBJECT))
                }
            )
        }
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
