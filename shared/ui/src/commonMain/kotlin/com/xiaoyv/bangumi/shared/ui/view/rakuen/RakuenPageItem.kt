package com.xiaoyv.bangumi.shared.ui.view.rakuen

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.core.types.ButtonType
import com.xiaoyv.bangumi.shared.core.types.RakuenFlagType
import com.xiaoyv.bangumi.shared.core.types.RakuenType
import com.xiaoyv.bangumi.shared.core.utils.clickWithoutRipped
import com.xiaoyv.bangumi.shared.core.utils.formatAgo
import com.xiaoyv.bangumi.shared.core.utils.withSpanStyle
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalSharedState
import com.xiaoyv.bangumi.shared.data.model.request.ReportParam
import com.xiaoyv.bangumi.shared.data.model.request.list.topic.LocalListTopicParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.rakuen.ComposeRakuenTopic
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import com.xiaoyv.bangumi.shared.ui.component.chip.DropMenuActionButton
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.rememberAlertDialogState
import com.xiaoyv.bangumi.shared.ui.component.dialog.report.ReportDialog
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.tab.rememberButtonTypeMenu
import com.xiaoyv.bangumi.shared.ui.component.text.HighlightedText
import com.xiaoyv.bangumi.shared.ui.component.text.StarColor
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.theme.ThinBorderStrokeVariant
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

/**
 * 超展开的条目
 */
@Composable
fun RakuenPageItem(
    item: ComposeRakuenTopic,
    modifier: Modifier = Modifier,
    showCategory: Boolean = true,
    onClick: (ComposeRakuenTopic) -> Unit = {},
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
                showCategory = showCategory,
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
private fun TopicPageItemOverline(
    item: ComposeRakuenTopic,
    showCategory: Boolean,
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
            // 展示用户名称
            RakuenType.GROUP, RakuenType.MY_GROUP -> {
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
    onReport: (ReportParam) -> Unit,
) {
    when (item.type) {
        RakuenType.GROUP,
        RakuenType.MY_GROUP,
        RakuenType.SUBJECT
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

        RakuenType.EP,
        RakuenType.PERSON,
        RakuenType.CHARACTER -> {
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