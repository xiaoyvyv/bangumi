package com.xiaoyv.bangumi.shared.ui.view.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.ui.component.button.collectionButtonColors
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf

/**
 * 时间光机与收藏概览复用的条目高亮卡片。
 */
@Composable
fun UserCollectionHighlightCard(
    title: String,
    imageUrl: String,
    badge: String,
    score: String,
    modifier: Modifier = Modifier,
    badgeType: Int = 0,
    onClick: () -> Unit = {},
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(ContentMarginHalf)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.76f)
            ) {
                StateImage(
                    modifier = Modifier.fillMaxWidth(),
                    model = imageUrl,
                    contentDescription = title,
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(ContentMarginHalf)
                        .background(
                            color = collectionButtonColors(badgeType).containerColor,
                            shape = MaterialTheme.shapes.extraSmall
                        )
                        .padding(horizontal = ContentMarginHalf, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelMedium,
                        color = collectionButtonColors(badgeType).contentColor
                    )
                    if (score.isNotBlank()) {
                        Text(
                            text = score,
                            style = MaterialTheme.typography.labelMedium,
                            color = collectionButtonColors(badgeType).contentColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Text(
                modifier = Modifier.padding(horizontal = ContentMargin, vertical = ContentMarginHalf),
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
