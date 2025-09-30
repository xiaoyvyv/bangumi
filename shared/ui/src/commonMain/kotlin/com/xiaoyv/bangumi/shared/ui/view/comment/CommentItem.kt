package com.xiaoyv.bangumi.shared.ui.view.comment

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.core.types.ButtonType
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.types.CommentType
import com.xiaoyv.bangumi.shared.core.utils.clickWithoutRipped
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeComment
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReaction
import com.xiaoyv.bangumi.shared.ui.component.bar.RatingBar
import com.xiaoyv.bangumi.shared.ui.component.button.collectionButtonColors
import com.xiaoyv.bangumi.shared.ui.component.chip.DropMenuActionButton
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.rememberAlertDialogState
import com.xiaoyv.bangumi.shared.ui.component.dialog.report.ReportDialog
import com.xiaoyv.bangumi.shared.ui.component.emoji.PopupReaction
import com.xiaoyv.bangumi.shared.ui.component.emoji.ReactionGroup
import com.xiaoyv.bangumi.shared.ui.component.emoji.rememberPopupReactionState
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.tab.rememberButtonTypeMenu
import com.xiaoyv.bangumi.shared.ui.component.text.BgmLinkedText
import com.xiaoyv.bangumi.shared.ui.component.text.StarColor
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons

/**
 * 评论的对应的话题的楼主用户名
 */
val LocalCommentTargetAuthorUsername = compositionLocalOf { "" }

/**
 * [CommentItem]
 *
 * @since 2025/5/7
 */
@Composable
fun CommentItem(
    item: ComposeComment,
    modifier: Modifier = Modifier,
    reactions: SerializeList<ComposeReaction>? = null,
    onClickReaction: (ComposeReaction) -> Unit = {},
    onClickUser: (String) -> Unit = {},
    onClick: () -> Unit = {},
) {
    ListItem(
        modifier = Modifier
            .clickable(onClick = onClick)
            .let { if (item.parent == null) it else it.padding(start = 60.dp) }
            .then(modifier),
        leadingContent = if (item.parent != null) null else {
            {
                StateImage(
                    modifier = Modifier
                        .size(44.dp)
                        .clickWithoutRipped {
                            onClickUser(item.user.username)
                        },
                    shape = MaterialTheme.shapes.small,
                    model = item.user.displayAvatar,
                )
            }
        },
        overlineContent = {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
                horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
                itemVerticalAlignment = Alignment.CenterVertically
            ) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf / 2),
                    horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
                    itemVerticalAlignment = Alignment.CenterVertically
                ) {
                    // 子评论使用小头像
                    if (item.parent != null) StateImage(
                        modifier = Modifier
                            .size(32.dp)
                            .clickWithoutRipped {
                                onClickUser(item.user.username)
                            },
                        shape = MaterialTheme.shapes.small,
                        model = item.user.displayAvatar
                    )
                    Text(
                        modifier = Modifier.clickWithoutRipped {
                            onClickUser(item.user.username)
                        },
                        text = item.user.nickname,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    )

                    if (LocalCommentTargetAuthorUsername.current == item.user.username) {
                        Text(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.extraSmall)
                                .border(1.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.extraSmall)
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            text = "楼主",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }

                    if (item.user.sign.isNotBlank()) Text(
                        text = item.user.sign.let { if (it.length > 15) "（${it.take(15)}…）" else "（${it}）" },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    )
                }

                // 条目底部评论收藏状态
                if (item.collectType != CollectionType.UNKNOWN) {
                    val buttonColors = collectionButtonColors(item.collectType)

                    Text(
                        text = CollectionType.string(item.subjectType, item.collectType),
                        modifier = Modifier
                            .background(buttonColors.containerColor, MaterialTheme.shapes.extraSmall)
                            .padding(vertical = 2.dp, horizontal = 4.dp),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = buttonColors.contentColor
                        )
                    )
                }

                // 底部条目评论评分
                if (item.star > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RatingBar(value = item.star, starSize = 16.dp)
                        Text(
                            text = "(${item.star})",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = StarColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        },
        headlineContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = LayoutPaddingHalf),
                verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
            ) {
                if (item.replyQuote.isNotBlank()) {
                    val colorScheme = MaterialTheme.colorScheme

                    Text(
                        modifier = Modifier
                            .background(colorScheme.surfaceContainer, MaterialTheme.shapes.extraSmall)
                            .drawBehind {
                                val strokeWidth = 4.dp.toPx()
                                drawLine(
                                    color = colorScheme.primary,
                                    start = androidx.compose.ui.geometry.Offset(strokeWidth / 2, 0f),
                                    end = androidx.compose.ui.geometry.Offset(strokeWidth / 2, size.height),
                                    strokeWidth = strokeWidth
                                )
                            }
                            .padding(vertical = LayoutPaddingHalf)
                            .padding(start = 12.dp, end = LayoutPaddingHalf),
                        text = item.replyQuote,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = colorScheme.primary,
                            textDecoration = TextDecoration.Underline
                        )
                    )
                }

                BgmLinkedText(
                    modifier = Modifier.fillMaxWidth(),
                    text = item.commentHtml,
                )

                if (reactions != null) {
                    ReactionGroup(
                        modifier = Modifier.fillMaxWidth(),
                        reactions = reactions,
                        onClick = onClickReaction
                    )
                }
            }
        },
        trailingContent = {
            val reactionState = rememberPopupReactionState()
            val reportDialogState = rememberAlertDialogState()

            ReportDialog(
                state = reportDialogState,
                onClick = { value, content ->

                }
            )

            PopupReaction(
                state = reactionState,
                onClick = {
                    onClickReaction(ComposeReaction(value = it, type = item.type))
                }
            )

            DropMenuActionButton(
                modifier = Modifier.size(20.dp),
                imageVector = BgmIcons.MoreHoriz,
                imageTint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                options = rememberButtonTypeMenu {
                    if (CommentType.isSupportRection(item.type)) {
                        add(ButtonType.Reaction)
                    }
                    add(ButtonType.Report)
                },
                onOptionClick = {
                    when (it.type) {
                        ButtonType.Report -> reportDialogState.show()
                        ButtonType.Reaction -> reactionState.show()
                        else -> Unit
                    }
                }
            )
        },
        supportingContent = {
            Text(
                text = buildAnnotatedString {
                    if (item.floor.isNotBlank()) {
                        withStyle(
                            SpanStyle(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append(item.floor)
                            append(" ")
                        }
                    }
                    append(item.time)
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}
