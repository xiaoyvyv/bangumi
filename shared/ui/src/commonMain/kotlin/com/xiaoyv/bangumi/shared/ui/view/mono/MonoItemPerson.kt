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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_comments_cnt
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.types.PersonType
import com.xiaoyv.bangumi.shared.core.utils.withSpanStyle
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoDisplay
import com.xiaoyv.bangumi.shared.ui.component.divider.BgmHorizontalDivider
import com.xiaoyv.bangumi.shared.ui.component.image.InfoImage
import com.xiaoyv.bangumi.shared.ui.component.text.StarColor
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import com.xiaoyv.bangumi.shared.ui.view.tag.TagItem
import org.jetbrains.compose.resources.stringResource


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
                .padding(ContentMargin)
                .width(100.dp),
            model = item.mono.images.displayMediumImage,
            onClick = { onClick(item.mono.id, MonoType.PERSON) }
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

            // 职位信息（只有请求的条目相关的制作人员才有数据）
            if (item.positions.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
                    verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
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
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        onClick = { onClick(item.mono.id, MonoType.PERSON) }
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
                text = let {
                    val texts = mutableListOf<String>()
                    if (item.mono.type != PersonType.MAIN && item.mono.type != PersonType.UNKNOWN) {
                        texts.add(PersonType.string(item.mono.type))
                    }
                    texts.addAll(item.mono.displayCareer.map { stringResource(it) })
                    texts.joinToString(" · ").ifBlank { item.mono.name }
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
