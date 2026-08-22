package com.xiaoyv.bangumi.shared.ui.view.timeline

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.core.types.ButtonType
import com.xiaoyv.bangumi.shared.core.types.ReportType
import com.xiaoyv.bangumi.shared.core.types.TimelineCat
import com.xiaoyv.bangumi.shared.core.types.TimelineStatusAction
import com.xiaoyv.bangumi.shared.core.types.TimelineSubjectAction
import com.xiaoyv.bangumi.shared.core.types.TopicType
import com.xiaoyv.bangumi.shared.core.utils.clickWithoutRipped
import com.xiaoyv.bangumi.shared.core.utils.formatAgo
import com.xiaoyv.bangumi.shared.data.manager.shared.currentUser
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeGroup
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeRating
import com.xiaoyv.bangumi.shared.data.model.response.bgm.reaction.ComposeReaction
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline.ComposeTimeline
import com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline.ComposeTimelineBatch
import com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline.ComposeTimelineDaily
import com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline.ComposeTimelineMemo
import com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline.ComposeTimelineSingle
import com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline.ComposeTimelineSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import com.xiaoyv.bangumi.shared.ui.component.chip.DropMenuActionButton
import com.xiaoyv.bangumi.shared.ui.component.emoji.PopupReaction
import com.xiaoyv.bangumi.shared.ui.component.emoji.ReactionGroup
import com.xiaoyv.bangumi.shared.ui.component.emoji.rememberPopupReactionState
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.tab.rememberButtonTypeMenu
import com.xiaoyv.bangumi.shared.ui.component.text.BgmLinkedText
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import kotlinx.collections.immutable.persistentListOf

@Composable
fun TimelinePageItem(
    modifier: Modifier,
    item: ComposeTimeline,
    onNavigate: (Screen) -> Unit,
    onReactionClick: (ComposeTimeline, ComposeReaction) -> Unit,
    onDeleteClick: (ComposeTimeline) -> Unit,
    enableDetailNavigation: Boolean = true,
) {
    ListItem(
        modifier = modifier.clickable(enabled = enableDetailNavigation) {
            if (item.cat == TimelineCat.STATUS && item.type == TimelineStatusAction.COMMENT) {
                onNavigate(Screen.TimelineDetail(item))
            }
        },
        leadingContent = {
            StateImage(
                modifier = Modifier
                    .size(44.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.small)
                    .clickWithoutRipped { onNavigate(Screen.UserDetail(item.user.username)) },
                shape = MaterialTheme.shapes.small,
                model = item.user.avatar.displaySmallImage,
            )
        },
        headlineContent = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = ContentMarginHalf),
                verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
            ) {
                if (item.cat == TimelineCat.STATUS && item.type == TimelineStatusAction.COMMENT) {
                    BgmLinkedText(text = item.memo.status.tsukkomi)
                } else {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = item.rememberTimelineTitle(
                            onUserClickListener = { onNavigate(Screen.UserDetail(it.username)) },
                            onGroupClickListener = { onNavigate(Screen.GroupDetail(it.name)) },
                            onSubjectClickListener = { onNavigate(Screen.SubjectDetail(it.id)) },
                            onEpisodeClickListener = { onNavigate(Screen.TopicDetail(it.id, TopicType.TYPE_EP)) },
                            onBlogClickListener = { onNavigate(Screen.TopicDetail(it.id, TopicType.TYPE_BLOG)) },
                            onIndexClickListener = { onNavigate(Screen.IndexDetail(it.id)) },
                            onMonoClickListener = { mono, type -> onNavigate(Screen.MonoDetail(mono.id, type)) },
                        ),
                    )
                }

                if (item.reactions.isNotEmpty()) {
                    ReactionGroup(
                        modifier = Modifier.fillMaxWidth(),
                        reactions = item.reactions,
                        onClick = { onReactionClick(item, it) },
                    )
                }

                when (item.cat) {
                    TimelineCat.SUBJECT -> TimelinePageItemSubject(item) { onNavigate(Screen.SubjectDetail(it.id)) }
                    TimelineCat.PROGRESS if (item.memo.progress.single != ComposeTimelineSingle.Empty) -> {
                        TimelinePageItemSubjectItem(item.memo.progress.single.subject) { onNavigate(Screen.SubjectDetail(it.id)) }
                    }

                    TimelineCat.PROGRESS if (item.memo.progress.batch != ComposeTimelineBatch.Empty) -> {
                        TimelinePageItemSubjectItem(item.memo.progress.batch.subject) { onNavigate(Screen.SubjectDetail(it.id)) }
                    }

                    TimelineCat.WIKI -> TimelinePageItemSubjectItem(item.memo.wiki.subject) { onNavigate(Screen.SubjectDetail(it.id)) }
                    TimelineCat.MONO -> TimelinePageItemMono(item) { mono, type -> onNavigate(Screen.MonoDetail(mono.id, type)) }
                    TimelineCat.DAILY -> TimelinePageItemDaily(
                        item = item,
                        onClickGroup = { onNavigate(Screen.GroupDetail(it.name)) },
                        onClickUser = { onNavigate(Screen.UserDetail(it.username)) },
                    )

                    TimelineCat.BLOG -> TimelinePageItemBlog(
                        item = item,
                        onClick = { onNavigate(Screen.TopicDetail(it.id, TopicType.TYPE_BLOG)) },
                    )

                    TimelineCat.INDEX -> TimelinePageItemIndex(
                        item = item,
                        onClick = { onNavigate(Screen.IndexDetail(it.id)) },
                    )
                }
            }
        },
        supportingContent = {
            Text(
                text = buildString {
                    append(item.createdAt.formatAgo())
                    if (item.source.name.isNotBlank()) append(" · ").append(item.source.name)
                },
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            )
        },
        overlineContent = {
            Row(Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.weight(1f).clickWithoutRipped { onNavigate(Screen.UserDetail(item.user.username)) },
                    text = item.user.nickname,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                    ),
                )
                val selfTimeline = item.user.id == currentUser().id
                if (item.cat == TimelineCat.STATUS || selfTimeline) {
                    Box {
                        val reactionState = rememberPopupReactionState()
                        PopupReaction(state = reactionState) { onReactionClick(item, ComposeReaction(value = it)) }
                        DropMenuActionButton(
                            modifier = Modifier.size(20.dp),
                            imageVector = BgmIcons.MoreHoriz,
                            imageTint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            options = rememberButtonTypeMenu {
                                if (item.cat == TimelineCat.STATUS) add(ButtonType.Reaction)
                                if (selfTimeline) add(ButtonType.Delete)
                                add(ButtonType.Report)
                            },
                            onOptionClick = {
                                when (it.type) {
                                    ButtonType.Reaction -> reactionState.show()
                                    ButtonType.Delete -> onDeleteClick(item)
                                    ButtonType.Report -> onNavigate(Screen.Report(ReportType.TIMELINE, item.id))
                                    else -> Unit
                                }
                            },
                        )
                    }
                }
            }
        },
    )
}


@Preview
@Composable
private fun PreviewTimelinePageScreen() {
    PreviewColumn {
        TimelinePageItem(
            modifier = Modifier.fillMaxWidth(),
            item = ComposeTimeline(
                id = 0,
                type = TimelineSubjectAction.DROPPED,
                cat = TimelineCat.SUBJECT,
                user = ComposeUser(nickname = "Test"),
                memo = ComposeTimelineMemo(
                    daily = ComposeTimelineDaily(
                        users = persistentListOf(ComposeUser(nickname = "Test")),
                        groups = persistentListOf(ComposeGroup())
                    ),
                    subject = persistentListOf(
                        ComposeTimelineSubject(
                            subject = ComposeSubject(
                                name = "Subject",
                                info = "Test Info",
                                rating = ComposeRating(score = 9.9)
                            )
                        )
                    )
                ),
                batch = false,
            ),
            onNavigate = {},
            onDeleteClick = {},
            onReactionClick = { _, _ -> }
        )
    }
}