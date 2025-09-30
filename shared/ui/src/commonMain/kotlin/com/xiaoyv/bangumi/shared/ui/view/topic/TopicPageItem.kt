package com.xiaoyv.bangumi.shared.ui.view.topic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaoyv.bangumi.shared.core.types.ButtonType
import com.xiaoyv.bangumi.shared.core.types.RakuenFlagType
import com.xiaoyv.bangumi.shared.core.types.RakuenIdType
import com.xiaoyv.bangumi.shared.core.utils.clickWithoutRipped
import com.xiaoyv.bangumi.shared.core.utils.formatAgo
import com.xiaoyv.bangumi.shared.core.utils.withSpanStyle
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalSharedState
import com.xiaoyv.bangumi.shared.data.model.request.ReportParam
import com.xiaoyv.bangumi.shared.data.model.request.list.topic.LocalListTopicParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeTopic
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUser
import com.xiaoyv.bangumi.shared.ui.component.chip.DropMenuActionButton
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.rememberAlertDialogState
import com.xiaoyv.bangumi.shared.ui.component.dialog.report.ReportDialog
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.tab.rememberButtonTypeMenu
import com.xiaoyv.bangumi.shared.ui.component.text.HighlightedText
import com.xiaoyv.bangumi.shared.ui.component.text.StarColor
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

@Composable
fun TopicPageItem(
    modifier: Modifier,
    item: ComposeTopic = ComposeTopic.Empty,
    onClick: (ComposeTopic) -> Unit = {},
    onClickUser: (ComposeUser) -> Unit = {},
    onClickSubject: (ComposeSubject) -> Unit = {},
    onClickMono: (ComposeMonoDisplay) -> Unit = {},
    onReport: (ReportParam) -> Unit = {},
) {
    ListItem(
        modifier = modifier.clickable { onClick(item) },
        leadingContent = {
            TopicPageItemAvatar(
                item = item,
                onClickUser = onClickUser,
                onClickMono = onClickMono,
                onClickSubject = onClickSubject
            )
        },
        overlineContent = {
            TopicPageItemOverline(
                item = item,
                onClickUser = onClickUser,
                onClickMono = onClickMono,
                onClickSubject = onClickSubject
            )
        },
        headlineContent = {
            TopicPageItemHeadline(item = item)
        },
        supportingContent = {
            Text(
                text = item.updatedAt.formatAgo(),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        },
        trailingContent = {
            TopicPageItemTrailing(
                item = item,
                onReport = onReport
            )
        }
    )
}

@Composable
fun TopicPageItemHeadline(item: ComposeTopic) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = LayoutPaddingHalf),
        verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
    ) {
        val title = when (item.topicType) {
            RakuenIdType.TYPE_GROUP,
            RakuenIdType.TYPE_EP,
            RakuenIdType.TYPE_SUBJECT,
            RakuenIdType.TYPE_BLOG,
                -> item.title

            RakuenIdType.TYPE_PERSON,
            RakuenIdType.TYPE_CRT,
                -> item.mono.info.mono.displayName

            else -> ""
        }

        val param = LocalListTopicParam.current
        val keyword = param.search.keyword

        HighlightedText(
            modifier = Modifier.fillMaxWidth(),
            text = buildAnnotatedString {
                append(title)
                if (item.replyCount > 0) {
                    withSpanStyle(
                        color = StarColor,
                        fontWeight = FontWeight.Normal,
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize
                    ) {
                        append(" (+${item.replyCount})")
                    }
                }
            },
            highlights = remember(keyword) { persistentListOf(keyword) },
            highlightColor = Color.Green.copy(green = 0.8f)
        )

        // 搜索的条目会填充
        if (item.summary.isNotBlank() && param.ui.showSummary) {
            OutlinedCard(Modifier.fillMaxWidth()) {
                HighlightedText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    text = item.summary,
                    highlights = remember(keyword) { persistentListOf(keyword) },
                    highlightColor = Color.Green.copy(green = 0.8f),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun TopicPageItemTrailing(
    item: ComposeTopic,
    onReport: (ReportParam) -> Unit,
) {
    when (item.topicType) {
        RakuenIdType.TYPE_SUBJECT,
        RakuenIdType.TYPE_GROUP,
        RakuenIdType.TYPE_BLOG,
            -> {
            val reportDialogState = rememberAlertDialogState()
            val user = LocalSharedState.current.user

            ReportDialog(
                state = reportDialogState,
                onClick = { value, content ->
                    onReport(item.reportParam(value, content, user.formHash))
                }
            )

            DropMenuActionButton(
                modifier = Modifier.size(20.dp),
                imageVector = BgmIcons.MoreHoriz,
                imageTint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                options = rememberButtonTypeMenu { add(ButtonType.Report) },
                onOptionClick = {
                    when (it.type) {
                        ButtonType.Report -> reportDialogState.show()
                        else -> Unit
                    }
                }
            )
        }

        RakuenIdType.TYPE_EP,
        RakuenIdType.TYPE_PERSON,
        RakuenIdType.TYPE_CRT,
            -> {
            DropMenuActionButton(
                modifier = Modifier.size(20.dp),
                imageVector = BgmIcons.MoreHoriz,
                imageTint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                options = rememberButtonTypeMenu { add(ButtonType.Share) },
                onOptionClick = {

                }
            )
        }

        else -> Unit
    }
}

@Composable
private fun TopicPageItemOverline(
    item: ComposeTopic,
    onClickUser: (ComposeUser) -> Unit,
    onClickMono: (ComposeMonoDisplay) -> Unit,
    onClickSubject: (ComposeSubject) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        itemVerticalAlignment = Alignment.CenterVertically
    ) {
        when (item.topicType) {
            // 展示用户名称
            RakuenIdType.TYPE_GROUP -> {
                Text(
                    modifier = Modifier.clickWithoutRipped { onClickUser(item.creator) },
                    text = item.creator.nickname,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
            // 展示条目名称
            RakuenIdType.TYPE_BLOG,
            RakuenIdType.TYPE_SUBJECT,
            RakuenIdType.TYPE_EP,
                -> {
                Text(
                    modifier = Modifier.clickWithoutRipped {
                        if (item.subject != ComposeSubject.Empty) {
                            onClickSubject(item.subject)
                        } else if (item.creator != ComposeUser.Empty) {
                            onClickUser(item.creator)
                        }
                    },
                    text = item.subject.displayName.ifBlank { item.creator.nickname },
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
            // 展示人物名称
            RakuenIdType.TYPE_PERSON,
            RakuenIdType.TYPE_CRT,
                -> {
                Text(
                    modifier = Modifier.clickWithoutRipped { onClickMono(item.mono) },
                    text = item.mono.mono.displayName,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }

        Text(
            text = stringResource(RakuenIdType.string(item.topicType)),
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary, MaterialTheme.shapes.extraSmall)
                .padding(vertical = 2.dp, horizontal = 4.dp),
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onPrimary
            )
        )

        TopicPageFlag(item)
    }
}

@Composable
private fun TopicPageFlag(item: ComposeTopic) {
    item.flags.forEach {
        when (it) {
            RakuenFlagType.TYPE_HOT -> Text(
                text = "🔥火热",
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.error, MaterialTheme.shapes.extraSmall)
                    .padding(vertical = 2.dp, horizontal = 4.dp),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onError
                )
            )

            RakuenFlagType.TYPE_OLD -> Text(
                text = "旧贴",
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.extraSmall)
                    .padding(vertical = 2.dp, horizontal = 4.dp),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.surface
                )
            )

            RakuenFlagType.TYPE_OLDEST -> Text(
                text = "坟贴",
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.extraSmall)
                    .padding(vertical = 2.dp, horizontal = 4.dp),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.surface
                )
            )

            RakuenFlagType.TYPE_NEW -> Text(
                text = "新帖",
                modifier = Modifier
                    .background(Color.Green.copy(green = 0.8f), MaterialTheme.shapes.extraSmall)
                    .padding(vertical = 2.dp, horizontal = 4.dp),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White
                )
            )
        }
    }
}

@Composable
private fun TopicPageItemAvatar(
    item: ComposeTopic,
    onClickUser: (ComposeUser) -> Unit,
    onClickMono: (ComposeMonoDisplay) -> Unit,
    onClickSubject: (ComposeSubject) -> Unit,
) {
    when (item.topicType) {
        RakuenIdType.TYPE_SUBJECT -> {
            StateImage(
                modifier = Modifier
                    .size(44.dp)
                    .clickWithoutRipped {
                        if (item.subject != ComposeSubject.Empty) onClickSubject(item.subject)
                        else if (item.creator != ComposeUser.Empty) onClickUser(item.creator)
                    },
                shape = MaterialTheme.shapes.small,
                model = item.creator.displayAvatar
            )
        }

        RakuenIdType.TYPE_GROUP -> {
            StateImage(
                modifier = Modifier
                    .size(44.dp)
                    .clickWithoutRipped { onClickUser(item.creator) },
                shape = MaterialTheme.shapes.small,
                model = item.creator.displayAvatar
            )
        }

        RakuenIdType.TYPE_BLOG -> {
            StateImage(
                modifier = Modifier
                    .size(44.dp)
                    .clickWithoutRipped { onClickUser(item.creator) },
                shape = MaterialTheme.shapes.small,
                model = item.creator.displayAvatar
            )
        }

        RakuenIdType.TYPE_EP -> {
            StateImage(
                modifier = Modifier
                    .width(44.dp)
                    .aspectRatio(3 / 4f)
                    .clickWithoutRipped { onClickSubject(item.subject) },
                shape = MaterialTheme.shapes.small,
                model = item.subject.images.displayLargeImage
            )
        }

        RakuenIdType.TYPE_PERSON,
        RakuenIdType.TYPE_CRT,
            -> {
            StateImage(
                modifier = Modifier
                    .width(44.dp)
                    .aspectRatio(3 / 4f)
                    .clickWithoutRipped { onClickMono(item.mono) },
                shape = MaterialTheme.shapes.small,
                model = item.mono.mono.images.displayLargeImage,
                alignment = Alignment.TopCenter
            )
        }

        RakuenIdType.TYPE_INDEX -> {
            Spacer(modifier = Modifier.size(44.dp))
        }

        else -> {
            Spacer(modifier = Modifier.size(44.dp))
        }
    }
}