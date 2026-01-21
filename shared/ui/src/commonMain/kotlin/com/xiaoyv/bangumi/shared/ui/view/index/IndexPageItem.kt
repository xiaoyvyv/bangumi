package com.xiaoyv.bangumi.shared.ui.view.index

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Topic
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_index
import com.xiaoyv.bangumi.core_resource.resources.global_timeline
import com.xiaoyv.bangumi.shared.core.utils.formatAgo
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeIndex
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import org.jetbrains.compose.resources.stringResource

@Composable
fun IndexCardItem(
    item: ComposeIndex,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    OutlinedCard(modifier = modifier, onClick = onClick) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
            ) {
                StateImage(
                    modifier = Modifier.size(24.dp),
                    model = item.creator.avatar.displayMediumImage,
                    shape = MaterialTheme.shapes.small
                )
                Text(
                    text = item.creator.nickname,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

}

@Composable
fun IndexPageItem(
    item: ComposeIndex,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        ListItem(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 4.dp),
            leadingContent = {
                IndexFocusCard(
                    modifier = Modifier.size(120.dp),
                    item = item,
                )
            },
            overlineContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
                ) {
                    if (item.creator.avatar.displayMediumImage.isNotBlank()) StateImage(
                        modifier = Modifier.size(24.dp),
                        model = item.creator.avatar.displayMediumImage,
                        shape = MaterialTheme.shapes.extraSmall,
                    )
                    Text(
                        modifier = Modifier.weight(1f),
                        text = item.creator.nickname,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            headlineContent = {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = LayoutPaddingHalf),
                    verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = item.title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (item.desc.isNotBlank()) Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = item.desc,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            },
            supportingContent = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
                ) {
                    if (item.total > 0) Text(
                        text = "收录：${item.total}",
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
                        text = item.updatedAt.formatAgo() + "更新",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )
    }
}


@Composable
fun IndexDialogItem(
    item: ComposeIndex,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    ListItem(
        modifier = modifier.clickable(onClick = onClick),
        colors = ListItemDefaults.colors(containerColor = Color.Unspecified),
        leadingContent = {
            Icon(
                imageVector = BgmIcons.Topic,
                contentDescription = stringResource(Res.string.global_index)
            )
        },
        headlineContent = {
            Text(text = item.title)
        },
        supportingContent = if (item.desc.isBlank()) null else {
            {
                Text(text = item.desc)
            }
        }
    )

    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
}