package com.xiaoyv.bangumi.shared.ui.view.user

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_avatar
import com.xiaoyv.bangumi.core_resource.resources.global_blog
import com.xiaoyv.bangumi.core_resource.resources.global_collection
import com.xiaoyv.bangumi.core_resource.resources.global_friend
import com.xiaoyv.bangumi.core_resource.resources.global_group
import com.xiaoyv.bangumi.core_resource.resources.profile_joined_at
import com.xiaoyv.bangumi.core_resource.resources.profile_no_sign
import com.xiaoyv.bangumi.shared.core.utils.formatDate
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import org.jetbrains.compose.resources.stringResource

/**
 * 用户主页头部信息卡，统一承载头像、签名和核心统计信息。
 */
@Composable
fun UserProfileHeroCard(
    user: ComposeUser,
    modifier: Modifier = Modifier,
    onAvatarClick: () -> Unit = {},
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ContentMargin),
            verticalArrangement = Arrangement.spacedBy(ContentMargin)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(ContentMargin),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StateImage(
                    modifier = Modifier
                        .size(88.dp)
                        .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        .clickable(onClick = onAvatarClick),
                    model = user.avatar.displayMediumImage,
                    contentDescription = stringResource(Res.string.global_avatar),
                    shape = CircleShape,
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(ContentMarginHalf)
                ) {
                    Text(
                        text = user.nickname,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "@${user.username}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = user.sign.ifBlank { stringResource(Res.string.profile_no_sign) },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
                verticalArrangement = Arrangement.spacedBy(ContentMarginHalf)
            ) {
                UserProfileMetric(
                    value = user.stats.subject.all.total.toString(),
                    label = stringResource(Res.string.global_collection)
                )
                UserProfileMetric(
                    value = user.stats.friend.toString(),
                    label = stringResource(Res.string.global_friend)
                )
                UserProfileMetric(
                    value = user.stats.group.toString(),
                    label = stringResource(Res.string.global_group)
                )
                UserProfileMetric(
                    value = user.stats.blog.toString(),
                    label = stringResource(Res.string.global_blog)
                )
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
                verticalArrangement = Arrangement.spacedBy(ContentMarginHalf)
            ) {
                if (user.location.isNotBlank()) {
                    UserProfileInfoChip(user.location)
                }
                if (user.joinedAt > 0) {
                    UserProfileInfoChip(
                        stringResource(
                            Res.string.profile_joined_at,
                            user.joinedAt.formatDate("yyyy-MM-dd")
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun UserProfileMetric(
    value: String,
    label: String,
) {
    Column(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                shape = MaterialTheme.shapes.medium
            )
            .padding(horizontal = ContentMargin, vertical = ContentMarginHalf),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun UserProfileInfoChip(text: String) {
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.72f),
                shape = MaterialTheme.shapes.extraLarge
            )
            .padding(horizontal = ContentMargin, vertical = ContentMarginHalf)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
