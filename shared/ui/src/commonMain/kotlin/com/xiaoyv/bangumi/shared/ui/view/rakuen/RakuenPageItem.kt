@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.ui.view.rakuen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.core.types.ButtonType
import com.xiaoyv.bangumi.shared.core.types.RakuenFlagType
import com.xiaoyv.bangumi.shared.core.types.RakuenType
import com.xiaoyv.bangumi.shared.core.utils.clickWithoutRipped
import com.xiaoyv.bangumi.shared.core.utils.formatAgo
import com.xiaoyv.bangumi.shared.core.utils.withSpanStyle
import com.xiaoyv.bangumi.shared.data.model.request.list.topic.LocalListTopicParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeEpisode
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeGroup
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.rakuen.ComposeRakuenTopic
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import com.xiaoyv.bangumi.shared.ui.component.action.LocalActionHandler
import com.xiaoyv.bangumi.shared.ui.component.chip.DropMenuActionButton
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.tab.rememberButtonTypeMenu
import com.xiaoyv.bangumi.shared.ui.component.text.HighlightedText
import com.xiaoyv.bangumi.shared.ui.component.text.StarColor
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import com.xiaoyv.bangumi.shared.ui.theme.ThinBorderStrokeVariant
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

/**
 * 超展开的条目
 */
@Composable
fun RakuenPageItem(
    item: ComposeRakuenTopic,
    modifier: Modifier = Modifier,
    needShowCategory: Boolean = true,
    onClick: (ComposeRakuenTopic) -> Unit = {},
    onClickGroup: (ComposeGroup) -> Unit = {},
    onClickUser: (ComposeUser) -> Unit = {},
    onClickSubject: (ComposeSubject) -> Unit = {},
    onClickMono: (ComposeMonoDisplay) -> Unit = {},
    onReport: () -> Unit = {},
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
                showCategory = needShowCategory,
                onClickUser = onClickUser,
                onClickMono = onClickMono,
                onClickGroup = onClickGroup,
                onClickSubject = onClickSubject
            )
        },
        headlineContent = {
            TopicPageItemHeadline(item = item)
        },
        supportingContent = {
            Row(modifier = Modifier.fillMaxWidth()) {
                if (item.creator != ComposeUser.Empty) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "@" + item.creator.nickname,
                        maxLines = 1,
                        textDecoration = TextDecoration.Underline,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                Text(
                    modifier = Modifier.offset(x = if (item.creator != ComposeUser.Empty) 38.dp else 0.dp),
                    text = item.updatedAt.formatAgo(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
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
private fun TopicPageItemOverline(
    item: ComposeRakuenTopic,
    showCategory: Boolean,
    onClickGroup: (ComposeGroup) -> Unit,
    onClickUser: (ComposeUser) -> Unit,
    onClickMono: (ComposeMonoDisplay) -> Unit,
    onClickSubject: (ComposeSubject) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
        verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
        itemVerticalAlignment = Alignment.CenterVertically
    ) {
        when (item.type) {
            // 展示小组名称
            RakuenType.GROUP, RakuenType.MY_GROUP -> {
                Text(
                    modifier = Modifier.clickWithoutRipped { onClickGroup(item.group) },
                    text = item.group.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
            // 展示条目名称
            RakuenType.SUBJECT,
            RakuenType.EP -> {
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
            RakuenType.CHARACTER,
            RakuenType.PERSON -> {
                Text(
                    modifier = Modifier.clickWithoutRipped { onClickMono(item.toMono()) },
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }

        if (showCategory) Text(
            text = stringResource(RakuenType.string(item.type)),
            modifier = Modifier
                .background(MaterialTheme.colorScheme.tertiary, MaterialTheme.shapes.extraSmall)
                .padding(vertical = 2.dp, horizontal = 4.dp),
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onTertiary
            )
        )

        TopicPageFlag(item)
    }
}

@Composable
fun TopicPageItemHeadline(item: ComposeRakuenTopic) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = ContentMarginHalf),
        verticalArrangement = Arrangement.spacedBy(ContentMarginHalf)
    ) {
        val title = when (item.type) {
            RakuenType.GROUP,
            RakuenType.MY_GROUP,
            RakuenType.SUBJECT -> item.title

            RakuenType.EP -> "Ep" + item.episode.displayTitle

            RakuenType.CHARACTER,
            RakuenType.PERSON -> item.displayName

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
    }
}

@Composable
private fun TopicPageItemTrailing(
    item: ComposeRakuenTopic,
    onReport: () -> Unit,
) {
    val actionHandler = LocalActionHandler.current

    when (item.type) {
        RakuenType.GROUP,
        RakuenType.MY_GROUP,
        RakuenType.SUBJECT -> {
            DropMenuActionButton(
                modifier = Modifier.size(20.dp),
                imageVector = BgmIcons.MoreHoriz,
                imageTint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                options = rememberButtonTypeMenu {
                    add(ButtonType.Share)
                    add(ButtonType.Report)
                },
                onOptionClick = {
                    when (it.type) {
                        ButtonType.Report -> onReport()
                        ButtonType.Share -> actionHandler.shareContent(item.shareUrl)
                        else -> Unit
                    }
                }
            )
        }

        RakuenType.EP,
        RakuenType.PERSON,
        RakuenType.CHARACTER -> {
            DropMenuActionButton(
                modifier = Modifier.size(20.dp),
                imageVector = BgmIcons.MoreHoriz,
                imageTint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                options = rememberButtonTypeMenu { add(ButtonType.Share) },
                onOptionClick = {
                    actionHandler.shareContent(item.shareUrl)
                }
            )
        }

        else -> Unit
    }
}


@Composable
private fun TopicPageFlag(item: ComposeRakuenTopic) {
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
    item: ComposeRakuenTopic,
    onClickUser: (ComposeUser) -> Unit,
    onClickMono: (ComposeMonoDisplay) -> Unit,
    onClickSubject: (ComposeSubject) -> Unit,
) {
    when (item.type) {
        RakuenType.SUBJECT -> {
            StateImage(
                modifier = Modifier
                    .size(44.dp)
                    .clickWithoutRipped {
                        if (item.subject != ComposeSubject.Empty) onClickSubject(item.subject)
                        else if (item.creator != ComposeUser.Empty) onClickUser(item.creator)
                    },
                shape = MaterialTheme.shapes.small,
                border = ThinBorderStrokeVariant,
                model = item.creator.avatar.displayMediumImage
            )
        }

        RakuenType.GROUP, RakuenType.MY_GROUP -> {
            StateImage(
                modifier = Modifier
                    .size(44.dp)
                    .clickWithoutRipped { onClickUser(item.creator) },
                shape = MaterialTheme.shapes.small,
                border = ThinBorderStrokeVariant,
                model = item.creator.avatar.displayMediumImage
            )
        }

        RakuenType.EP -> {
            StateImage(
                modifier = Modifier
                    .width(44.dp)
                    .aspectRatio(3 / 4f)
                    .clickWithoutRipped { onClickSubject(item.subject) },
                shape = MaterialTheme.shapes.small,
                border = ThinBorderStrokeVariant,
                model = item.subject.images.displayMediumImage
            )
        }

        RakuenType.PERSON,
        RakuenType.CHARACTER -> {
            StateImage(
                modifier = Modifier
                    .width(44.dp)
                    .aspectRatio(1f)
                    .clickWithoutRipped { onClickMono(item.toMono()) },
                shape = MaterialTheme.shapes.small,
                border = ThinBorderStrokeVariant,
                model = item.images.displayMediumImage,
                alignment = Alignment.TopCenter
            )
        }

        else -> {
            Spacer(modifier = Modifier.size(44.dp))
        }
    }
}

@Preview
@Composable
private fun PreviewRakuenPageItem() {
    PreviewColumn {
        RakuenPageItem(
            item = ComposeRakuenTopic(
                type = RakuenType.GROUP,
                title = "这是一个小组话题标题，非常火热",
                replyCount = 99,
                creator = ComposeUser(nickname = "小组成员"),
                group = ComposeGroup(name = "小组名称", title = "小组中文名称"),
                updatedAt = 1738000000000L,
                flags = persistentListOf(RakuenFlagType.TYPE_HOT)
            )
        )
        RakuenPageItem(
            item = ComposeRakuenTopic(
                type = RakuenType.SUBJECT,
                title = "这是一个条目话题标题",
                replyCount = 5,
                subject = ComposeSubject(name = "条目名称", nameCn = "条目中文名称"),
                updatedAt = 1738000000000L - 3600000,
                flags = persistentListOf(RakuenFlagType.TYPE_NEW)
            )
        )
        RakuenPageItem(
            item = ComposeRakuenTopic(
                type = RakuenType.EP,
                episode = ComposeEpisode(sortOrder = 12.0, chineseName = "最终回"),
                subject = ComposeSubject(name = "某动画"),
                updatedAt = 1738000000000L - 86400000,
            )
        )
        RakuenPageItem(
            item = ComposeRakuenTopic(
                type = RakuenType.CHARACTER,
                name = "某个角色",
                nameCN = "某个角色中文名",
                replyCount = 1,
                updatedAt = 1738000000000L - 86400000 * 2,
            )
        )
    }
}
