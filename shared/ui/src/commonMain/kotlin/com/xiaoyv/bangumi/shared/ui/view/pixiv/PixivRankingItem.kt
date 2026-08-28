package com.xiaoyv.bangumi.shared.ui.view.pixiv

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivRankingContent
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import kotlin.math.abs

@Composable
fun PixivRankingItem(
    item: ComposePixivRankingContent,
    modifier: Modifier = Modifier,
    onClick: (ComposePixivRankingContent) -> Unit = {},
) {
    val pageCount = item.illust_page_count.toIntOrNull() ?: 1
    val isNew = item.yes_rank == 0
    val rankDiff = item.yes_rank - item.rank
    val isUgoira = item.illust_type == "2"

    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        onClick = { onClick(item) }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(
                        MaterialTheme.shapes.medium.copy(
                            bottomStart = CornerSize(0.dp),
                            bottomEnd = CornerSize(0.dp)
                        )
                    )
            ) {
                StateImage(
                    model = item.url,
                    contentDescription = item.title,
                    modifier = Modifier.matchParentSize()
                )

                if (isUgoira) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(44.dp)
                            .clip(MaterialTheme.shapes.extraLarge)
                            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.48f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.inverseOnSurface,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                }

                if (item.rank > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(ContentMarginHalf)
                            .background(
                                color = when (item.rank) {
                                    1 -> MaterialTheme.colorScheme.primaryContainer
                                    2 -> MaterialTheme.colorScheme.secondaryContainer
                                    3 -> MaterialTheme.colorScheme.tertiaryContainer
                                    else -> MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.9f)
                                },
                                shape = MaterialTheme.shapes.extraSmall,
                            )
                            .padding(horizontal = ContentMarginHalf, vertical = 2.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "#${item.rank}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = when (item.rank) {
                                    1 -> MaterialTheme.colorScheme.onPrimaryContainer
                                    2 -> MaterialTheme.colorScheme.onSecondaryContainer
                                    3 -> MaterialTheme.colorScheme.onTertiaryContainer
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                },
                            )

                            Spacer(modifier = Modifier.width(ContentMarginHalf / 2))

                            Text(
                                text = when {
                                    isNew -> "NEW"
                                    rankDiff > 0 -> "↑$rankDiff"
                                    rankDiff < 0 -> "↓${abs(rankDiff)}"
                                    else -> "-"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    isNew -> MaterialTheme.colorScheme.error
                                    rankDiff > 0 -> MaterialTheme.colorScheme.primary
                                    rankDiff < 0 -> MaterialTheme.colorScheme.error
                                    else -> MaterialTheme.colorScheme.outline
                                },
                            )
                        }
                    }
                }

                // 多图 Badge (右上角)
                if (pageCount > 1) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(ContentMarginHalf)
                            .background(
                                color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.65f),
                                shape = MaterialTheme.shapes.extraSmall
                            )
                            .padding(horizontal = ContentMarginHalf, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${pageCount}P",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.inverseOnSurface
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(ContentMarginHalf)
            ) {
                // 标题
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(ContentMarginHalf / 2))

                // 作者头像 (AsyncImage) + 名字
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    if (item.profile_img.isNotBlank()) {
                        AsyncImage(
                            model = item.profile_img,
                            contentDescription = item.user_name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(20.dp)
                                .clip(MaterialTheme.shapes.extraLarge)
                        )
                        Spacer(modifier = Modifier.width(ContentMarginHalf))
                    }

                    Text(
                        text = item.user_name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PixivRankingItemPreview() {
    PreviewColumn {
        PixivRankingItem(
            modifier = Modifier.padding(ContentMarginHalf),
            item = ComposePixivRankingContent(
                title = "作品标题作品标题作品标题作品标题",
                user_name = "画师名称",
                url = "",
                profile_img = "",
                rank = 1,
                yes_rank = 2,
                illust_page_count = "3",
                illust_type = "0"
            )
        )
        PixivRankingItem(
            modifier = Modifier.padding(ContentMarginHalf),
            item = ComposePixivRankingContent(
                title = "动图作品示例",
                user_name = "画师名称",
                url = "",
                profile_img = "",
                rank = 4,
                yes_rank = 0,
                illust_page_count = "1",
                illust_type = "2"
            )
        )
    }
}
