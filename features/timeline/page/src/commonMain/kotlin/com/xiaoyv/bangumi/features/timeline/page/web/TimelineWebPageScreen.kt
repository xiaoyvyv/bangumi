package com.xiaoyv.bangumi.features.timeline.page.web

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaoyv.bangumi.shared.core.types.TimelineTab
import com.xiaoyv.bangumi.shared.core.utils.clickWithoutRipped
import com.xiaoyv.bangumi.shared.core.utils.toLongValue
import com.xiaoyv.bangumi.shared.data.model.request.list.timeline.ListTimelineParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeCollection
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeRating
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubjectWebInfo
import com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline.ComposeWebTimeline
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import com.xiaoyv.bangumi.shared.ui.component.bar.RatingBar
import com.xiaoyv.bangumi.shared.ui.component.divider.BgmHorizontalDivider
import com.xiaoyv.bangumi.shared.ui.component.image.InfoImage
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLazyColumn
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.paging.collectAsLazyPagingItems
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.text.BgmLinkedText
import com.xiaoyv.bangumi.shared.ui.component.text.StarColor
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import kotlinx.collections.immutable.persistentListOf

private const val CONTENT_TYPE_TIMELINE = "CONTENT_TYPE_TIMELINE"
private const val CONTENT_TYPE_TIMELINE_SUBJECT = "CONTENT_TYPE_TIMELINE_SUBJECT"
private const val CONTENT_TYPE_TIMELINE_MONO = "CONTENT_TYPE_TIMELINE_MONO"

@Composable
fun TimelineWebPageScreen(
    param: ListTimelineParam,
    onNavScreen: (Screen) -> Unit,
) {
    if (LocalInspectionMode.current) return
    val viewModel: TimelineWebPageViewModel = koinTimelineWebPageViewModel(param)

    StateLazyColumn(
        modifier = Modifier.fillMaxSize(),
        pagingItems = viewModel.timelineFlow.collectAsLazyPagingItems(),
        showScrollUpBtn = true,
        key = { item, index -> item.id },
        contentType = { CONTENT_TYPE_TIMELINE }
    ) { item, index ->
        if (index != 0 && item.user.avatar.displayMediumImage.isNotBlank()) BgmHorizontalDivider()

        TimelinePageItem(
            modifier = Modifier.fillMaxWidth(),
            item = item,
            onClick = {
                onNavScreen(Screen.TimelineDetail(it.id.toLongValue()))
            },
            onClickMono = {
                onNavScreen(Screen.MonoDetail(it.id, it.type))
            },
            onClickUser = {
                onNavScreen(Screen.UserDetail(it.username))
            },
            onClickSubject = {
                onNavScreen(Screen.SubjectDetail(it.id))
            }
        )
    }
}


@Composable
private fun TimelinePageItem(
    modifier: Modifier,
    item: ComposeWebTimeline = ComposeWebTimeline.Empty,
    headlineContent: @Composable (() -> Unit)? = null,
    onClick: (ComposeWebTimeline) -> Unit = {},
    onClickUser: (ComposeUser) -> Unit = {},
    onClickSubject: (ComposeSubject) -> Unit = {},
    onClickMono: (ComposeMonoDisplay) -> Unit = {},
) {
    ListItem(
        modifier = modifier.clickable { onClick(item) },
        leadingContent = {
            if (item.user.avatar.displayMediumImage.isBlank()) {
                Spacer(modifier = Modifier.size(44.dp))
            } else {
                StateImage(
                    modifier = Modifier
                        .size(44.dp)
                        .clickWithoutRipped { onClickUser(item.user) },
                    shape = MaterialTheme.shapes.small,
                    model = item.user.avatar.displayMediumImage
                )
            }
        },
        headlineContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = LayoutPaddingHalf),
                verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
            ) {
                if (item.title.isNotBlank()) {
                    BgmLinkedText(
                        modifier = Modifier.fillMaxWidth(),
                        text = item.title,
                        maxLines = 4,
                    )
                }
                if (item.content.isNotBlank()) {
                    BgmLinkedText(
                        modifier = Modifier.fillMaxWidth(),
                        text = item.content,
                    )
                }

                if (item.blog.isNotBlank()) {
                    TimelinePageItemBlog(item = item)
                }

                if (item.collection != ComposeCollection.Empty) {
                    TimelinePageItemCollection(
                        item = item.collection,
                        onClick = {}
                    )
                }

                if (item.subjects.isNotEmpty()) {
                    TimelinePageItemSubject(
                        item = item,
                        onClick = onClickSubject
                    )
                }

                if (item.monos.isNotEmpty()) {
                    TimelinePageItemMono(
                        item = item,
                        onClick = onClickMono
                    )
                }

                if (headlineContent != null) {
                    headlineContent()
                }
            }
        },
        supportingContent = {
            Text(
                text = buildString {
                    append(item.time)
                    if (item.platform.isNotBlank()) {
                        append(" · ")
                        append(item.platform.lowercase())
                    }
                },
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        },
        overlineContent = {
            Text(
                modifier = Modifier.clickWithoutRipped { onClickUser(item.user) },
                text = item.user.nickname,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    )
}

@Composable
private fun TimelinePageItemBlog(
    item: ComposeWebTimeline,
) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        BgmLinkedText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            text = item.blog,
        )
    }
}

@Composable
private fun TimelinePageItemCollection(
    item: ComposeCollection,
    onClick: (ComposeCollection) -> Unit,
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onClick(item) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
        ) {
            if (item.rate > 0) {
                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
                ) {
                    RatingBar(
                        value = item.rate.toDouble(),
                        starSize = 16.dp
                    )
                    Text(
                        text = "(${item.rate})",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = StarColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
            if (item.comment.isNotBlank()) Text(
                text = item.comment,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                )
            )
        }
    }
}

@Composable
private fun TimelinePageItemSubject(
    item: ComposeWebTimeline,
    onClick: (ComposeSubject) -> Unit,
) {
    when {
        item.subjects.size == 1 -> {
            val subject = item.subjects.first()

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onClick(subject) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
                ) {
                    InfoImage(
                        modifier = Modifier.width(80.dp),
                        model = subject.images.displayMediumImage,
                        text = subject.relation,
                        onClick = { onClick(subject) }
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .height(120.dp)
                    ) {
                        Text(
                            text = subject.nameCn.ifBlank { subject.name },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = subject.webInfo.info,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 20.sp
                            ),
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        if (subject.rating != ComposeRating.Empty) {
                            Text(
                                text = subject.rating.score.toString(),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = StarColor,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }
        }

        item.subjects.size > 1 -> {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
            ) {
                items(
                    items = item.subjects,
                    contentType = { CONTENT_TYPE_TIMELINE_SUBJECT }
                ) { subject ->
                    InfoImage(
                        modifier = Modifier.width(80.dp),
                        model = subject.images.displayMediumImage,
                        text = subject.relation,
                        onClick = { onClick(subject) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelinePageItemMono(
    item: ComposeWebTimeline,
    onClick: (ComposeMonoDisplay) -> Unit,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
    ) {
        items(items = item.monos, contentType = { CONTENT_TYPE_TIMELINE_MONO }) { display ->
            InfoImage(
                modifier = Modifier.width(80.dp),
                model = display.mono.images.displayMediumImage,
                text = display.mono.displayName,
                onClick = { onClick(display) }
            )
        }
    }
}

@Preview
@Composable
fun PreviewTimelinePageItem() {
    PreviewColumn {
        TimelinePageItem(
            modifier = Modifier.fillMaxWidth(),
            item = ComposeWebTimeline(
                title = buildAnnotatedString { append("落秋 读过 今日から始める幼なじみ 第100话 ") },
                time = "15小时54分钟前",
                user = ComposeUser(
                    id = 1,
                    nickname = "小玉儿",
                    username = "test-01"
                ),
                platform = "mobile",
                subjects = persistentListOf(
                    ComposeSubject(
                        name = "葬送的芙莉莲",
                        webInfo = ComposeSubjectWebInfo(
                            info = "25话 / 2018年7月7日 / 武居正能、田口清隆、市野龍一、辻本貴則、伊藤良"
                        ),
                        rating = ComposeRating(
                            total = 300,
                            score = 7.7
                        )
                    )
                ),
                timelineType = TimelineTab.DYNAMIC
            )
        )
    }
}
