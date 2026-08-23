package com.xiaoyv.bangumi.shared.ui.view.pixiv

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.pixiv_user_following
import com.xiaoyv.bangumi.core_resource.resources.pixiv_user_id
import com.xiaoyv.bangumi.core_resource.resources.pixiv_user_mypixiv
import com.xiaoyv.bangumi.core_resource.resources.pixiv_user_official
import com.xiaoyv.bangumi.core_resource.resources.pixiv_user_premium
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivUserInfoBody
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import org.jetbrains.compose.resources.stringResource

/**
 * Displays a full-bleed Pixiv user cover that collapses with the page header.
 */
@Composable
fun PixivUserProfileHeader(
    user: ComposePixivUserInfoBody,
    topPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(360.dp)
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        StateImage(
            modifier = Modifier.fillMaxSize(),
            model = user.background.url.ifBlank { user.imageBig.ifBlank { user.image } },
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.52f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                            MaterialTheme.colorScheme.surface,
                        )
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(topPadding)
                .padding(ContentMargin),
            verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ContentMargin),
            ) {
                StateImage(
                    modifier = Modifier
                        .size(84.dp)
                        .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape),
                    model = user.imageBig.ifBlank { user.image },
                    shape = CircleShape,
                )
                Column(verticalArrangement = Arrangement.spacedBy(ContentMarginHalf)) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = stringResource(Res.string.pixiv_user_id, user.userId.toString()),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf)) {
                        if (user.premium) PixivUserBadge(stringResource(Res.string.pixiv_user_premium))
                        if (user.official) PixivUserBadge(stringResource(Res.string.pixiv_user_official))
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(ContentMargin)) {
                PixivUserCounter(stringResource(Res.string.pixiv_user_following), user.following)
                PixivUserCounter(stringResource(Res.string.pixiv_user_mypixiv), user.mypixivCount)
            }
        }
    }
}

/**
 * Renders a compact labeled user counter.
 */
@Composable
private fun PixivUserCounter(label: String, value: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(ContentMarginHalf)) {
        Text(text = value.toString(), style = MaterialTheme.typography.bodyLarge)
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * Renders a status badge for premium and official accounts.
 */
@Composable
private fun PixivUserBadge(label: String) {
    AssistChip(
        onClick = {},
        label = { Text(label) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
        border = null,
    )
}
