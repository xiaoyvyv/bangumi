package com.xiaoyv.bangumi.shared.ui.view.episode

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.automirrored.rounded.Comment
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
import com.xiaoyv.bangumi.core_resource.resources.global_aired
import com.xiaoyv.bangumi.core_resource.resources.global_not_aired
import com.xiaoyv.bangumi.core_resource.resources.global_unknown
import com.xiaoyv.bangumi.core_resource.resources.parse_search_comment
import com.xiaoyv.bangumi.shared.core.types.CollectionEpisodeType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeEpisode
import com.xiaoyv.bangumi.shared.ui.component.button.episodeCollectionButtonColors
import com.xiaoyv.bangumi.shared.ui.component.divider.BgmHorizontalDivider
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.theme.BgmIconsMirrored
import com.xiaoyv.bangumi.shared.ui.theme.colorStateAiredContainer
import com.xiaoyv.bangumi.shared.ui.theme.colorStateAiredText
import org.jetbrains.compose.resources.stringResource

@Composable
fun EpisodeItem(
    modifier: Modifier,
    @SubjectType subjectType: Int,
    item: ComposeEpisode,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier.clickable(onClick = onClick).then(modifier),
        verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = item.rememberDisplayTitle(),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            when {
                item.collection.status != CollectionEpisodeType.UNKNOWN -> {
                    val colors = episodeCollectionButtonColors(item.collection.status, item.isAiring, item.isAired)
                    Text(
                        modifier = Modifier
                            .background(color = colors.containerColor, shape = MaterialTheme.shapes.small)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        text = CollectionEpisodeType.string(subjectType, item.collection.status),
                        color = colors.contentColor,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
                // 已放送
                item.isAired -> {
                    Text(
                        modifier = Modifier
                            .background(color = colorStateAiredContainer, shape = MaterialTheme.shapes.small)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        text = stringResource(Res.string.global_aired),
                        color = colorStateAiredText,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
                // 未放送
                item.airdate.isNotBlank() -> {
                    Text(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = MaterialTheme.shapes.small
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        text = stringResource(Res.string.global_not_aired),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = item.name.ifBlank { stringResource(Res.string.global_unknown) },
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = item.rememberDisplaySubtitle(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )

            if (item.commentCount != 0) {
                Icon(
                    modifier = Modifier.size(14.dp),
                    imageVector = BgmIconsMirrored.Comment,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = null,
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = stringResource(Res.string.parse_search_comment, item.commentCount),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    BgmHorizontalDivider()
}