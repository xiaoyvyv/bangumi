package com.xiaoyv.bangumi.shared.ui.view.subject

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_ep_cnt
import com.xiaoyv.bangumi.core_resource.resources.global_rank_no
import com.xiaoyv.bangumi.core_resource.resources.global_timeline
import com.xiaoyv.bangumi.core_resource.resources.subject_rate_count
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.types.list.ListSubjectType
import com.xiaoyv.bangumi.shared.core.utils.formatDate
import com.xiaoyv.bangumi.shared.core.utils.noNull
import com.xiaoyv.bangumi.shared.core.utils.toFixed
import com.xiaoyv.bangumi.shared.core.utils.toTrimString
import com.xiaoyv.bangumi.shared.data.model.PreviewComposeSubject
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.ListSubjectParam
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.LocalListSubjectParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeRelation
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubjectInterest
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubjectRelation
import com.xiaoyv.bangumi.shared.ui.component.bar.RatingBar
import com.xiaoyv.bangumi.shared.ui.component.button.collectionButtonColors
import com.xiaoyv.bangumi.shared.ui.component.image.InfoImage
import com.xiaoyv.bangumi.shared.ui.component.text.StarColor
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.theme.ContentCoverWidth
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import com.xiaoyv.bangumi.shared.ui.view.tag.TagItems
import org.jetbrains.compose.resources.stringResource

@Composable
fun SubjectCardItem(
    display: ComposeSubjectRelation,
    modifier: Modifier = Modifier,
    maxLine: Int = 2,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    onClick: () -> Unit,
) {
    val subject = display.subject

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ContentMarginHalf)
    ) {
        Box(
            Modifier
                .clip(MaterialTheme.shapes.small)
                .clickable(onClick = onClick)
        ) {
            InfoImage(
                modifier = Modifier.fillMaxWidth(),
                model = subject.images.displayMediumImage,
                shape = MaterialTheme.shapes.small,
                text = if (subject.rating.score > 0) {
                    subject.rating.score.toFixed(1).toString()
                } else {
                    stringResource(SubjectType.string(subject.type))
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = if (subject.rating.score > 0) StarColor else Color.White,
                    fontWeight = if (subject.rating.score > 0) FontWeight.SemiBold else FontWeight.Normal,
                )
            )

            if (subject.rating.rank > 0) {
                Text(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 8.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                            RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp),
                        )
                        .padding(8.dp, 4.dp),
                    text = stringResource(Res.string.global_rank_no) + " " + subject.rating.rank,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            if (subject.relation.isNotBlank()) {
                Text(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.TopEnd)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                            RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp),
                        )
                        .padding(8.dp, 4.dp),
                    text = subject.relation,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }


        Text(
            text = subject.displayName,
            style = style,
            fontWeight = FontWeight.Medium,
            maxLines = maxLine,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = buildAnnotatedString {
                append(stringResource(SubjectType.string(subject.type)))
                append(" ")
                append(display.relation.displayRelation)
                if (display.relation.desc.isNotBlank()) {
                    append(" - ")
                    append(display.relation.desc)
                }
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SubjectLineItem(
    display: ComposeSubjectRelation,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(contentPadding),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SubjectInfoCover(
            subject = display.subject
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(ContentMarginHalf / 2)
        ) {
            Text(
                text = display.subject.displayName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = display.subject.info.ifBlank { display.subject.summary },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Normal,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 24.sp
            )

            when {
                // 如果在收藏列表，优先展示收藏的数据
                LocalListSubjectParam.current.type == ListSubjectType.USER_COLLECTION -> {
                    SubjectLineCollectionItem(display.subject, onClick)
                }
                // 其次展示全站评分
                display.subject.rating.score > 0 -> {
                    FlowRow(
                        verticalArrangement = Arrangement.spacedBy(ContentMarginHalf / 2, Alignment.CenterVertically),
                        itemVerticalAlignment = Alignment.CenterVertically
                    ) {
                        RatingBar(
                            value = display.subject.rating.score,
                            starSize = 20.dp
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = display.subject.rating.score.toTrimString(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFFFAA00)
                        )

                        Spacer(modifier = Modifier.width(ContentMarginHalf))

                        // 总评人数
                        if (display.subject.displayRateTotalCount > 0) {
                            Text(
                                text = stringResource(
                                    Res.string.subject_rate_count,
                                    display.subject.displayRateTotalCount
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SubjectLineCollectionItem(
    subject: ComposeSubject,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(ContentMarginHalf)
        ) {
            val collection = subject.interest

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val buttonColors = collectionButtonColors(collection.type)
                Text(
                    modifier = Modifier
                        .background(buttonColors.containerColor, MaterialTheme.shapes.extraSmall)
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    text = CollectionType.string(subject.type, collection.type),
                    color = buttonColors.contentColor,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.weight(1f))
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = BgmIcons.CalendarMonth,
                    contentDescription = stringResource(Res.string.global_timeline),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = collection.updatedAt.formatDate("yyyy/MM/dd"),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 收藏的评分，吐槽，标签
            if (collection.rate > 0 || collection.comment.isNotBlank() || collection.tags.isNotEmpty()) {

                // 评分
                if (collection.rate > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf)
                    ) {
                        RatingBar(
                            value = collection.rate.toDouble(),
                            starSize = 16.dp
                        )
                        Text(
                            text = "(${collection.rate})",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = StarColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                // 收藏填写的标签
                if (collection.tags.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
                        verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
                    ) {
                        TagItems(collection.tags, color = MaterialTheme.colorScheme.primary)
                    }
                }

                // 收藏填写的吐槽
                if (collection.comment.isNotBlank()) {
                    Text(
                        text = collection.comment,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 22.sp
                        )
                    )
                }
            }
        }
    }
}


/**
 * 人物最近参与的作品 Item
 */
@Composable
fun SubjectWorkItem(
    display: ComposeSubjectRelation,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.clickable(onClick = onClick).then(modifier),
        horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf)
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(ContentMargin)
        ) {
            InfoImage(
                modifier = Modifier.width(ContentCoverWidth),
                model = display.subject.images.displayMediumImage,
                text = stringResource(SubjectType.string(display.subject.type)),
                textStyle = MaterialTheme.typography.bodySmall,
                onClick = onClick
            )

            Column(Modifier.weight(1f), Arrangement.spacedBy(ContentMarginHalf)) {
                Text(
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    text = display.subject.displayName,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    )
                )

                Text(
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    text = display.subject.info.ifBlank { display.subject.summary },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                // 参与作品中的职位
                if (display.positions.isNotEmpty()) FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
                    verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
                ) {
                    display.positions.forEach { item ->
                        Text(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small)
                                .padding(vertical = 4.dp, horizontal = 8.dp),
                            text = item.type.cn.ifBlank { item.type.en },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
    HorizontalDivider()
}

@Composable
fun SubjectInfoCover(
    subject: ComposeSubject,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Box(modifier = modifier.noNull(onClick) { clickable(onClick = it) }) {
        InfoImage(
            modifier = Modifier.width(ContentCoverWidth),
            model = subject.images.displayMediumImage,
            shape = MaterialTheme.shapes.small,
            text = subject.airtime.date
        )

        // 右上角文案
        val badgeText = when {
            // 排名
            subject.rating.rank != 0 -> buildString {
                append(stringResource(Res.string.global_rank_no))
                append(" ")
                append(subject.rating.rank)
            }
            // 话数
            subject.eps > 0 -> stringResource(Res.string.global_ep_cnt, subject.eps)
            else -> null
        }

        if (!badgeText.isNullOrBlank()) {
            Text(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.TopEnd)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                        RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp),
                    )
                    .padding(8.dp, 4.dp),
                text = badgeText,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}


@Preview
@Composable
fun SubjectItemPreview() {
    PreviewColumn {
        SubjectCardItem(
            modifier = Modifier
                .width(120.dp)
                .padding(ContentMargin),
            display = ComposeSubjectRelation(
                subject = PreviewComposeSubject,
                relation = ComposeRelation(cn = "导演", desc = "制作人")
            ),
            onClick = {}
        )
        HorizontalDivider()

        SubjectLineItem(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ContentMargin),
            display = ComposeSubjectRelation(
                subject = PreviewComposeSubject
            ),
            onClick = {}
        )
        HorizontalDivider()

        CompositionLocalProvider(LocalListSubjectParam provides ListSubjectParam(type = ListSubjectType.USER_COLLECTION)) {
            SubjectLineItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(ContentMargin),
                display = ComposeSubjectRelation(
                    subject = PreviewComposeSubject.copy(
                        interest = ComposeSubjectInterest(
                            comment = "测试评论",
                            rate = 7,
                            type = CollectionType.DONE
                        )
                    )
                ),
                onClick = {}
            )
        }
    }
}
