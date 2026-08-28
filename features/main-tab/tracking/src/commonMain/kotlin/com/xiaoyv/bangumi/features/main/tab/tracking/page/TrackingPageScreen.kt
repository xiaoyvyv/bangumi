package com.xiaoyv.bangumi.features.main.tab.tracking.page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_rank_no
import com.xiaoyv.bangumi.core_resource.resources.tracking_action_comment
import com.xiaoyv.bangumi.core_resource.resources.tracking_action_discuss
import com.xiaoyv.bangumi.core_resource.resources.tracking_action_review
import com.xiaoyv.bangumi.core_resource.resources.tracking_chap_all_completed
import com.xiaoyv.bangumi.core_resource.resources.tracking_doing_count
import com.xiaoyv.bangumi.core_resource.resources.tracking_ep_all_watched
import com.xiaoyv.bangumi.core_resource.resources.tracking_vol_all_read
import com.xiaoyv.bangumi.features.main.tab.tracking.business.TrackingEvent
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.types.SubjectDetailTab
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.types.TopicType
import com.xiaoyv.bangumi.shared.core.utils.resetSize
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.core.utils.toFixed
import com.xiaoyv.bangumi.shared.core.utils.toTrimString
import com.xiaoyv.bangumi.shared.data.model.request.bgm.CollectionSubjectParam
import com.xiaoyv.bangumi.shared.data.model.request.bgm.CollectionSubjectProgressParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeEpisode
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeImages
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeRating
import com.xiaoyv.bangumi.shared.data.model.response.bgm.home.ComposeHomeProgress
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubjectInterest
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.layout.state.rememberCacheWindowLazyListState
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.text.StarColor
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import com.xiaoyv.bangumi.shared.ui.view.episode.EpisodeGrid
import com.xiaoyv.bangumi.shared.ui.view.subject.SubjectTrackingBar
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

private const val CONTENT_TYPE_PROGRESS_SUBJECT_ITEM = "CONTENT_TYPE_PROGRESS_SUBJECT_ITEM"

@Composable
fun TrackingPageScreen(
    @SubjectType subjectType: Int,
    items: SerializeList<ComposeHomeProgress>,
    onUiEvent: (TrackingEvent.UI) -> Unit,
    onActionEvent: (TrackingEvent.Action) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = rememberCacheWindowLazyListState(),
        contentPadding = PaddingValues(vertical = ContentMarginHalf / 2)
    ) {
        items(
            items = items,
            key = { it.subject.id },
            contentType = { CONTENT_TYPE_PROGRESS_SUBJECT_ITEM }
        ) { item ->
            TrackingPageItem(
                item = item,
                subjectType = subjectType,
                onClickSubject = {
                    onUiEvent(TrackingEvent.UI.OnNavScreen(Screen.SubjectDetail(item.subject.id)))
                },
                onClickSubjectComments = {
                    onUiEvent(TrackingEvent.UI.OnNavScreen(Screen.SubjectDetail(item.subject.id, selectedTab = SubjectDetailTab.RANT)))
                },
                onClickCreateTopic = {
                    onUiEvent(TrackingEvent.UI.OnNavScreen(Screen.SubjectDetail(item.subject.id, selectedTab = SubjectDetailTab.TOPIC)))
                },
                onClickCreateBlog = {
                    onUiEvent(TrackingEvent.UI.OnNavScreen(Screen.SubjectDetail(item.subject.id, selectedTab = SubjectDetailTab.BLOG)))
                },
                onClickEpisode = { episode ->
                    onUiEvent(TrackingEvent.UI.OnNavScreen(Screen.TopicDetail(episode.id, TopicType.TYPE_EP)))
                },
                onUpdateCollect = {
                    onActionEvent(TrackingEvent.Action.OnUpdateSubjectCollection(item.subject, it))
                },
                onUpdateProgress = {
                    onActionEvent(TrackingEvent.Action.OnUpdateSubjectProgress(item.subject, it))
                },
                onUpdateEpisode = { eps, type ->
                    onActionEvent(TrackingEvent.Action.OnUpdateEpisode(item.subject, eps, type))
                }
            )
        }
    }
}

@Composable
private fun TrackingPageItem(
    item: ComposeHomeProgress,
    onClickSubject: () -> Unit = {},
    onClickSubjectComments: () -> Unit = {},
    onClickCreateTopic: () -> Unit = {},
    onClickCreateBlog: () -> Unit = {},
    onClickEpisode: (ComposeEpisode) -> Unit = {},
    onUpdateEpisode: (List<ComposeEpisode>, Int) -> Unit = { _, _ -> },
    onUpdateProgress: (CollectionSubjectProgressParam) -> Unit = {},
    onUpdateCollect: (CollectionSubjectParam) -> Unit = {},
    subjectType: Int
) {
    val subject = item.subject

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ContentMarginHalf, vertical = ContentMarginHalf / 2),
        shape = MaterialTheme.shapes.large
    ) {
        ListItem(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClickSubject)
                .padding(top = ContentMarginHalf / 2),
            leadingContent = {
                Box(
                    modifier = Modifier
                        .width(85.dp)
                        .aspectRatio(3 / 4f)
                ) {
                    StateImage(
                        modifier = Modifier.matchParentSize(),
                        shape = MaterialTheme.shapes.small,
                        model = subject.images.displayMediumImage,
                    )

                    if (subject.rating.rank != 0) Text(
                        modifier = Modifier
                            .padding(top = ContentMarginHalf)
                            .align(Alignment.TopStart)
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                                RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp),
                            )
                            .padding(ContentMarginHalf, 4.dp),
                        text = buildString {
                            append(stringResource(Res.string.global_rank_no))
                            append(" ")
                            append(subject.rating.rank)
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            overlineContent = {
                Text(
                    text = buildAnnotatedString {
                        append(subject.displayName)
                        if (subject.rating.score > 0) {
                            append(" ")
                            withStyle(SpanStyle(color = StarColor, fontWeight = FontWeight.Medium)) {
                                append(subject.rating.score.toFixed(1).toTrimString())
                            }
                        }
                    },
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            headlineContent = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = ContentMarginHalf / 2),
                    verticalArrangement = Arrangement.spacedBy(ContentMarginHalf / 2)
                ) {
                    Text(
                        text = stringResource(Res.string.tracking_doing_count, subject.doing),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    val doneActionText = CollectionType.string(subject.type, CollectionType.DONE)
                    val nextEp = item.interest.epStatus + 1
                    val nextVol = item.interest.volStatus + 1

                    val isEpCompleted = nextEp > subject.eps && subject.eps != 0
                    val isVolCompleted = nextVol > subject.volumes && subject.volumes != 0

                    // 动画或三次元
                    if (subject.type == SubjectType.ANIME || subject.type == SubjectType.REAL) {
                        SubjectTrackingBar(
                            modifier = Modifier.fillMaxWidth(),
                            status = item.interest.epStatus,
                            total = subject.eps,
                            button = buildString {
                                when {
                                    item.lastUnwatchedEp == ComposeEpisode.Empty -> append(stringResource(Res.string.tracking_ep_all_watched))
                                    else -> {
                                        append("Ep.")
                                        append(nextEp)
                                        append(doneActionText)
                                    }
                                }
                            },
                            onClickIncrease = {
                                if (item.lastUnwatchedEp == ComposeEpisode.Empty) {
                                    onUpdateCollect(CollectionSubjectParam(type = CollectionType.DONE))
                                } else {
                                    onUpdateEpisode(listOf(item.lastUnwatchedEp), CollectionType.DONE)
                                }
                            }
                        )
                    } else {
                        SubjectTrackingBar(
                            modifier = Modifier.fillMaxWidth(),
                            status = item.interest.epStatus,
                            total = subject.eps,
                            button = buildString {
                                when {
                                    isEpCompleted -> append(stringResource(Res.string.tracking_chap_all_completed))
                                    else -> {
                                        append("Chp.")
                                        append(nextEp)
                                        append(doneActionText)
                                    }
                                }
                            },
                            onInputChangeConfirm = {
                                onUpdateProgress(CollectionSubjectProgressParam(epStatus = it))
                            },
                            onClickIncrease = {
                                if (isEpCompleted) {
                                    onUpdateCollect(CollectionSubjectParam(type = CollectionType.DONE))
                                } else {
                                    onUpdateProgress(CollectionSubjectProgressParam(epStatus = nextEp))
                                }
                            }
                        )
                        SubjectTrackingBar(
                            modifier = Modifier.fillMaxWidth(),
                            status = item.interest.volStatus,
                            total = subject.volumes,
                            button = buildString {
                                when {
                                    isVolCompleted -> append(stringResource(Res.string.tracking_vol_all_read))
                                    else -> {
                                        append("Vol.")
                                        append(nextVol)
                                        append(doneActionText)
                                    }
                                }
                            },
                            onInputChangeConfirm = {
                                onUpdateProgress(CollectionSubjectProgressParam(volStatus = it))
                            },
                            onClickIncrease = {
                                if (isEpCompleted) {
                                    onUpdateCollect(CollectionSubjectParam(type = CollectionType.DONE))
                                } else {
                                    onUpdateProgress(CollectionSubjectProgressParam(volStatus = nextVol))
                                }
                            }
                        )
                    }
                }
            },
            supportingContent = {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .offset(x = (-4).dp),
                    horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
                    verticalArrangement = Arrangement.spacedBy(ContentMarginHalf)
                ) {
                    TextButton(
                        modifier = Modifier.resetSize(),
                        shape = MaterialTheme.shapes.small,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                        onClick = onClickCreateTopic
                    ) {
                        Text(text = stringResource(Res.string.tracking_action_discuss))
                    }
                    TextButton(
                        modifier = Modifier.resetSize(),
                        shape = MaterialTheme.shapes.small,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                        onClick = onClickSubjectComments,
                    ) {
                        Text(text = stringResource(Res.string.tracking_action_comment))
                    }
                    TextButton(
                        modifier = Modifier.resetSize(),
                        shape = MaterialTheme.shapes.small,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                        onClick = onClickCreateBlog
                    ) {
                        Text(text = stringResource(Res.string.tracking_action_review))
                    }
                }
            }
        )

        if (subjectType != SubjectType.BOOK) EpisodeGrid(
            episodes = item.eps,
            maxRows = 1000,
            onEpisodeChange = onUpdateEpisode,
            onClickEpisode = onClickEpisode
        )
    }
}

@Composable
@Preview
private fun PreviewTrackingPageScreenContent() {
    PreviewColumn(modifier = Modifier.fillMaxSize()) {
        val dummyItems = persistentListOf(
            ComposeHomeProgress(
                subject = ComposeSubject(
                    id = 1,
                    nameCn = "测试动漫 1",
                    images = ComposeImages(common = "https://bgm.tv/img/no_icon_subject.png"),
                    rating = ComposeRating(score = 8.5, rank = 100),
                    doing = 1234,
                    type = SubjectType.ANIME,
                    eps = 24,
                    interest = ComposeSubjectInterest(epStatus = 12)
                ),
                lastUnwatchedEp = ComposeEpisode(sortOrder = 13.0)
            ),
            ComposeHomeProgress(
                subject = ComposeSubject(
                    id = 2,
                    nameCn = "测试书籍 1",
                    images = ComposeImages(common = "https://bgm.tv/img/no_icon_subject.png"),
                    rating = ComposeRating(score = 7.2, rank = 500),
                    doing = 567,
                    type = SubjectType.BOOK,
                    eps = 50,
                    volumes = 5,
                    interest = ComposeSubjectInterest(epStatus = 10, volStatus = 1)
                )
            ),
            ComposeHomeProgress(
                subject = ComposeSubject(
                    id = 3,
                    nameCn = "测试游戏 1",
                    images = ComposeImages(common = "https://bgm.tv/img/no_icon_subject.png"),
                    rating = ComposeRating(score = 9.0, rank = 10),
                    doing = 8888,
                    type = SubjectType.GAME,
                    eps = 0,
                    interest = ComposeSubjectInterest(epStatus = 0)
                )
            )
        )

        TrackingPageScreen(
            subjectType = SubjectType.ANIME,
            items = dummyItems,
            onUiEvent = {},
            onActionEvent = {}
        )
    }
}
