package com.xiaoyv.bangumi.shared.ui.view.comment

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_master
import com.xiaoyv.bangumi.shared.core.types.ButtonType
import com.xiaoyv.bangumi.shared.core.utils.clickWithoutRipped
import com.xiaoyv.bangumi.shared.core.utils.formatAgo
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReaction
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.ui.component.chip.DropMenuActionButton
import com.xiaoyv.bangumi.shared.ui.component.emoji.PopupReaction
import com.xiaoyv.bangumi.shared.ui.component.emoji.ReactionGroup
import com.xiaoyv.bangumi.shared.ui.component.emoji.rememberPopupReactionState
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.tab.rememberButtonTypeMenu
import com.xiaoyv.bangumi.shared.ui.component.text.BgmLinkedText
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import org.jetbrains.compose.resources.stringResource

/**
 * [CommentItem]
 *
 * @since 2025/5/7
 */
@Composable
fun CommentReplyItem(
    item: ComposeReply,
    level: Int = 0,
    modifier: Modifier = Modifier,
    isLikeable: Boolean = false,
    onClickReaction: (ComposeReaction) -> Unit = {},
    onClickUser: (String) -> Unit = {},
    onClickReport: () -> Unit = {},
    onClick: () -> Unit = {},
) {
    ListItem(
        modifier = Modifier
            .clickable(onClick = onClick)
            .let { if (level == 0) it else it.padding(start = 60.dp) }
            .then(modifier),
        leadingContent = if (level > 0) null else {
            {
                StateImage(
                    modifier = Modifier
                        .size(44.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.small)
                        .clickWithoutRipped { onClickUser(item.user.username) },
                    shape = MaterialTheme.shapes.small,
                    model = item.user.avatar.displayMediumImage,
                )
            }
        },
        overlineContent = {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
                horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
                itemVerticalAlignment = Alignment.CenterVertically
            ) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(ContentMarginHalf / 2),
                    horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
                    itemVerticalAlignment = Alignment.CenterVertically
                ) {
                    // 子评论使用小头像
                    if (level > 0) StateImage(
                        modifier = Modifier
                            .size(32.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.small)
                            .clickWithoutRipped { onClickUser(item.user.username) },
                        shape = MaterialTheme.shapes.small,
                        model = item.user.avatar.displayMediumImage
                    )

                    Text(
                        modifier = Modifier.clickWithoutRipped { onClickUser(item.user.username) },
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
                                .background(MaterialTheme.colorScheme.tertiary, MaterialTheme.shapes.extraSmall)
                                .padding(horizontal = 4.dp, vertical = 1.dp),
                            text = stringResource(Res.string.global_master),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onTertiary,
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
            }
        },
        headlineContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = ContentMarginHalf),
                verticalArrangement = Arrangement.spacedBy(ContentMarginHalf)
            ) {
                if (item.displayQuote.isNotBlank()) {
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
                            .padding(vertical = ContentMarginHalf)
                            .padding(start = 12.dp, end = ContentMarginHalf),
                        text = item.displayQuote,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = colorScheme.primary,
                            textDecoration = TextDecoration.Underline
                        )
                    )
                }

                BgmLinkedText(
                    modifier = Modifier.fillMaxWidth(),
                    text = item.displayContent,
                )

                if (item.reactions.isNotEmpty()) {
                    ReactionGroup(
                        modifier = Modifier.fillMaxWidth(),
                        reactions = item.reactions,
                        onClick = onClickReaction
                    )
                }
            }
        },
        trailingContent = {
            val reactionState = rememberPopupReactionState()

            PopupReaction(
                state = reactionState,
                onClick = {
                    onClickReaction(ComposeReaction(value = it))
                }
            )

            DropMenuActionButton(
                modifier = Modifier.size(20.dp),
                imageVector = BgmIcons.MoreHoriz,
                imageTint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                options = rememberButtonTypeMenu {
                    if (isLikeable) {
                        add(ButtonType.Reaction)
                    }
                    add(ButtonType.Report)
                },
                onOptionClick = {
                    when (it.type) {
                        ButtonType.Report -> onClickReport()
                        ButtonType.Reaction -> reactionState.show()
                        else -> Unit
                    }
                }
            )
        },
        supportingContent = {
            Text(
                text = item.createdAt.formatAgo(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}
