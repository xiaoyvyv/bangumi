package com.xiaoyv.bangumi.shared.ui.view.mono

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.utils.withSpanStyle
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoDisplay
import com.xiaoyv.bangumi.shared.ui.component.divider.BgmHorizontalDivider
import com.xiaoyv.bangumi.shared.ui.component.image.InfoImage
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.text.StarColor
import com.xiaoyv.bangumi.shared.ui.view.tag.TagItem


@Composable
fun MonoLineItemPerson(
    display: ComposeMonoDisplay,
    modifier: Modifier = Modifier,
    onClick: (Long, Int) -> Unit = { _, _ -> },
) {
    val item = display.info

    Row(
        modifier = Modifier
            .clickable { onClick(item.mono.id, MonoType.PERSON) }
            .then(modifier)
    ) {
        InfoImage(
            modifier = Modifier
                .padding(LayoutPadding)
                .width(100.dp),
            model = item.mono.images.displayMediumImage,
            onClick = { onClick(item.mono.id, MonoType.PERSON) }
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

            // 职位信息（只有请求的条目相关的制作人员才有数据）
            if (item.positions.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
                    verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
                ) {
                    item.positions.forEach {
                        Text(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small)
                                .padding(vertical = 4.dp, horizontal = 8.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            text = it.type.cn.ifBlank { it.type.en }
                        )
                    }
                }
            }
        }
    }
    BgmHorizontalDivider()
}


@Composable
fun MonoCardItemPerson(
    item: ComposeMonoDisplay,
    modifier: Modifier = Modifier,
    onClick: (Long, Int) -> Unit = { _, _ -> },
) {
    Column(modifier = modifier) {
        InfoImage(
            modifier = Modifier.fillMaxWidth(),
            model = item.mono.images.displayMediumImage,
            text = item.mono.name,
            onClick = { onClick(item.mono.id, MonoType.PERSON) }
        )

        Spacer(modifier = Modifier.height(LayoutPaddingHalf))

        Text(
            text = item.mono.displayName,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}