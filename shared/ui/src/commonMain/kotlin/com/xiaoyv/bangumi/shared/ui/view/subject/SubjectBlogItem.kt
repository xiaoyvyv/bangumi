package com.xiaoyv.bangumi.shared.ui.view.subject

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_timeline
import com.xiaoyv.bangumi.core_resource.resources.reply_comment
import com.xiaoyv.bangumi.shared.core.utils.clickWithoutRipped
import com.xiaoyv.bangumi.shared.core.utils.formatDate
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeBlogDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUser
import com.xiaoyv.bangumi.shared.ui.component.divider.BgmHorizontalDivider
import com.xiaoyv.bangumi.shared.ui.component.image.InfoImage
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import org.jetbrains.compose.resources.stringResource

/**
 * [SubjectBlogItem]
 *
 * @since 2025/5/7
 */
@Composable
fun SubjectBlogItem(
    item: ComposeBlogDisplay,
    modifier: Modifier = Modifier,
    onClickUser: (ComposeUser) -> Unit = {},
    onClick: () -> Unit = {},
) {
    Column(modifier = Modifier.clickable(onClick = onClick).then(modifier)) {
        Row(
            modifier = Modifier.padding(LayoutPadding),
            horizontalArrangement = Arrangement.spacedBy(LayoutPadding)
        ) {
            InfoImage(
                modifier = Modifier.width(80.dp),
                model = item.blog.icon,
                shape = MaterialTheme.shapes.medium,
                blur = item.blog.icon,
                onClick = onClick
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
            ) {
                Row(
                    modifier = Modifier.clickWithoutRipped { onClickUser(item.user) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
                ) {
                    StateImage(
                        modifier = Modifier.size(24.dp),
                        model = item.user.avatar.displayMediumImage,
                        shape = MaterialTheme.shapes.extraSmall,
                    )
                    Text(
                        modifier = Modifier.weight(1f),
                        text = item.user.nickname,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = item.blog.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Medium
                )

                if (item.blog.summary.isNotBlank()) Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = item.blog.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
                ) {
                    if (item.blog.replies > 0) Text(
                        text = buildString {
                            append(item.blog.replies)
                            append(" ")
                            append(stringResource(Res.string.reply_comment))
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.weight(1f))
                    Icon(
                        modifier = Modifier.size(16.dp),
                        imageVector = BgmIcons.CalendarMonth,
                        contentDescription = stringResource(Res.string.global_timeline),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = item.blog.updatedAt.formatDate("yyyy-MM-dd"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        BgmHorizontalDivider()
    }
}