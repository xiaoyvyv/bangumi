package com.xiaoyv.bangumi.shared.ui.view.mono

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_comments_cnt
import com.xiaoyv.bangumi.shared.core.types.MonoCastType
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.utils.clickWithoutRipped
import com.xiaoyv.bangumi.shared.core.utils.withSpanStyle
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoDisplay
import com.xiaoyv.bangumi.shared.ui.component.divider.BgmHorizontalDivider
import com.xiaoyv.bangumi.shared.ui.component.image.InfoImage
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.text.StarColor
import com.xiaoyv.bangumi.shared.ui.theme.ContentCoverWidth
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import com.xiaoyv.bangumi.shared.ui.view.tag.TagItem
import org.jetbrains.compose.resources.stringResource


@Composable
fun MonoLineItemCharacter(
    display: ComposeMonoDisplay,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true,
    onClick: (Long, Int) -> Unit = { _, _ -> },
) {
    val item = display.info

    Row(
        modifier = Modifier
            .semantics { contentDescription = "character_item" }
            .clickable { onClick(item.mono.id, MonoType.CHARACTER) }
            .then(modifier)
    ) {
        InfoImage(
            modifier = Modifier
                .padding(ContentMargin)
                .width(ContentCoverWidth),
            model = item.mono.images.displayMediumImage,
            text = MonoCastType.string(item.type),
            onClick = { onClick(item.mono.id, MonoType.CHARACTER) }
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = ContentMargin)
                .padding(end = ContentMargin),
            verticalArrangement = Arrangement.spacedBy(ContentMarginHalf)
        ) {
            Text(
                text = buildAnnotatedString {
                    append(item.mono.nameCN.ifBlank { item.mono.name })
                    if (item.mono.nameCN.isNotBlank()) {
                        withSpanStyle(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Normal,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize
                        ) {
                            append(" ")
                            append(item.mono.name)
                        }
                    }

                    if (item.mono.comment > 0) {
                        withSpanStyle(
                            color = StarColor,
                            fontWeight = FontWeight.Normal,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize
                        ) {
                            append(" (+${item.mono.comment})")
                        }
                    }
                },
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            when {
                // 信息
                item.mono.infobox.isNotEmpty() -> FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
                    verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
                ) {
                    item.mono.infobox.forEach { info ->
                        TagItem(tag = remember(info.key) { info.displayInfo })
                    }
                }
                // 信息
                item.mono.info.isNotBlank() -> Text(
                    text = item.mono.info,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // CV 信息（只有请求的条目相关的角色才有数据）
            if (item.actors.isNotEmpty()) {
                item.actors.forEach {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickWithoutRipped { onClick(it.id, MonoType.PERSON) },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf)
                    ) {
                        StateImage(
                            modifier = Modifier.size(44.dp),
                            model = it.images.displayMediumImage,
                            shape = MaterialTheme.shapes.small,
                            alignment = Alignment.TopCenter
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(ContentMarginHalf)
                        ) {
                            Text(
                                text = it.nameCN.ifBlank { it.name },
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "CV",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
    if (showDivider) BgmHorizontalDivider()
}


@Composable
fun MonoCardItemCharacter(
    item: ComposeMonoDisplay,
    modifier: Modifier = Modifier,
    onClick: (Long, Int) -> Unit = { _, _ -> },
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        onClick = { onClick(item.mono.id, MonoType.CHARACTER) }
    ) {
        InfoImage(
            modifier = Modifier.fillMaxWidth(),
            model = item.mono.images.displayMediumImage,
            aspectRatio = 1f,
            shape = MaterialTheme.shapes.small.copy(
                bottomStart = CornerSize(0.dp),
                bottomEnd = CornerSize(0.dp)
            )
        ) {
            if (item.mono.comment > 0) {
                Text(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                            shape = MaterialTheme.shapes.extraSmall.copy(
                                bottomStart = CornerSize(0.dp),
                                topEnd = CornerSize(0.dp),
                                bottomEnd = CornerSize(0.dp)
                            )
                        )
                        .padding(paddingValues = PaddingValues(vertical = 1.dp, horizontal = 4.dp)),
                    text = stringResource(Res.string.global_comments_cnt, item.mono.comment),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = StarColor
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ContentMarginHalf),
            verticalArrangement = Arrangement.spacedBy(ContentMarginHalf / 2)
        ) {
            Text(
                text = item.mono.displayName,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
            )

            Text(
                text = if (item.mono.role != MonoCastType.UNKNOWN) MonoCastType.string(item.mono.role) else item.mono.name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
