package com.xiaoyv.bangumi.shared.ui.view.index

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.sp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_index
import com.xiaoyv.bangumi.core_resource.resources.index_page_item_collects
import com.xiaoyv.bangumi.core_resource.resources.index_page_item_replies
import com.xiaoyv.bangumi.core_resource.resources.index_page_item_total
import com.xiaoyv.bangumi.core_resource.resources.index_page_item_updated
import com.xiaoyv.bangumi.shared.core.utils.formatAgo
import com.xiaoyv.bangumi.shared.data.model.response.bgm.index.ComposeIndex
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import org.jetbrains.compose.resources.stringResource

/**
 * Displays a compact directory card for surfaces where several directories are shown together.
 *
 * @param item The directory to display.
 * @param modifier The modifier applied to the card.
 * @param onClick Handles navigation to the directory.
 */
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
                .padding(ContentMargin),
            verticalArrangement = Arrangement.spacedBy(ContentMarginHalf)
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
                horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf)
            ) {
                StateImage(
                    modifier = Modifier.size(ContentMargin + ContentMarginHalf),
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

/**
 * Displays a directory with its author, description, and activity metadata.
 *
 * The layout deliberately avoids using the creator avatar as a pseudo cover. A directory does
 * not have a reliable cover image, so the card instead gives its title, purpose, and activity
 * enough space to be scanned in a list.
 *
 * @param item The directory to display.
 * @param modifier The modifier applied to the card.
 * @param onClick Handles navigation to the directory detail page.
 */
@Composable
@OptIn(ExperimentalLayoutApi::class)
fun IndexPageItem(
    item: ComposeIndex,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ContentMargin),
            verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
        ) {
            IndexPageItemCreator(item)

            Text(
                text = item.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            if (item.desc.isNotBlank()) {
                Text(
                    text = item.desc,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    lineHeight = 22.sp,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
                verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
            ) {
                if (item.total > 0) {
                    IndexPageItemMetadata(stringResource(Res.string.index_page_item_total, item.total))
                }
                if (item.collects > 0) {
                    IndexPageItemMetadata(stringResource(Res.string.index_page_item_collects, item.collects))
                }
                if (item.replies > 0) {
                    IndexPageItemMetadata(stringResource(Res.string.index_page_item_replies, item.replies))
                }
            }
        }
    }
}

/**
 * Displays the creator and latest activity time in an index list card.
 *
 * @param item The directory whose creator metadata is shown.
 */
@Composable
private fun IndexPageItemCreator(item: ComposeIndex) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
    ) {
        StateImage(
            modifier = Modifier.size(ContentMargin + ContentMarginHalf),
            model = item.creator.avatar.displayMediumImage,
            shape = MaterialTheme.shapes.medium,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.creator.nickname,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = stringResource(Res.string.index_page_item_updated, item.updatedAt.formatAgo()),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/**
 * Displays one compact metadata value in an index list card.
 *
 * @param text The formatted metadata text.
 */
@Composable
private fun IndexPageItemMetadata(text: String) {
    Text(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                shape = MaterialTheme.shapes.small,
            )
            .padding(horizontal = ContentMarginHalf, vertical = ContentMarginHalf / 2),
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
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
