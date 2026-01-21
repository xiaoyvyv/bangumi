package com.xiaoyv.bangumi.shared.ui.view.mono

import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
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
import com.xiaoyv.bangumi.shared.core.types.MonoCastType
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.utils.clickWithoutRipped
import com.xiaoyv.bangumi.shared.core.utils.withSpanStyle
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoDisplay
import com.xiaoyv.bangumi.shared.ui.component.divider.BgmHorizontalDivider
import com.xiaoyv.bangumi.shared.ui.component.image.InfoImage
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.text.StarColor
import com.xiaoyv.bangumi.shared.ui.view.tag.TagItem


@Composable
fun MonoLineItemCharacter(
    display: ComposeMonoDisplay,
    modifier: Modifier = Modifier,
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
                .padding(LayoutPadding)
                .width(100.dp),
            blur = item.mono.images.displayGridImage,
            model = item.mono.images.displayMediumImage,
            maskText = MonoCastType.string(item.type),
            onClick = { onClick(item.mono.id, MonoType.CHARACTER) }
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = LayoutPadding)
                .padding(end = LayoutPadding),
            verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
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
                    horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
                    verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
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
                        horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
                    ) {
                        StateImage(
                            modifier = Modifier.size(44.dp),
                            model = it.images.displayMediumImage,
                            shape = MaterialTheme.shapes.small,
                            alignment = Alignment.TopCenter
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
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
    BgmHorizontalDivider()
}


@Composable
fun MonoCardItemCharacter(
    item: ComposeMonoDisplay,
    modifier: Modifier = Modifier,
    onClick: (Long, Int) -> Unit = { _, _ -> },
) {
    Column(modifier = modifier) {
        InfoImage(
            modifier = Modifier.fillMaxWidth(),
            blur = item.mono.images.displayGridImage,
            model = item.mono.images.displayMediumImage,
            maskText = item.mono.displayName,
            onClick = { onClick(item.mono.id, MonoType.CHARACTER) }
        )

        Spacer(modifier = Modifier.height(LayoutPaddingHalf))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf / 2)
        ) {
            Text(
                modifier = Modifier
                    .weight(1f, false)
                    .basicMarquee(Int.MAX_VALUE, spacing = MarqueeSpacing(LayoutPaddingHalf)),
                text = item.mono.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
            )
            if (item.mono.comment > 0) Text(
                text = "（${item.mono.comment}）",
                color = StarColor,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}